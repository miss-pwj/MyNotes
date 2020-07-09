/*
MySQL Data Transfer
Source Host: localhost
Source Database: notepad
Target Host: localhost
Target Database: notepad
Date: 2020/7/8 19:34:19
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for note
-- ----------------------------
CREATE TABLE `note` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `datetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `note` VALUES ('1', '今天', '今天天气不好，心情不好', '2020-07-08 01:51:48');
INSERT INTO `note` VALUES ('2', '明天', '明天要去玩游戏，希望运气好', '2020-07-08 01:55:30');
INSERT INTO `note` VALUES ('3', '星期天', '星期天，我们一起打游戏吧，游戏可好玩了。', '2020-07-08 03:49:37');
