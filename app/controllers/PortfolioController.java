package controllers;

import play.*;
import play.mvc.*;

import play.libs.Json;
import com.fasterxml.jackson.databind.node.ObjectNode;

import views.html.*;
import models.Portfolio;

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

}
