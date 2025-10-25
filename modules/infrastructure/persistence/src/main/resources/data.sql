INSERT INTO partner (id, code, name, active)
values( 1, 'test', '테스트 PG', true);

INSERT INTO partner_fee_policy (id, partner_id, effective_from, percentage, fixed_fee)
values(1, 1, now(), 0.03, 100);

INSERT INTO partner (id, code, name, active)
values( 2, 'mock', '더미 PG', true);

INSERT INTO partner_fee_policy (id, partner_id, effective_from, percentage, fixed_fee)
values( 2, 2, now(), 0.05, 500);