<?php
	include_once "connection.php";
	
	if(!$dbconn)
    {    
		echo "";    
	}	
		
	$sql="SELECT * FROM (SELECT e.id, e.etiqueta, e.latitud, e.longitud, e.tipo, t.nombre FROM \"GeoRFID\".\"Elemento\" AS e INNER JOIN \"GeoRFID\".\"TipoElemento\" AS t on e.tipo=t.id) AS t1 LEFT OUTER JOIN  (SELECT \"id\" as \"tareaId\",\"elementoId\", \"tipoTareaId\", \"descripcion\" as \"descripcionTarea\", \"imagen\" as \"imagenTarea\", \"estadoId\" as \"estadoTareaId\"  FROM \"GeoRFID\".\"ElementoTarea\" WHERE \"estadoId\"<5) as t2 on t1.\"id\"=t2.\"elementoId\"";
	$result = pg_query($dbconn, $sql);

	if (!$result) {
	  echo "An error occurred.\n";
	  exit;
	}

	while ($row = pg_fetch_row($result)) {
		$elementId=$row[0];
		$tipoElementoId=$row[4];
	 
		echo "$row[0];$row[1];$row[2];$row[3];$row[4];$row[5];$row[6]|";		
			
		include "getElementProperties.php";
		echo "\n";
	}

   pg_close($dbconn);
?>
