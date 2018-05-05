package com.github.mikey.jsonapp

import org.scalatra.test.scalatest._
import org.scalatest.BeforeAndAfterAll
import scala.io.Source

class JSONServletTests extends ScalatraFunSuite with BeforeAndAfterAll {

  addServlet(classOf[JSONServlet], "/*")

  // Set up database with test data
  override def beforeAll() {

  }

  // Remove test data
  override def afterAll() {

  }

  // test("GET / on JSONServlet should return status 200") {
  //   get("/") {
  //     status should equal (200)
  //   }

  val dir = System.getProperty("user.dir")
  val json_schema = Source.fromFile(dir + "/files/config-schema.json").getLines.mkString
  val valid_json_document = Source.fromFile(dir + "/files/config.json").getLines.mkString
  val valid_with_null_json_document = Source.fromFile(dir + "/files/config-no-null.json").getLines.mkString
  val invalid_json_docuemnt = Source.fromFile(dir + "/files/invalid.json").getLines.mkString
}
