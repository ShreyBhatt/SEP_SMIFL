package controllers;

import play.Logger;
import play.*;
import play.mvc.*;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.SecureSocial;
import service.MyUserService;
import views.html.*;
import views.html.linkResult;

public class Application extends Controller {

	static String testing = "sup.";
	public static Logger.ALogger logger = Logger.of("application.controllers.Application");

	public static String getName() { //testing function

		return testing;
	}

	@SecureSocial.SecuredAction
    	public static Result index() {
		if(logger.isWarnEnabled()){
		    logger.warn("access granted to index");
		}
		Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);

		return ok(index.render(user));
    	}

	@SecureSocial.UserAwareAction
	public static Result userAware() {
		Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
		final String userName = user != null ? user.fullName() : "guest";

		return ok("Hello " + userName + ", you are seeing a public page");
	}

	@SecureSocial.SecuredAction
	public static Result linkResult() {
		Identity identity = (Identity) ctx().args.get(SecureSocial.USER_KEY);
		// get the user identities
		MyUserService service = (MyUserService) Play.application().plugin(BaseUserService.class);
		MyUserService.User user = service.userForIdentity(identity);

		return ok(linkResult.render(identity, user.identities));
	
	}

}
