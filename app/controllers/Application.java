package controllers;

import play.*;
import play.mvc.*;

import securesocial.core.Identity;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.SecureSocial;

import service.MyUserService;

import views.html.*;

public class Application extends Controller {

  public static Logger.ALogger logger = Logger.of("application.controllers.Application");


  @SecureSocial.SecuredAction
  public static Result index() {
    if (logger.isWarnEnabled()) {
      logger.warn("access granted to index");
    }
    Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
    return ok(index.render(user));
  }

  @SecureSocial.SecuredAction
  public static Result portfolio() {
    if (logger.isWarnEnabled()) {
      logger.warn("access granted to index");
    }
    Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
    return ok(index.render(user));
  }

  //This is just example code
  @SecureSocial.UserAwareAction
  public static Result userAware() {
    Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
    final String userName = user != null ? user.fullName() : "guest";

    return ok("Hello" + userName);
  }

}
