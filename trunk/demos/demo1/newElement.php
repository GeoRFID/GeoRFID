<?php	
	$lat=$_GET['lat'];
	$lon=$_GET['lon'];
	$tipoElemento=$_GET['tipo'];

	include_once("connection.php");	
	
	if(!$dbconn)
    {    
		echo "";    
	}	
		
		echo "SELECT MAX(\"id\") FROM \"GeoRFID\".\"Elemento\"";
	$result = pg_query($dbconn, "SELECT MAX(\"id\") FROM \"GeoRFID\".\"Elemento\"");

	while ($row = pg_fetch_row($result))
	{
		$nextId=$row[0];
	}
	
	echo $nextId;
	$nextId = $nextId + 1;
	echo $nextId;
	
	echo "INSERT INTO \"GeoRFID\".\"Elemento\"(\"id\",\"etiqueta\",\"latitud\",\"longitud\",\"tipo\") VALUES (" . $nextId . ",''," . $lat . "," . $lon . "," . $tipoElemento . ")";
	$result = pg_query($dbconn, "INSERT INTO \"GeoRFID\".\"Elemento\"(\"id\",\"etiqueta\",\"latitud\",\"longitud\",\"tipo\") VALUES (" . $nextId . ",''," . $lat . "," . $lon . "," . $tipoElemento . ")");

	if (!$result) {
	  echo "An error occurred.\n";
	  exit;
	}
	
	pg_close($dbconn);
?>
