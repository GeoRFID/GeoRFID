<?php
	include_once "connection.php";
	
	if(!$dbconn)
    {    
		echo "";    
	}	
		
	$result = pg_query($dbconn, "SELECT id, valor FROM \"GeoRFID\".\"CampoListaValores\" ");

	if (!$result) {
	  echo "An error occurred.\n";
	  exit;
	}

	while ($row = pg_fetch_row($result)) {		
		echo "$row[0];$row[1]";			
		echo "\n";
	}

   pg_close($dbconn);
?>
