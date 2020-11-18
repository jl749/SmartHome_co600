/*dragon*/
CREATE TABLE User_Info (
Username varchar(20) NOT NULL,
Password varchar(20) NOT NULL,
FirstName varchar(30) NOT NULL,
Surname varchar(30) NOT NULL,
TimeOfReg timestamp NOT NULL,
IntegrityCheck varchar(100) NOT NULL
PRIMARY KEY (Username));

/*penguin*/
CREATE TABLE User_Info (
	Username varchar(20) PRIMARY KEY,
	Password varchar(20) NOT NULL,
	FirstName varchar(30) NOT NULL,
	Surname varchar(30) NOT NULL,
	TimeOfReg timestamp NOT NULL,
	IntegrityCheck varchar(100) NOT NULL
);