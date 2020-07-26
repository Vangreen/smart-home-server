create table if not exists configuration (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  ip VARCHAR(250) NOT NULL,
  device_name VARCHAR (250) not null,
  red INT not null,
  green INT not null,
  blue INT not null,
  state varchar (250) not null,
  room varchar (250) not null,
  device_type varchar (250) not null
);

create table if not exists users(
id  identity,
login varchar (250) not null,
password varchar (250) not null,
admin varchar (250) not null
);

create table if not exists rooms(
id Int auto_increment primary key,
room varchar (250) not null
);