<html>
<head>
<link rel="stylesheet" href="css/setElementImage.css" />	
<script src="js/common.js"></script>
<?php 
	if (session_status() == PHP_SESSION_NONE) {
		session_start();
	}
	
	include_once("localization.php");
	setLanguage("en");
?>		
</head>
<body>
<form id="setElementImageUploadForm" action="" method="post" enctype="multipart/form-data">
    <div class="fullWidth panelheader center"><?php loc('selectImage'); ?></div>	
	<br>
	<br>	
    <div class="fullWidth center">
	<input type="file" name="fileToUpload" id="fileToUpload">
	<br>
	<br>	
	<br>	
    <input type="submit" value="<?php loc('updateImage'); ?>" name="submit">&nbsp;<input type="button" value="<?php loc('close'); ?>" onclick="javascript:cerrar();">
	</div>
</form>
</body>
<script src="js/setElementImage.js"></script>
</html>