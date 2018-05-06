package com.github.mikey.jsonapp

import org.scalatra.test.scalatest._
import org.scalatest.BeforeAndAfterAll
import scala.io.Source
import java.sql.{Connection, DriverManager, ResultSet}

class JSONServletTests extends ScalatraFunSuite with BeforeAndAfterAll {

  addServlet(classOf[JSONServlet], "/*")

  // Drop config schema if already in DB
  override def beforeAll() {
    // The super class needs to be called first
    super.beforeAll()
    val con_str = "jdbc:postgresql://localhost:5432/jsonapp?user=postgres"
    val conn = DriverManager.getConnection(con_str)

    try {
      val stm = conn.createStatement()
      // Delete config-schema2 if exists so we can try inserting it
      // Upload config-schema so we can check if uploading something by the same name fails
      val c1 = stm.executeUpdate(s"DELETE FROM jsonschemas where schemaid = 'config-schema'");
      val c2 = stm.executeUpdate(s"DELETE FROM jsonschemas where schemaid = 'config-schema2'");
      val c3 = stm.executeUpdate(s"INSERT INTO jsonschemas VALUES ('config-schema', '$config_schema')");
    } finally {
      conn.close()
    }
  }

  // Drop config schema after adding
  override def afterAll() {
    super.afterAll()
    val con_str = "jdbc:postgresql://localhost:5432/jsonapp?user=postgres"
    val conn = DriverManager.getConnection(con_str)

    try {
      val stm = conn.createStatement()
      val c1 = stm.executeUpdate(s"DELETE FROM jsonschemas where schemaid = 'config-schema'");
      val c2 = stm.executeUpdate(s"DELETE FROM jsonschemas where schemaid = 'config-schema2'");
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

  test("POST config.json to /schema/config-schema2 should return 200") {
    post("/validate/config-schema2", valid_json_document, Map("Content-Type" -> "application/json")) {
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

  test("POST config-schema.json to /schema/config-schema should return 500") {
    post("/validate/config-schema", config_schema, Map("Content-Type" -> "application/json")) {
      status should equal (500)
    }
  }

  test("POST invalid-schema.json to /schema/invalid-schema should return 500") {
    post("/validate/invalid-schema", invalid_json_schema, Map("Content-Type" -> "application/json")) {
      status should equal (500)
    }
  }

  test("POST config-schema.json to /schema/config-schema2 should return 201") {
    post("/schema/config-schema2", config_schema, Map("Content-Type" -> "application/json")) {
      status should equal (201)
    }
  }


  val dir = System.getProperty("user.dir")
  val config_schema = Source.fromFile(dir + "/files/config-schema.json").getLines.mkString
  val valid_json_document = Source.fromFile(dir + "/files/config.json").getLines.mkString
  val valid_with_null_json_document = Source.fromFile(dir + "/files/config-no-null.json").getLines.mkString
  val invalid_json_document = Source.fromFile(dir + "/files/invalid.json").getLines.mkString
  val invalid_json_schema = Source.fromFile(dir + "/files/invalid-schema.json").getLines.mkString  
}
