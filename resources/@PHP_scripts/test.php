<?php
include("connect.php");

$conn = Connection();
$sql = "SELECT * FROM System_Threshold WHERE HouseID=1234";
$result = mysqli_query($conn, $sql);

if (mysqli_num_rows($result) > 0) {
	$row = mysqli_fetch_assoc($result);
	echo "TMP_set=".$row["TMP_set"]."\n";
	echo "Intruder_Alarm=".$row["Intruder_Alarm"]."\n";
	echo "Lock1=".$row["Lock1"];
}

mysqli_close($conn);

?>
