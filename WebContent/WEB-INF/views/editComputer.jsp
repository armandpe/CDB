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
<body onload='$.fn.alert(${errors});'>
	<header class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<a class="navbar-brand" href="dashboard"> Application - Computer
				Database </a>
		</div>
	</header>
	<section id="main">
		<div class="container">
			<div class="row">
				<div class="col-xs-8 col-xs-offset-2 box">
					<div class="label label-default pull-right">id:
						${computerDTO.id}</div>
					<h1>Edit Computer</h1>
					<form:form action="editComputer" method="POST" modelAttribute="computerDTO">
						<input type="hidden" value="${computerDTO.id}" id="id" name="id" />
						<!-- TODO: Change this value with the computer id -->
						<fieldset>
							<div class="form-group">
								<label for="computerName">Computer name</label> 
								<form:input
									type="text" class="form-control" id="computerName"
									name="computerName" path="name" placeholder="Computer name"
									value="${computerDTO.name}"/>
							</div>
							<div class="form-group">
								<label for="introduced">Introduced date</label> 
								<form:input
									type="date" class="form-control" id="introduced"
									name="introduced" path="introduced" placeholder="Introduced date"
									value="${computerDTO.introduced}"/>
							</div>
							<div class="form-group">
								<label for="discontinued">Discontinued date</label> 
								<form:input
									type="date" class="form-control" id="discontinued"
									name="discontinued" path="discontinued" placeholder="Discontinued date"
									value="${computerDTO.discontinued}"/>
							</div>
							<div class="form-group">
								<label for="companyId">Company</label> 
								<form:select
									class="form-control" id="companyId" path="companyId" name="companyId">
									<option value=0>None</option>
									<c:forEach items="${companyList}" var="company">
										<option value="${companyDTO.id}"
											${company.id == computer.companyId ? 'selected="selected"' : ''}>${company.name}</option>
									</c:forEach>
								</form:select>
							</div>
						</fieldset>
						<div class="actions pull-right">
							<input type="submit" value="Edit" class="btn btn-primary"
								id="editButton"> or <a href="dashboard"
								class="btn btn-default">Cancel</a>
						</div>
					</form:form>
				</div>
			</div>
		</div>
	</section>
	<script src=" js/jquery.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/edit.js"></script>
	<script src="js/errors.js"></script>
</body>
</html>