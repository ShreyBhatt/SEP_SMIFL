package controllers;

import play.*;
import play.mvc.*;

import securesocial.core.Identity;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.SecureSocial;
import com.fasterxml.jackson.databind.node.ObjectNode;

import service.MyUserService;

import models.*;

import views.html.*;

/**
 * Our main controller for routing major pages
 */
public class Application extends Controller {

  public static Logger.ALogger logger = Logger.of("application.controllers.Application");


  /**
   * We don't ever go to index, everything sits at login or elsewhere
   * so we redirect as appropriate
   */
  //TODO Make login page fit our style and eliminate warning message
  @SecureSocial.SecuredAction
  public static Result index() {

    Identity identity = (Identity) ctx().args.get(SecureSocial.USER_KEY);

    if ( identity == null ) {
      return redirect("/login");
    }
    return redirect("/p");
  }

  /**
   * Get a user and load their portfolio, or if the user doesn't exist,
   * register them and generate a new global portfolio.
   */
  //TODO protect against an empty option, shouldn't be a problem though
  //TODO this should be done in the portfolio controller
  //this should simply load the page but we'll leave this for testing
  @SecureSocial.SecuredAction
  public static Result portfolio() {
    
    Identity identity = (Identity) ctx().args.get(SecureSocial.USER_KEY);
    User user = User.find(identity.email().get());
    ObjectNode result;
    Portfolio port;
    
    if ( user != null ) {
      result = user.getJson();
      port = Portfolio.getPortfolio( user.getId(), 1 );
      result.put("globalPortfolio", port.getJson() );
      result.put("cashPosition", Position.getCashPosition( port.getId() ).getJson() );
      return ok(result);
    }
    //TODO check to make sure nothing here returns null
    //user isn't in DB so we add them
    user = User.add(identity.firstName(), identity.lastName(), identity.email().get());
    //add a global portfolio for them
    port = Portfolio.getPortfolio( user.getId(), 1 );
    //Give them their cash position
    Position pos = Position.addCashPosition(port.getId(), 250000);

    result = user.getJson();
    result.put("globalPortfolio", port.getJson());
    result.put("cashPosition", pos.getJson() );

    return ok(result);
  }

  //This is just example code
  @SecureSocial.UserAwareAction
  public static Result userAware() {
    Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
    final String userName = user != null ? user.fullName() : "guest";

    return ok("Hello" + userName);
  }

}

