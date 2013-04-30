/**
 * Simple global auth for making play apps private - on cloudbees.
 * to use: 
 * 
 * Drop this file in your project as controllers/Global.scala
 * then when you want to make it private run: 
 * bees config:set password=SECRET and restart your app.
 * It will then use basic auth to look for the SECRET password (user name ignored)
 * 
 * Author: Michael Neale
 */

import play.api.GlobalSettings
import play.api.mvc.{Action, RequestHeader}
import play.api.mvc.Results._



object Global extends GlobalSettings {


def decodeBasicAuth(auth: String) = {
    val baStr = auth.replaceFirst("Basic ", "")
    val Array(user, pass) = 
        new String(new sun.misc.BASE64Decoder()
                       .decodeBuffer(baStr), "UTF-8").split(":")
    (user, pass)
}

def requireAuth = Some(Action { request => 
        Unauthorized("Please login")
          .withHeaders("WWW-Authenticate" -> "Basic")})  

def checkAuth(request: RequestHeader) = {
    (System.getProperty("password"), request.headers.get("Authorization"))  match {
      case (null, _) => true
      case (_, None) => false
      case (expected, Some(authHeader)) => {
        val (user, password) = decodeBasicAuth(authHeader)
        println(s"Hello $user")
        password == expected
      }
    }
}

override def onRouteRequest(request: RequestHeader) = {      
        if (checkAuth(request)) super.onRouteRequest(request)
        else requireAuth              
}



}