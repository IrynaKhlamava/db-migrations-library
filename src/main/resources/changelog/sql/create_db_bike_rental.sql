-- MySQL Script generated by MySQL Workbench
-- Wed Jan 22 12:04:01 2025
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema bike_rental
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `bike_rental` DEFAULT CHARACTER SET utf8 ;
USE `bike_rental` ;

-- -----------------------------------------------------
-- Table `bike_rental`.`clients`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bike_rental`.`clients` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  UNIQUE INDEX `phone_UNIQUE` (`phone` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bike_rental`.`client_accounts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bike_rental`.`client_accounts` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `balance` DECIMAL(10,2) NULL DEFAULT 0.00,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `client_id` INT NOT NULL,
  PRIMARY KEY (`id`, `client_id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `fk_client_accounts_clients_idx` (`client_id` ASC) VISIBLE,
  CONSTRAINT `fk_client_accounts_clients`
    FOREIGN KEY (`client_id`)
    REFERENCES `bike_rental`.`clients` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bike_rental`.`locations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bike_rental`.`locations` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `address` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bike_rental`.`rates`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bike_rental`.`rates` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NULL,
  `price_per_hour` DECIMAL(5,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bike_rental`.`bikes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bike_rental`.`bikes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `model` VARCHAR(100) NOT NULL,
  `status` ENUM('available', 'in_use', 'maintenance') NULL DEFAULT 'available',
  `locations_id` INT NOT NULL,
  `rates_id` INT NOT NULL,
  PRIMARY KEY (`id`, `locations_id`, `rates_id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `fk_bikes_locations1_idx` (`locations_id` ASC) INVISIBLE,
  INDEX `fk_bikes_rates1_idx` (`rates_id` ASC) VISIBLE,
  CONSTRAINT `fk_bikes_locations1`
    FOREIGN KEY (`locations_id`)
    REFERENCES `bike_rental`.`locations` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_bikes_rates1`
    FOREIGN KEY (`rates_id`)
    REFERENCES `bike_rental`.`rates` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bike_rental`.`rentals`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bike_rental`.`rentals` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `clients_id` INT NOT NULL,
  `bikes_id` INT NOT NULL,
  `rental_start` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `rental_end` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `clients_id`, `bikes_id`),
  INDEX `fk_rentals_bikes1_idx` (`bikes_id` ASC) VISIBLE,
  INDEX `fk_rentals_clients1_idx` (`clients_id` ASC) INVISIBLE,
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  CONSTRAINT `fk_rentals_clients1`
    FOREIGN KEY (`clients_id`)
    REFERENCES `bike_rental`.`clients` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_rentals_bikes1`
    FOREIGN KEY (`bikes_id`)
    REFERENCES `bike_rental`.`bikes` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
