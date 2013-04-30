/**
 * Simple global auth for making play apps private - on cloudbees.
 * to use: 
 * 1. Drop this file in your project as controllers/Global.scala
 * 2. deploy your app to cloudbees via whatever means
 * 3. run bees config:set password=SECRETHERE
 * restart the app - and from then on basic authentication will be required until you unset that config (via bees )
 * (in this case user name doesn't matter)
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