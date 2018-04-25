<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

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
<body onload='$.fn.alert(${errors});'>
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
		<form:form action="register" method="post" modelAttribute="user">
			<p>
				<label for="username"><spring:message code="username"/></label>
				<form:input type="text" id="username" name="username" path="username" required="required" pattern=".{3,}"/>
			</p>
			<p>
				<label for="password"><spring:message code="password"/></label>
				<form:input type="password" id="password" name="password" path="password" required="required" pattern=".{3,}"/>
			</p>
			<p>
				<label for="password2"><spring:message code="passwordConfirmation"/></label>
				<input type="password" id="password2" name="password2" required="required" pattern=".{3,}"/>
			</p>
			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}" />
			<button type="submit" class="btn" id="registerButton"><spring:message code="register"/></button>
		</form:form>
		<a href="login" class="btn" type="button"><spring:message code="backToLogin"/></a>
	</section>
	<script src="js/register.js"></script>
	<script src="js/errors.js"></script>
</body>
</html>