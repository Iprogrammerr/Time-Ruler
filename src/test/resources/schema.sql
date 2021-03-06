CREATE TABLE user (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	email VARCHAR(50) NOT NULL UNIQUE,
	name VARCHAR(50) NOT NULL UNIQUE,
	password VARCHAR(255) NOT NULL,
	active TINYINT NOT NULL DEFAULT 0,
	PRIMARY KEY (id)
);

CREATE TABLE activity (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	user_id INT UNSIGNED NOT NULL,
	name VARCHAR(100) NOT NULL,
	start_date BIGINT UNSIGNED NOT NULL DEFAULT 0,
	end_date BIGINT UNSIGNED NOT NULL DEFAULT 0,
	done TINYINT NOT NULL DEFAULT 0,
	PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE description (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	activity_id INT UNSIGNED NOT NULL UNIQUE,
	content TEXT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (activity_id) REFERENCES activity(id) ON DELETE CASCADE
);