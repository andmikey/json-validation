package com.github.mikey.jsonapp

import org.scalatra.test.scalatest._

class JSONServerletTests extends ScalatraFunSuite {

  addServlet(classOf[JSONServerlet], "/*")

  test("GET / on JSONServerlet should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

}
