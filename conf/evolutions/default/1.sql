# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table league (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  goal                      varchar(255),
  constraint pk_league primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  first                     varchar(255),
  last                      varchar(255),
  email                     varchar(255),
  constraint pk_user primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table league;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

