CREATE TABLE IF NOT EXISTS `user_permission` (
  `id_user` bigint NOT NULL,
  `id_permission` bigint NOT NULL,
  KEY `FKo47t1we6do84ku6rb9jcey2s9` (`id_permission`),
  KEY `FKprpp02ivhe66b5nrc0a3a4lk8` (`id_user`),
  CONSTRAINT `FKo47t1we6do84ku6rb9jcey2s9` FOREIGN KEY (`id_permission`) REFERENCES `permission` (`id`),
  CONSTRAINT `FKprpp02ivhe66b5nrc0a3a4lk8` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;