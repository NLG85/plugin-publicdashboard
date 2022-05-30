
--
-- Structure for table componentdashboard_dashboard
--

DROP TABLE IF EXISTS componentdashboard_dashboard;
CREATE TABLE componentdashboard_dashboard (
id_dashboard int AUTO_INCREMENT,
id_bean varchar(255) default '' NOT NULL,
zone int default '0',
position int default '0',
PRIMARY KEY (id_dashboard)
);
