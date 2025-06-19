INSERT INTO accounts (id, user_id, balance, version)
VALUES ('550e8400-e29b-41d4-a716-446655440000', '6ec0bd7f-11c0-43da-975e-2a8ad9ebae0b', 0, 0);

UPDATE accounts
SET balance = balance + 100, version = version + 1
WHERE user_id = '6ec0bd7f-11c0-43da-975e-2a8ad9ebae0b';

INSERT INTO transactions (id, account_id, type, amount, created_at)
VALUES (gen_random_uuid(),
        '550e8400-e29b-41d4-a716-446655440000',
        'DEPOSIT', 100, NOW());

UPDATE accounts
SET balance = balance - 50, version = version + 1
WHERE user_id = '6ec0bd7f-11c0-43da-975e-2a8ad9ebae0b'
  AND balance >= 50;

INSERT INTO transactions (id, account_id, type, amount, created_at)
VALUES (gen_random_uuid(),
        '550e8400-e29b-41d4-a716-446655440000',
        'WITHDRAWAL', 50, NOW());

SELECT * FROM transactions
WHERE account_id = '550e8400-e29b-41d4-a716-446655440000'
  AND created_at >= NOW() - INTERVAL '30 DAYS'
ORDER BY created_at DESC;