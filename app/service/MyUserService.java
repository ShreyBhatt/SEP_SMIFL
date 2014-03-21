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
 * Service for handling OpenID/OAuth
 */
public class MyUserService extends BaseUserService {

  public Logger.ALogger logger = play.Logger.of("application.service.MyUserService");

  public MyUserService(Application application) {
    super(application);
  }

  @Override
  public Identity doSave( final Identity identity ) {
    if (Logger.isDebugEnabled()) {
      Logger.debug("save...");
      Logger.debug(String.format("user = %s", identity));
    }

    User user = User.findUserId( identity.identityId().userId() );
    OAuth2 oauth;
    
    if ( user == null ) {
      user = new User();
      user.userId = identity.identityId().userId();
      user.provider = identity.identityId().providerId();
      user.first = identity.firstName();
      user.last = identity.lastName();
      user.email = identity.email().get();
      user.save();
      user = User.find(user.email);
      Portfolio port = Portfolio.getPortfolio( user.id, 1 );
      Position.addCashPosition( port.getId(), 250000 );
      oauth = new OAuth2();
      oauth.id = user.id;
      oauth.token = identity.oAuth2Info().get().accessToken();
      oauth.type = null;//identity.oAuth2Info().get().tokenType().get();
      oauth.expiresIn = (Integer) identity.oAuth2Info().get().expiresIn().get();
      oauth.refresh = null;//identity.oAuth2Info().get().refreshToken().get();
      oauth.save();
    }
    else {
      oauth = OAuth2.find( user.id );
      user.userId = identity.identityId().userId();
      user.provider = identity.identityId().providerId();
      user.first = identity.firstName();
      user.last = identity.lastName();
      user.email = identity.email().get();
      oauth.token = identity.oAuth2Info().get().accessToken();
      oauth.type = null;//identity.oAuth2Info().get().tokenType().get();
      oauth.expiresIn = (Integer) identity.oAuth2Info().get().expiresIn().get();
      oauth.refresh = null;//identity.oAuth2Info().get().refreshToken().get();
      oauth.update();
      user.update();
    }
    return identity;
  }

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


  public void doLink(Identity current, Identity to) {
  }


  @Override
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

