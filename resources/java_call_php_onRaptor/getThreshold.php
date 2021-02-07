<?php
include("connect.php");
$conn = Connection();
 
if(isset($_POST["houseID"])){
	$houseID = $_POST["houseID"];
	$sql = "SELECT * FROM System_Threshold WHERE HouseID='$houseID'";
	$result = mysqli_query($conn, $sql);
	
	if (mysqli_num_rows($result) > 0) {
		$row = mysqli_fetch_assoc($result);
		echo "TMP_set=".$row["TMP_set"]."\n";
		echo "Intruder_Alarm=".$row["Intruder_Alarm"];
	}
}
mysqli_close($conn);
?>