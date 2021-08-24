package controllers

import model._

import scala.concurrent.ExecutionContext

import akka.actor._
import play.api.db.slick.DatabaseConfigProvider

object ChatActor {
  def props(out: ActorRef, model: MessageModel) = Props(new ChatActor(out, model))
}

class ChatActor(out: ActorRef, model: MessageModel) extends Actor {
  def receive = {
    case msg: String =>
      println("I received your message: " + msg)
    case unknown => println(s"Unhandled message in ChatActor: ${unknown}")
  }
}
