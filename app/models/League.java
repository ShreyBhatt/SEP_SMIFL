package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

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

  public League( final String name ) {
    this.name = name;
    this.goal = "default";
  }

  public ObjectNode getJson() {
    return Json.newObject()
      .put("name", this.name)
      .put("goal", this.goal)
      .put("id", this.id);
  }

  public static League find ( final String name ) {
    return Ebean.find(League.class)
      .where()
      .eq("name", name)
      .findUnique();
  }

  public static League add ( final String name ) {
    League league = League.find( name );

    if ( league != null ) {
      return league;
    }

    league = new League( name );
    Ebean.save(league);
    return League.find( name );
  }
}
