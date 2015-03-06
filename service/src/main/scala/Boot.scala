package com.flurdy.example

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import com.flurdy.example.model._
import com.flurdy.example.service._
import com.flurdy.example.infrastructure._

object Boot extends App {

  implicit val system = ActorSystem("pizza-service-system")

  implicit val componentRegistry = new RuntimeComponentRegistry

  val service = system.actorOf(Props(classOf[PizzaServiceActor],componentRegistry), "pizza-service")
  
  implicit val timeout = Timeout(5.seconds)

  (new RepositoryInitialiser).initialiseDatabase

  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = 8880)

} 
