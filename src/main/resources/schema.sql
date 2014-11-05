create table `config`(
	`id` int(11) not null primary key auto_increment,
	`name` varchar(100) not null,
	`value` text not null,
	unique key `uidx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
