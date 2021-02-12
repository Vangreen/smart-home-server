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
