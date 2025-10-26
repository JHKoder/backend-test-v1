INSERT INTO partner (id, code, name, active)
values (1, 'test', '테스트 PG', true);

INSERT INTO partner_fee_policy (id, partner_id, effective_from, percentage, fixed_fee)
values (2, 1, DATEADD('DAY', -10, NOW()), 0.03, 100);
INSERT INTO partner_fee_policy (id, partner_id, effective_from, percentage, fixed_fee)
values (3, 1, DATEADD('DAY', -5, NOW()), 0.07, 300);
INSERT INTO partner_fee_policy (id, partner_id, effective_from, percentage, fixed_fee)
values (4, 1, DATEADD('DAY', -1, NOW()), 0.10, 500);

INSERT INTO partner (id, code, name, active)
values (2, 'mock', '더미 PG', true);

INSERT INTO partner_fee_policy (id, partner_id, effective_from, percentage, fixed_fee)
values (5, 2, now(), 0.05, 500);

ALTER TABLE partner
    ALTER COLUMN id RESTART WITH 3;
ALTER TABLE partner_fee_policy
    ALTER COLUMN id RESTART WITH 6;