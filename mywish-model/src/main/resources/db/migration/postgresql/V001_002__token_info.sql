CREATE TABLE token_info (
  id SERIAL PRIMARY KEY ,
  user_id VARCHAR(50) ,
  ducatus_address VARCHAR(120) ,
  ducatux_address VARCHAR(120),
  token_type VARCHAR(20),
  unique (user_id)
                                 );
