package com.flurdy.example.infrastructure

import slick.driver.PostgresDriver.simple._
// import org.joda.time.DateTime
// import com.github.tototoshi.slick.PostgresJodaSupport._
import com.flurdy.example.model._
// import akka.event.Logging
import scala.slick.jdbc.meta.MTable


class PizzaSchema(tag: Tag) extends Table[(Long,String)](tag,"pizzaorder"){
  def id       = column[Long]("id",O.PrimaryKey,O.AutoInc)
  def pizza    = column[String]("pizza")
  def * = (id, pizza)
}

class RepositoryInitialiser(implicit val registry: ComponentRegistry) extends Repository {

   def initialiseDatabase = {
      database.withSession{ implicit session =>
         List(pizzas).map( createTableIfNotExists(_))
      }
   }

   private def isTableCreated(table: TableQuery[_ <: Table[_]])(implicit session: Session): Boolean = {
      MTable.getTables(table.baseTableRow.tableName).list.isEmpty
   }

   private def createTableIfNotExists(table: TableQuery[_ <: Table[_]])(implicit session: Session){
      if(isTableCreated(table)) table.ddl.create
   }

   def createTables {
      database.withSession{ implicit session =>
         (pizzas.ddl).create
      }
   }

   def cleanDatabase = {
      database.withSession{ implicit session =>
         pizzas.delete
      }
   }

}


trait Repository {

   val registry: ComponentRegistry

   lazy val database = Database.forDataSource(registry.datasourceConfig.datasource)

   val pizzas = TableQuery[PizzaSchema]

}

class PizzaRepository(implicit val registry: ComponentRegistry) extends Repository with Logging {

   def save(pizza: String): Option[Long] = {
      database.withSession{ implicit session =>
         Some( (pizzas returning pizzas.map(_.id) += (-1,pizza) ) )
      }
   }

    def findPizzas: Seq[PizzaOrderDetails] = {
      database.withSession{ implicit session =>
         ( for {
            pizza <- pizzas 
         } yield (pizza.id,pizza.pizza) ).list.map{
            case (pizzaId,pizzaName) => new PizzaOrderDetails(pizzaId,pizzaName)
         }
      }
   }

}
