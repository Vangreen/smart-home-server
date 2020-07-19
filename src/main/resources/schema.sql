create table if not exists configuration (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  ip VARCHAR(250) NOT NULL,
  device_name VARCHAR (250) not null,
  red INT not null,
  green INT not null,
  blue INT not null,
  state varchar (250) not null
);