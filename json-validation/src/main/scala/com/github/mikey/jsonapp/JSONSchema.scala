package com.github.mikey.jsonapp

import play.api.libs.json._
import com.github.fge.jsonschema._
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.fasterxml.jackson.databind.JsonNode
import org.json4s._
import org.json4s.jackson.JsonMethods._

class JSONSchema {
  // Class to hold instances of JSON schemas

  def add(schemaid: String, schema: String): JsValue = {
    // Add a schema to the database if not already in database

    // Responses
    val successful_upload = (Json.obj("action" -> "uploadSchema", "id" -> schemaid,
      "status" -> "success"));
    val invalid_upload = (Json.obj("action" -> "uploadSchema", "id" -> schemaid,
      "status" -> "error", "message" -> "Invalid JSON"));
    val db_issue = (Json.obj("action" -> "uploadSchema", "id" -> schemaid,
      "status" -> "error", "message" -> "Cannot save to database"));

    // Convert schema to JSON
    val json_schema: JsValue =
      try {
        Json.parse(schema);
      }
      catch {
        // Cannot parse - invalid JSON, return invalid response
        case e: Exception => return invalid_upload;
          null
      }

    return successful_upload;
  }

  def get(schemaid: String): String = {
    // Retrieve a schema of specified schemaid from database
    return "";
  }

  def withoutNull(json: JsValue): JsValue = json match {
    // https://gist.github.com/d6y/eda9d968e78943e672ce
    case JsObject(fields) =>
      JsObject(fields.flatMap {
        case (_, JsNull)          => None // could match on specific field name here
        case other @ (name,value) => Some(other) // consider recursing on the value for nested objects
      })
    case other => other
  }

  def validate(schemaid: String, json: String) : JsValue = {
    // Validate a JSON document against the named schema

    // Responses
    val successful_validation = (Json.obj("action" -> "validateDocument", "id" -> schemaid,
      "status" -> "success"));
    val invalid_validation = (Json.obj("action" -> "validateDocument", "id" -> schemaid,
      "status" -> "error", "message" -> "Could not validate JSON document against given schema"));

    // Parse schema, supplied json
    val schema: JsonNode = asJsonNode(parse(this.get(schemaid)));
    val json_parsed: JsonNode = asJsonNode(parse(json));

    val validator = JsonSchemaFactory.byDefault().getValidator;
    val processingReport = validator.validate(schema, json_parsed);

    if (processingReport.isSuccess) {
      return successful_validation;
    }

    return invalid_validation;
  }
}
