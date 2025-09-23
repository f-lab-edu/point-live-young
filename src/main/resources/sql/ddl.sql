-- user
CREATE TABLE `user`
(
    `id`            INT          NOT NULL AUTO_INCREMENT,
    `user_name`     VARCHAR(50)  NOT NULL,
    `user_email`    VARCHAR(100) NOT NULL,
    `user_password` VARCHAR(100) NOT NULL,
    `user_role`     VARCHAR(10)  NOT NULL,
    `created_at`    DATETIME(0)  NOT NULL,
    `birthDate`     DATE         NOT NULL,
    `refresh_token` VARCHAR(500) NULL,
    `last_login_at` DATETIME(0)  NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_email` (`user_email`),
    CONSTRAINT `ck_user_role` CHECK (`user_role` IN ('USER', 'ADMIN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- product
CREATE TABLE `product`
(
    `id`           INT          NOT NULL AUTO_INCREMENT,
    `product_name` VARCHAR(100) NOT NULL,
    `description`  TEXT NULL,
    `stock`        INT          NOT NULL,
    `price`        INT          NOT NULL,
    `category`     VARCHAR(30)  NOT NULL,
    `is_deleted`   BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (`id`),
    CONSTRAINT `ck_product_stock` CHECK (`stock` >= 0),
    CONSTRAINT `ck_product_price` CHECK (`price` > 0),
    CONSTRAINT `ck_product_category` CHECK (`category` IN (
                                                           'DIGITAL_GIFT',
                                                           'CAFE_SNACK',
                                                           'FOOD_CONVENIENCE',
                                                           'BEAUTY_HEALTH_CARE',
                                                           'LIVING_SHOPPING'
        ))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- point_policy
CREATE TABLE `point_policy`
(
    `id`              INT         NOT NULL AUTO_INCREMENT,
    `policy_type`     VARCHAR(50) NOT NULL,
    `expiration_days` INT NULL,
    `is_activation`   TINYINT(1) NOT NULL DEFAULT 1,
    `point_amount`    INT         NOT NULL,
    PRIMARY KEY (`id`),
    -- 엔티티에 unique=true 이므로 유지
    UNIQUE KEY `uk_point_policy_type` (`policy_type`),
    CONSTRAINT `ck_point_policy_amount_pos` CHECK (`point_amount` >= 1),
    CONSTRAINT `ck_point_policy_exp_days` CHECK (`expiration_days` IS NULL OR `expiration_days` >= 7)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- user_point_lot
CREATE TABLE `user_point_lot`
(
    `id`              INT         NOT NULL AUTO_INCREMENT,
    `point_balance`   INT         NOT NULL,
    `created_at`      DATETIME(0) NOT NULL,
    `expiration_at`   DATETIME(0) NULL,
    `status`          VARCHAR(20) NOT NULL,
    `point_policy_id` INT         NOT NULL,
    `user_id`         INT         NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_upl_policy` FOREIGN KEY (`point_policy_id`) REFERENCES `point_policy` (`id`)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT `fk_upl_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT `ck_upl_point_balance_nonneg` CHECK (`point_balance` >= 0),
    CONSTRAINT `ck_upl_status` CHECK (`status` IN ('ACTIVE', 'CANCELLED', 'EXPIRED', 'USED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- order
CREATE TABLE `order`
(
    `id`             INT         NOT NULL AUTO_INCREMENT,
    `purchase_price` INT         NOT NULL,
    `quantity`       INT         NOT NULL,
    `status`         VARCHAR(10) NOT NULL,
    `created_at`     DATETIME(0) NOT NULL,
    `expired_at`     DATETIME(0) NOT NULL,
    `product_id`     INT         NOT NULL,
    `user_id`        INT         NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_order_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT `ck_order_price_pos` CHECK (`purchase_price` > 0),
    CONSTRAINT `ck_order_quantity_pos` CHECK (`quantity` > 0),
    CONSTRAINT `ck_order_status` CHECK (`status` IN ('COMPLETED', 'CANCELED', 'EXPIRED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- order_item
CREATE TABLE `order_item`
(
    `id`                INT          NOT NULL AUTO_INCREMENT,
    `product_code`      VARCHAR(200) NOT NULL,
    `price_at_purchase` INT          NOT NULL,
    `order_id`          INT          NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_item_product_code` (`product_code`),
    CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT `ck_order_item_price_pos` CHECK (`price_at_purchase` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- order_point_usage
CREATE TABLE `order_point_usage`
(
    `id`                INT NOT NULL AUTO_INCREMENT,
    `order_id`          INT NOT NULL,
    `user_point_lot_id` INT NOT NULL,
    `used_amount`       INT NOT NULL,
    `created_at`        DATETIME(0) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_opu_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT `fk_opu_lot` FOREIGN KEY (`user_point_lot_id`) REFERENCES `user_point_lot` (`id`)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT `ck_opu_used_amount_pos` CHECK (`used_amount` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- 전문 검색 인덱스
ALTER TABLE `product`
    ADD FULLTEXT KEY `idx_ft_product_name_desc` (`product_name`, `description`) WITH PARSER ngram;