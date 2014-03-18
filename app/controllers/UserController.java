package controllers;

import play.*;
import play.mvc.*;

import play.libs.Json;
import com.fasterxml.jackson.databind.node.ObjectNode;

import views.html.*;

import models.User;

public class UserController extends Controller {


  public static Result addUser (
      final String first, final String last, final String email
      ) {
    User user = User.add ( first, last, email );
    ObjectNode result = Json.newObject();

    if ( user == null) {
      result.put("status","KO")
        .put("message", "fail");
      return badRequest(result);
    }
    else {
      result.put("status","OK");
      result.put("user", user.getJson());
      return ok(result);
    }
      }

  public static Result getUser ( final String email ) {
    User user = User.find( email );
    ObjectNode result = Json.newObject();

    if ( user == null) {
      result.put("status","KO");
      result.put("message", "fail");
      return badRequest(result);
    }
    else {
      result.put("status","OK");
      result.put("user", user.getJson());
      return ok(result);
    }
  }

}
