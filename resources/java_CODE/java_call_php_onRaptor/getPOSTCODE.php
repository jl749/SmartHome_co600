<?php
include("connect.php");
 
if(isset($_POST["houseID"])){
	$conn = Connection();
	$houseID = $_POST["houseID"];
	$sql = "SELECT PostCode FROM System_Threshold WHERE HouseID='$houseID'";
	$result = mysqli_query($conn, $sql);
	
	if (mysqli_num_rows($result) > 0) {
		$row = mysqli_fetch_assoc($result);
		echo $row["PostCode"];
	}
	
	mysqli_close($conn);
}
?>