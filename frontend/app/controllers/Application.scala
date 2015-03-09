package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.mvc.Results._
import play.api.data.Forms._
import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.Play.current
import scala.concurrent.{Future,Promise,Await}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

case class PizzaQueue(pizzas: Seq[PizzaOrder])

case class PizzaOrder(id: Option[Long], pizzaName: String){
   def this(pizzaName: String) = this(None,pizzaName)
   def toJson = Json.obj("pizza" -> pizzaName)   
   def order  = PizzeriaAdapter.orderPizza(this)
}

object PizzaOrder {   

  implicit val pizzaOrderReads: Reads[PizzaOrder] = {
    ( (JsPath \ "id").readNullable[Long] and
      (JsPath \ "pizza").read[String] 
    )(PizzaOrder.apply _)
  }

  implicit val pizzaQueueReads: Reads[PizzaQueue] = {
    ( (JsPath \ "pizzas").read[Seq[PizzaOrder]]
    ).map(PizzaQueue.apply _)
  }

   def parsePizzas(queue: String): Seq[PizzaOrder] = {
      Json.parse(queue).validate[PizzaQueue] match {
        case pizzaQueue: JsSuccess[PizzaQueue] => pizzaQueue.get.pizzas
        case error: JsError => {
          Logger.warn("Json fail: "+JsError.toFlatJson(error).toString())
          Nil
        }
      }
   }
}

object PizzeriaAdapter {

   implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
   val timeoutHttp = 5000
   val timeoutCall = 6 seconds
   private def serviceContextUrl: String = Play.configuration.getString("app.service.url").getOrElse("http://localhost")
   private val orderPizzaUrl = s"$serviceContextUrl/pizza"
   private val listPizzasUrl = s"$serviceContextUrl/pizzas"


  def orderPizza(pizzaOrder: PizzaOrder): Future[Option[PizzaOrder]] = {   
      WS.url(orderPizzaUrl).withRequestTimeout(timeoutHttp).post(pizzaOrder.toJson) map { response =>
         if(response.status == 201) Some(pizzaOrder)
         else None
      }
  }

  def findPizzas: Future[Seq[PizzaOrder]] = {   
      WS.url(listPizzasUrl).withRequestTimeout(timeoutHttp).get map { response =>
         if(response.status == 200) {
            PizzaOrder.parsePizzas(response.body)
         } else Nil
      }
   }
}

object Application extends Controller {

  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def index = Action.async {
    PizzeriaAdapter.findPizzas.map { pizzas => 
      Ok(views.html.index(pizzas))
    }
  }
  
  val orderFields = mapping (
      "id" -> ignored(None: Option[Long]),
      "pizza" -> text
  )(PizzaOrder.apply)(PizzaOrder.unapply) 

  val orderForm = Form( orderFields )

  def orderPizza = Action { implicit request =>
      orderForm.bindFromRequest.fold(
         errors => {
            Logger.warn("Order form error")
            BadRequest
         },
         pizzaOrder => {     
            pizzaOrder.order
            Logger.info("Pizza order sent") 
            Ok
         }
      )
   }
   
   implicit val pizzaOrderWrites: Writes[PizzaOrder] = (
     (JsPath \ "id").write[Option[Long]] and
     (JsPath \ "pizza").write[String]
   )(unlift(PizzaOrder.unapply))

  def listPizzas = Action.async {
    PizzeriaAdapter.findPizzas.map { pizzas => 
      Ok( Json.toJson(pizzas) )
    }
  }

}
