<?php
include("connect.php");
 
if(isset($_POST["houseID"])){
	$conn = Connection();
	$houseID = $_POST["houseID"];
	$PostCode = $_POST["PostCode"];
	$sql = "UPDATE System_Threshold SET PostCode='$PostCode' WHERE HouseID='$houseID'";
	
	if (mysqli_query($conn, $sql)) {
	  echo "Record updated successfully";
	} else {
	  echo "Error updating record: " . mysqli_error($conn);
	}
	
	mysqli_close($conn);
}
?>