<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page language="java" pageEncoding="UTF-8"%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<c:set var="context" value="${pageContext.request.contextPath}"/>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="${context}/img/favicon.ico">

    <script src="${context}/js/jquery-1.9.1.js"></script>
    <script src="${context}/bootstrap/js/bootstrap.min.js"></script>
    <script src="${context}/js/highcharts.js"></script>
    <script src="${context}/js/exporting.js"></script>
    <script src="${context}/js/jquery-ui.js"></script>
    <script src="${context}/js/jquery-validate.js"></script>

    <link href="${context}/css/jquery-ui.css" rel="stylesheet"/>
    <link href="${context}/css/jquery-ui.theme.css" rel="stylesheet"/>
    <link href="${context}/css/jquery-ui.structure.css" rel="stylesheet"/>
    <link href="${context}/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="${context}/css/style.css" rel="stylesheet">

    <title><tiles:insertAttribute name="title" /></title>

    <script>
        $(function() {
            $(".datepicker").datepicker(
                    {dateFormat: "dd-mm-yy"}
            );

            $("#selectedLanguage").val("${pageContext.response.locale}");
        });
    </script>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <c:if test="${pageContext.request.userPrincipal.name != null}">
            <span>Welcome : ${pageContext.request.userPrincipal.name}
                | <a href="<c:url value="/j_spring_security_logout" />">Logout</a></span>
            </c:if>

            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <%= new java.util.Date()%>


            <div id="locale">
                <form action="" id="language-select" method="get">
                    <select name="language" id="selectedLanguage">
                        <option value="ru">Русский</option>
                        <option value="en">English</option>
                    </select>
                    <input type="submit" value="change"/>
                </form>
            </div>

        </div>
    </div>

    <tiles:insertAttribute name="header" />

    <tiles:insertAttribute name="body" />
</div>
</body>
</html>








