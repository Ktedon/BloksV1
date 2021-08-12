package actors

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef

import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import model._
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class ChatActor(out: ActorRef, manager: ActorRef, db: DatabaseModel, dbConfigProvider: DatabaseConfigProvider, ec: ExecutionContext, fromID: Int, toID: Int) extends Actor {
  manager ! ChatManager.NewChatter(self, fromID, toID)

  import ChatActor._
  def receive = {
    case s: String => 
    	db.addMessage(fromID, toID, s)
    	manager ! ChatManager.Message(s, fromID, toID)
    case SendMessage(msg, fromId, toId) => out ! msg
    case m => println("Unhandled message in ChatActor: " + m)
  }
}

object ChatActor {
  def props(out: ActorRef, manager: ActorRef, db: DatabaseModel, dbConfigProvider: DatabaseConfigProvider, ec: ExecutionContext, fromID: Int, toID: Int) = Props(new ChatActor(out, manager, db, dbConfigProvider, ec, fromID, toID))

  case class SendMessage(msg: String, fromID: Int, toID: Int)
}