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
    User user = new User();
    user.first = first;
    user.last = last;
    user.email = email;
    user.save();
		user.achv = 0;
    user = User.find(user.email);

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
