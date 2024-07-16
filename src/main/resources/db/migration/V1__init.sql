CREATE TABLE IF NOT EXISTS companies
(
    uuid  UUID PRIMARY KEY,
    name TEXT NOT NULL,
    co2_emissions FLOAT8
);

CREATE TABLE IF NOT EXISTS users
(
    uuid UUID PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS transactions (
    uuid UUID PRIMARY KEY,
    company_uuid UUID NOT NULL, 
    user_uuid UUID NOT NULL,
    amount FLOAT8 NOT NULL,

    CONSTRAINT fk_company_transaction_uuid
        FOREIGN KEY (company_uuid)
        REFERENCES companies(uuid)
        ON UPDATE NO ACTION ON DELETE CASCADE,
        
    CONSTRAINT fk_user_transaction_uuid
        FOREIGN KEY (user_uuid)
        REFERENCES users(uuid)
        ON UPDATE NO ACTION ON DELETE CASCADE                 
);