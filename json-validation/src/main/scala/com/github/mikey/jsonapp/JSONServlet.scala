package com.github.mikey.jsonapp

import org.scalatra._
import play.api.libs.json._

class JSONServlet extends ScalatraServlet {

  val not_found_error = (Json.obj("action" -> "unknown", "status" -> "error",
    "message" -> "Could not find specified route."));

  // These are at the top because Scalatra reads routes from the bottom up
  // Respond to anything other than the expected request with 404 Not Found
  post("*") {
    NotFound(not_found_error)
  }

  get("*") {
    NotFound(not_found_error)
  }

  post("/schema/:schemaid") {
    // Add JSON Schema with schemaid to database
    val schemaid = params("schemaid");
    val jsonSchema = request.body;

    val s = new JSONSchema(schemaid);
    val (resp, code) = s.add(schemaid, jsonSchema);

    // 201 or 500?
    if (code == 201) {
      Created(resp);
    }
    else {
      InternalServerError(resp);
    }
  }

  get("/schema/:schemaid") {
    // Retrieve JSON schema with schemaid from database
    val schemaid = params("schemaid");

    val s = new JSONSchema(schemaid);
    val (resp, code) = s.get(schemaid);

    // 200 or 404?
    if (code == 200) {
      Ok(resp);
    }
    else {
      NotFound(resp);
    }
  }

  post("/validate/:schemaid") {
    // Validate JSON document against schemaid from database
    val schemaid = params("schemaid");
    val jsonDocument = request.body;

    val s = new JSONSchema(schemaid);
    val (resp, code) = s.validate(schemaid, jsonDocument);

    // 200 or 500?
    if (code == 200) {
      Ok(resp)
    }
    else {
      InternalServerError(resp)
    }
  }

}
