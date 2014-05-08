package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import models.*;

import com.avaje.ebean.*;

import play.libs.Json;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * League entity managed by EBean
 */
@Entity
@Table(name="league")
public class League extends Model {

  private static final long serialVersionIUD = 1L;

  @Id
  public long id;

  @Constraints.Required
  public String name;

  @Constraints.Required
  public String goal;

  @Constraints.Required
  public String passkey;

	@Constraints.Required
  public long ownerId;

  @Constraints.Required
  public double initialBalance;

  @Constraints.Required
  public double brokerageFee;

  public League( final String name, final String goal, final String passkey, final long ownerId, final double initialBalance, final double brokerageFee ) {
    this.name = name;
    this.goal = goal;
    this.passkey = passkey;
		this.ownerId = ownerId;
		this.initialBalance = initialBalance;
		this.brokerageFee = brokerageFee;
		
  }

  public ObjectNode getJson() {
    return Json.newObject()
      .put("id", this.id)
      .put("name", this.name)
      .put("goal", this.goal)
      .put("passkey", this.passkey)
      .put("ownerId", this.ownerId)
      .put("initialBalance", this.initialBalance)
      .put("brokerageFee", this.brokerageFee);
  }

  public static League find ( final String name ) {
    return Ebean.find(League.class)
      .where()
      .eq("name", name)
      .findUnique();
  }

  public static League findById ( final Long id ) {
    return Ebean.find(League.class)
      .where()
      .eq("id", id)
      .findUnique();
  }
  
  public static List<League> findAllById ( final Long id ) {
    return Ebean.find(League.class)
      .where()
      .eq("id", id)
      .findList();
  }

  public static League add ( final String name, final String goal, final String passkey, final long ownerId, final double initialBalance, final double brokerageFee ) {
    League league = League.find( name );

    if ( league != null ) {
      return league;
    }

    league = new League( name, goal, passkey, ownerId, initialBalance, brokerageFee );
    Ebean.save(league);
    return League.find( name );
  }

  public static List<League> getAllPublicLeagues() {
    return Ebean.find(League.class)
			.where()
      .eq("passkey", null)
      .findList();
  }
  
  public static List<League> searchByName ( String leagueName ) {
    return Ebean.find(League.class)
      .where()
      .ilike("name", "%" + leagueName + "%")
      .findList();
  }

}
