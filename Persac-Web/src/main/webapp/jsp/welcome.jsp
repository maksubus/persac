<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page language="java" pageEncoding="UTF-8" %>

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

    <script src="${context}/js/jquery-ui.js"></script>
    <script src="${context}/js/jquery-validate.js"></script>

    <link href="${context}/css/jquery-ui.css" rel="stylesheet"/>
    <link href="${context}/css/jquery-ui.theme.css" rel="stylesheet"/>
    <link href="${context}/css/jquery-ui.structure.css" rel="stylesheet"/>
    <link href="${context}/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="${context}/css/style.css" rel="stylesheet">

    <style>
        body {
            padding-top: 20px;
            padding-bottom: 20px;
        }
    </style>

    <title><tiles:insertAttribute name="title"/></title>
</head>
<body>

<div class="container">
    <div class="jumbotron">
        <h1><spring:message code="label.form.title"/></h1>

        <p>This application was developed for fun and learning purposes. Personal Accounting (Persac) represents web
            application for managing personal incomes and outcomes, assets.
            <br>With Persac you always can track your wealth.</p>


    </div>

    <c:if test="${not empty errMsg}">
        <div class="row">
            <div class="col-sm-12">
                <div class="alert alert-danger" role="alert">
                        ${errMsg}
                </div>
            </div>
        </div>
    </c:if>

    <div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true" style="width:350px">
        <!-- START OF LOGIN PANEL -->
        <div class="panel panel-default">
            <div class="panel-heading" role="tab" id="headingOne">
                <h4 class="panel-title">
                    <a data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true"
                       aria-controls="collapseOne">
                        <spring:message code="panel.login" text="Login"/>
                    </a>
                </h4>
            </div>
            <div id="collapseOne" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
                <form action="${context}/j_spring_security_check" method="post" class="form-horizontal">
                    <div class="panel-body">
                        <div class="form-group">
                            <label for="loginUsername" class="col-sm-4 control-label">
                                <spring:message code="label.user.name"/>
                            </label>

                            <div class="col-sm-8">
                                <input type="text" name="j_username" id="loginUsername" class="form-control input-sm"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="loginPassword" class="col-sm-4 control-label">
                                <spring:message code="label.user.password"/>
                            </label>

                            <div class="col-sm-8">
                                <input type="password" name="j_password" id="loginPassword"
                                       class="form-control input-sm"/>

                            </div>
                        </div>

                        <input type="submit" value="Login" class="btn btn-warning btn-sm center-block"/>
                    </div>
                </form>
            </div>
        </div>
        <!-- END OF LOGIN PANEL -->

        <!-- START OF REGISTER PANEL -->
        <div class="panel panel-default">
            <div class="panel-heading" role="tab" id="headingTwo">
                <h4 class="panel-title">
                    <a class="collapsed" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo"
                       aria-expanded="false" aria-controls="collapseTwo">
                        <spring:message code="panel.register"/>
                    </a>
                </h4>
            </div>
            <div id="collapseTwo" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingTwo">
                <form:form action="register-account" modelAttribute="user" method="POST" enctype="utf8" cssClass="form-horizontal">
                    <div class="panel-body">
                        <div class="form-group">
                            <label for="registerName" class="col-sm-4 control-label">
                                <spring:message code="label.user.name"/>
                            </label>

                            <div class="col-sm-8">
                                <form:input path="name" value="" id="registerName" cssClass="form-control input-sm"/>
                            </div>
                        </div>
                        <form:errors path="name" element="p" cssClass="text-danger"/>

                        <%--<div class="form-group">
                            <label for="firstName" class="col-sm-4 control-label">
                                <spring:message code="label.user.firstName"/>
                            </label>

                            <div class="col-sm-8">
                                <form:input path="firstName" value="" id="firstName"
                                            cssClass="form-control input-sm"/>
                            </div>
                            <form:errors path="firstName" element="div"/>
                        </div>

                        <div class="form-group">
                            <label for="lastName" class="col-sm-4 control-label">
                                <spring:message code="label.user.lastName"/>
                            </label>

                            <div class="col-sm-8">
                                <form:input path="lastName" value="" id="lastName"
                                            cssClass="form-control input-sm"/>
                            </div>
                            <form:errors path="lastName" element="div"/>
                        </div>--%>

                        <div class="form-group">
                            <label for="email" class="col-sm-4 control-label">
                                <spring:message code="label.user.email"/>
                            </label>

                            <div class="col-sm-8">
                                <form:input path="email" value="" id="email" cssClass="form-control input-sm"/>
                            </div>
                        </div>
                        <form:errors path="email" element="div" cssClass="text-danger"/>


                        <div class="form-group">
                            <label for="password" class="col-sm-4 control-label">
                                <spring:message code="label.user.password"/>
                            </label>

                            <div class="col-sm-8">
                                <form:input path="password" value="" type="password"
                                            cssClass="form-control input-sm"/>
                            </div>
                        </div>
                        <form:errors path="password" element="div" cssClass="text-danger"/>


                        <div class="form-group">
                            <label for="matchingPassword" class="col-sm-4 control-label">
                                <spring:message code="label.user.confirmPass"/>
                            </label>

                            <div class="col-sm-8">
                                <form:input path="matchingPassword" value="" type="password"
                                            cssClass="form-control input-sm"/>
                            </div>
                        </div>
                        <form:errors path="matchingPassword" element="div" cssClass="text-danger"/>


                        <input type="submit" value="Submit" class="btn btn-sm btn-warning center-block">
                    </div>
                </form:form>
            </div>
        </div>
        <!-- END OF REGISTER PANEL -->
    </div>

    <div class="footer">&copy; Persac by Maksym Zhokha 2014-2015</div>
</div>
</body>
</html>
