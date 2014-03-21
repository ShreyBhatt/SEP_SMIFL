package service;

import play.*;
import scala.Option;

import securesocial.core.Identity;
import securesocial.core.IdentityId;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.Token;

import java.util.*;

/**
 * ??
 */
public class MyUserService extends BaseUserService {

  public Logger.ALogger logger = play.Logger.of("application.service.MyUserService");

  public static class User {

    public String id;
    public List<Identity> identities;

    public User ( final String id, final Identity ident ) {
      this.id = id;
      identities = new ArrayList<Identity>();
      identities.add(ident);
    }
  }

  private HashMap<String, User> users = new HashMap<String, User>();
  private HashMap<String, Token> tokens = new HashMap<String, Token>();

  public MyUserService(Application application) {
    super(application);
  }

  @Override
  public Identity doSave(Identity identity) {

    logger.warn("In doSave");

    User found = null;

    for ( User u : users.values() ) {
      if ( u.identities.contains(identity) ) {
        found = u;
        break;
      }
    }

    if ( found != null ) {
      found.identities.remove(identity);
      found.identities.add(identity);
    }
    else {
      User u = new User(String.valueOf(System.currentTimeMillis()), identity);
      users.put(u.id, u);
    }

    return identity;
  }


  public void doLink(Identity current, Identity to) {
    logger.warn("In doLink");
    User target = null;

    for ( User u: users.values() ) {
      if ( u.identities.contains(current) ) {
        target = u;
        break;
      }
    }

    if ( target == null ) {
      throw new RuntimeException("Can't find a user for identity: " + current.identityId());
    }
    if ( !target.identities.contains(to)) target.identities.add(to);
  }

  @Override
  public void doSave(Token token) {
    logger.warn("In doSave for token");
    tokens.put(token.uuid, token);
  }

  @Override
  public Identity doFind(IdentityId userId) {
    logger.warn("In doFind");
    if(logger.isDebugEnabled()){
      logger.debug("Finding user " + userId);
    }
    Identity found = null;

    for ( User u: users.values() ) {
      for ( Identity i : u.identities ) {
        if ( i.identityId().equals(userId) ) {
          found = i;
          break;
        }
      }
    }

    return found;
  }

  @Override
  public Token doFindToken(String tokenId) {
    logger.warn("In doFindToken");
    return tokens.get(tokenId);
  }

  @Override
  public Identity doFindByEmailAndProvider(String email, String providerId) {
    logger.warn("In doFindByEmailAndProvider");
    Identity result = null;
    for( User user : users.values() ) {
      for ( Identity identity : user.identities ) {
        Option<String> optionalEmail = identity.email();
        if ( identity.identityId().providerId().equals(providerId) &&
            optionalEmail.isDefined() &&
            optionalEmail.get().equalsIgnoreCase(email))
        {
          result = identity;
          break;
        }
      }
    }
    return result;
  }

  @Override
  public void doDeleteToken(String uuid) {
    logger.warn("In doDeleteToken");
    tokens.remove(uuid);
  }

  @Override
  public void doDeleteExpiredTokens() {
    logger.warn("In doDleteExpiredTokens");
    Iterator<Map.Entry<String,Token>> iterator = tokens.entrySet().iterator();
    while ( iterator.hasNext() ) {
      Map.Entry<String, Token> entry = iterator.next();
      if ( entry.getValue().isExpired() ) {
        iterator.remove();
      }
    }
  }

  public User userForIdentity(Identity identity) {
    User result = null;

    for ( User u : users.values() ) {
      if ( u.identities.contains(identity) ) {
        result = u;
        break;
      }
    }

    return result;
  }
}
