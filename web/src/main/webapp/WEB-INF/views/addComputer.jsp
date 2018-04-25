<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html>
<html>
<head>
<title>Computer Database</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta charset="utf-8">
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
		<div class="container">
			<div class="row">
				<div class="col-xs-8 col-xs-offset-2 box">
					<h1><spring:message code="addComputer"/></h1>
					<form:form action="addComputer" method="POST"
						modelAttribute="computerDTO">
						<fieldset>
							<div class="form-group">
								<label for="computerName"><spring:message code="computerName"/></label>
								<form:input type="text" class="form-control" id="computerName"
									name="computerName" path="name" value="${computerDTO.name}" required="required"/>
							</div>
							<div class="form-group">
								<label for="introduced"><spring:message code="introducedDate"/></label>
								<form:input type="date" class="form-control" id="introduced"
									placeholder="Introduced date" min="1970-01-01"
									name="introduced" path="introduced" />
							</div>
							<div class="form-group">
								<label for="discontinued"><spring:message code="discontinuedDate"/></label>
								<form:input type="date" class="form-control" id="discontinued"
									placeholder="Discontinued date" min="1970-01-01"
									name="discontinued" path="discontinued" />
							</div>
							<div class="form-group">
								<label for="companyId"><spring:message code="company"/></label>
								<form:select class="form-control" id="companyId"
									name="companyId" path="companyId">
									<option value=0><spring:message code="none"/></option>
									<c:forEach items="${companyList}" var="company">
										<option value="${company.id}">${company.name}</option>
									</c:forEach>
								</form:select>
							</div>
						</fieldset>
						<div class="actions pull-right">
							<input type="submit" value="<spring:message code="add"/>" class="btn btn-primary" id="addButton"> <spring:message code="or" /> 
							<a href="dashboard" class="btn btn-default"><spring:message code="cancel"/></a>
						</div>
					</form:form>
				</div>
			</div>
		</div>
	</section>
	<script src=" js/jquery.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/add.js"></script>
	<script src="js/errors.js"></script>
</body>
</html>