<?php
include("connect.php");

if(!isset($_POST["intruder"]) && isset($_POST["houseID"])){
	$conn = Connection();
	$houseID = $_POST["houseID"];
	$tmp = $_POST["tmp"];

	$sql = "UPDATE System_Threshold SET TMP_set='$tmp' WHERE HouseID='$houseID'";
	if (mysqli_query($conn, $sql)) {//$conn->query($sql)
	  echo "Record updated successfully";
	} else {
	  echo "Error updating record: " . mysqli_error($conn);
	}
	
	mysqli_close($conn);
}else if(!isset($_POST["tmp"]) && isset($_POST["houseID"])){
	$conn = Connection();
	$houseID = $_POST["houseID"];
	$intruder = $_POST["intruder"];
	$sql = "UPDATE System_Threshold SET Intruder_Alarm='$intruder' WHERE HouseID='$houseID'";
	if (mysqli_query($conn, $sql)) {//$conn->query($sql)
	  echo "Record updated successfully";
	} else {
	  echo "Error updating record: " . mysqli_error($conn);
	}
	
	mysqli_close($conn);
}
?>