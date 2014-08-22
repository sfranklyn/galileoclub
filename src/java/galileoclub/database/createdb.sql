create database if not exists galileoclub;
create user 'galileoclub'@'localhost' identified by 'galileoclub';
grant all on galileoclub.* to 'galileoclub'@'localhost';