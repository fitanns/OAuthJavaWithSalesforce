<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Auth with Salesforce</title>
</head>
<body>
	<form method="LINK" action="https://localhost:8443/TestOauth/oauth">
		<input type="submit" value="Auth">
	</form>
	<p>Access token: ${access_Token}.</p>
</body>
</html>