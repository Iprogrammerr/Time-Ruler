[![Build Status](https://travis-ci.com/Iprogrammerr/Time-Ruler.svg?branch=master)](https://travis-ci.com/Iprogrammerr/Time-Ruler)
[![Test Coverage](https://img.shields.io/codecov/c/github/iprogrammerr/time-ruler/master.svg)](https://codecov.io/gh/Iprogrammerr/Time-Ruler/branch/master)
# Time Ruler
Self-contained web application for taking charge of your time.

## Deployment
Everything is contained in a single executable jar. The only thing that is needed from the outside is MySQL(you obviously can change connector in pom.xml and make it work with any relational database) database. All you need to do is to create it from this script(src/test/resources/schema.sql):
```
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
```
## Running
```
mvn clean install
cd target
java -jar time-ruler-jar-with-dependencies.jar <path-to-configuration>
```
## Configuration
Path to configuration is optional, if not supplied src/main/resources/application.properties will be used. It is mostly self-explanatory:
```
#application process port
port=8080
database.user=time_ruler
database.password=time_ruler
jdbc-url=jdbc:mysql://localhost:3306/time_ruler?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
#credentials and configuration of smtp server
admin.email=a68c02e9232d83
admin.password=fd38406ed994ff
smtp.host=smtp.mailtrap.io
smtp.port=25
#it should be the same as ip/domain where app is hosted
emailsLinksBase=http://127.0.0.1:8080/
#emails configuration
signUpEmailSubject=Welcome to Time Ruler
signUpEmailTemplate=Congratulations!<br/> You have successfully signed up. Click this <a href=%s>link</a> to activate your account.
passwordResetEmailSubject=Password reset request
passwordResetEmailTemplate=We have received a request to reset your password. If you didn't issue it, simply ignore this message. \
  If you did, follow this <a href=%s>link</a>.
``` 
   