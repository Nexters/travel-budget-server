-- -----------------------------------------------------
-- Table `travel_budget_db`.`budget`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `travel_budget_db`.`budget`
(
    `id`             BIGINT   NOT NULL AUTO_INCREMENT,
    `purpose_budget` BIGINT   NOT NULL,
    `create_dt`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_dt`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `travel_budget_db`.`trip_plan`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `travel_budget_db`.`trip_plan`
(
    `id`             BIGINT          NOT NULL AUTO_INCREMENT,
    `name`           VARCHAR(255)    NOT NULL,
    `start_date`     DATE            NOT NULL,
    `end_date`       DATE            NOT NULL,
    `create_user_id` BIGINT          NOT NULL,
    `create_dt`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_user_id` BIGINT          NOT NULL,
    `update_dt`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_delete`      ENUM ('Y', 'N') NOT NULL,
    `budget_id`      BIGINT          NOT NULL,
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `travel_budget_db`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `travel_budget_db`.`user`
(
    `id`              BIGINT        NOT NULL AUTO_INCREMENT,
    `kakao_id`        BIGINT        NOT NULL,
    `nickname`        VARCHAR(40)   NOT NULL,
    `profile_image`   VARCHAR(1000) NOT NULL,
    `thumbnail_image` VARCHAR(1000) NOT NULL,
    `create_dt`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_dt`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `travel_budget_db`.`trip_member`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `travel_budget_db`.`trip_member`
(
    `id`           BIGINT   NOT NULL AUTO_INCREMENT,
    `create_dt`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_dt`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Member 1 budget 1 \n',
    `user_id`      BIGINT   NOT NULL,
    `trip_plan_id` BIGINT   NOT NULL,
    `budget_id`    BIGINT   NOT NULL,
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `travel_budget_db`.`payment_case`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `travel_budget_db`.`payment_case`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `price`          BIGINT       NOT NULL,
    `title`          VARCHAR(255) NOT NULL,
    `payment_dt`     DATETIME     NOT NULL,
    `create_user_id` BIGINT       NOT NULL,
    `create_dt`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_user_id` BIGINT       NOT NULL,
    `update_dt`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `category_id`    BIGINT       NOT NULL,
    `budget_id`      BIGINT       NOT NULL,
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;
