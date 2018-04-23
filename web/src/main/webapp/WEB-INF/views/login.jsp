<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<!DOCTYPE html>
<html>
<head>
<title>Computer Database</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="css/font-awesome.css" rel="stylesheet" media="screen">
<link href="css/main.css" rel="stylesheet" media="screen">
</head>
<body>
	<header class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<a class="navbar-brand" href="dashboard"> Application - Computer
				Database </a>
			<div class="pull-right">
				<a href="?langue=fr">FR</a> <a href="?langue=en">EN</a>
			</div>
		</div>
	</header>
	<section id="main">
		<form:form action="login" method="post">
			<c:if test="${error != null}">
				<p>Invalid username and password.</p>
			</c:if>
			<c:if test="${logout != null}">
				<p>You have been logged out.</p>
			</c:if>
			<p>
				<label for="username">Username</label>
				<input type="text" id="username" name="username" required="required" pattern=".{3,}"/>
			</p>
			<p>
				<label for="password">Password</label>
				<input type="password" id="password" name="password" required="required" pattern=".{6,}"/>
			</p>
			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}" />
			<button type="submit" class="btn" id="loginButton">Log in</button>
		</form:form>
		<a href="register" class="btn" type="button">Create a new account</a>
	</section>
	<script src="js/login.js"></script>
</body>
</html>