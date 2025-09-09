CREATE TABLE IF NOT EXISTS user_transactions
(
   uuid UUID PRIMARY KEY,
   company_uuid UUID NOT NULL,
   user_uuid UUID NOT NULL,
   amount FLOAT8 NOT NULL,
   created_at TIMESTAMP NOT NULL,

   CONSTRAINT fk_company_transaction_uuid
        FOREIGN KEY (company_uuid)
        REFERENCES companies(uuid)
        ON UPDATE NO ACTION ON DELETE CASCADE,

CONSTRAINT fk_user_transaction_uuid
   FOREIGN KEY (user_uuid)
       REFERENCES users(uuid)
       ON UPDATE NO ACTION ON DELETE CASCADE
);

ALTER TABLE IF EXISTS transactions
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();

ALTER TABLE IF EXISTS transactions
    RENAME TO open_banking_transactions;
