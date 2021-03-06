-- /*
--  * Licensed to the Apache Software Foundation (ASF) under one or more
--  * contributor license agreements.  See the NOTICE file distributed with
--  * this work for additional information regarding copyright ownership.
--  * The ASF licenses this file to You under the Apache License, Version 2.0
--  * (the "License"); you may not use this file except in compliance with
--  * the License.  You may obtain a copy of the License at
--  *
--  *    http://www.apache.org/licenses/LICENSE-2.0
--  *
--  * Unless required by applicable law or agreed to in writing, software
--  * distributed under the License is distributed on an "AS IS" BASIS,
--  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--  * See the License for the specific language governing permissions and
--  * limitations under the License.
--  *
--  */

CREATE TABLE IF NOT EXISTS applications (
  uuid varchar(50) PRIMARY KEY,
  appid varchar(100) NOT NULL,
  siteid varchar(100) DEFAULT NULL,
  apptype varchar(30) DEFAULT NULL,
  appmode varchar(10) DEFAULT NULL,
  jarpath varchar(255) DEFAULT NULL,
  appstatus  varchar(20) DEFAULT NULL,
  configuration mediumtext DEFAULT NULL,
  context mediumtext DEFAULT NULL,
  createdtime bigint(20) NOT NULL,
  modifiedtime  bigint(20) NOT NULL,
  UNIQUE INDEX `appid_UNIQUE` (`appid` ASC))
COMMENT = 'eagle application metadata';

CREATE TABLE IF NOT EXISTS sites (
  uuid varchar(50) PRIMARY KEY,
  siteid varchar(100) NOT NULL,
  sitename varchar(100) NOT NULL,
  description varchar(255) DEFAULT NULL,
  createdtime bigint(20) NOT NULL,
  modifiedtime  bigint(20) NOT NULL,
  UNIQUE INDEX `siteid_UNIQUE` (`siteid` ASC))
COMMENT = 'eagle site metadata';

CREATE TABLE IF NOT EXISTS `dashboards` (
  `uuid` VARCHAR(50) NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `description` VARCHAR(500) NOT NULL,
  `settings` mediumtext NULL,
  `charts` longtext NULL,
  `modifiedtime` BIGINT(20) NOT NULL,
  `createdtime` BIGINT(20) NOT NULL,
  `author` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
COMMENT = 'eagle dashboard metadata';

CREATE TABLE IF NOT EXISTS `policy_prototype` (
  `uuid` VARCHAR(50) NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `definition` longtext NOT NULL,
  `alertPublisherIds` VARCHAR(500) NULL,
  `modifiedtime` BIGINT(20) NOT NULL,
  `createdtime` BIGINT(20) NOT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE INDEX `policy_proto_UNIQUE` (`name` ASC))
COMMENT = 'eagle policy prototype metadata';