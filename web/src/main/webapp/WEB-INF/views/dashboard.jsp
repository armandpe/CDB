<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="custom" uri="/WEB-INF/cdb.tld"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<title>Computer Database</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="css/font-awesome.css" rel="stylesheet" media="screen">
<link href="css/main.css" rel="stylesheet" media="screen">
</head>
<body onload='$.fn.alert(${errors});'>
	<c:choose>
		<c:when test="${pageData.order == 'asc'}">
			<c:set var="opposite" value="desc" />
		</c:when>
		<c:otherwise>
			<c:set var="opposite" value="asc" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test="${pageData.orderby == 'name'}">
			<c:set var="orderName" value="${opposite}" />
		</c:when>
		<c:when test="${pageData.orderby == 'introduced'}">
			<c:set var="orderIntroduced" value="${opposite}" />
		</c:when>
		<c:when test="${pageData.orderby == 'company'}">
			<c:set var="orderCompany" value="${opposite}" />
		</c:when>
		<c:when test="${pageData.orderby == 'discontinued'}">
			<c:set var="orderDiscontinued" value="${opposite}" />
		</c:when>
	</c:choose>

	<form action="logout" method="post" id="logoutForm">
		<input type="hidden" name="${_csrf.parameterName}"
			value="${_csrf.token}" />
	</form>
	<script>
	function formSubmit() {
		document.getElementById("logoutForm").submit();
	}
</script>

	<header class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<a class="navbar-brand" href="dashboard"> Application - Computer
				Database </a>
			<div class="pull-right">
				<a href="javascript:formSubmit()"><spring:message code="logout" /></a> <br> <br> <a
					href="?langue=fr"><img src="img/fr.png" height="24" width="24"
					alt="FR" /></a> <a href="?langue=en"><img src="img/en.png" alt="EN"></a>
			</div>
		</div>
	</header>
	<section id="main">
		<div class="container">
			<h1 id="homeTitle">${pageData.count}
				<spring:message code="computersFound" />
			</h1>

			<div id="actions" class="form-horizontal">
				<div class="pull-left">
					<form id="searchForm" action="#" method="GET" class="form-inline">

						<input type="search" id="searchbox" name="search"
							class="form-control" placeholder="ex : Apple" /> <input
							type="submit" id="searchsubmit" value="<spring:message code="filterByName"/>"
							class="btn btn-primary" />
					</form>
				</div>

				<sec:authorize access="hasRole('ADMIN')">
					<div class="pull-right">
						<a class="btn btn-success" id="addComputer" href="addComputer"><spring:message code="addComputer"/></a> <a class="btn btn-default" id="editComputer" href="#"
							onclick="$.fn.toggleEditMode();"><spring:message code="edit"/></a>
					</div>
				</sec:authorize>
			</div>
		</div>

		<form id="deleteForm" action="#" method="POST">
			<input type="hidden" name="selection" value="">
		</form>

		<div class="container" style="margin-top: 10px;">
			<table class="table table-striped table-bordered">
				<thead>
					<tr>
						<th class="editMode" style="width: 60px; height: 22px;"><input
							type="checkbox" id="selectall" /> <span
							style="vertical-align: top;"> - <a href="#"
								id="deleteSelected" onclick="$.fn.deleteSelected();"> <i
									class="fa fa-trash-o fa-lg"></i>
							</a>
						</span></th>
						<th><a
							href="dashboard?orderby=name&order=${orderName}&limit=${pageData.limit}
							&search=${pageData.search}&page=${pageData.currentPage}"><spring:message code="computerName" /></a></th>
						<th><a
							href="dashboard?orderby=introduced&order=${orderIntroduced}&limit=${pageData.limit}
							&search=${pageData.search}&page=${pageData.currentPage}"><spring:message code="introducedDate" /></a></th>
						<th><a
							href="dashboard?orderby=discontinued&order=${orderDiscontinued}&limit=${pageData.limit}
							&search=${pageData.search}&page=${pageData.currentPage}"><spring:message code="discontinuedDate" /></a></th>
						<th><a
							href="dashboard?orderby=company&order=${orderCompany}&limit=${pageData.limit}
							&search=${pageData.search}&page=${pageData.currentPage}"><spring:message code="company" /></a></th>
					</tr>
				</thead>
				<tbody id="results">
					<c:forEach items="${pageData.dataList}" var="computer">
						<tr>
							<td class="editMode"><input type="checkbox" name="cb"
								class="cb" value="${computer.id}"></td>

							<sec:authorize access="hasRole('ADMIN')">
								<td><a href="editComputer?id=${computer.id}" onclick="">${computer.name}</a></td>
							</sec:authorize>
							<sec:authorize access="!hasRole('ADMIN')">
								<td>${computer.name}</td>
							</sec:authorize>
							<td>${computer.introduced}</td>
							<td>${computer.discontinued}</td>
							<td>${computer.companyName}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</section>

	<footer class="navbar-fixed-bottom">
		<div class="container text-center">
			<ul class="pagination">
				<custom:pagination maxPage="${pageData.maxPage}"
					currentPage="${pageData.currentPage}" limit="${pageData.limit}"
					orderBy="${pageData.orderby}" search="${pageData.search}" />
			</ul>
			<c:set var="limitArray" value="${fn:split('10,20,50,100', ',')}" />
			<div class="btn-group btn-group-sm pull-right" role="group">
				<c:forEach items="${limitArray}" var="limitVal">
					<input type="button" class="btn btn-default"
						onclick="location.href='dashboard?orderby=${pageData.orderby}&order=${pageData.order}&limit=${limitVal}&search=${pageData.search}&page=${pageData.currentPage}'"
						value="${limitVal}" />
				</c:forEach>
			</div>
		</div>
	</footer>
	<script src="js/jquery.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/dashboard.js"></script>
	<script src="js/errors.js"></script>
</body>
</html>