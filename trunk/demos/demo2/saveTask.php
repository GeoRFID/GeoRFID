<?php
	$elementoId=$_GET['id'];	
	$estadoId=$_GET['stateid'];	

	parse_str($_SERVER["QUERY_STRING"], $query_array);
	
	include_once("connection.php");	
	
	if(!$dbconn)
    {    
		echo "";    
	}		
	
	$sql= "UPDATE \"GeoRFID\".\"ElementoTarea\" SET \"estadoId\"='$estadoId' WHERE \"id\"='$elementoId';";						
		
	echo $sql;
	$result = pg_query($dbconn,$sql);	

	if (!$result) {
	  echo "An error occurred.\n";
	  exit;
	}
	
	pg_close($dbconn);
?>
