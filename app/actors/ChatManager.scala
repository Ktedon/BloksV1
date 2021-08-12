package actors

import akka.actor.Actor
import akka.actor.ActorRef

import model._
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.collection.mutable.ListBuffer

class ChatManager extends Actor {
  private var chatters: ListBuffer[Tuple2[Option[Tuple2[Int, Int]], ActorRef]] = ListBuffer.empty

  import ChatManager._
  def receive = {
    case NewChatter(chatter, fromID, toID) => 
    	println(fromID)
    	println("yes")
    	chatters :+ Tuple2(Option(Tuple2(fromID, toID)), chatter)
    case Message(msg, fromID, toID) => 
    	
    	for (chatter <- chatters) {
    		// println(chatter)
    		chatter._1 match {
    			case Some(chatterFound) => 
    				if (chatterFound == Tuple2(fromID, toID) || chatterFound == Tuple2(toID, fromID))
    					chatter._2 ! ChatActor.SendMessage(msg, fromID, toID)


    			case None               =>
    		}
    		
    	} 
    case m => println("Unhandled message in ChatManager: " + m)
  }
}

object ChatManager {
  case class NewChatter(chatter: ActorRef, fromID: Int, toID: Int)
  case class Message(msg: String, fromID: Int, toID: Int)
}

