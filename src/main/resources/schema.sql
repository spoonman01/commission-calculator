CREATE TABLE transactions (
  client_id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL DEFAULT NOW(),
  amount decimal(12,2) unsigned NOT NULL,
  currency char(3) DEFAULT 'EUR',
  commission_amount decimal(12,2) unsigned NOT NULL,
  commission_currency char(3) DEFAULT 'EUR',
  PRIMARY KEY (client_id)
);
