<?php
include("connect.php");
$conn = Connection();
 
if(isset($_POST["id"]) && $_POST["pass"]){
	$id = $_POST["id"];
	$pass = $_POST["pass"];

	$sql = "SELECT * FROM User_Info WHERE Username='$id' AND Password='$pass'";
	$result = mysqli_query($conn, $sql); //$conn->query($sql)

	if (mysqli_num_rows($result) > 0) { //$result->num_rows
		echo "True";
	}else{
		echo "False";
	}

	mysqli_close($conn);
}
?>