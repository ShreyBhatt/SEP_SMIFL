
# --- !Ups


create table users (
  id                        bigint auto_increment not null,
  first                     varchar(255),
  last                      varchar(255),
  email                     varchar(255),
  constraint pk_user primary key (id))
;

create table league (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  goal                      varchar(255),
  constraint pk_league primary key (id))
;


# --- !Downs

