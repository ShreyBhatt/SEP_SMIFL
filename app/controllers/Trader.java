package controllers;

import java.util.*;

import play.*;
import play.mvc.*;
import play.libs.Json;

import securesocial.core.Identity;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.SecureSocial;

import play.libs.Json;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.*;
import service.*;
import views.html.*;

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
     * Method for ensuring that a user attempting to make a trade
     * is authorized to do so.
     * @param portfolioId is the DB id for a portfolio
     * @param userId is the OAuth userId
     * @return true is they are authorized, false otherwise.
     */
    private static boolean validateUser( final long portfolioId, final String userId ) {
        User user = User.findUserId(userId);
        Portfolio port = Portfolio.find( portfolioId );
        if ( user == null || port == null) {
            return false;
        }
        return user.id == port.userId;
    }

    /**
     * Method for purchasing stock in a portfolio.
     * @param portfolioId is the unique id of the portfolio to purchase stock in
     * @param ticker is the stock ticker of the stock that is to be purchased
     * @param qty is the amount of stock to purchase
     * @return returns a JSON result.
     */
    @SecureSocial.SecuredAction
    public static Result buyStock (
            final long portfolioId, final String ticker, final long qty
            ) {
        Stock stock = YAHOO.getStock( ticker );
        Identity identity = (Identity) ctx().args.get(SecureSocial.USER_KEY);

        if ( qty <= 0 ) {
            return invalidRequest("Can not purchase a negative amount of shares");
        }

        if ( !validateUser(portfolioId, identity.identityId().userId()) ) {
            return invalidRequest("Unauthorized Operation");
        }

        if ( stock == null || stock.getPrice() == 0 ) {
            return invalidRequest("Invalid Ticker Symbol");
        }

        Position pos = Position.addOwnPosition(portfolioId, qty, stock);
        if ( pos == null ) {
            return invalidRequest("Insufficient Funds");
        }

        User user = User.findUserId(identity.identityId().userId());

        ObjectNode result = Json.newObject();
        result.put("status", "OK");
        result.put("order", pos.getJson());
        result.put("achv", user.achv);

        //check for first buy then update achievements
        if ((user.achv & 1L) == 0) {
            user.achv = user.achv | 1;
            user.update();
        }

        return ok(result);
            }

    /**
     * Method for shorting stock in a portfolio.
     * @param portfolioId is the unique id of the portfolio to purchase stock in
     * @param ticker is the stock ticker of the stock that is to be purchased
     * @param qty is the amount of stock to purchase
     * @return returns a JSON result.
     */
    @SecureSocial.SecuredAction
    public static Result shortStock (
            final long portfolioId, final String ticker, final long qty
            ) {
        Stock stock = YAHOO.getStock( ticker );
        Identity identity = (Identity) ctx().args.get(SecureSocial.USER_KEY);

        if ( qty <= 0 ) {
            return invalidRequest("Can not purchase a negative amount of shares");
        }

        if ( !validateUser(portfolioId, identity.identityId().userId()) ) {
            return invalidRequest("Unauthorized Operation");
        }

        if ( stock == null || stock.getPrice() == 0 ) {
            return invalidRequest("Invalid Ticker Symbol");
        }

        Position pos = Position.addShortPosition(portfolioId, qty, stock);
        if ( pos == null ) {
            return invalidRequest("Insufficient Funds");
        }

        User user = User.findUserId(identity.identityId().userId());

        ObjectNode result = Json.newObject();
        result.put("status", "OK");
        result.put("order", pos.getJson());
        result.put("achv", user.achv);

        //check for first short then update achievements
        /*
        if ((user.achv & 1L) == 0) {
            user.achv = user.achv | 1;
            user.update();
        }*/

        return ok(result);
            }


    /**
     * Method for selling stock in a portfolio
     * @param portfolioId is the unique id of the portfolio to purchase stock in
     * @param ticker is the stock ticker of the stock that is to be purchased
     * @param qty is the amount of stock to purchase
     * @return returns a JSON result.
     */
    @SecureSocial.SecuredAction
    public static Result sellStock (
            final long portfolioId, final String ticker, final long qty
            ) {

        if (qty < 1) {
            return invalidRequest("Cannot sell a negative amount of stock");
        }

        Stock stock = YAHOO.getStock( ticker );
        Identity identity = (Identity) ctx().args.get(SecureSocial.USER_KEY);

        if ( !validateUser(portfolioId, identity.identityId().userId()) ) {
            return invalidRequest("Unauthorized Operation");
        }

        if ( stock == null ) {
            return invalidRequest("Invalid Ticker Symbol");
        }

        List<Position> ownPositions =
            Position.getAllOwnPositionsForTicker( portfolioId, ticker );

        if ( ownPositions == null || ownPositions.isEmpty() ) {
            return invalidRequest("No positions in " + ticker + " found");
        }

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
        double cashValueOfSale = 0;
        for ( Position position : ownPositions ) {
            if ( position.qty <= qtyToSell ) {
                cashValueOfSale += position.qty * price - position.qty * position.price;
                cashPosition.price += position.qty * price;
                qtyToSell -= position.qty;
                position.qty = 0;
                position.delete();

            }
            else {
                cashValueOfSale += position.qty * price - position.qty * position.price;
                cashPosition.price += qtyToSell * price;
                position.qty -= qtyToSell;
                position.update();
                qtyToSell = 0;
            }
            if ( qtyToSell == 0 ) {
                break;
            }
        }

        User user = User.findUserId(identity.identityId().userId());

        cashPosition.update();
        ObjectNode result = Json.newObject();
        result.put("status", "OK");
        result.put("sold", ticker);
        result.put("qty", qty);
        result.put("price", price);
        result.put("total", price * qty);
        result.put("cashPosition", Position.getCashPosition( portfolioId ).getJson());
        result.put("achv", user.achv);
        result.put("cashValueOfSale", cashValueOfSale);

        //check for first buy then update achievements
        if (((user.achv & 1<<1) == 0) && (cashValueOfSale > 0)) {
            user.achv = user.achv | 1<<1;
            user.update();
        }

        return ok(result);
            }

}

