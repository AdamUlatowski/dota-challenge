DROP TABLE IF EXISTS dota_match;

CREATE TABLE dota_match (
  id INT AUTO_INCREMENT  PRIMARY KEY
);

DROP TABLE IF EXISTS hero;

CREATE TABLE hero (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL
);

DROP TABLE IF EXISTS item;

CREATE TABLE item (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL
);

DROP TABLE IF EXISTS spell;

CREATE TABLE spell (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL
);

DROP TABLE IF EXISTS hero_kill_event;

CREATE TABLE hero_kill_event (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  match_id INT NOT NULL,
  assailant_id INT NOT NULL,
  victim_id INT NOT NULL,
  created TIME,
  FOREIGN KEY (match_id) REFERENCES dota_match(id),
  FOREIGN KEY (assailant_id) REFERENCES hero(id),
  FOREIGN KEY (victim_id) REFERENCES hero(id)
);
CREATE INDEX hero_kill_event_match_assailant_id ON hero_kill_event(match_id, assailant_id);

DROP TABLE IF EXISTS item_purchase_event;

CREATE TABLE item_purchase_event (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  match_id INT NOT NULL,
  hero_id INT NOT NULL,
  item_id INT NOT NULL,
  created TIME,
  FOREIGN KEY (match_id) REFERENCES dota_match(id),
  FOREIGN KEY (hero_id) REFERENCES hero(id),
  FOREIGN KEY (item_id) REFERENCES item(id)
);
CREATE INDEX item_purchase_event_match_hero_id ON item_purchase_event(match_id, hero_id);

DROP TABLE IF EXISTS casted_spell_event;

CREATE TABLE casted_spell_event (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  match_id INT NOT NULL,
  hero_id INT NOT NULL,
  spell_id INT NOT NULL,
  created TIME,
  FOREIGN KEY (match_id) REFERENCES dota_match(id),
  FOREIGN KEY (hero_id) REFERENCES hero(id),
  FOREIGN KEY (spell_id) REFERENCES spell(id)
);
CREATE INDEX casted_spell_event_match_hero_id ON casted_spell_event(match_id, hero_id);

DROP TABLE IF EXISTS damage_event;

CREATE TABLE damage_event (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  match_id INT NOT NULL,
  assailant_id INT NOT NULL,
  victim_id INT NOT NULL,
  damage INT,
  created TIME,
  FOREIGN KEY (match_id) REFERENCES dota_match(id),
  FOREIGN KEY (assailant_id) REFERENCES hero(id),
  FOREIGN KEY (victim_id) REFERENCES hero(id)
);
CREATE INDEX damage_event_match_assailant_id ON damage_event(match_id, assailant_id);