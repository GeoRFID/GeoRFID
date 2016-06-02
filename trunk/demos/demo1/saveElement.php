<?php
	$elementoId=$_GET['id'];	

	parse_str($_SERVER["QUERY_STRING"], $query_array);
	
	include_once("connection.php");	
	
	if(!$dbconn)
    {    
		echo "";    
	}	
	
	$sql='';
	
	foreach($query_array as $key=> $value)
	{			
		if($key!='id')
		{
			$sql= $sql . "UPDATE \"GeoRFID\".\"ValorCampo\" SET valor='$value' WHERE \"elementoId\"='$elementoId' AND \"campoTipoElementoId\"='" .  preg_replace('/[\x00-\x1F\x80-\xFF]/', '', $key) . "';";				
		}
	}
		
	echo $sql;
	$result = pg_query($dbconn,$sql);	

	if (!$result) {
	  echo "An error occurred.\n";
	  exit;
	}
	
	pg_close($dbconn);
?>
