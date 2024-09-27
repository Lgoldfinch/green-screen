CREATE TABLE IF NOT EXISTS companies
(
    uuid  UUID PRIMARY KEY,
    name TEXT NOT NULL,
    co2Emissions FLOAT4
);

CREATE INDEX IF NOT EXISTS companies_index ON companies(uuid);

CREATE TABLE IF NOT EXISTS transactions (
    uuid UUID PRIMARY KEY,
    company_uuid UUID NOT NULL,
    amount FLOAT4 NOT NULL,

    CONSTRAINT fk_company_transaction_uuid
        FOREIGN KEY (company_uuid)
        REFERENCES companies(uuid)
        ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS transactions_index ON transactions(uuid);
