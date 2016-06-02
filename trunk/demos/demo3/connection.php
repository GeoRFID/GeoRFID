<?php
$dbconn = pg_connect("host= dbname= user= password=")
    or die('Could not connect: ' . pg_last_error());
?>