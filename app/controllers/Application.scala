package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    import play.api.Play.current
    println(Play.application.resourceAsStream("mydata"))
    val data = scala.io.Source.fromInputStream(Play.application.resourceAsStream("mydata").getOrElse(null))
    Ok(views.html.index("Your new application is ready: " + data.getLines.mkString("\n")))
  }


  
}