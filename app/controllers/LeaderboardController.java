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
import com.fasterxml.jackson.databind.node.ArrayNode;
import service.*;
import views.html.*;

public class LeaderboardController extends Controller {

				public static class Standing implements Comparable<Standing> {

					public long id;
					public double value;
					public Standing( final long userId, final double totalPortfolioValue ) {
									id = userId;
									value = totalPortfolioValue;
					}

					public int compareTo(Standing compareStanding) {
						int compareQuantity = (int) ((Standing) compareStanding).value; 
						if (this.value > compareStanding.value) return -1;
						if (this.value < compareStanding.value) return 1;
						return 0;
					}

					public ObjectNode getJson(int rank) {
						User user = User.findById(this.id);
						String fullName = user.first + " " + user.last;
						
						
						return Json.newObject()
							.put("rank", rank)
							.put("userId", this.id)
							.put("fullName", fullName)
							.put("totalPortfolioValue", this.value);
					}	
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

				@SecureSocial.SecuredAction
				public static Result getLeaderboard( final long leagueId ) {

					Identity identity = (Identity) ctx().args.get(SecureSocial.USER_KEY);
					User user = User.find(identity.email().get());	

					//TODO: validate user can see league

					List<Portfolio> portfolios = Portfolio.findByLeadgueId(leagueId);
					ArrayList<Standing> standings = new ArrayList<Standing>();

					for( Portfolio portfolio : portfolios ) {
						List<Position> positions = Position.getAllPortfolioPositions(portfolio.id);
						double totalPortfolioValue = 0;
						YahooFinanceService yahoo = YahooFinanceService.getInstance();

						for ( final Position position : positions ) {
							if ( position.typeOf.compareTo("CASH") != 0) {
								double currentPrice = yahoo.getStock(position.ticker).getPrice();
								totalPortfolioValue += position.qty * currentPrice;
							} else {
								totalPortfolioValue += position.price;
							}
						}

						Standing standing = new Standing( portfolio.userId, totalPortfolioValue ); 
						standings.add(standing);
					}

					Collections.sort(standings);
					ObjectNode result = Json.newObject();
					ArrayNode leaderboardObj = result.putArray("standings");

					int i = 1;
					for (Standing stnd : standings) {
	
						leaderboardObj.add(stnd.getJson(i));
						i++;			
					}

					return ok(result);
				}
}


