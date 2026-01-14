-- Add cancellation tracking to payment_events table
ALTER TABLE payment_events
    ADD COLUMN is_cancellation_done BOOLEAN NOT NULL DEFAULT FALSE;

-- Add reversal tracking to payment_orders table
ALTER TABLE payment_orders
    ADD COLUMN is_ledger_reversed BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN is_wallet_reversed BOOLEAN NOT NULL DEFAULT FALSE;

-- Add index on payment_key for faster lookups during cancellation
CREATE INDEX idx_payment_key ON payment_events(payment_key);
