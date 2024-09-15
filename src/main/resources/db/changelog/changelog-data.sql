--liquibase formatted sql

-- ChangeSet petr.havelka:1
INSERT INTO povrch(nazev, cena, zmenauzivatel) VALUES  ('tráva', 10, 'sa');
INSERT INTO povrch(nazev, cena, zmenauzivatel) VALUES  ('antuka', 20, 'sa');

INSERT INTO kurt(refpovrch, nazev, zmenauzivatel) VALUES ((select id from povrch where nazev = 'tráva'), 'Kurt č.1 - tráva', 'sa');
INSERT INTO kurt(refpovrch, nazev, zmenauzivatel) VALUES ((select id from povrch where nazev = 'tráva'), 'Kurt č.2 - tráva', 'sa');
INSERT INTO kurt(refpovrch, nazev, zmenauzivatel) VALUES ((select id from povrch where nazev = 'antuka'), 'Kurt č.3 - antuka', 'sa');
INSERT INTO kurt(refpovrch, nazev, zmenauzivatel) VALUES ((select id from povrch where nazev = 'antuka'), 'Kurt č.4 - antuka', 'sa');

-- ChangeSet petr.havelka:2
INSERT INTO zakaznik (telefon, celejmeno, zmenauzivatel) VALUES ('739000111', 'Zakaznik 1', 'sa');
INSERT INTO zakaznik (telefon, celejmeno, zmenauzivatel) VALUES ('739000222', 'Zakaznik 2', 'sa');
INSERT INTO zakaznik (telefon, celejmeno, zmenauzivatel) VALUES ('739000333', 'Zakaznik 2', 'sa');

INSERT INTO rezervace
  (refkurt, 
  refzakaznik, 
  datumod, 
  datumdo, 
  ctyrhra,
  cenapronajem,
  zmenauzivatel) 
VALUES 
  ((select id from kurt where nazev = 'Kurt č.1 - tráva'), 
  (select id from zakaznik where telefon = '739000111'), 
  '2024-01-01 15:00', 
  '2024-01-01 16:00',
  0,
  10,
  'sa');

INSERT INTO rezervace
  (refkurt, 
  refzakaznik, 
  datumod, 
  datumdo, 
  ctyrhra,
  cenapronajem,
  zmenauzivatel) 
VALUES 
  ((select id from kurt where nazev = 'Kurt č.3 - antuka'), 
  (select id from zakaznik where telefon = '739000222'), 
  '2024-02-01 16:00', 
  '2024-02-01 17:00',
  1,
  35,
  'sa');
  
INSERT INTO rezervace
  (refkurt, 
  refzakaznik, 
  datumod, 
  datumdo, 
  ctyrhra,
  cenapronajem,
  zmenauzivatel) 
VALUES 
  ((select id from kurt where nazev = 'Kurt č.3 - antuka'), 
  (select id from zakaznik where telefon = '739000222'), 
  '2025-01-01 16:00', 
  '2025-01-01 17:00',
  1,
  35,
  'sa');
