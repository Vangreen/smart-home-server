create table if not exists configuration (
    serial INT PRIMARY KEY,
    device_name VARCHAR (250) not null,
    hue INT not null,
    sat INT not null,
    bright INT not null,
    state varchar (250) not null,
    status varchar (250) not null,
    roomID INT not null,
    device_type varchar (250) not null
);

create table if not exists unassignedDevices (
    serial INT PRIMARY KEY,
    device_type varchar (250) not null
);

create table if not exists roomConfiguration (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_name varchar (250) not null,
    main VARCHAR (250) not null
    );


create table if not exists sceneryConfiguration (
    id int AUTO_INCREMENT PRIMARY KEY,
--     user_id int not null,
    room_id int not null,
    scenery_name varchar (250) not null,
    logo varchar (250) not null,
    scenery_status varchar (250) not null
);

create table if not exists deviceConfigurationInSceneries (
    id int AUTO_INCREMENT PRIMARY KEY,
    scenery_id int not null,
    device_serial int not null,
    hue INT not null,
    sat INT not null,
    bright INT not null,
    state varchar (250) not null
)
