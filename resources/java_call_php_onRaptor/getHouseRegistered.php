<?php
$servername = "dragon.kent.ac.uk";
$username = "jl749";
$password = "p@ssword1";
$dbname = "jl749";

 
$conn = mysqli_connect($servername, $username, $password, $dbname);
if(!$conn) {
	die('MySQL ERROR: ' . mysql_error());
}
 
if(isset($_POST["username"])){
	$username = $_POST["username"];
	$sql = "SELECT HouseID FROM User_Register WHERE Username='$username'";
	$result = mysqli_query($conn, $sql);
	
	while ($row = mysqli_fetch_assoc($result)) {
		echo $row["HouseID"]."\n";
	}
}
mysqli_close($conn);
?>