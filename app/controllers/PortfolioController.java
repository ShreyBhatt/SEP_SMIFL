package controllers;

import play.*;
import play.mvc.*;
import java.util.*;
import securesocial.core.Identity;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.SecureSocial;
import models.*;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import service.*;
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

	//TODO: Make this work. bitch :) and fix tabs
	@SecureSocial.SecuredAction
	public static Result getPortfolioOverview( final long userId, final long leagueId ) {

	    Portfolio portfolio = Portfolio.getPortfolio(userId, leagueId);
	    Identity identity = (Identity) ctx().args.get(SecureSocial.USER_KEY);

	    if ( !validateUser(portfolio.id, identity.identityId().userId()) ) {
      		return invalidRequest("Unauthorized Operation");
    	    }

	    User user = User.find(identity.email().get());
	    
//league name, get from 1 query
//available cash, 1 query
//calculation of total value of all stocks in portfolio
//return first 10 stocks in portfolio

	    League league = League.findById(leagueId);
	    Position cash = Position.getCashPosition(portfolio.id);
	    List<Position> positions = Position.getAllOwnPositions(portfolio.id);
	   
  	    ObjectNode result = Json.newObject();
			
	    result.put("leagueName", league.name);
	    result.put("cash", cash.price);
	
	    double totalStockValue = 0;
	    YahooFinanceService yahoo = YahooFinanceService.getInstance();
			String positionsObj = "[";
	    for (Position position: positions) {
				double currentPrice = yahoo.getStock(position.ticker).getPrice();
				positionsObj = positionsObj + "{\"ticker\":\"" + position.ticker + "\",\"typeOf\":\"" + position.typeOf + "\",\"qty\":\"" + position.qty + "\",\"price\":\"" + position.price + "\", \"dateOf\":\"" + position.dateOf + "\",\"currentPrice\":\"" + currentPrice + "\"},";
				totalStockValue += position.qty * currentPrice;
	    }
			
			positionsObj = positionsObj.substring(0, positionsObj.length()-1) + "]";
			System.out.println(positionsObj);
	    result.put("totalStockValue", totalStockValue);    
	    result.put("startingValue", 250000);
			//result.put("positions", "[{\"name\":\"jack\"},{\"name\":\"john\"},{\"name\":\"joe\"}]");
			result.put("positions", positionsObj);
	    

	return ok(result);

	}

}





