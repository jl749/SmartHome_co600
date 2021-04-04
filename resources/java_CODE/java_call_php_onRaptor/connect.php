<?php
function Connection(){
	$servername = "dragon.kent.ac.uk";
	$username = "jl749";
	$password = "p@ssword1";
	$dbname = "jl749";

	 
	$conn = mysqli_connect($servername, $username, $password, $dbname);
	if(!$conn) {
		die('MySQL ERROR: ' . mysql_error());
	}

	return $conn;
}
?>
