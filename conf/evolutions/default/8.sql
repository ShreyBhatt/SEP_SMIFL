
# --- !Ups

alter table league add (
  passkey                 varchar(255),
  owner_id                bigint not null,
  initial_balance         double default 250000,
  brokerage_fee           double default 0
)
;

# --- !Downs
