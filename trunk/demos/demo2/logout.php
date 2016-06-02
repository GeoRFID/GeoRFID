<?php 
    session_start(); 

	$link = 'index.php?l=' . $_SESSION["lang"];
	
    session_destroy(); 
  
    header('location: ' . $link); 
?>