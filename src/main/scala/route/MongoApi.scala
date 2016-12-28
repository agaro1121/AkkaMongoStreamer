package route

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import mongo.{MongoRepo, Person}
import org.mongodb.scala.Observable
import rxStreams.Implicits._
import spray.json.pimpAny

import scala.concurrent.Future

class MongoApi(val mongoRepo: MongoRepo) {

  val routes: Route = {
    pathPrefix("mongo") {
      path("insert") {
        post {
          entity(as[Person]) { person ⇒
            val result = mongoRepo.insertOne(person)
            complete {
              HttpEntity(ContentTypes.`text/plain(UTF-8)`,
                Source.fromFuture(result)
                  .flatMapConcat(completed ⇒ Source.fromPublisher(completed))
                  .map(res ⇒ ByteString(res.toString))
              )
            }
          }
        }
      } ~
        path("find") {
          val result: Future[Observable[Person]] = mongoRepo.find()
          complete {
            HttpEntity(ContentTypes.`application/json`,
              Source.fromFuture(result)
                .flatMapConcat(obs ⇒ Source.fromPublisher(obs))
                .map(person ⇒ ByteString(person.toJson.toString()))
            )
          }
        }
    }
  }

}
