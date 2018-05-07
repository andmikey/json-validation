package com.github.mikey.jsonapp

import play.api.libs.json._
import com.github.fge.jsonschema._
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.fasterxml.jackson.databind.JsonNode
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.{Json => JJson, _}
import java.sql.{Connection, DriverManager, ResultSet}
import io.circe.{Json => CJson, _}
import io.circe.parser.{parse => cparse, _}

class JSONSchema {
  // Class to hold instances of JSON schemas

  private[this] def resp(action: String, id: String, status: String, message: String) : JsValue= {
    // Generates a response to a request
    if (message == "") {
      return Json.obj("action" -> action, "id" -> id, "status" -> status);
    }

    else {
      return Json.obj("action" -> action, "id" -> id, "status" -> status, "message" -> message);
    }
  }

  def add(schemaid: String, schema: String): (JsValue, Int) = {
    // Add a schema to the database if not already in database

    // Convert schema to JSON
    val json_schema: JsValue =
      try {
        Json.parse(schema);
      }
      catch {
        // Cannot parse - invalid JSON, return invalid response
        case e: Exception => return (resp("uploadSchema", schemaid, "error", "Invalid JSON"), 500);
          null
      }

    // Save to database
    try {
      this.insertDB(schemaid, schema);
    }
    catch {
      // Cannot save to database
      case e: Exception => return (resp("uploadSchema", schemaid, "error", "Could not save to database"), 500);
        System.out.println(e);
        null
    }
    return (resp("uploadSchema", schemaid, "success", ""), 201);
  }

  def get(schemaid: String): (JsValue, Int) = {
    // Retrieve a schema of specified schemaid from database
    try {
      return (Json.parse(this.queryDB(schemaid)), 200);
    }
    catch {
      // Couldn't load from database - probably doesn't exist
      case e: Exception => return (resp("fetchSchema", schemaid, "error", "Could not load schema from database"), 404);
        System.out.println(e);
        null
    }
  }

  def removeNulls(js: String): JsValue = {
    // Returns an either value, so get the right-value
    val parsed = cparse(js).right.get
    val printer = Printer.spaces2.copy(dropNullValues=true)
    val pr = printer.pretty(parsed);
    return Json.parse(pr);
  }

  def validate(schemaid: String, json: String) : (JsValue, Int) = {
    // Validate a JSON document against the named schema
    System.out.println("Given JSON: " + json)
    // Clean json
    val no_nulls = try {
      removeNulls(json);
    }
    catch {
      case e: Exception => return (resp("validateDocument", schemaid, "error", "Trying to validate invalid JSON"), 500);
        System.out.println(e);
        null
    }

    System.out.println(no_nulls);
    val json_clean = Json.stringify(no_nulls);

    // Parse schema, supplied json
    val (schemaContents, _) = this.get(schemaid);
    val schema: JsonNode = asJsonNode(parse(Json.stringify(schemaContents)));
    val json_parsed: JsonNode = asJsonNode(parse(json_clean));

    System.out.println("Parsed json: " + json_parsed);
    System.out.println("Schema: " + schema);

    System.out.println(json_parsed.getClass);
    val validator = JsonSchemaFactory.byDefault().getValidator;
    val processingReport = validator.validate(schema, json_parsed);
    System.out.println("Processing report: " + json);

    System.out.println(processingReport)

    if (processingReport.isSuccess) {
      return (resp("validateDocument", schemaid, "success", ""), 200);
    }

    // Return the processing error message
    val itr = processingReport.iterator()
    val message = itr.next()
    return (resp("validateDocument", schemaid, "error", message.asJson().get("reports").asText()), 500);
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
      val result = rs.getString("json");
      System.out.println("Got schema from database" );
      return result;

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

