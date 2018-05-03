package com.github.mikey.jsonapp

import org.scalatra._

class JSONServerlet extends ScalatraServlet {

  post("/schema/:schemaid") {
    val schemaid = params("schemaid");
    <p> POST request for {schemaid} </p>
  }

  get("/schema/:schemaid") {
    val schemaid = params("schemaid");
    <p> GET request for {schemaid} </p>
  }

  post("/validate/:schemaid") {
    val schemaid = params("schemaid");
    <p> POST request for {schemaid} </p>
  }

}
