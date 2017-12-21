-- Exportiere Datenbank Struktur für dominiondb
DROP DATABASE IF EXISTS `dominiondb`;
CREATE DATABASE IF NOT EXISTS `dominiondb` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `dominiondb`;

CREATE USER 'dominion'@'localhost' IDENTIFIED BY 'dominion';
GRANT USAGE ON *.* TO 'dominion'@'localhost';
GRANT SELECT, EXECUTE, SHOW VIEW, ALTER, ALTER ROUTINE, CREATE, CREATE ROUTINE, CREATE TEMPORARY TABLES, CREATE VIEW, DELETE, DROP, EVENT, INDEX, INSERT, REFERENCES, TRIGGER, UPDATE, LOCK TABLES  ON `dominiondb`.* TO 'dominion'@'localhost' WITH GRANT OPTION;

-- Exportiere Struktur von Tabelle dominiondb.cards
DROP TABLE IF EXISTS `cards`;
CREATE TABLE IF NOT EXISTS `cards` (
  `cardID` int(11) NOT NULL AUTO_INCREMENT,
  `cardType` varchar(200) NOT NULL,
  `name_EN` varchar(200) NOT NULL,
  `name_DE` varchar(200) NOT NULL,
  `points` int(11) NOT NULL,
  `value` int(11) NOT NULL,
  `costs` int(11) NOT NULL,
  `plus_Action` int(11) DEFAULT NULL,
  `plus_Card` int(11) DEFAULT NULL,
  `plus_Money` int(11) DEFAULT NULL,
  `plus_Buy` int(11) DEFAULT NULL,
  `specialActionID` int(11) DEFAULT NULL,
  `defaultStackSize` int(11) DEFAULT NULL,
  PRIMARY KEY (`cardID`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

-- Exportiere Daten aus Tabelle dominiondb.cards: ~13 rows (ungefähr)
DELETE FROM `cards`;
/*!40000 ALTER TABLE `cards` DISABLE KEYS */;
INSERT INTO `cards` (`cardID`, `cardType`, `name_EN`, `name_DE`, `points`, `value`, `costs`, `plus_Action`, `plus_Card`, `plus_Money`, `plus_Buy`, `specialActionID`, `defaultStackSize`) VALUES
	(1, 'Action', 'Village', 'Dorf', 0, 0, 3, 2, 1, 0, 0, 0, 10);
INSERT INTO `cards` (`cardID`, `cardType`, `name_EN`, `name_DE`, `points`, `value`, `costs`, `plus_Action`, `plus_Card`, `plus_Money`, `plus_Buy`, `specialActionID`, `defaultStackSize`) VALUES
	(2, 'Action', 'Woodcutter', 'Holzfäller', 0, 0, 3, 0, 0, 2, 1, 0, 10);
INSERT INTO `cards` (`cardID`, `cardType`, `name_EN`, `name_DE`, `points`, `value`, `costs`, `plus_Action`, `plus_Card`, `plus_Money`, `plus_Buy`, `specialActionID`, `defaultStackSize`) VALUES
	(3, 'Action', 'Festival', 'Jahrmarkt', 0, 0, 5, 2, 0, 2, 1, 0, 10);
INSERT INTO `cards` (`cardID`, `cardType`, `name_EN`, `name_DE`, `points`, `value`, `costs`, `plus_Action`, `plus_Card`, `plus_Money`, `plus_Buy`, `specialActionID`, `defaultStackSize`) VALUES
	(6, 'Action', 'Laboratory', 'Laboratorium', 0, 0, 5, 1, 2, 0, 0, 0, 10);
INSERT INTO `cards` (`cardID`, `cardType`, `name_EN`, `name_DE`, `points`, `value`, `costs`, `plus_Action`, `plus_Card`, `plus_Money`, `plus_Buy`, `specialActionID`, `defaultStackSize`) VALUES
	(7, 'Action', 'Market', 'Markt', 0, 0, 5, 1, 1, 2, 1, 0, 10);
INSERT INTO `cards` (`cardID`, `cardType`, `name_EN`, `name_DE`, `points`, `value`, `costs`, `plus_Action`, `plus_Card`, `plus_Money`, `plus_Buy`, `specialActionID`, `defaultStackSize`) VALUES
	(8, 'Action', 'Smithy', 'Schmiede', 0, 0, 4, 0, 3, 0, 0, 0, 10);
INSERT INTO `cards` (`cardID`, `cardType`, `name_EN`, `name_DE`, `points`, `value`, `costs`, `plus_Action`, `plus_Card`, `plus_Money`, `plus_Buy`, `specialActionID`, `defaultStackSize`) VALUES
	(21, 'Money', 'Copper', 'Kupfer', 0, 1, 0, 0, 0, 0, 0, 0, 60);
INSERT INTO `cards` (`cardID`, `cardType`, `name_EN`, `name_DE`, `points`, `value`, `costs`, `plus_Action`, `plus_Card`, `plus_Money`, `plus_Buy`, `specialActionID`, `defaultStackSize`) VALUES
	(22, 'Money', 'Silver', 'Silber', 0, 2, 3, 0, 0, 0, 0, 0, 40);
INSERT INTO `cards` (`cardID`, `cardType`, `name_EN`, `name_DE`, `points`, `value`, `costs`, `plus_Action`, `plus_Card`, `plus_Money`, `plus_Buy`, `specialActionID`, `defaultStackSize`) VALUES
	(23, 'Money', 'Gold', 'Gold', 0, 3, 6, 0, 0, 0, 0, 0, 30);
INSERT INTO `cards` (`cardID`, `cardType`, `name_EN`, `name_DE`, `points`, `value`, `costs`, `plus_Action`, `plus_Card`, `plus_Money`, `plus_Buy`, `specialActionID`, `defaultStackSize`) VALUES
	(31, 'Point', 'Estate', 'Anwesen', 1, 0, 2, 0, 0, 0, 0, 0, 24);
INSERT INTO `cards` (`cardID`, `cardType`, `name_EN`, `name_DE`, `points`, `value`, `costs`, `plus_Action`, `plus_Card`, `plus_Money`, `plus_Buy`, `specialActionID`, `defaultStackSize`) VALUES
	(32, 'Point', 'Duchy', 'Herzogtum', 3, 0, 5, 0, 0, 0, 0, 0, 12);
INSERT INTO `cards` (`cardID`, `cardType`, `name_EN`, `name_DE`, `points`, `value`, `costs`, `plus_Action`, `plus_Card`, `plus_Money`, `plus_Buy`, `specialActionID`, `defaultStackSize`) VALUES
	(33, 'Point', 'Province', 'Provinz', 6, 0, 8, 0, 0, 0, 0, 0, 12);
INSERT INTO `cards` (`cardID`, `cardType`, `name_EN`, `name_DE`, `points`, `value`, `costs`, `plus_Action`, `plus_Card`, `plus_Money`, `plus_Buy`, `specialActionID`, `defaultStackSize`) VALUES
	(34, 'Action', 'Bazaar', 'Bazar', 0, 0, 5, 2, 1, 1, 0, 0, 10);
/*!40000 ALTER TABLE `cards` ENABLE KEYS */;

-- Exportiere Struktur von Tabelle dominiondb.highscore_list
DROP TABLE IF EXISTS `highscore_list`;
CREATE TABLE IF NOT EXISTS `highscore_list` (
  `scoreID` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(200) DEFAULT NULL,
  `points` int(11) DEFAULT NULL,
  PRIMARY KEY (`scoreID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- Exportiere Daten aus Tabelle dominiondb.highscore_list: ~0 rows (ungefähr)
DELETE FROM `highscore_list`;
/*!40000 ALTER TABLE `highscore_list` DISABLE KEYS */;
INSERT INTO `highscore_list` (`scoreID`, `username`, `points`) VALUES
	(1, 'TheLegend27', 12);
INSERT INTO `highscore_list` (`scoreID`, `username`, `points`) VALUES
	(2, 'MasterOfDesaster', 20);
/*!40000 ALTER TABLE `highscore_list` ENABLE KEYS */;

-- Exportiere Struktur von Tabelle dominiondb.version
DROP TABLE IF EXISTS `version`;
CREATE TABLE IF NOT EXISTS `version` (
  `version` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportiere Daten aus Tabelle dominiondb.version: ~0 rows (ungefähr)
DELETE FROM `version`;
/*!40000 ALTER TABLE `version` DISABLE KEYS */;
INSERT INTO `version` (`version`) VALUES
	('0.1');
