package controllers;

import java.util.*;

import play.*;
import play.mvc.*;
import play.libs.Json;

import com.fasterxml.jackson.databind.node.ObjectNode;

import views.html.*;

import models.*;
import service.YahooFinanceService;

/**
 * Controller for handling buy, sell, and short market orders.
 */
public class Trader extends Controller {

  /** The service that provides our stock data. */
  private static final YahooFinanceService YAHOO = YahooFinanceService.getInstance();


  /**
   * Method for returning a badRequest for an invalid stock ticker.
   * @param message is the message to return in the Result
   * @return a JSON Result
   */
  private static Result invalidRequest( final String message ) {
      return badRequest(
          Json.newObject()
          .put("status", "KO")
          .put("message", message)
          );
  }
  
  /**
   * Method for purchasing stock in a portfolio.
   * @param portfolioId is the unique id of the portfolio to purchase stock in
   * @param ticker is the stock ticker of the stock that is to be purchased
   * @param qty is the amount of stock to purchase
   * @return returns a JSON result.
   */
  public static Result buyStock (
      final long portfolioId, final String ticker, final long qty
      ) {
    Stock stock = YAHOO.getStock( ticker );

    if ( stock == null ) {
      return invalidRequest("Invalid Ticker Symbol");
    }

    Position pos = Position.addOwnPosition(portfolioId, qty, stock);
    if ( pos == null ) {
      return invalidRequest("Insufficient Funds");
    }

    ObjectNode result = Json.newObject();
    result.put("status", "OK");
    result.put("order", pos.getJson());
    return ok(result);
  }


  /**
   * Method for selling stock in a portfolio
   * @param portfolioId is the unique id of the portfolio to purchase stock in
   * @param ticker is the stock ticker of the stock that is to be purchased
   * @param qty is the amount of stock to purchase
   * @return returns a JSON result.
   */
  public static Result sellStock (
    final long portfolioId, final String ticker, final long qty
    ) {
    Stock stock = YAHOO.getStock( ticker );

    if ( stock == null ) {
      return invalidRequest("Invalid Ticker Symbol");
    }

    List<Position> ownPositions =
      Position.getAllOwnPositionsForTicker( portfolioId, ticker );

    long ownQty = 0;
    for ( final Position position : ownPositions ) {
      ownQty += position.qty;
    }

    if ( ownQty < qty ) {
      return invalidRequest("Insufficient quantity of " + ticker + " owned");
    }

    Position cashPosition = Position.getCashPosition( portfolioId );

    long qtyToSell = qty;
    final double price = stock.getPrice();
    for ( Position position : ownPositions ) {
      if ( position.qty <= qtyToSell ) {
        cashPosition.price += position.qty * price;
        qtyToSell -= position.qty;
        position.qty = 0;
        position.delete();

      }
      else {
        cashPosition.price += qtyToSell * price;
        position.qty -= qtyToSell;
        position.update();
        qtyToSell = 0;
      }
      if ( qtyToSell == 0 ) {
        break;
      }
    }
    cashPosition.update();
    ObjectNode result = Json.newObject();
    result.put("status", "OK");
    result.put("sold", ticker);
    result.put("qty", qty);
    result.put("price", price);
    result.put("total", price * qty);
    result.put("cashPosition", Position.getCashPosition( portfolioId ).getJson());

    return ok(result);
  }

}

