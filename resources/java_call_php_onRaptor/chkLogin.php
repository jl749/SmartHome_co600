<?php
$servername = "dragon.kent.ac.uk";
$username = "jl749";
$password = "p@ssword1";
$dbname = "jl749";

 
$conn = mysqli_connect($servername, $username, $password, $dbname);
if(!$conn) {
	die('MySQL ERROR: ' . mysql_error());
}
 
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
}
mysqli_close($conn);
?>