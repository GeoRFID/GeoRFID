<?php
$target_dir = "uploads/";
$target_file = $target_dir . basename($_FILES["fileToUpload"]["name"]);
$uploadOk = 1;
$imageFileType = pathinfo($target_file,PATHINFO_EXTENSION);
// Check if image file is a actual image or fake image
if(isset($_POST["submit"])) {
    $check = getimagesize($_FILES["fileToUpload"]["tmp_name"]);
    if($check !== false) {
        //echo "File is an image - " . $check["mime"] . ".";
        $uploadOk = 1;
    } else {
        echo "File is not an image.";
        $uploadOk = 0;
    }
	
	$data = base64_encode(file_get_contents($_FILES["fileToUpload"]["tmp_name"]));
		
	$elementoId=$_GET['id'];	
		
	include_once("connection.php");	
	
	if(!$dbconn)
    {    
		echo "";    
	}	

	$sql= "UPDATE \"GeoRFID\".\"Elemento\" SET imagen='$data' WHERE \"id\"=$elementoId";
	
	$result = pg_query($dbconn,$sql);

	if (!$result) {
	  echo "An error occurred.\n";
	  exit;
	}
	
	pg_close($dbconn);
	
	echo "<html><body><script type='text/javascript'>parent.imagenActualizada();</script></body></html>";
}
?>