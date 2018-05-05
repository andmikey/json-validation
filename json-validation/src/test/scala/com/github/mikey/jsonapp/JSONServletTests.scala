package com.github.mikey.jsonapp

import org.scalatra.test.scalatest._

class JSONServletTests extends ScalatraFunSuite {

  addServlet(classOf[JSONServlet], "/*")

  test("GET / on JSONServlet should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

}
