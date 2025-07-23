
-- User Table
CREATE TABLE IF NOT EXISTS `w_user` (
  `u_id` BIGINT NOT NULL AUTO_INCREMENT,
  `u_guid` VARCHAR(100),
  `u_alias` VARCHAR(200),
  `u_email` VARCHAR(200) NOT NULL,
  `u_password` VARCHAR(200),
  `u_is_email_varified` TINYINT(1),

  `created_by` VARCHAR(200),
  `created_date` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
  `updated_by` VARCHAR(200),
  `updated_date` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

  PRIMARY KEY (`u_id`),
  UNIQUE KEY `UK_USER_EMAIL` (`u_email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Endpoint Table
CREATE TABLE IF NOT EXISTS `w_endpoint` (
  `e_endpoint_id` BIGINT NOT NULL AUTO_INCREMENT,
  `e_endpoint_name` VARCHAR(255) NOT NULL,
  `e_description` VARCHAR(255) NOT NULL,
  `e_created_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),

  `created_by` VARCHAR(200),
  `created_date` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
  `updated_by` VARCHAR(200),
  `updated_date` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

  `u_user_id` BIGINT,

  PRIMARY KEY (`e_endpoint_id`),
  UNIQUE KEY `UK_ENDPOINT_NAME` (`e_endpoint_name`),
  KEY `FK_ENDPOINT_USER_ID` (`u_user_id`),
  CONSTRAINT `FK_ENDPOINT_USER_ID` FOREIGN KEY (`u_user_id`) REFERENCES `w_user` (`u_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Incoming Request Table
CREATE TABLE IF NOT EXISTS `w_incoming_request` (
  `r_request_id` BIGINT NOT NULL AUTO_INCREMENT,
  `r_method` VARCHAR(255),
  `r_headers` LONGTEXT,
  `r_body` LONGTEXT,
  `r_query_params` LONGTEXT,
  `r_path` LONGTEXT,
  `r_received_at` DATETIME(6),
  `r_ip_address` VARCHAR(255),

  `e_endpoint_id` BIGINT,

  `created_by` VARCHAR(200),
  `created_date` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
  `updated_by` VARCHAR(200),
  `updated_date` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

  PRIMARY KEY (`r_request_id`),
  KEY `FK_INCOMING_ENDPOINT_ID` (`e_endpoint_id`),
  CONSTRAINT `FK_INCOMING_ENDPOINT_ID` FOREIGN KEY (`e_endpoint_id`) REFERENCES `w_endpoint` (`e_endpoint_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


