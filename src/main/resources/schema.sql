-- MySQL Script generated by MySQL Workbench
-- Вт 16 авг 2022 00:44:42
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`types`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`types` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`campuses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`campuses` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`booking_objects`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`booking_objects` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `type_id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  `image` VARCHAR(255) NOT NULL,
  `campus_id` INT NOT NULL,
  `floor` TINYINT(255) NOT NULL,
  `room_number` TINYINT(50) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `campus_idx` (`campus_id` ASC) VISIBLE,
  INDEX `type_idx` (`type_id` ASC) VISIBLE,
  CONSTRAINT `type`
    FOREIGN KEY (`type_id`)
    REFERENCES `mydb`.`types` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `campus`
    FOREIGN KEY (`campus_id`)
    REFERENCES `mydb`.`campuses` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `mydb`.`roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`roles` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `role_id` INT NOT NULL,
  `login` VARCHAR(20) NOT NULL,
  `campus_id` INT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `role_idx` (`role_id` ASC) VISIBLE,
  INDEX `campus_idx` (`campus_id` ASC) VISIBLE,
  CONSTRAINT `role`
    FOREIGN KEY (`role_id`)
    REFERENCES `mydb`.`roles` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `campus`
    FOREIGN KEY (`campus_id`)
    REFERENCES `mydb`.`campuses` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`statuses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`statuses` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`boоkings`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`bookings` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `time_start` DATETIME(18) NOT NULL,
  `time_end` DATETIME(18) NOT NULL,
  `status_id` INT NOT NULL,
  `booking_object_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `status_idx` (`status_id` ASC) VISIBLE,
  INDEX `user_idx` (`user_id` ASC) VISIBLE,
  INDEX `book_obj_idx` (`booking_object_id` ASC) VISIBLE,
  CONSTRAINT `status`
    FOREIGN KEY (`status_id`)
    REFERENCES `mydb`.`statuses` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `user`
    FOREIGN KEY (`user_id`)
    REFERENCES `mydb`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `book_obj`
    FOREIGN KEY (`booking_object_id`)
    REFERENCES `mydb`.`booking_objects` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
