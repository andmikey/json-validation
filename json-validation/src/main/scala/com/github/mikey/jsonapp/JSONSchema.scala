package com.github.mikey.jsonapp

import play.api.libs.json._

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

  def get(schemaid: String): JsValue = {
    // Retrieve a schema of specified schemaid from database
    return Json.obj();
  }

  def validate(schemaid:String, json: JsValue) : JsValue = {
    // Validate a JSON document against the named schema

    // Responses
    val successful_validation = (Json.obj("action" -> "validateDocument", "id" -> schemaid,
      "status" -> "success"));
    val invalid_validation = (Json.obj("action" -> "validateDocument", "id" -> schemaid,
      "status" -> "error", "message" -> "Could not validate JSON document against given schema"));

    // Placeholder
    return invalid_validation;
  }
}
