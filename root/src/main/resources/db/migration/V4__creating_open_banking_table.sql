CREATE TABLE IF NOT EXISTS user_open_banking_data
(
   uuid UUID PRIMARY KEY,
   consent_id TEXT NOT NULL,
   user_uuid UUID NOT NULL,
   created_at TIMESTAMP NOT NULL,
   CONSTRAINT fk_user_uuid
        FOREIGN KEY (user_uuid)
        REFERENCES users(uuid)
        ON UPDATE NO ACTION ON DELETE CASCADE
);