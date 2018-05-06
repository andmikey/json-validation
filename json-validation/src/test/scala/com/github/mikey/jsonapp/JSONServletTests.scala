package com.github.mikey.jsonapp

import org.scalatra.test.scalatest._
import org.scalatest.BeforeAndAfterAll
import scala.io.Source
import java.sql.{Connection, DriverManager, ResultSet}

class JSONServletTests extends ScalatraFunSuite with BeforeAndAfterAll {

  addServlet(classOf[JSONServlet], "/*")

  // Set up database with test data
  override def beforeAll() {
    // The super class needs to be called first

    super.beforeAll()
    val con_str = "jdbc:postgresql://localhost:5432/jsonapp?user=postgres"
    val conn = DriverManager.getConnection(con_str)

    try {
      val stm = conn.createStatement()
      val c = stm.executeUpdate(s"INSERT INTO jsonschemas VALUES ('config-schema', '$config_schema')");
    }
    catch { 
      case e:Exception => System.out.println("Already exists in DB");
    }

    finally {
      conn.close()
    }
  }

  // Remove test data
  override def afterAll() {
    super.afterAll()
    val con_str = "jdbc:postgresql://localhost:5432/jsonapp?user=postgres"
    val conn = DriverManager.getConnection(con_str)

    try {
      val stm = conn.createStatement()
      val c = stm.executeUpdate(s"DELETE FROM jsonschemas where schemaid = 'config-schema'");
    } finally {
      conn.close()
    }
  }

  test("GET /schema/config-schema should return 200") {
    get("/schema/config-schema") {
      status should equal (200)
    }
  }

  test("GET /schema/foobarbaz should return 404") {
    get("/schema/foobarbaz") {
      status should equal (404)
    }
  }

  test("POST config.json to /schema/config-schema should return 200") {
    post("/validate/config-schema", valid_json_document, Map("Content-Type" -> "application/json")) {
      status should equal (200)
    }
  }

  test("POST invalid.json to /schema/config-schema should return 500") {
    post("/validate/config-schema", invalid_json_document, Map("Content-Type" -> "application/json")) {
      status should equal (500)
    }
  }

  test("POST config-no-null.json to /schema/config-schema should return 200") {
    post("/validate/config-schema", valid_with_null_json_document, Map("Content-Type" -> "application/json")) {
      status should equal (200)
    }
  }

  val dir = System.getProperty("user.dir")
  val config_schema = Source.fromFile(dir + "/files/config-schema.json").getLines.mkString
  val valid_json_document = Source.fromFile(dir + "/files/config.json").getLines.mkString
  val valid_with_null_json_document = Source.fromFile(dir + "/files/config-no-null.json").getLines.mkString
  val invalid_json_document = Source.fromFile(dir + "/files/invalid.json").getLines.mkString
}
