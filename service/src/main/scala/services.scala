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
}

import PizzaJsonProtocol._

trait PizzaService extends HttpService {

  implicit val registry: ComponentRegistry

  val log = LoggingContext.fromActorRefFactory

  val myRoute =
    path("heartbeat") {
      get {
        respondWithMediaType(MediaTypes.`text/html`){
          complete {
            <h1>ALIVE!</h1>
          }
        }
      }
    } ~
    respondWithMediaType(MediaTypes.`application/json`){
      path("pizza") {
        post {
          entity(as[PizzaOrder]){ order =>
            detach(){
              rejectEmptyResponse {
                respondWithStatus(201) {
                  log.info(s"Ordered ${order.pizza}")
                  val id = -1
                  val pizzaOrderDetails = PizzaOrderDetails(id,order.pizza)
                  respondWithHeader(RawHeader("Location", s"/pizza/${id}")) {
                    complete(pizzaOrderDetails)
                  }
                }
              }
            }
          }
        }
      } ~
      path("pizzas") {
        pathEnd {
          get {
            log.debug(s"Finding all pizzas")
            rejectEmptyResponse {
              val pizzas: Stream[PizzaOrderDetails] = PizzaOrders.findPizzas.toStream
              complete(pizzas)
            }
          }
        }
      }       
    }

}
