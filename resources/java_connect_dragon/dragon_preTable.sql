/*dragon*/
CREATE TABLE User_Info (
Username varchar(20) NOT NULL PRIMARY KEY,
Password varchar(100) NOT NULL,
FirstName varchar(30) NOT NULL,
Surname varchar(30) NOT NULL,
RegTime timestamp DEFAULT CURRENT_TIMESTAMP,
PrevHash varchar(100) UNIQUE
);

# Insert Statement 1
INSERT INTO User_Info (Username, Password, FirstName, Surname, RegTime, PrevHash)
VALUES ("John98", "19513FDC9DA4FB72A4A05EB66917548D3C90FF94D5419E1F2363EEA89DFEE1DD", "John", "Jones", '2021-01-01 16:29:59', "0");


# Password without hash is Password1


# Insert Statement 2
INSERT INTO User_Info (Username, Password, FirstName, Surname, RegTime, PrevHash)
VALUES ("James89", "1BE0222750AAF3889AB95B5D593BA12E4FF1046474702D6B4779F4B527305B23", "James", "Smith", '2021-01-02 16:29:59', "1FF22836D1A0EC54ACDF64D575DD61FA75C3CBE13190DCDB27462B238B02FA56");


# Password without hash is Password2
# PrevHash is Sha 256 of concatenated values from the Insert Statement 1
#SHA256(John9819513FDC9DA4FB72A4A05EB66917548D3C90FF94D5419E1F2363EEA89DFEE1DDJohnJones2021-01-01 16:29:59)



# Insert Statement 3
INSERT INTO User_Info (Username, Password, FirstName, Surname, RegTime, PrevHash)
VALUES ("Waqas97", "2538F153F36161C45C3C90AFAA3F9CCC5B0FA5554C7C582EFE67193ABB2D5202", "Waqas", "Khan", '2021-01-03 12:13:32', "BD09080C310CFEB11942759315A90D9EE91CE1BEC4CF0BBF895DE5A3B34C66EB");


# Password without hash is Password3
# PrevHash is Sha 256 of concatenated values from the Insert Statement 2
#SHA256(James891BE0222750AAF3889AB95B5D593BA12E4FF1046474702D6B4779F4B527305B23JamesSmith2021-01-02 16:29:59)

CREATE TABLE User_Register (
Username varchar(20) NOT NULL,
HouseID int NOT NULL,
FOREIGN KEY (Username) REFERENCES User_Info(Username) ON DELETE CASCADE,
FOREIGN KEY (HouseID) REFERENCES System_Threshold(HouseID) ON DELETE CASCADE,
PRIMARY KEY (Username, HouseID)
);

INSERT INTO User_Register VALUES("John98",1234);
INSERT INTO User_Register VALUES("James89",1234);
INSERT INTO User_Register VALUES("Waqas97",1234);


CREATE TABLE System_Threshold (
HouseID int NOT NULL PRIMARY KEY,
TMP_set double DEFAULT 12.0,
Intruder_Alarm boolean DEFAULT FALSE
);

INSERT INTO System_Threshold (HouseID) VALUES (1234);