-- Insert default accounts for double-entry ledger
INSERT INTO accounts (id, name) VALUES
(1, 'Customer Account'),
(2, 'Merchant Account'),
(3, 'Revenue Account'),
(4, 'Liability Account');

-- Insert test wallets for sellers
INSERT INTO wallets (user_id, balance, version) VALUES
(1, 0.00, 0),
(2, 0.00, 0),
(3, 0.00, 0);
