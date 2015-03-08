package com.flurdy.example.service

import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.http.HttpHeaders._
import spray.httpx.unmarshalling._
import spray.httpx.marshalling._
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.json.DefaultJsonProtocol._
import HttpCharsets._
import MediaTypes._
import com.flurdy.example.model._
import com.flurdy.example.infrastructure._
import spray.util._
import akka.event.Logging


class PizzaServiceActor(val registry: ComponentRegistry) extends Actor with PizzaService {
  def actorRefFactory = context
  def receive = runRoute(myRoute)
}

object PizzaJsonProtocol extends DefaultJsonProtocol {
  implicit val pizzaOrderFormat        = jsonFormat1(PizzaOrder)
  implicit val pizzaOrderDetailsFormat = jsonFormat2(PizzaOrderDetails)
  implicit val pizzaQueueFormat        = jsonFormat1(PizzaQueue)
}

import PizzaJsonProtocol._

trait PizzaService extends HttpService {

  implicit val registry: ComponentRegistry

  val log = LoggingContext.fromActorRefFactory

  lazy val myRoute =
    respondWithMediaType(MediaTypes.`application/json`){
      path("pizzas") {
        get {       
          complete{
            log.info(s"Finding all pizzas")
            PizzaQueue(registry.pizzaRepository.findPizzas) //.toStream
          }
        }
      } ~
      path("pizza") {
        post {
          entity(as[PizzaOrder]){ order =>
            detach(){
              order.save match {
                case Some(details) => {
                  log.info(s"Ordered ${order.pizza}")
                  respondWithStatus(201) {
                    respondWithHeader(RawHeader("Location", s"/pizza/${details.id}")){
                      complete(details)
                    }
                  }
                }
                case None => {
                  respondWithStatus(400) {
                    complete("Order failed")
                  }
                }
              }
            }
          }
        }   
      }   
    }

}
