package com.github.mikey.jsonapp

import play.api.libs.json._

class JSONSchema {
  // Class to hold instances of JSON schemas

  def add(schemaid: String, schema: String): JsValue = {
    // Add a schema to the database if not already in database
    val successful_upload = (Json.obj("action" -> "uploadSchema", "id" -> "config-schema",
      "status" -> "success"));
    val invalid_upload = (Json.obj("action" -> "uploadSchema", "id" -> "config-schema",
      "status" -> "error", "message" -> "Invalid JSON"));
    val name_clash = (Json.obj("action" -> "uploadSchema", "id" -> "config-schema",
      "status" -> "error", "message" -> "Schema with this name already exists"));
    
    // Placeholder
    return invalid_upload;
  }

  def get(schemaid: String): JsValue = {
    // Retrieve a schema of specified schemaid from database
    return Json.obj();
  }

  def validate(schemaid:String, json: JsValue) : JsValue = {
    // Validate a JSON document against the named schema
    val successful_validation = (Json.obj("action" -> "validateDocument", "id" -> "config-schema",
      "status" -> "success"));
    val invalid_validation = (Json.obj("action" -> "validateDocument", "id" -> "config-schema",
      "status" -> "error", "message" -> "Could not validate JSON document against given schema"));

    // Placeholder
    return invalid_validation;
  }
}
