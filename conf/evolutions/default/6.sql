
# --- !Ups

alter table oauth2 add (
  type                     varchar(255)
)
;
# --- !Downs
