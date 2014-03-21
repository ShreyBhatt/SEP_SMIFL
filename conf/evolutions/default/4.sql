# --- !Ups

drop table position;

create table position (
  id                       bigint auto_increment not null,
  portfolio_id             bigint not null,
  type_of                  varchar(255),
  ticker                   varchar(255),
  qty                      bigint,
  price                    double,
  date_of                   datetime,
  constraint pk_position primary key (id))
;

# --- !Downs
