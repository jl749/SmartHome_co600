<?php
function integrityChk($hash, $prevHash) {
  	$result=[];
	$nodeIndex=-1;
	//find genesis node
	for($j=0 ; $j<sizeof($prevHash) ; $j++){
		if($prevHash[$j]==0){
			$nodeIndex = $j;
			array_push($result, $j);
			break;
		}
	}
	if(empty($result)){
		echo "something wrong with the genesis node\n";
		return null;
	}
	
	$flag = 0;
	do{
		$flag = 0;
		//loop $prevHash find $hash[$nodeIndex]
		for($i=0 ; $i<sizeof($prevHash) ; $i++){
			if(in_array($i, $result)){
				continue;
			}
			//echo "\n$prevHash[$i] compare $hash[$nodeIndex]\n";
			if($prevHash[$i] == $hash[$nodeIndex]){
				//echo "\nHash match at index $i\n";
				$nodeIndex = $i;
				$flag = 1;
				array_push($result, $i);
				break;
			}
		}
	}while($flag == 1);
	
	print_r($result);
	//if(sizeof($result) == sizeof($hash))
		return $result;
	//return null;
} 

include("connect.php");

if(isset($_POST["username"]) && isset($_POST["N"]) && isset($_POST["FN"]) && isset($_POST["pass"])){
	$conn = Connection();
	$sql = "SELECT * FROM User_Info";
	$rows=[];
	$hash = [];	$prevHash = [];
	if($result = mysqli_query($conn, $sql)){
		$rows = mysqli_fetch_all($result);
		foreach($rows as $row){
			$str="";
			for($i=0 ; $i<5 ; $i++){
				$str.=$row[$i];
			}
			array_push($prevHash, $row[5]);
			//print_r($str."\n\n");
			array_push($hash, strtoupper(hash('sha256', $str)));
		}
		mysqli_free_result($result);
	}
	//print_r($hash);
	//print_r($prevHash);

	$order = integrityChk($hash, $prevHash);

	//$sql = "DELETE FROM User_Info WHERE Username = (SELECT Username FROM (SELECT Username FROM User_Info LIMIT $offset,1) AS t)";
	//delete missing link nodes
	for($i=0 ; $i<sizeof($rows) ; $i++){
		if(in_array($i, $order)){
			continue;
		}
		$sql = "DELETE FROM User_Info WHERE Username = (SELECT Username FROM (SELECT Username FROM User_Info LIMIT $i,1) AS t)";
		if(!mysqli_query($conn, $sql))
			echo "error removing missing link node at index $i";
	}
	
	$username = $_POST["username"];
	$pass = hash('sha256', $_POST["pass"]);
	$name = $_POST["N"];
	$familyN = $_POST["FN"];
	$timestamp = date('Y-m-d H:i:s');

	$tmp = $hash[$order[sizeof($order)-1]];
	$sql = "INSERT INTO User_Info VALUES ('$username', '$pass', '$name', '$familyN', '$timestamp', '$tmp')";
	if(!mysqli_query($conn, $sql))
		echo "INSERT fail, VALUES ('$username', '$pass', '$name', '$familyN', '$timestamp', '$tmp')";
	else
		echo "INSERT SUCCESS!";
	mysqli_close($conn);
}
?>