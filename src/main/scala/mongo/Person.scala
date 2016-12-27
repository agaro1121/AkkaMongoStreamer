package mongo

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.mongodb.scala.Document
import spray.json.{DefaultJsonProtocol, JsValue, JsonWriter, RootJsonFormat, pimpString}

import scala.language.implicitConversions

case class Person(firstName: String, lastName: String, age: Int){
  def toDocument: Document = {
    /*
    * calling `person.toJson` causes a crazy recursive loop of the implicits
    * the above call converts the person to a Document first and attemps to call .toJson on the Document object
    * */
    val json: JsValue = Person.toJson(this)
    val personString: String = json.toString()
    Document(personString)
  }
}

object Person extends DefaultJsonProtocol with SprayJsonSupport {

  //borrowed from spray.json.pimpAny
  //TODO: find alternative solution -> possibly removing implicits from toDocument and fromDocument???
  def toJson[T](any: T)(implicit writer: JsonWriter[T]): JsValue = writer.write(any)

  implicit val PersonJsonFormat: RootJsonFormat[Person] = jsonFormat3(Person.apply)



  implicit def fromDocument(document: Document): Person = {
    val jsonString: String = document.toJson()
    jsonString.parseJson.convertTo[Person]
  }

}

