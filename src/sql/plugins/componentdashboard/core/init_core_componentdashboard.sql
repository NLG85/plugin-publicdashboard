
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'COMPONENTDASHBOARD_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('COMPONENTDASHBOARD_MANAGEMENT','publicdashboard.adminFeature.ManageDashboard.name',1,'jsp/admin/plugins/publicdashboard/ManageDashboards.jsp','publicdashboard.adminFeature.ManageDashboard.description',0,'componentdashboard',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'COMPONENTDASHBOARD_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('COMPONENTDASHBOARD_MANAGEMENT',1);

