
# --- !Ups

alter table users add (
  provider                varchar(255),
  user_id                 varchar(255)
)
;

create table localtoken (
  id                       bigint auto_increment not null,
  email                    varchar(255),
  created_at               datetime,
  expired_at               datetime,
  constraint pk_token primary key (id))
;

create table oauth2 (
  id                       bigint not null,
  token                    text,
  expires                  bigint,
  refresh                  varchar(255),
  constraint pk_oauth2 primary key (id))
;

# --- !Downs
