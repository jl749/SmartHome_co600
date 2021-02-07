<?php
include("connect.php");
$conn = Connection();
 
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