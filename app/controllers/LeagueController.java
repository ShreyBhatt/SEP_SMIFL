package controllers;

import play.*;
import play.mvc.*;

import play.libs.Json;
import com.fasterxml.jackson.databind.node.ObjectNode;

import views.html.*;

import models.League;

public class LeagueController extends Controller {

  public static Result addLeague ( final String name ) {
    League league = League.add ( name );
    ObjectNode result = Json.newObject();

    if ( league == null ) {
      result.put("status", "KO");
      result.put("message", "Failed to create a league");
      return badRequest(result);
    }
    else {
      result.put("status", "OK");
      result.put("league", league.getJson());
      return ok(result);
    }
  }

}


