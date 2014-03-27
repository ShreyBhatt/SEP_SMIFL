package controllers;

import play.*;
import play.mvc.*;

import securesocial.core.Identity;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.SecureSocial;
import models.*;

import play.libs.Json;
import com.fasterxml.jackson.databind.node.ObjectNode;

import views.html.*;

public class PortfolioController extends Controller {

  public static Result getPortfolio ( final long userId, final long leagueId ) {
    Portfolio portfolio = Portfolio.getPortfolio(userId, leagueId);
    ObjectNode result = Json.newObject();
    if ( portfolio == null ) {
      result.put("status", "KO");
      result.put("message", "Portfolio.getPortfolio returned null");
      return badRequest(result);
    }
    result.put("status", "OK");
    result.put("portfolio", portfolio.getJson());
    return ok(result);
  }


	//TODO: Make this work. bitch :) and fix tabs
	public static Result portfolioTest() {

	    Identity identity = (Identity) ctx().args.get(SecureSocial.USER_KEY);
	    User user = User.find(identity.email().get());
	    ObjectNode result;
	    Portfolio port;
	    
	    if ( user != null ) {
	      result = user.getJson();
	      port = Portfolio.getPortfolio( user.id, 1 );
	      result.put("globalPortfolio", port.getJson() );
	      result.put("cashPosition", Position.getCashPosition( port.id ).getJson() );
	      return ok(result);
	    }

	    //THIS SHOULDN'T HAPPEN
	    result = Json.newObject();
	    result.put("status", "KO");
	    result.put("message", "fixme" );

	    return badRequest(result);

	}

}
