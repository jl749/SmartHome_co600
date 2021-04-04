<?php
function Connection(){
	$servername = "localhost";
	$username = "root";
	$password = "raspberry";
	$dbname = "dragon";

	 
	$conn = mysqli_connect($servername, $username, $password, $dbname);
	if(!$conn) {
		die('MySQL ERROR: ' . mysql_error());
	}

	return $conn;
}
?>
