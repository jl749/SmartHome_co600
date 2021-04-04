<?php
$servername = "dragon.kent.ac.uk";
$username = "jl749";
$password = "p@ssword1";
$dbname = "jl749";

 
$conn = mysqli_connect($servername, $username, $password, $dbname);
if(!$conn) {
	die('MySQL ERROR: ' . mysql_error());
}

if(!isset($_POST["intruder"]) && isset($_POST["houseID"])){
	$houseID = $_POST["houseID"];
	$tmp = $_POST["tmp"];

	$sql = "UPDATE System_Threshold SET TMP_set='$tmp' WHERE HouseID='$houseID'";
	if (mysqli_query($conn, $sql)) {//$conn->query($sql)
	  echo "Record updated successfully";
	} else {
	  echo "Error updating record: " . mysqli_error($conn);
	}
}else if(!isset($_POST["tmp"]) && isset($_POST["houseID"])){
	$houseID = $_POST["houseID"];
	$intruder = $_POST["intruder"];
	$sql = "UPDATE System_Threshold SET Intruder_Alarm='$intruder' WHERE HouseID='$houseID'";
	if (mysqli_query($conn, $sql)) {//$conn->query($sql)
	  echo "Record updated successfully";
	} else {
	  echo "Error updating record: " . mysqli_error($conn);
	}
}

mysqli_close($conn);
?>