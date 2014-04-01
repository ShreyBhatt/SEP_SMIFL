
# --- !Ups

alter table users add (
  achv                     bigint default 0
)
;
# --- !Downs
