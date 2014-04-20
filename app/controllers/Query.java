package controllers;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.*;
import play.mvc.*;

import service.YahooFinanceService;
import service.YahooHistorical;

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
      result.put("message", "Stock symbol: " + symbol.toUpperCase() + " can not be found");
      return badRequest(result);
    }
    else if ( stock.getPrice() == 0 ) {
      result.put("status", "KO");
      result.put("message", "Stock symbol: " + symbol.toUpperCase() + " appears to be defunct");
      return badRequest(result);
    }
    else {
      result.put("status", "OK");
      result.put("stock", stock.getJson());
      return ok(result);
    }
  }

  public static Result getData(final String symbol) {
      YahooHistorical yahoo = YahooHistorical.getInstance();
      ObjectNode result = yahoo.getHistoricalStock(symbol);
      if (result == null) {
          result = Json.newObject();
          result.put("status", "KO");
          result.put("message", "Stock symbol: " + symbol.toUpperCase() + " can not be found");
          return badRequest(result);
      }
      return ok(result);
  }
}
