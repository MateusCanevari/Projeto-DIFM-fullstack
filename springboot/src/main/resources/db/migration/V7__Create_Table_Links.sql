CREATE TABLE IF NOT EXISTS `links` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `page_name` varchar(255) NOT NULL,
  `link` text NOT NULL,
  `create_at` datetime(6) NOT NULL,
  `expire_at` datetime(6) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi0o5fueopsnuoypws575u5yso` (`user_id`),
  CONSTRAINT `FKi0o5fueopsnuoypws575u5yso` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;