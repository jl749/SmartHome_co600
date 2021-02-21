<?php
include("connect.php");
 
if(isset($_POST["username"])){
	$conn = Connection();
	$username = $_POST["username"];
	$sql = "SELECT HouseID FROM User_Register WHERE Username='$username'";
	$result = mysqli_query($conn, $sql);
	
	while ($row = mysqli_fetch_assoc($result)) {
		echo $row["HouseID"]."\n";
	}
	
	mysqli_close($conn);
}
?>