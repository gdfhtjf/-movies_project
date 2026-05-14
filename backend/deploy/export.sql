mysqldump: [Warning] Using a password on the command line interface can be insecure.
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: movie_db
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `movie_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `movie_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `movie_db`;

--
-- Table structure for table `favorites`
--

DROP TABLE IF EXISTS `favorites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorites` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `movie_id` int NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_movie` (`user_id`,`movie_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_movie_id` (`movie_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorites`
--

LOCK TABLES `favorites` WRITE;
/*!40000 ALTER TABLE `favorites` DISABLE KEYS */;
INSERT INTO `favorites` VALUES (3,4,23,'2026-05-09 21:59:35'),(4,4,22,'2026-05-13 19:30:45');
/*!40000 ALTER TABLE `favorites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `genres`
--

DROP TABLE IF EXISTS `genres`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `genres` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=123 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `genres`
--

LOCK TABLES `genres` WRITE;
/*!40000 ALTER TABLE `genres` DISABLE KEYS */;
INSERT INTO `genres` VALUES (3,'剧情'),(1,'动作'),(8,'动画'),(12,'历史'),(2,'喜剧'),(9,'奇幻'),(105,'姝︿緺'),(7,'恐怖'),(6,'悬疑'),(11,'战争'),(13,'武侠'),(101,'濂囧够'),(5,'爱情'),(10,'犯罪'),(4,'科幻'),(14,'纪录片'),(96,'绉戝够'),(106,'绾綍鐗�'),(95,'鍓ф儏'),(93,'鍔ㄤ綔'),(100,'鍔ㄧ敾'),(104,'鍘嗗彶'),(94,'鍠滃墽'),(99,'鎭愭��'),(98,'鎮枒'),(103,'鎴樹簤'),(97,'鐖辨儏'),(102,'鐘姜'),(107,'闊充箰'),(15,'音乐');
/*!40000 ALTER TABLE `genres` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hall_layouts`
--

DROP TABLE IF EXISTS `hall_layouts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hall_layouts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `hall_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rows_num` int NOT NULL DEFAULT '8',
  `cols_num` int NOT NULL DEFAULT '12',
  `layout_json` text COLLATE utf8mb4_unicode_ci COMMENT 'JSON: {aisles:[], exits:[], blocked:[]}',
  PRIMARY KEY (`id`),
  UNIQUE KEY `hall_number` (`hall_number`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hall_layouts`
--

LOCK TABLES `hall_layouts` WRITE;
/*!40000 ALTER TABLE `hall_layouts` DISABLE KEYS */;
INSERT INTO `hall_layouts` VALUES (1,'1号厅',8,12,NULL),(2,'2号厅',8,12,NULL),(3,'3号厅',8,12,NULL),(4,'4号厅',6,10,NULL),(5,'5号厅',6,10,NULL),(6,'IMAX厅',12,18,NULL),(7,'VIP厅',5,8,NULL),(45,'1鍙峰巺',8,12,NULL),(46,'2鍙峰巺',8,12,NULL),(47,'3鍙峰巺',8,12,NULL),(48,'4鍙峰巺',6,10,NULL),(49,'5鍙峰巺',6,10,NULL),(50,'IMAX鍘�',12,18,NULL),(51,'VIP鍘�',5,8,NULL);
/*!40000 ALTER TABLE `hall_layouts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movie_genre`
--

DROP TABLE IF EXISTS `movie_genre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie_genre` (
  `id` int NOT NULL AUTO_INCREMENT,
  `movie_id` int NOT NULL,
  `genre_id` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie_genre`
--

LOCK TABLES `movie_genre` WRITE;
/*!40000 ALTER TABLE `movie_genre` DISABLE KEYS */;
/*!40000 ALTER TABLE `movie_genre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movies`
--

DROP TABLE IF EXISTS `movies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movies` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `director` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock` int DEFAULT '100',
  `poster_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `trailer_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `cast` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `release_date` date DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `genre` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movies`
--

LOCK TABLES `movies` WRITE;
/*!40000 ALTER TABLE `movies` DISABLE KEYS */;
INSERT INTO `movies` VALUES (1,'流浪地球3','郭帆',39.90,200,'1778249966962_83557eca.webp','trailer_liulang3.mp4','科幻史诗','吴京',NULL,133,'科幻','2026-05-07 15:31:29','2026-05-07 20:56:39'),(3,'飞驰人生3','韩寒',35.00,498,'1778249972558_2d014103.webp','trailer_feichirensheng3.mp4','昔日冠军车手张驰受邀作为车队主教练，带领新老朋友共同征战被称为“亚洲最难洲际赛道”的“沐尘100拉力赛”。面对严苛规则与自我极限的双重挑战，他们用速度与热爱，书写人生赛道上新的飞驰篇章。',NULL,NULL,NULL,NULL,'2026-05-07 15:31:29',NULL),(22,'流浪地球2','郭帆',49.90,101,'1778247116302_c0f1f9f4.webp','trailer_liulang2.mp4','太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新的家园。然而宇宙之路危机四伏，为了拯救地球，流浪地球时代的年轻人再次挺身而出。','吴京,李雪健,沙溢,宁理,王智','2023-01-22',173,'科幻,冒险','2026-05-08 09:33:11','2026-05-10 09:15:30'),(23,'满江红','张艺谋',45.00,80,'1778247163362_d3413a2c.webp','trailer_manjianghong.mp4','南宋绍兴年间，岳飞死后四年，秦桧率兵与金国会谈。会谈前夜，金国使者死在宰相驻地，所携密信不翼而飞。','沈腾,易烊千玺,张译,雷佳音,岳云鹏','2023-01-22',159,'悬疑,喜剧','2026-05-08 09:33:11','2026-05-08 09:33:11'),(24,'长津湖','陈凯歌,徐克,林超贤',42.00,90,'1778247195910_501d1b56.webp','trailer_changjinhu.mp4','1950年，中国人民志愿军赴朝作战，在极寒严酷环境下，东线作战部队凭着钢铁意志和英勇无畏的战斗精神一路追击，奋勇杀敌。','吴京,易烊千玺,段奕宏,朱亚文,李晨','2021-09-30',176,'战争,历史','2026-05-08 09:33:11','2026-05-08 09:33:11'),(25,'你好，李焕英','贾玲',38.00,75,'1778247223250_d4e79bec.webp','trailer_lihuanying.mp4','2001年的某一天，刚刚考上大学的贾晓玲经历了人生中的一次大起大落。一心想要成为母亲骄傲的她却因母亲突遭严重意外而悲痛万分。','贾玲,张小斐,沈腾,陈赫,刘佳','2021-02-12',128,'喜剧,剧情','2026-05-08 09:33:11','2026-05-08 09:33:11'),(26,'唐人街探案3','陈思诚',40.00,85,'1778247252329_9803b6cc.webp','trailer_tangren3.mp4','继曼谷、纽约之后，东京再出大案。唐人街神探唐仁、秦风受侦探野田昊的邀请前往破案。','王宝强,刘昊然,妻夫木聪,托尼·贾,长泽雅美','2021-02-12',136,'喜剧,悬疑','2026-05-08 09:33:11','2026-05-08 09:33:11'),(27,'我不是药神','文牧野',35.00,95,'1778249983502_9964343b.webp','trailer_yaoshen.mp4','一位不速之客的意外到访，打破了神油店老板程勇的平凡人生，他从一个交不起房租的男性保健品商贩，一跃成为印度仿制药“格列宁”的独家代理商。','徐峥,王传君,周一围,谭卓,章宇','2018-07-05',117,'剧情,喜剧','2026-05-08 09:33:11','2026-05-08 09:33:11'),(28,'哪吒之魔童降世','饺子',39.00,100,'1778249959238_a91e59d4.webp','trailer_nezha.mp4','天地灵气孕育出一颗能量巨大的混元珠，元始天尊将混元珠提炼成灵珠和魔丸，灵珠投胎为人，助周伐纣时可堪大用；而魔丸则会诞出魔王，为祸人间。','吕艳婷,囧森瑟夫,瀚墨,陈浩,绿绮','2019-07-26',110,'动画,奇幻','2026-05-08 09:33:11','2026-05-08 09:33:11'),(29,'流浪地球','郭帆',38.00,88,'liu-lang-di-qiu.jpg','trailer_liulang.mp4','太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新的家园。','吴京,屈楚萧,李光洁,吴孟达,赵今麦','2019-02-05',125,'科幻,冒险','2026-05-08 09:33:11','2026-05-08 09:33:11'),(30,'长津湖之水门桥','徐克,陈凯歌,林超贤',48.00,85,'1778247338668_99ef19cb.webp','trailer_shuimenqiao.mp4','以抗美援朝战争第二次战役中的长津湖战役为背景，讲述七连战士们在结束了新兴里和下碣隅里的战斗之后，又接到了更艰巨的任务。','吴京,易烊千玺,朱亚文,李晨,段奕宏','2022-02-01',149,'战争,历史','2026-05-08 09:33:11','2026-05-08 09:33:11'),(31,'独行月球','张吃鱼',42.00,90,'1778247375443_9fb258f6.webp','trailer_duxingyueqiu.mp4','人类为抵御小行星的撞击，拯救地球，在月球部署了月盾计划。陨石提前来袭，全员紧急撤离时，维修工独孤月因为意外，错过了领队马蓝星的撤离通知，一个人落在了月球。','沈腾,马丽,常远,李诚儒,黄才伦','2022-07-29',122,'喜剧,科幻','2026-05-08 09:33:11','2026-05-08 09:33:11'),(32,'这个杀手不太冷静','邢文雄',39.00,80,'sha-shou-bu-tai-leng-jing.jpg','trailer_shashou.mp4','魏成功非常热爱表演，然而只是个跑龙套的小演员。一个偶然的机会，他被女演员米兰邀请假扮“杀手卡尔”，从此开启了笑中带泪的经历。','马丽,魏翔,陈明昊,周大勇,黄才伦','2022-02-01',109,'喜剧,剧情','2026-05-08 09:33:11','2026-05-08 09:33:11'),(33,'万里归途','饶晓志',45.00,85,'1778247403692_70f57c56.webp','trailer_wanliguitu.mp4','努米亚共和国爆发战乱，前驻地外交官宗大伟与外交部新人成朗受命前往协助撤侨。','张译,王俊凯,殷桃,成泰燊,张子贤','2022-09-30',137,'战争,剧情','2026-05-08 09:33:11','2026-05-08 09:33:11'),(34,'消失的她','崔睿,刘翔',44.00,90,'1778247433476_a135e334.webp','trailer_xiaoshideta.mp4','何非的妻子李木子在结婚周年旅行中离奇消失，在何非苦寻无果之时，妻子再次现身，何非却坚持眼前的陌生女人并非妻子。','朱一龙,倪妮,文咏珊,杜江,黄子琪','2023-06-22',122,'悬疑,剧情','2026-05-08 09:33:11','2026-05-08 09:33:11'),(35,'孤注一掷','申奥',46.00,88,'1778249995520_051f58d6.webp','trailer_guzhuyizhi.mp4','诈骗工厂主管陆秉坤招揽潘生等人出国，他们实则是被拐卖到诈骗工厂进行网络诈骗的受害者。','张艺兴,金晨,咏梅,王传君,王大陆','2023-08-08',130,'犯罪,剧情','2026-05-08 09:33:11','2026-05-08 09:33:11');
/*!40000 ALTER TABLE `movies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_logs`
--

DROP TABLE IF EXISTS `operation_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `method` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `uri` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `operation_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `args_count` int DEFAULT NULL,
  `elapsed_ms` bigint DEFAULT NULL,
  `status` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_logs`
--

LOCK TABLES `operation_logs` WRITE;
/*!40000 ALTER TABLE `operation_logs` DISABLE KEYS */;
INSERT INTO `operation_logs` VALUES (1,'陈磊','DELETE','/api/favorites/22','删除','127.0.0.1',2,7,'成功','2026-05-13 17:39:12'),(2,'陈磊','POST','/api/orders','创建','127.0.0.1',2,20,'成功','2026-05-13 17:39:50'),(3,'陈磊','PUT','/api/orders/44','更新','127.0.0.1',2,12,'成功','2026-05-13 17:40:37'),(4,'陈磊','POST','/api/favorites/22','创建','127.0.0.1',2,12,'成功','2026-05-13 19:30:44'),(5,'陈磊','POST','/api/auth/logout','创建','127.0.0.1',2,9,'成功','2026-05-13 19:31:02'),(6,'匿名用户','POST','/api/auth/login','创建','127.0.0.1',5,87,'成功','2026-05-13 19:31:17');
/*!40000 ALTER TABLE `operation_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `movie_id` int NOT NULL COMMENT '电影ID',
  `screening_id` int NOT NULL COMMENT '场次ID',
  `seat_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '座位号',
  `total_price` decimal(10,2) NOT NULL COMMENT '总价',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'paid',
  `version` int DEFAULT '1',
  `cancel_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `movie_id` (`movie_id`),
  KEY `idx_screening_status` (`screening_id`,`status`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`),
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`screening_id`) REFERENCES `screenings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (39,4,23,33,'4排5座',45.00,'2026-05-09 18:27:32','paid',1,NULL),(40,4,23,33,'6排4座,6排5座,6排6座',135.00,'2026-05-09 20:27:28','paid',1,NULL),(41,4,1,24,'5排5座,5排6座',79.80,'2026-05-10 09:53:27','paid',1,NULL),(42,4,22,26,'2排4座,2排6座',99.80,'2026-05-10 10:06:47','paid',1,NULL),(43,4,25,35,'2排6座',38.00,'2026-05-13 09:47:25','TICKETED',1,NULL),(44,4,24,64,'1排6座,2排6座',84.00,'2026-05-13 17:39:51','paid',1,NULL);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `movie_id` int NOT NULL,
  `rating` int NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `reviews_chk_1` CHECK (((`rating` >= 1) and (`rating` <= 5)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `screenings`
--

DROP TABLE IF EXISTS `screenings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `screenings` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '场次ID',
  `movie_id` int NOT NULL COMMENT '关联电影ID',
  `hall_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '影厅号',
  `show_time` datetime DEFAULT NULL,
  `total_seats` int NOT NULL COMMENT '总座位数',
  `remaining_seats` int NOT NULL COMMENT '剩余座位数',
  `version` int DEFAULT '1',
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `show_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `movie_id` (`movie_id`),
  CONSTRAINT `screenings_ibfk_1` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场次表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `screenings`
--

LOCK TABLES `screenings` WRITE;
/*!40000 ALTER TABLE `screenings` DISABLE KEYS */;
INSERT INTO `screenings` VALUES (24,1,'3号厅',NULL,120,118,2,'2026-05-08 16:00:00','2026-05-08 18:43:00',39.90,'AVAILABLE','2026-05-08 16:00:00'),(25,3,'2号厅',NULL,100,100,1,'2026-05-08 09:30:00','2026-05-08 12:00:00',35.00,'AVAILABLE','2026-05-08 09:30:00'),(26,22,'5号厅',NULL,60,58,2,'2026-05-08 16:00:00','2026-05-08 19:23:00',49.90,'AVAILABLE','2026-05-08 16:00:00'),(27,23,'2号厅',NULL,80,80,1,'2026-05-08 21:30:00','2026-05-09 00:39:00',45.00,'AVAILABLE','2026-05-08 21:30:00'),(28,24,'2号厅',NULL,100,100,1,'2026-05-08 13:00:00','2026-05-08 16:26:00',42.00,'AVAILABLE','2026-05-08 13:00:00'),(29,25,'2号厅',NULL,100,100,1,'2026-05-08 09:30:00','2026-05-08 12:08:00',38.00,'AVAILABLE','2026-05-08 09:30:00'),(30,1,'3号厅',NULL,120,120,1,'2026-05-09 09:30:00','2026-05-09 12:13:00',39.90,'AVAILABLE','2026-05-09 09:30:00'),(31,3,'3号厅',NULL,120,120,1,'2026-05-09 21:30:00','2026-05-10 00:00:00',35.00,'AVAILABLE','2026-05-09 21:30:00'),(32,22,'VIP厅',NULL,30,30,1,'2026-05-09 09:30:00','2026-05-09 12:53:00',49.90,'AVAILABLE','2026-05-09 09:30:00'),(33,23,'IMAX厅',NULL,200,196,3,'2026-05-09 13:00:00','2026-05-09 16:09:00',45.00,'AVAILABLE','2026-05-09 13:00:00'),(34,24,'4号厅',NULL,90,90,1,'2026-05-09 16:00:00','2026-05-09 19:26:00',42.00,'AVAILABLE','2026-05-09 16:00:00'),(35,25,'IMAX厅',NULL,200,199,2,'2026-05-09 19:00:00','2026-05-09 21:38:00',38.00,'AVAILABLE','2026-05-09 19:00:00'),(36,26,'5号厅',NULL,60,60,1,'2026-05-09 21:30:00','2026-05-10 00:16:00',40.00,'AVAILABLE','2026-05-09 21:30:00'),(37,1,'2号厅',NULL,100,100,1,'2026-05-10 19:00:00','2026-05-10 21:43:00',39.90,'AVAILABLE','2026-05-10 19:00:00'),(38,3,'IMAX厅',NULL,200,200,1,'2026-05-10 09:30:00','2026-05-10 12:00:00',35.00,'AVAILABLE','2026-05-10 09:30:00'),(39,22,'5号厅',NULL,60,60,1,'2026-05-10 09:30:00','2026-05-10 12:53:00',49.90,'AVAILABLE','2026-05-10 09:30:00'),(40,23,'4号厅',NULL,90,90,1,'2026-05-10 16:00:00','2026-05-10 19:09:00',45.00,'AVAILABLE','2026-05-10 16:00:00'),(41,24,'2号厅',NULL,100,100,1,'2026-05-10 19:00:00','2026-05-10 22:26:00',42.00,'AVAILABLE','2026-05-10 19:00:00'),(42,25,'1号厅',NULL,80,80,1,'2026-05-10 21:30:00','2026-05-11 00:08:00',38.00,'AVAILABLE','2026-05-10 21:30:00'),(43,26,'3号厅',NULL,120,120,1,'2026-05-10 19:00:00','2026-05-10 21:46:00',40.00,'AVAILABLE','2026-05-10 19:00:00'),(44,1,'3号厅',NULL,120,120,1,'2026-05-11 13:00:00','2026-05-11 15:43:00',39.90,'AVAILABLE','2026-05-11 13:00:00'),(45,3,'3号厅',NULL,120,120,1,'2026-05-11 19:00:00','2026-05-11 21:30:00',35.00,'AVAILABLE','2026-05-11 19:00:00'),(46,22,'VIP厅',NULL,30,30,1,'2026-05-11 21:30:00','2026-05-12 00:53:00',49.90,'AVAILABLE','2026-05-11 21:30:00'),(47,23,'VIP厅',NULL,30,30,1,'2026-05-11 13:00:00','2026-05-11 16:09:00',45.00,'AVAILABLE','2026-05-11 13:00:00'),(48,24,'2号厅',NULL,100,100,1,'2026-05-11 21:30:00','2026-05-12 00:56:00',42.00,'AVAILABLE','2026-05-11 21:30:00'),(49,1,'IMAX厅',NULL,200,200,1,'2026-05-12 13:00:00','2026-05-12 15:43:00',39.90,'AVAILABLE','2026-05-12 13:00:00'),(50,3,'1号厅',NULL,80,80,1,'2026-05-12 21:30:00','2026-05-13 00:00:00',35.00,'AVAILABLE','2026-05-12 21:30:00'),(51,22,'3号厅',NULL,120,120,1,'2026-05-12 09:30:00','2026-05-12 12:53:00',49.90,'AVAILABLE','2026-05-12 09:30:00'),(52,23,'IMAX厅',NULL,200,200,1,'2026-05-12 09:30:00','2026-05-12 12:39:00',45.00,'AVAILABLE','2026-05-12 09:30:00'),(53,24,'1号厅',NULL,80,80,1,'2026-05-12 19:00:00','2026-05-12 22:26:00',42.00,'AVAILABLE','2026-05-12 19:00:00'),(54,1,'2号厅',NULL,100,100,1,'2026-05-13 09:30:00','2026-05-13 12:13:00',39.90,'AVAILABLE','2026-05-13 09:30:00'),(56,22,'2号厅',NULL,100,100,1,'2026-05-13 21:30:00','2026-05-14 00:53:00',49.90,'AVAILABLE','2026-05-13 21:30:00'),(57,23,'IMAX厅',NULL,200,200,1,'2026-05-13 19:00:00','2026-05-13 22:09:00',45.00,'AVAILABLE','2026-05-13 19:00:00'),(58,24,'1号厅',NULL,80,80,1,'2026-05-13 09:30:00','2026-05-13 12:56:00',42.00,'AVAILABLE','2026-05-13 09:30:00'),(59,25,'4号厅',NULL,90,90,1,'2026-05-13 21:30:00','2026-05-14 00:08:00',38.00,'AVAILABLE','2026-05-13 21:30:00'),(60,1,'4号厅',NULL,90,90,1,'2026-05-14 19:00:00','2026-05-14 21:43:00',39.90,'AVAILABLE','2026-05-14 19:00:00'),(61,3,'3号厅',NULL,120,120,1,'2026-05-14 09:30:00','2026-05-14 12:00:00',35.00,'AVAILABLE','2026-05-14 09:30:00'),(62,22,'3号厅',NULL,120,120,1,'2026-05-14 09:30:00','2026-05-14 12:53:00',49.90,'AVAILABLE','2026-05-14 09:30:00'),(63,23,'IMAX厅',NULL,200,200,1,'2026-05-14 16:00:00','2026-05-14 19:09:00',45.00,'AVAILABLE','2026-05-14 16:00:00'),(64,24,'5号厅',NULL,60,58,2,'2026-05-14 16:00:00','2026-05-14 19:26:00',42.00,'AVAILABLE','2026-05-14 16:00:00'),(65,25,'4号厅',NULL,90,90,1,'2026-05-14 21:30:00','2026-05-15 00:08:00',38.00,'AVAILABLE','2026-05-14 21:30:00');
/*!40000 ALTER TABLE `screenings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spring_session`
--

DROP TABLE IF EXISTS `spring_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `spring_session` (
  `PRIMARY_ID` char(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `SESSION_ID` char(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `CREATION_TIME` bigint NOT NULL,
  `LAST_ACCESS_TIME` bigint NOT NULL,
  `MAX_INACTIVE_INTERVAL` int NOT NULL,
  `EXPIRY_TIME` bigint NOT NULL,
  `PRINCIPAL_NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`PRIMARY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spring_session`
--

LOCK TABLES `spring_session` WRITE;
/*!40000 ALTER TABLE `spring_session` DISABLE KEYS */;
/*!40000 ALTER TABLE `spring_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spring_session_attributes`
--

DROP TABLE IF EXISTS `spring_session_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `spring_session_attributes` (
  `SESSION_PRIMARY_ID` char(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ATTRIBUTE_NAME` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ATTRIBUTE_BYTES` blob NOT NULL,
  PRIMARY KEY (`SESSION_PRIMARY_ID`,`ATTRIBUTE_NAME`),
  CONSTRAINT `SPRING_SESSION_ATTRIBUTES_FK` FOREIGN KEY (`SESSION_PRIMARY_ID`) REFERENCES `spring_session` (`PRIMARY_ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spring_session_attributes`
--

LOCK TABLES `spring_session_attributes` WRITE;
/*!40000 ALTER TABLE `spring_session_attributes` DISABLE KEYS */;
/*!40000 ALTER TABLE `spring_session_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `student_id` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reset_token` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reset_token_expire` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Admin','$2a$10$PgNI/ZKaT21JX0mc35Duu.sTTWLNQnghQV7yQgdfBnf20eUtoTuFC','admin','2026-05-07 15:30:34',NULL,NULL,NULL,NULL,NULL),(2,'张三','$2a$10$LP6F/Xh/7/Gc1/oaiO0pNOxw5KudexQcdMUFyLtJrhV1390PoMYd2','user','2026-05-07 15:30:34',NULL,NULL,NULL,NULL,NULL),(4,'陈磊','$2a$10$XWrrb5c.s7ZKGu8kgf/V/uPhZX3GOad9b5SyQsghP3GONn2Ji/v5e','admin','2026-05-07 15:30:34',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'movie_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-14 17:13:56
