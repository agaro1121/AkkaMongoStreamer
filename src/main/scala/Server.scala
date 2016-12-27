import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import mongo.MongoRepo
import route.MongoApi

object Server extends App with MongoApi {
  implicit val system = ActorSystem("MongoStreamers")
  implicit val mat = ActorMaterializer()

  val mongoRepo = new MongoRepo()


  private val port = 9000
  Http().bindAndHandle(routes, "localhost", port)
  println(s"Server up and running on $port")
}
