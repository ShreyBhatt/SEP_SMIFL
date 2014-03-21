# --- !Ups

drop table portfolio;

create table portfolio (
  id                       bigint auto_increment not null,
  user_id                  bigint not null,
  league_id                bigint not null,
  constraint pk_portfolio primary key (id))
;

# --- !Downs
