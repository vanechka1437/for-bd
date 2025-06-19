CREATE TABLE accounts (
                          id UUID PRIMARY KEY,
                          user_id UUID NOT NULL UNIQUE,
                          balance NUMERIC(19,2) NOT NULL CHECK (balance >= 0),
                          version BIGINT NOT NULL
);

CREATE TABLE transactions (
                              id UUID PRIMARY KEY,
                              account_id UUID NOT NULL REFERENCES accounts(id),
                              type VARCHAR(20) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAWAL')),
                              amount NUMERIC(19,2) NOT NULL CHECK (amount > 0),
                              created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_transactions_account ON transactions(account_id);
CREATE INDEX idx_transactions_created ON transactions(created_at);