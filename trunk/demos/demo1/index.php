<?php 
	if (session_status() == PHP_SESSION_NONE) {
		session_start();
	}

	include_once("localization.php");

	$msg = '';
				
	if (isset($_GET['l'])) {
		$lang=$_GET['l'];
		setLanguage($lang);
	}
		
	

if (isset($_POST['login']) && !empty($_POST['username']) 
   && !empty($_POST['password'])) {
	
	include_once("connection.php");
	
	if(!$dbconn)
	{    
		echo "";    
	}	
		
	$user= $_POST['username'];
	$password= md5($_POST['password']);			
		
	$sql="SELECT \"nombre\", \"tipoUsuario\" FROM \"GeoRFID\".\"Usuario\" WHERE usuario='$user' AND contrasena='$password'";				
	$result = pg_query($dbconn, $sql);

	if (!$result) {
	  echo "An error occurred.\n";
	  exit;
	}			
	
	$nombre="";
	$tipoUsuario=0;
	
	while ($row = pg_fetch_row($result)) {
		$nombre=$row[0];
		$tipoUsuario=$row[1];
		$_SESSION["nombre"] = $nombre;
		$_SESSION["tipoUsuario"] = $tipoUsuario;
	}

	pg_close($dbconn);		
		
	if ($nombre!="")
	{
		header("Location: default.php?l=" . $_SESSION["lang"]);
	}else {
		$msg = locPhp('userNoValid');		
	}
}
?>
<html>
     <head>          
          <title>GeoRFID</title>
          <meta charset="utf-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0" />
          <link rel="stylesheet" href="css/main.css" />
		  <link rel="stylesheet" href="css/login.css" />		  
		  <script src="js/common.js"></script>		
		  <link rel="icon" type="image/png" href="images/georfid.ico">
     </head>
	 
     <body class="indexBackground">
			<div class="header">
				<img class='headerLogo' src='images/GeoRFIDlogo.png' />
				<div class='languages'>
					<a href="javascript:setLanguage('en')">en</a>
					<a href="javascript:setLanguage('es')">es</a>
					<a href="javascript:setLanguage('eu')">eu</a>
				</div>
			</div>
		</div>
          <div class="loginDiv centerDiv">
		  <div class="loginHeader">
          <img src="images/account_menu.png" border="0" style="vertical-align:middle;" />&nbsp;&nbsp;&nbsp;<?php loc('login'); ?>
		  </div>
			  <form action="<?php echo htmlspecialchars($_SERVER['PHP_SELF']);?>" method = "post">
				  <div class="loginFields">
				  <p>
					   <input type="textbox" name="username" placeholder="<?php loc('user'); ?>" class="fullWidth" value="demo"/>
				  </p>
				  <p>
					   <input type="password" name="password" placeholder="<?php loc('password'); ?>" class="fullWidth" value="demo"/>
				  </p>
				  <p class="right">
					<input type="submit" value="<?php loc('login'); ?>" class="button" name="login"/>
				  </p>
				  <?php echo $msg; ?>
			  </form>
		  </div>
		  </div>
			<div id="divOpencartis" class="opencartis">
				<a target="blank" href='http://www.opencartis.com'><img class='opencartis' src='images/opencartis.png' /></a>
			</div>
     </body>
</html>
