-- insert into t_drivers (id, username, phone_number) values (1, 'username1', '+443934909909');
insert into t_app_users(id, first_name, last_name, phone_number, password, app_user_role, enabled, locked)
    values (999, 'test_user', 'test_user_last_name', '12345678', '$2a$10$YjaovYQQritMhIoU67aVtO4dWGFLqzZX7t95gQv44MBfifPPN.4T.', 'USER', 'true', 'false');
insert into t_drivers(id) values (999);
insert into t_vehicles(id, name, color, registration_number, registered_at) values (777, 'dacia', 'white', 'RGR12321', now());
update t_drivers set fk_vehicle_id=777 where id = 999;