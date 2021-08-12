CREATE TABLE bloks_school (
	id SERIAL PRIMARY KEY,
	name VARCHAR(150) NOT NULL,
	email_extension VARCHAR(150) NOT NULL,
	population INT2 NOT NULL,
	state VARCHAR(85) NOT NULL,
	school_requirements INT2 NOT NULL,
	school_funding INT2 NOT NULL,
	date_of_registration DATE NOT NULL
);

CREATE TABLE bloks_user (
	id SERIAL PRIMARY KEY,
	blok_id Int4 NOT NULL,
	is_validated bool NOT NULL,
	name VARCHAR(150) NOT NULL,
	email VARCHAR(150) NOT NULL,
	elected_rank INT2 NOT NULL,
	password VARCHAR(100) NOT NULL,
	grade INT4 NOT NULL,
	relationship_status VARCHAR(100) NOT NULL,
	gender VARCHAR(50) NOT NULL,
	show_gender BOOL NOT NULL,
	biological_sex VARCHAR(50) NOT NULL,
	show_biological_sex BOOL NOT NULL,
	biography VARCHAR(1000) NOT NULL,
	date_of_registration DATE NOT NULL,
	profile_pic VARCHAR(650) NOT NULL
);

CREATE TABLE notification (
	id SERIAL PRIMARY KEY,
	to_id INT4 NOT NULL,
	type INT2 NOT NULL,
	from_id INT4 NOT NULL,
	event_time time NOT NULL,
	from_user_name VARCHAR(150) NOT NULL,
	from_user_email VARCHAR(150) NOT NULL,
	from_user_profile_pic VARCHAR(650) NOT NULL
);

CREATE TABLE blok_group (
	id SERIAL PRIMARY KEY,
	school INT4 NOT NULL,
	name VARCHAR(200) NOT NULL,
	type INT2 NOT NULL,
	icon VARCHAR(650) NOT NULL
);

CREATE TABLE group_membership (
	id SERIAL PRIMARY KEY,
	group_id INT4 NOT NULL,
	user_id INT4 NOT NULL,
	role INT2 NOT NULL
);

CREATE TABLE group_thread (
	id SERIAL PRIMARY KEY,
	group_id INT4 NOT NULL,
	user_id INT4 NOT NULL,
	root_thread INT4 NOT NULL,
	thread_title VARCHAR(200) NOT NULL,
	thread TEXT NOT NULL,
	thread_date timestamp with time zone NOT NULL
);

CREATE TABLE message (
	id SERIAL PRIMARY KEY,
	from_id INT4 NOT NULL,
	to_id INT4 NOT NULL,
	message VARCHAR(50000),
	date_of_message timestamp with time zone NOT NULL
);
