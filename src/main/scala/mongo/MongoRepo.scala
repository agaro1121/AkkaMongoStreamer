package mongo

import com.mongodb.CursorType
import com.mongodb.client.model.CreateCollectionOptions
import org.mongodb.scala.{Completed, Document, FindObservable, MongoClient, MongoDatabase, Observable}
import org.mongodb.scala._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoRepo {

  private val mongoClient: MongoClient = MongoClient()
  private val database: MongoDatabase = mongoClient.getDatabase("MongoStreams")
  private val CollectionName = "People"

  def getCollection: Future[MongoCollection[Document]] = {
    val options = new CreateCollectionOptions()
    options.capped(true)
    options.sizeInBytes(1000000L)
    println("******** isCapped" + options.isCapped)
    val coll: Observable[Completed] = database.createCollection(CollectionName, options)
    val eventualMongoCollection = coll.toFuture().map {
      seqComplete ⇒
        println("Created Capped Collection")
        seqComplete.foreach(com ⇒ println(com.toString()))
        database.getCollection(CollectionName)
    }.recover {
      case _ ⇒
        println("******* something went wrong. Will try retrieving the collection anyway")
        database.getCollection(CollectionName)
    }

    eventualMongoCollection
  }

  def insertOne(person: Person): Future[Observable[Completed]] = {
    getCollection.map {
      collection ⇒
        collection.insertOne(person.toDocument)
    }
  }

  /* TODO: is there a way to convert a function `Person => Boolean` to a bson query ?
  def deleteOne(field: String, value: String) = {
    collection.deleteOne(???)
  }*/

  def find(): Future[Observable[Person]] = {
    getCollection.map {
      collection ⇒
        val cursorType: FindObservable[Document] = collection.find().cursorType(CursorType.TailableAwait)
        cursorType.map(doc ⇒ Person.fromDocument(doc))
    }
  }

}
