use `computer-database-db`;
 
drop table if exists authorities;
drop table if exists users;

create table users(
	username varchar(50) not null primary key,
	password varchar(100) not null,
  	enabled TINYINT NOT NULL DEFAULT 1
);

create table authorities (
	user_role_id bigint not null auto_increment,
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint pk_authorities primary key (user_role_id)
);

alter table authorities add constraint fk_authorities_users foreign key (username) references users (username);

create unique index ix_auth_username on authorities (username);

insert into users(username,password, enabled)
	values('admin','admin', 1);
insert into authorities(username,authority) 
	values('admin','ROLE_ADMIN');

insert into users(username,password, enabled)
	values('user','user', 1);
insert into authorities(username,authority) 
	values('user','ROLE_USER');

