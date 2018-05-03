package com.github.mikey.jsonapp

import org.scalatra._

class JSONServerlet extends ScalatraServlet {

  post("/schema/:schemaid") {
    // Add JSON Schema with schemaid to database
    val schemaid = params("schemaid");
    val jsonSchema = request.body;

  }

  get("/schema/:schemaid") {
    // Retrieve JSON schema with schemaid from database
    val schemaid = params("schemaid");

  }

  post("/validate/:schemaid") {
    // Validate JSON document against schemaid from database
    val schemaid = params("schemaid");
    val jsonDocument = request.body;
  }

}
