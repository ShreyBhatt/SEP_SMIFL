package controllers;

import play.*;
import play.mvc.*;
import play.libs.Json;

import com.fasterxml.jackson.databind.node.ObjectNode;

import views.html.*;

import models.*;
import service.YahooFinanceService;

public class Trader extends Controller {

  private static final YahooFinanceService YAHOO = YahooFinanceService.getInstance();

  public static Result buyStock (
      final long portfolioId, final String ticker, final long qty
      ) {
    Stock stock = YAHOO.getStock( ticker );

    if ( stock == null ) {
      return badRequest(
          Json.newObject()
          .put("status", "KO")
          .put("message", "Bad Ticker")
          );
    }

    Position pos = Position.addOwnPosition(portfolioId, qty, stock);
    if ( pos == null ) {
      return badRequest(
          Json.newObject()
          .put("status", "KO")
          .put("message", "Insufficient Funds")
          );
    }
    return ok(pos.getJson());
      }
}

