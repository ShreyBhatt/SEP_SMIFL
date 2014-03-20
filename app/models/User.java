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
  private long id;
  public long getId() { return this.id; }

  @Constraints.Required
  private String first;

  @Constraints.Required
  private String last;
  
  @Constraints.Required
  private String email;

  /** We use this to get all the users portfolios. */
  private Set<Portfolio> portfolios;

  /** Constructor */
  private User (
      final String first, final String last, final String email
      ) {
    this.first = first;
    this.last = last;
    this.email = email;
      }

  /**
   * Returns this object as JSON
   */
  public ObjectNode getJson() {
    return Json.newObject()
      .put("first", this.first)
      .put("last", this.last)
      .put("email", this.email)
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
   * Method for finding a registered user by their db id
   */
  public static User findById( final long id ) {
    return Ebean.find(User.class)
      .where()
      .eq("id", id)
      .findUnique();
  }

  /**
   * Method for adding a user
   */
  public static User add (
      final String first, final String last, final String email
      ) {

    //Make sure this endpoint doesn't try adding a duplicate entry
    User user = User.find( email );

    if ( user != null ) {
      return user;
    }
      
    user =  new User(first, last, email);
    Ebean.save(user);
    return User.find(email);
      }

}

