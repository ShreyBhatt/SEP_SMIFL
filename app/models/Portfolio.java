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
  public long id;

  @Constraints.Required
  public long userId;

  @Constraints.Required
  public long leagueId;

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
   * @param userId is the DB id for a user
   * @param leagueId is the DB id for a league
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
   * Method for finding a Portfolio in the DB.
   * @param portfolioId is the DB id for the portfolio
   * @return Returns a Portfolio if found, null otherwise.
   */
  public static Portfolio find( final long portfolioId ) {
    return Ebean.find(Portfolio.class)
      .where()
      .eq("id", portfolioId)
      .findUnique();
  }

  /**
   * Method for finding a Portfolio in the DB.
   * @param portfolioId is the DB id for the portfolio
   * @return Returns a Portfolio if found, null otherwise.
   */
  public static List<Portfolio> findByLeadgueId( final long leagueId ) {
    return Ebean.find(Portfolio.class)
      .where()
      .eq("leagueId", leagueId)
      .findList();
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
  
  public static Portfolio add ( final long userId, final long leagueId ) {
    Portfolio portfolio = Portfolio.find( userId, leagueId );

    if ( portfolio != null ) {
      return portfolio;
    }

    portfolio = new Portfolio( userId, leagueId );
    Ebean.save(portfolio);
    return Portfolio.find( userId, leagueId );
  }

}

