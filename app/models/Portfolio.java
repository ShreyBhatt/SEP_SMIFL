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
 * Portfolio
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

  public Portfolio( final long userId, final long leagueId ) {
    this.userId = userId;
    this.leagueId = leagueId;
  }

  public ObjectNode getJson() {
    return Json.newObject()
      .put("userId", this.userId)
      .put("leagueId", this.leagueId)
      .put("id", this.id);
  }
  /**
   * Method sets up a portfolio for a given user in a given league.
   */
  public static Portfolio getPortfolio( final long userId, final long leagueId ) {
    Portfolio port = Ebean.find(Portfolio.class)
      .where()
      .eq("userId", userId)
      .eq("leagueId", leagueId)
      .findUnique();

    if ( port != null ) {
      return port;
    }

    port = new Portfolio(userId, leagueId);
    Ebean.save(port);
    return Ebean.find(Portfolio.class)
      .where()
      .eq("userId", userId)
      .eq("leagueId", leagueId)
      .findUnique();
  }

}

