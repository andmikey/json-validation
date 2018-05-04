package com.github.mikey.jsonapp

import org.scalatra._

class JSONServlet extends ScalatraServlet {

  post("/schema/:schemaid") {
    // Add JSON Schema with schemaid to database
    val schemaid = params("schemaid");
    val jsonSchema = request.body;

    val s = new JSONSchema(schemaid);
    val resp = s.add(schemaid, jsonSchema);

    // Placeholder response
    Ok(resp);
  }

  get("/schema/:schemaid") {
    // Retrieve JSON schema with schemaid from database
    val schemaid = params("schemaid");

    val s = new JSONSchema(schemaid);
    val resp = s.get(schemaid);

    Ok(resp);
  }

  post("/validate/:schemaid") {
    // Validate JSON document against schemaid from database
    val schemaid = params("schemaid");
    val jsonDocument = request.body;

    val s = new JSONSchema(schemaid);
    val resp = s.validate(schemaid, jsonDocument);

    Ok(resp);
  }

}
