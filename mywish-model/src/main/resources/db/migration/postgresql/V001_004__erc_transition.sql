CREATE TABLE erc_transition (
  id SERIAL PRIMARY KEY ,
  amount NUMERIC (78, 0),
  tx_hash VARCHAR(66) ,
  transfer_status VARCHAR(20),
  token_id SERIAL REFERENCES token_info (id)
);