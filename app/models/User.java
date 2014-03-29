package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;

import play.libs.Json;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * User entity managed by EBean
 */
@Entity
@Table(name="users")
public class User extends Model {

  private static final long serialVersionUID = 1L;

  @Id
  public long id;

  @Constraints.Required
  public String first;

  @Constraints.Required
  public String last;

  @Constraints.Required
  public String email;

  @Constraints.Required
  public String provider;

  @Constraints.Required
  public String userId;

  /** We use this to get all the users portfolios. */
  private Set<Portfolio> portfolios;

  /**
   * Returns this object as JSON
   */
  public ObjectNode getJson() {
    return Json.newObject()
      .put("first", this.first)
      .put("last", this.last)
      .put("email", this.email)
      .put("provider", this.provider)
      .put("userId", this.userId)
      .put("id", this.id);
  }

  /**
   * Returns this object with JSON and all the portfolios it owns
   */
  public ObjectNode getJsonWithPortfolios() {
    ObjectNode node = this.getJson();
    ArrayNode arr = node.putArray("portfolios");
    for ( final Portfolio portfolio : portfolios ) {
      arr.add(portfolio.getJson());
    }
    return node;
  }

  /**
   * Method for finding a registered user by their email.
   */
  public static User find ( final String email ) {
    return Ebean.find(User.class)
      .where()
      .eq("email", email)
      .findUnique();
  }

  /**
   * Method for finding a registered user by their email.
   */
  public static User findUserId ( final String userId ) {
    return Ebean.find(User.class)
      .where()
      .eq("userId", userId)
      .findUnique();
  }

  /**
   * Method for finding a registered user by their db id
   */
  public static User findById( final long id ) {
    return Ebean.find(User.class)
      .where()
      .eq("id", id)
      .findUnique();
  }

}

