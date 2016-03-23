-- MySQL dump 10.13  Distrib 5.6.28, for debian-linux-gnu (i686)
--
-- Host: localhost    Database: dbo
-- ------------------------------------------------------
-- Server version	5.6.28-0ubuntu0.15.10.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `GroupUsers`
--

DROP TABLE IF EXISTS `GroupUsers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GroupUsers` (
  `groupUserID` int(11) NOT NULL AUTO_INCREMENT,
  `groupID` int(11) NOT NULL,
  `userID` int(11) NOT NULL,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`groupUserID`),
  UNIQUE KEY `groupUserID_UNIQUE` (`groupUserID`),
  KEY `FK__userID_idx` (`userID`),
  KEY `FK__groupID_idx` (`groupID`),
  CONSTRAINT `FK__GroupUsers_groupID` FOREIGN KEY (`groupID`) REFERENCES `Groups` (`groupID`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK__GroupUsers_userID` FOREIGN KEY (`userID`) REFERENCES `Users` (`userID`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GroupUsers`
--

LOCK TABLES `GroupUsers` WRITE;
/*!40000 ALTER TABLE `GroupUsers` DISABLE KEYS */;
/*!40000 ALTER TABLE `GroupUsers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Groups`
--

DROP TABLE IF EXISTS `Groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Groups` (
  `groupID` int(11) NOT NULL,
  `userID` int(11) NOT NULL,
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`groupID`),
  UNIQUE KEY `groupID_UNIQUE` (`groupID`),
  KEY `FK__Groups_userID_idx` (`userID`),
  CONSTRAINT `FK__Groups_userID` FOREIGN KEY (`userID`) REFERENCES `Users` (`userID`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Groups`
--

LOCK TABLES `Groups` WRITE;
/*!40000 ALTER TABLE `Groups` DISABLE KEYS */;
INSERT INTO `Groups` VALUES (1,11,'2016-02-22 00:59:04','2016-02-22 00:59:04');
/*!40000 ALTER TABLE `Groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Message`
--

DROP TABLE IF EXISTS `Message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Message` (
  `msgID` int(11) NOT NULL AUTO_INCREMENT,
  `src_userID` int(11) NOT NULL COMMENT 'User ID of user that sent the message',
  `groupID` int(11) DEFAULT NULL,
  `sendTime` datetime NOT NULL COMMENT 'Time message was sent from user. (Independent from time that message was inserted into the database)',
  `message` varchar(45) NOT NULL COMMENT 'Message content',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Time that tuple was inserted into the table',
  `read` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`msgID`),
  UNIQUE KEY `msgID_UNIQUE` (`msgID`),
  KEY `FK__src_userID_idx` (`src_userID`),
  CONSTRAINT `FK__Message_src_userID` FOREIGN KEY (`src_userID`) REFERENCES `Users` (`userID`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Message`
--

LOCK TABLES `Message` WRITE;
/*!40000 ALTER TABLE `Message` DISABLE KEYS */;
/*!40000 ALTER TABLE `Message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Recipients`
--

DROP TABLE IF EXISTS `Recipients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Recipients` (
  `recipientID` int(11) NOT NULL,
  `messageID` int(11) NOT NULL,
  `seen` tinyint(1) DEFAULT '0',
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`recipientID`,`messageID`),
  KEY `FK__Message_Recipients_messageID` (`messageID`),
  CONSTRAINT `FK__Message_Recipients_messageID` FOREIGN KEY (`messageID`) REFERENCES `Message` (`msgID`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK__Users_Recipients_recipientID` FOREIGN KEY (`recipientID`) REFERENCES `Users` (`userID`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Recipients`
--

LOCK TABLES `Recipients` WRITE;
/*!40000 ALTER TABLE `Recipients` DISABLE KEYS */;
/*!40000 ALTER TABLE `Recipients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Users` (
  `userID` int(11) NOT NULL AUTO_INCREMENT,
  `fname` varchar(20) NOT NULL,
  `lname` varchar(20) NOT NULL,
  `email` varchar(45) NOT NULL,
  `pw` varchar(45) NOT NULL,
  `phone` varchar(12) NOT NULL,
  `profilePic` varchar(45) NOT NULL,
  `salt` varchar(45) NOT NULL,
  `verified` tinyint(1) NOT NULL DEFAULT '0',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userID`),
  UNIQUE KEY `userID_UNIQUE` (`userID`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Users`
--

LOCK TABLES `Users` WRITE;
/*!40000 ALTER TABLE `Users` DISABLE KEYS */;
INSERT INTO `Users` VALUES (11,'aaron','salinas','aaron_salinas@baylor.edu','password','12344325124','aaron.jpg','salt',0,'2016-02-22 00:56:35','2016-02-22 00:56:35');
/*!40000 ALTER TABLE `Users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'dbo'
--
/*!50003 DROP PROCEDURE IF EXISTS `addUser` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `addUser`(IN fname VARCHAR(20), IN lname VARCHAR(20), IN email VARCHAR(45),
IN pw VARCHAR(45), IN salt VARCHAR(45), IN phone VARCHAR(12), IN pic VARCHAR(45))
BEGIN
INSERT INTO `dbo`.`Users`(`fname`, `lname`, `email`, `pw`, `phone`, `profilePic`)
    VALUES(fname, lname, email, pw, salt, phone, pic);
    CALL updateUserPassword(email, pw);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `allUsers` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `allUsers`()
BEGIN
SELECT `fname`, `lname`, `email`, `phone`, `verified`
    FROM`dbo`.`Users`;
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `deleteUser` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteUser`(IN email VARCHAR(45))
BEGIN
SET @usr = (SELECT `userID`
FROM `dbo`.`Users` usr2
WHERE email = usr2.`email`);
              
DELETE FROM `dbo`.`Users`
    WHERE `userID` = @usr;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getUser` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUser`(IN email VARCHAR(45))
BEGIN
SELECT `fname`, `lname`, `email`, `phone`
    FROM `dbo`.`Users`
    Where `email` = email;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `logIn` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `logIn`(IN email VARCHAR(45), IN pass VARCHAR(45), OUT uid INT(11))
BEGIN
    SELECT `userID` into uid FROM `dbo`.`Users` WHERE email = `email` and pw = SHA2(CONCAT(pass, `salt`), 256);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sendMessage` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sendMessage`(IN sender INT(11), IN recipient VARCHAR(45), IN message VARCHAR(45), IN send_time VARCHAR(45))
BEGIN
        SET @recipientID = (SELECT `userId` from `Users` where `email` like recipient);
        INSERT INTO `dbo`.`Message` (`src_userID`, `groupID`, `sendTime`, `message`) VALUES (sender, NULL, sendTime, message);
        SET @mid = (SELECT msgID from Message where src_userID = sender order by sendTime desc limit 1);
        INSERT INTO Recipients (recipientID, messageID) VALUES (@recipientID, @mid);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `updateUserPassword` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateUserPassword`(IN email VARCHAR(45), IN pw VARCHAR(45))
BEGIN
    SET @uID = (SELECT `userID` FROM `dbo`.`Users` WHERE `email` = email);
    set @salt = uuid();
    set @hash = SHA2(CONCAT(pw, @salt), 256);
    UPDATE `dbo`.`Users`
    set `pw` = @hash, `salt` = @salt
    WHERE  `userID` = @uID;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `verifyUser` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `verifyUser`(IN email VARCHAR(45))
BEGIN
SET @uID = (SELECT `userID` FROM `dbo`.`Users` WHERE email = `email`);
UPDATE `dbo`.`Users`
    SET `verified` = 1
    WHERE `userId` = @uID;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-03-22  8:48:44
