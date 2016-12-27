package client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.OutgoingConnection
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Client extends App {
  implicit val system = ActorSystem("MongoStreamers")
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val request: Flow[HttpRequest, HttpResponse, Future[OutgoingConnection]] =
    Http().outgoingConnection(host = "localhost", port = 9000)

  private val response = Source.single(HttpRequest(uri = "/mongo/find"))
    .via(request)
    .runWith(Sink.head)

  response.onComplete{
    case Success(x) ⇒ x.entity.dataBytes.runForeach(bs ⇒ println(bs.utf8String))
    case Failure(e) ⇒ println(e.getMessage)
  }


}
