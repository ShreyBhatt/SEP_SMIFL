package models;


import java.util.Date;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;

@Entity
@Table(name="localtoken")
public class LocalToken extends Model {

  private static final long serialVersionUID = 1L;

  @Id
  public String id;

  public String email;

  public Date createdAt;

  public Date expireAt;


  public static Finder<String, LocalToken> find = new Finder<String, LocalToken>(
      String.class, LocalToken.class
      );
}
