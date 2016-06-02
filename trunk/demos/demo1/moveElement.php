<?php
	$elementoId=$_GET['id'];
	$lat=$_GET['lat'];
	$lon=$_GET['lon'];

	include_once("connection.php");	
	
	if(!$dbconn)
    {    
		echo "";    
	}	
		
	$result = pg_query($dbconn, "UPDATE \"GeoRFID\".\"Elemento\" SET latitud='" . $lat . "', longitud='" . $lon . "' WHERE id=" . $elementoId );

	if (!$result) {
	  echo "An error occurred.\n";
	  exit;
	}
	
	pg_close($dbconn);
?>
