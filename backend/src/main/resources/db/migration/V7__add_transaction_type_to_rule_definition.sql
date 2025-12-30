-- Add transaction_type column to rule_definition
ALTER TABLE rule_definition
    ADD COLUMN transaction_type VARCHAR(10) NOT NULL DEFAULT 'ANY';

-- Optional: ensure existing rows have a valid value
UPDATE rule_definition SET transaction_type = 'ANY' WHERE transaction_type IS NULL;
