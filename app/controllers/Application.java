package controllers;

import play.*;
import play.mvc.*;

import securesocial.core.Identity;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.SecureSocial;

import service.MyUserService;

import models.User;

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
  @SecureSocial.SecuredAction
  public static Result portfolio() {
    
    Identity identity = (Identity) ctx().args.get(SecureSocial.USER_KEY);
    User user = User.find(identity.email().get());
    
    if ( user == null ) {
      //user isn't in DB so we add them
      user = User.add(identity.firstName(), identity.lastName(), identity.email().get());
    }
    if ( user == null ) {
      //Something bad happend
      //TODO log this and build a message
      return badRequest();
    }
    return ok(user.getJson());
  }

  //This is just example code
  @SecureSocial.UserAwareAction
  public static Result userAware() {
    Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
    final String userName = user != null ? user.fullName() : "guest";

    return ok("Hello" + userName);
  }

}
