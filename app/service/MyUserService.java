package service;

import play.*;
import models.*;
import scala.Option;
import scala.Some;

import securesocial.core.*;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.Token;

import java.util.*;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DateFormat;

import org.joda.time.DateTime;

/**
 * Service for handling OAuth
 */
public class MyUserService extends BaseUserService {

  public Logger.ALogger logger = play.Logger.of("application.service.MyUserService");

  /**
   * Constructor for instantiating the service
   */
  public MyUserService(Application application) {
    super(application);
  }

  /**
   * Internal method for creating/updating a user.
   * @param user is a User object to be initialized/updated
   * @param identity is the container for the OAuth information
   * @return the user
   */
  private User userHelper( final User user, final Identity identity ) {
    user.userId = identity.identityId().userId();
    user.provider = identity.identityId().providerId();
    user.first = identity.firstName();
    user.last = identity.lastName();
    try { 
      user.email = identity.email().get());
    } catch ( final Exception e ) {
      user.email = "testing@test.com";
    }
    return user;
  }

  /**
   * Internal method for creating/updating an OAuth2 object.
   * @param oauth is a OAuth2 object to be initialized/updated
   * @param identity is the container for the OAuth information
   * @return the oauth
   */
  private OAuth2 oauth2Helper( final OAuth2 oauth, final Identity identity ) {
    try {
      OAuth2Info info = identity.oAuth2Info().get();
      oauth.token = info.accessToken();
      //need to figure this out
      oauth.type = null; //info.tokenType().get();
      oauth.expiresIn = (Integer) identity.oAuth2Info().get().expiresIn().get();
      oauth.refresh = null;//info.refreshToken().get();
      return oauth;
    }
    catch ( final Exception e ) {
      logger.warn("OAuth Info failed to be delivered");
      return null;
    }

  }

  /** {@inheritDoc} */
  @Override
  public Identity doSave( final Identity identity ) {
    if (Logger.isDebugEnabled()) {
      Logger.debug("save...");
      Logger.debug(String.format("user = %s", identity));
    }

    User user = User.findUserId( identity.identityId().userId() );
    OAuth2 oauth;
    
    if ( user == null ) {
      user = userHelper(new User(), identity);
      user.save();
      user = User.find(user.email);
      oauth = oauth2Helper(new OAuth2(), identity);
      oauth.id = user.id;
      oauth.save();
      Portfolio port = Portfolio.getPortfolio( user.id, 1 );
      Position.addCashPosition( port.id, 250000 );
    }
    else {
      user = userHelper(user, identity);
      user.update();
      oauth = OAuth2.find( user.id );
      if ( oauth != null ) {
        oauth = oauth2Helper(oauth, identity);
      }
      else {
        oauth = oauth2Helper(new OAuth2(), identity);
      }
      oauth.id = user.id;
      oauth.update();
    }
    return identity;
  }

  /** {@inheritDoc} */
  @Override
  public Identity doFind( final IdentityId userId ) {
    if (Logger.isDebugEnabled()) {
      Logger.debug("find...");
      Logger.debug(String.format("id = %s", userId.userId()));
    }

    User localUser = User.findUserId(userId.userId());
    if ( localUser == null ) {
      return null;
    }
    SocialUser socialUser =
      new SocialUser(
          new IdentityId(localUser.userId, localUser.provider),
          localUser.first,
          localUser.last,
          String.format("%s %s", localUser.first, localUser.last),
          Option.apply(localUser.email),
          Option.apply((String) null), //avatarURL
          new AuthenticationMethod("oauth2"),
          Option.apply((OAuth1Info) null), //Oauth1Info
          Option.apply(OAuth2.find(localUser.id).toOAuth2Info()), //Oauth2Info
          Option.apply((PasswordInfo) null) //password
        );  
    if (Logger.isDebugEnabled()) {
      Logger.debug(String.format("socialUser = %s", socialUser));
    }
    return socialUser;
  }


  /** {@inheritDoc} */
  @Override
  //We don't use tokens right now so we aren't saving them
  public void doSave(Token token) {/*
    if (Logger.isDebugEnabled()) {
      Logger.debug("save...");
      Logger.debug(String.format("token = %s", token.uuid));
    }

    LocalToken localToken = new LocalToken();
    localToken.id = token.uuid;
    localToken.email = token.email;
    try {
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      localToken.createdAt = df.parse(token.creationTime.toString("yyyy-MM-dd HH:mm:ss"));
      localToken.expireAt = df.parse(token.expirationTime.toString("yyyy-MM-dd HH:mm:ss"));
    } catch (ParseException e) {
      Logger.error("SqlUserService.doSave(): ", e);
    }
    localToken.save();*/
  }

  /** {@inheritDoc} */
  @Override
  public Token doFindToken(String token) {
    if (Logger.isDebugEnabled()) {
      Logger.debug("findToken...");
      Logger.debug(String.format("token = %s", token));
    }
    LocalToken localToken = LocalToken.find.byId(token);
    if(localToken == null) {
      return null;
    }
    Token result = new Token();
    result.uuid = localToken.id;
    result.creationTime = new DateTime(localToken.createdAt);
    result.email = localToken.email;
    result.expirationTime = new DateTime(localToken.expireAt);
    result.isSignUp = false;
    if (Logger.isDebugEnabled()) {
      Logger.debug(String.format("foundToken = %s", result));
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public Identity doFindByEmailAndProvider(String email, String providerId) {
    if (Logger.isDebugEnabled()) {
      Logger.debug("findByEmailAndProvider...");
      Logger.debug(String.format("email = %s", email));
      Logger.debug(String.format("providerId = %s", providerId));
    }

    User localUser = User.find(email);
    if( localUser == null) {
      return null;
    }
    SocialUser socialUser =
      new SocialUser(
          new IdentityId(localUser.userId, localUser.provider),
          localUser.first,
          localUser.last,
          String.format("%s %s", localUser.first, localUser.last),
          Option.apply(localUser.email),
          Option.apply((String) null), //avatarURL
          new AuthenticationMethod("oauth2"),
          Option.apply((OAuth1Info) null), //Oauth1Info
          Option.apply(OAuth2.find(localUser.id).toOAuth2Info()), //Oauth2Info
          Option.apply((PasswordInfo) null) //password
        );  
    if (Logger.isDebugEnabled()) {
      Logger.debug(String.format("socialUser = %s", socialUser));
    }
    return socialUser;
  }

  /** {@inheritDoc} */
  @Override
  public void doDeleteToken(String uuid) {
    if (Logger.isDebugEnabled()) {
      Logger.debug("deleteToken...");
      Logger.debug(String.format("uuid = %s", uuid));
    }
    LocalToken localToken = LocalToken.find.byId(uuid);
    if(localToken != null) {
      localToken.delete();
    }
  }

  /** {@inheritDoc} */
  @Override
  public void doDeleteExpiredTokens() {
    if (Logger.isDebugEnabled()) {
      Logger.debug("deleteExpiredTokens...");
    }
    List<LocalToken> list =
      LocalToken.find.where().lt("expireAt", new DateTime().toString()).findList();
    for(LocalToken localToken : list) {
      localToken.delete();
    }
  }

}

