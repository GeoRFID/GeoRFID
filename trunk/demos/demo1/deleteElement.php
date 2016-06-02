<?php
	$elementoId=$_GET['id'];

	include_once("connection.php");	
	
	if(!$dbconn)
    {    
		echo "";    
	}	
	
	$result = pg_query($dbconn, "DELETE FROM \"GeoRFID\".\"Elemento\" WHERE id=" . $elementoId );

	if (!$result) {
	  echo "An error occurred.\n";
	  exit;
	}
	
	pg_close($dbconn);
?>
