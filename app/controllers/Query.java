package controllers;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.*;
import play.mvc.*;

import service.YahooFinanceService;

import views.html.*;
import models.Stock;

public class Query extends Controller {

  @BodyParser.Of(BodyParser.Json.class)
  public static Result getQuery(final String symbol) {

    YahooFinanceService yahoo = YahooFinanceService.getInstance();
    ObjectNode result = Json.newObject();
    Stock stock = yahoo.getStock(symbol);
    if ( stock == null ) {
      result.put("status", "KO");
      result.put("message", "Stock symbol: " + symbol + " can not be found");
      return badRequest(result);
    }
    else {
      result.put("status", "OK");
      result.put("ticker", symbol);
      result.put("price", stock.getPrice());
      return ok(result);
    }
  }
}
