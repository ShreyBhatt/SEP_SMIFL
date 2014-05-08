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

  /**
   * Method for making a JSON representation of this object.
   * @return Returns an ObjectNode that contains the JSON for this object.
   */
  public ObjectNode getJson() {
    return Json.newObject()
      .put("portfolioId", this.portfolioId)
      .put("typeOf", this.typeOf)
      .put("ticker", this.ticker)
      .put("qty", this.qty)
      .put("price", this.price)
      .put("dateOf", this.dateOf.toString());
  }

  /**
   * Method for making a JSON representation of this object.
   * @return Returns an ObjectNode that contains the JSON for this object.
   */
  public ObjectNode getJson( final double price, final double open ) {
    return Json.newObject()
      .put("portfolioId", this.portfolioId)
      .put("typeOf", this.typeOf)
      .put("ticker", this.ticker)
      .put("qty", this.qty)
      .put("price", this.price)
      .put("dateOf",
              this.dateOf.toString()
                .substring(0, this.dateOf.toString().length() - 9))
      .put("currentPrice", price)
      .put("open", open);
  }

  /**
   * Method to get all the positions in a portfolio.
   */
  public static List<Position> getAllPortfolioPositions( final long portfolioId ) {
    return Ebean.find(Position.class)
      .where()
      .eq("portfolioId", portfolioId)
      .findList();
  }

  /**
   * Method for getting all the OWN positions of a stock in the portfolio.
   * @param portfolioId is the unique database id of the portfolio
   * @return a list of all the OWN positions of the ticker in portfolioId
   */
  public static List<Position> getAllOwnPositions(
          final long portfolioId
          ) {
    return Ebean.find(Position.class)
      .where()
      .eq("portfolioId", portfolioId)
      .eq("typeOf", "OWN")
      .findList();
  }

  /**
   * Method for getting all the OWN positions of a stock for a ticker in the
   * portfolio.
   * @param portfolioId is the unique database id of the portfolio
   * @param ticker is the ticker symbol of the stock positions to retrieve.
   * @return a list of all the OWN positions of the ticker in portfolioId
   */
  public static List<Position> getAllOwnPositionsForTicker(
      final long portfolioId, final String ticker
      ) {
    return Ebean.find(Position.class)
      .where()
      .eq("portfolioId", portfolioId)
      .eq("typeOf", "OWN")
      .eq("ticker", ticker)
      .findList();
  }

  /**
   * Method for getting all the SHORT positions of a stock for a ticker in the
   * portfolio.
   * @param portfolioId is the unique database id of the portfolio
   * @param ticker is the ticker symbol of the stock positions to retrieve.
   * @return a list of all the OWN positions of the ticker in portfolioId
   */
  public static List<Position> getAllShortPositions ( final long portfolioId ) {
    return Ebean.find(Position.class)
      .where()
      .eq("portfolioId", portfolioId)
      .eq("typeOf", "SHORT")
      .findList();
  }

  /**
   * A method for adding a new OWN position to a portfolio, this is for a stock purchase.
   * @param portfolioId is the unique database id of the portfolio
   * @param qty is the number of shares to own
   * @param stock is the stock information to conduct the trade
   * @return the object representation of the created position
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

  /**
   * A method for adding a new SHORT position to a portfolio.
   * @param portfolioId is the unique database id of the portfolio
   * @param qty is the number of shares to short
   * @param stock is the stock information to conduct the trade
   * @return the object representation of the created position
   */
  public static Position addShortPosition (
      final long portfolioId, final long qty, final Stock stock ) {
    Position cashPosition = Ebean.find(Position.class)
      .where()
      .eq("portfolioId", portfolioId)
      .eq("typeOf", "CASH")
      .findUnique();

    final double cashValue = cashPosition.price;
    if ( stock.getPrice() * qty > cashValue / 4) {
      return null;
    }

    Position pos = new Position( portfolioId, "SHORT",
        stock.getTicker(), stock.getPrice(), qty );
    Ebean.save(pos);

    return Ebean.find(Position.class)
      .where()
      .eq("dateOf", pos.dateOf)
      .findUnique();
  }

  /**
   * Method for initializing a portfolio with a cash position.
   * @param portfolioId is the unique database id of the portfolio
   * @param price is the amount of cash to add to the portfolio
   * @return the object representation of the created position
   */
  public static Position addCashPosition (final long portfolioId, final double price) {
    Position cashPosition = getCashPosition( portfolioId );
    if ( cashPosition != null ) {
      return cashPosition;
    }
    cashPosition = new Position(portfolioId, "CASH", "", price, 1 );
    Ebean.save(cashPosition);
    return Ebean.find(Position.class)
      .where()
      .eq("dateOf", cashPosition.dateOf)
      .findUnique();
  }

  /**
   * Method for getting just the cash position of a portfolio.
   * @param portfolioId is the unique database id of the portfolio
   * @return the object representation of the created position
   */
  public static Position getCashPosition ( final long portfolioId ) {
    return Ebean.find(Position.class)
      .where()
      .eq("portfolioId", portfolioId)
      .eq("typeOf", "CASH")
      .findUnique();
  }

}

