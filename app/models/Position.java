package models;

import java.util.*;
import javax.persistence.*;
import java.util.Date;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.libs.Json;

import com.avaje.ebean.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Position entity managed by Ebean
 */
@Entity
@Table(name="position")
public class Position extends Model {
  
  private static final long serialVersionUID = 1L;

  @Id
  public long id;

  public long portfolioId;
  
  public String typeOf;

  public String ticker;

  public long qty;

  public double price;

  @Temporal(TemporalType.TIMESTAMP)
  public Date dateOf; 
  
  /**
   * Constructor
   */
  private Position (
      final long portfolioId, final String typeOf,
      final String ticker, final double price, final long qty) {
    this.portfolioId = portfolioId;
    this.typeOf = typeOf;
    this.ticker = ticker;
    this.price = price;
    this.qty = qty;
    this.dateOf = new Date();
      }
  public ObjectNode getJson() {
    return Json.newObject()
      .put("portfolioId", this.portfolioId)
      .put("typeOf", this.typeOf)
      .put("ticker", this.ticker)
      .put("qty", this.qty)
      .put("price", this.price)
      .put("dateOf", this.dateOf.toString());
  }

  public static List<Position> getAllPortfolioPositions( final long portfolioId ) {
    return Ebean.find(Position.class)
      .where()
      .eq("portfolioId", portfolioId)
      .findList();
  }

  /**
   * A method for adding a new position to a portfolio, this is for a stock purchase.
   */
  public static Position addOwnPosition (
      final long portfolioId, final long qty, final Stock stock ) {
    Position cashPosition = Ebean.find(Position.class)
      .where()
      .eq("portfolioId", portfolioId)
      .eq("typeOf", "CASH")
      .findUnique();

    final double cashValue = cashPosition.price;
    if ( stock.getPrice() * qty > cashValue ) {
      return null;
    }

    cashPosition.price = cashPosition.price -= stock.getPrice() * qty;
    Ebean.update(cashPosition);

    Position pos = new Position( portfolioId, "OWN",
        stock.getTicker(), stock.getPrice(), qty );
    Ebean.save(pos);

    return Ebean.find(Position.class)
      .where()
      .eq("dateOf", pos.dateOf)
      .findUnique();
  }

  //TODO allow additional cash to be added
  public static Position addCashPosition (final long portfolioId, final double price) {
    Position cashPosition = new Position(portfolioId, "CASH", "", price, 1 );
    Ebean.save(cashPosition);
    return Ebean.find(Position.class)
      .where()
      .eq("dateOf", cashPosition.dateOf)
      .findUnique();
  }

  public static Position getCashPosition ( final long portfolioId ) {
    return Ebean.find(Position.class)
      .where()
      .eq("portfolioId", portfolioId)
      .eq("typeOf", "CASH")
      .findUnique();
  }
}
