/*dragon*/
CREATE TABLE User_Info (
Username varchar(20) NOT NULL PRIMARY KEY,
Password varchar(20) NOT NULL,
FirstName varchar(30) NOT NULL,
Surname varchar(30) NOT NULL,
RegTime timestamp NOT NULL,
PrevHash varchar(100) NOT NULL
);

/*penguin*/
CREATE TABLE User_Info (
	Username varchar(20) PRIMARY KEY,
	Password varchar(20) NOT NULL,
	FirstName varchar(30) NOT NULL,
	Surname varchar(30) NOT NULL,
	RegTime timestamp NOT NULL,
	PrevHash varchar(100) NOT NULL
);