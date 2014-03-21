package models;


import java.util.Date;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;

import scala.Option;
import securesocial.core.*;

@Entity
@Table(name="oauth2")
public class OAuth2 extends Model {

  private static final long serialVersionUID = 1L;

  @Id
  public long id;

  public String token;

  public String type;

  public Integer expiresIn;

  public String refresh;

  public static OAuth2 find ( final long id ) {

    return Ebean.find(OAuth2.class)
      .where()
      .eq("id", id)
      .findUnique();
  }

  public OAuth2Info toOAuth2Info() {
    return new OAuth2Info(
        token,
        Option.apply(type),
        Option.apply((Object) expiresIn),
        Option.apply(refresh));
  }
}

