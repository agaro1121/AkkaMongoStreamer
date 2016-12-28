import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import mongo.MongoRepo
import route.MongoApi

object Server extends App  {
  implicit val system = ActorSystem("MongoStreamers")
  implicit val mat = ActorMaterializer()

  val mongoRepo = new MongoRepo()
  val api = new MongoApi(mongoRepo)


  private val port = 9000
  Http().bindAndHandle(api.routes, "localhost", port)
  println(s"Server up and running on $port")
}
