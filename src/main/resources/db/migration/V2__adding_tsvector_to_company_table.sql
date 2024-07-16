ALTER TABLE companies
    ADD COLUMN name_tsv tsvector;

UPDATE companies c SET name_tsv = to_tsvector(c.name);

ALTER TABLE companies
    ALTER COLUMN name_tsv SET NOT NULL;