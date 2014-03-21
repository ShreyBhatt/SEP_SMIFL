package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.libs.Json;

import com.avaje.ebean.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Portfolio entity managed by Ebean
 */
@Entity
@Table(name="portfolio")
public class Portfolio extends Model {

  private static final long serialVersionUID = 1L;

  @Id
  private long id;
  public long getId() { return this.id; }

  @Constraints.Required
  private long userId;

  @Constraints.Required
  private long leagueId;

  /**
   * Constructor
   */
  private Portfolio( final long userId, final long leagueId ) {
    this.userId = userId;
    this.leagueId = leagueId;
  }

  /**
   * Method to get the JSON for this object.
   */
  //TODO add positions
  public ObjectNode getJson() {
    return Json.newObject()
      .put("userId", this.userId)
      .put("leagueId", this.leagueId)
      .put("id", this.id);
  }

  /**
   * Method for finding a Portfolio in the DB.
   * @return Returns a Portfolio if found, null otherwise.
   */
  public static Portfolio find( final long userId, final long leagueId ) {
    return Ebean.find(Portfolio.class)
      .where()
      .eq("userId", userId)
      .eq("leagueId", leagueId)
      .findUnique();
  }

  /**
   * Method sets up a portfolio for a given user in a given league.
   */
  public static Portfolio getPortfolio( final long userId, final long leagueId ) {
    Portfolio port = Portfolio.find( userId, leagueId );

    if ( port != null ) {
      //Already in the DB
      return port;
    }

    port = new Portfolio(userId, leagueId);
    Ebean.save(port);
    return Portfolio.find( userId, leagueId );
  }

  /**
   * Method finds all the portfolios for a user.
   */
  //TODO change this to a PagingList
  public static List<Portfolio> findAllByUserId ( final long userId ) {
    return Ebean.find(Portfolio.class)
      .where()
      .eq("userId", userId)
      .findList();
  }

}

