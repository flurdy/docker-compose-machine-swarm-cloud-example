package com.flurdy.example.model

import spray.util.LoggingContext
import com.jolbox.bonecp.BoneCPDataSource
import javax.sql.DataSource
import com.typesafe.config.{Config,ConfigFactory}
import org.slf4j.LoggerFactory
import ch.qos.logback.core.util.StatusPrinter
import com.flurdy.example.infrastructure._


case class DatasourceConfig(
             driver: String, url: String, 
             username: String, password: String)  {

  def datasource: DataSource = {
    val datasource = new BoneCPDataSource
    datasource.setJdbcUrl(url)
    datasource.setUsername(username)
    datasource.setPassword(password)
    datasource.setDriverClass( classOf[org.postgresql.Driver].getName )
    datasource
  }

}

object Environment {

  private val config = ConfigFactory.load()

  lazy val datasourceConfig = findDatasource

  private def findDatasource: DatasourceConfig = {
    val driver = config.getString(s"datasource.driver")
    val url = config.getString(s"datasource.url")
    val username = config.getString(s"datasource.username")
    val password = config.getString(s"datasource.password")
    DatasourceConfig(driver,url,username,password)
  }

}

trait Logging {
   def logger = LoggerFactory.getLogger(this.getClass)
}

trait ComponentRegistry {

   val datasourceConfig: DatasourceConfig

   val pizzaRepository: PizzaRepository

}

class RuntimeComponentRegistry extends ComponentRegistry {

   val datasourceConfig = Environment.datasourceConfig

   val pizzaRepository = new PizzaRepository()(this)

}

case class PizzaOrder(pizza: String)

case class PizzaOrderDetails(id: Long, pizza: String)

object PizzaOrders {

   def findPizzas: Seq[PizzaOrderDetails] = Seq.empty

}
