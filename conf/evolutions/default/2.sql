# --- !Ups

create table portfolio (
  id                       bigint auto_increment not null,
  userId                   bigint not null,
  leagueId                 bigint not null,
  constraint pk_portfolio primary key (id))
;

create table position (
  id                       bigint auto_increment not null,
  userId                   bigint not null,
  portfolioId              bigint not null,
  positionType             varchar(255),
  ticker                   varchar(255),
  price                    double,
  dateOf                   datetime,
  constraint pk_position primary key (id))
;

# --- !Downs
