CREATE TABLE token_info (
  id SERIAL PRIMARY KEY ,
  user_id VARCHAR(50) ,
  ducatus_address VARCHAR(120) ,
  ducatux_address VARCHAR(120),
  register BOOLEAN,
  unique (user_id)
                                 );
