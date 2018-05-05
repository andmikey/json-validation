package com.github.mikey.jsonapp

import play.api.libs.json._
import com.github.fge.jsonschema._
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.fasterxml.jackson.databind.JsonNode
import org.json4s._
import org.json4s.jackson.JsonMethods._
import java.sql.{Connection, DriverManager, ResultSet}


class JSONSchema(schemaid : String) {
  // Class to hold instances of JSON schemas

  val successful_upload = (Json.obj("action" -> "uploadSchema", "id" -> schemaid,
    "status" -> "success"));
  val invalid_upload = (Json.obj("action" -> "uploadSchema", "id" -> schemaid,
    "status" -> "error", "message" -> "Invalid JSON"));
  val db_save_issue = (Json.obj("action" -> "uploadSchema", "id" -> schemaid,
    "status" -> "error", "message" -> "Cannot save to database; perhaps the item already exists?"));
  val db_load_issue = (Json.obj("action" -> "uploadSchema", "id" -> schemaid,
    "status" -> "error", "message" -> "Cannot fetch from database; perhaps the item doesn't exist?"));
  val successful_validation = (Json.obj("action" -> "validateDocument", "id" -> schemaid,
    "status" -> "success"));
  val invalid_validation = (Json.obj("action" -> "validateDocument", "id" -> schemaid,
    "status" -> "error", "message" -> "Could not validate JSON document against given schema"));

  def add(schemaid: String, schema: String): (JsValue, Int) = {
    // Add a schema to the database if not already in database

    // Convert schema to JSON
    val json_schema: JsValue =
      try {
        Json.parse(schema);
      }
      catch {
        // Cannot parse - invalid JSON, return invalid response
        case e: Exception => return (invalid_upload, 500);
          null
      }

    // Save to database
    try {
      this.insertDB(schemaid, schema);
    }
    catch {
      case e: Exception => return (db_save_issue, 500);
        System.out.println(e);
        null
    }
    return (successful_upload, 201);
  }

  def get(schemaid: String): (JsValue, Int) = {
    // Retrieve a schema of specified schemaid from database
    try {
      return (Json.parse(this.queryDB(schemaid)), 200);
    }
    catch {
      case e: Exception => return (db_load_issue, 404);
        System.out.println(e);
        null
    }
  }

  private[this] def withoutNull(json: JsValue): JsValue = json match {
    // https://gist.github.com/d6y/eda9d968e78943e672ce
    case JsObject(fields) =>
      JsObject(fields.flatMap {
        case (_, JsNull)          => None // could match on specific field name here
        case other @ (name,value) => Some(other) // consider recursing on the value for nested objects
      })
    case other => other
  }

  def validate(schemaid: String, json: String) : (JsValue, Int) = {
    // Validate a JSON document against the named schema

    // Clean json
    val json_clean = Json.stringify(withoutNull(Json.parse(json)));
    // Parse schema, supplied json
    val (schemaContents, _) = this.get(schemaid);
    val schema: JsonNode = asJsonNode(parse(Json.stringify(schemaContents)));
    val json_parsed: JsonNode = asJsonNode(parse(json_clean));

    val validator = JsonSchemaFactory.byDefault().getValidator;
    val processingReport = validator.validate(schema, json_parsed);

    if (processingReport.isSuccess) {
      return (successful_validation, 200);
    }

    return (invalid_validation, 500);
  }

  private[this] def queryDB(schemaid: String) : String = {
    // Query the database for the contents associated with schemaid
    // Return the contents

    val con_str = "jdbc:postgresql://localhost:5432/jsonapp?user=postgres"
    val conn = DriverManager.getConnection(con_str)

    try {
      val stm = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
      val rs = stm.executeQuery(s"SELECT * from jsonschemas where schemaid = '$schemaid'")
      // Move the pointer one along to get first row of results
      rs.next()
      return rs.getString("json");

    } finally {
      conn.close()
    }
  }

  private[this] def insertDB(schemaid: String, schemaContents: String) : Integer = {
    // Insert into database JSON schema of name schemaid with contents schemaContents
    // Return the number of rows changed

    val con_str = "jdbc:postgresql://localhost:5432/jsonapp?user=postgres"
    val conn = DriverManager.getConnection(con_str)

    try {
      val stm = conn.createStatement()
      val c = stm.executeUpdate(s"INSERT INTO jsonschemas VALUES ('$schemaid', '$schemaContents')");
      return c;
    } finally {
      conn.close()
    }
  }
}

