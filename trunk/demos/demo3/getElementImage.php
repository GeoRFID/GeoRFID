<?php
	$elementoId=$_GET['id'];
		
	$w=$_GET['w'];
	$h=$_GET['h'];
	$imageSize="height:$h";
	$close=$_GET['c'];
	$link="getElementImage.php?id=$elementoId&w=$w&h=$h&c=1";
	
	include_once("connection.php");	
	
	if($close==1)
	{
		//$imageSize="";
		$link="javascript:window.close();";
	}
	
	if(!$dbconn)
    {    
		echo "";    
	}	
	
	$result = pg_query($dbconn, "SELECT imagen FROM \"GeoRFID\".\"Elemento\" WHERE id=" . $elementoId );

	if (!$result) {
	  echo "An error occurred.\n";
	  exit;
	}

	while ($row = pg_fetch_row($result)) {
		if($row[0]=="null" || $row[0]=="")
		{			
			echo "<img style=\"position: absolute; margin:auto; top:0; left:0; right:0; bottom:0; width:$w;\" alt=\"Embedded Image\" src=\"images/no-photo.jpg\"/>";	 								
		}
		else		
		{
			echo "<a target='blank' href='$link'><img style=\"position: absolute; margin:auto; top:0; left:0; right:0; bottom:0 ;$imageSize\" alt=\"Embedded Image\" src=\"data:image/png;base64,$row[0]\"/></a>";
		}
	}
	
	pg_close($dbconn);
?>