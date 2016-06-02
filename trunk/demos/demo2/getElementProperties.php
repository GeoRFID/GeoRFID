<?php
		
	$result2 = pg_query($dbconn, "SELECT \"id\", c.\"tipoElementoId\", c.\"nombre\", c.\"tipo\",c.\"listaValoresId\", v.\"valor\" FROM \"GeoRFID\".\"CampoTipoElemento\" AS c LEFT OUTER JOIN \"GeoRFID\".\"ValorCampo\" AS v ON c.\"id\"=v.\"campoTipoElementoId\" AND v.\"elementoId\"=" . $elementId . " WHERE c.\"tipoElementoId\"= '" . $tipoElementoId . "' ORDER BY \"id\"");
	
	if (!$result2) {
	  echo "An error occurred.\n";
	  exit;
	}

	while ($row2 = pg_fetch_row($result2)) {
	  		
		echo  "$row2[0];$row2[1];$row2[2];$row2[3];$row2[4];$row2[5]|";
	 		
	}
?>
