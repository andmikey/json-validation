package com.github.mikey.jsonapp

import org.scalatra._

class JSONServerlet extends ScalatraServlet {

  get("/") {
    views.html.hello()
  }

}
