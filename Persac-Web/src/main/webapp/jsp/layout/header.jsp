<%@ page language="java" pageEncoding="UTF-8" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="context" value="${pageContext.request.contextPath}"/>

<div id="persac-nav-bar" class="navbar navbar-default" role="navigation">
    <div class="navbar-header" style="display: inline-block; white-space: nowrap">
        <a class="navbar-brand" href="${context}/welcome">
            <img src="${context}/img/persac-logo.jpg" height="30px" width="30px" id="persac-logo"/>
            Personal Accounting
        </a>
    </div>
    <div class="navbar-collapse collapse">
        <ul class="nav navbar-nav">
            <li class="active"><a href="${context}/welcome">Home</a></li>
            <li><a href="${context}/index">Index</a></li>

            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Monthly
                    <b class="caret"></b>
                </a>
                <ul class="dropdown-menu">
                    <li><a href="${context}/monthly">Monthly data</a></li>
                    <li><a href="${context}/recalculate-months">Recalculate months</a></li>
                    <li class="divider"></li>
                </ul>
            </li>

            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Weekly
                    <b class="caret"></b>
                </a>
                <ul class="dropdown-menu">
                    <li><a href="${context}/weekly">Weekly data</a></li>
                    <li><a href="${context}/recalculate-weeks">Recalculate weeks</a></li>
                </ul>
            </li>

            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Daily
                    <b class="caret"></b>
                </a>
                <ul class="dropdown-menu">
                    <li><a href="${context}/daily">Daily data</a></li>
                    <li><a href=""></a></li>
                    <li><a href=""></a></li>
                </ul>
            </li>

            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Assets
                    <b class="caret"></b>
                </a>
                <ul class="dropdown-menu">
                    <li><a href="${context}/assets">Assets</a></li>
                    <li><a href="${context}/assets/recalculate-currency-amounts">Recalculate Currency Assets</a></li>
                    <li><a href=""></a></li>
                </ul>
            </li>

        </ul>
        <ul class="nav navbar-nav navbar-right">
            <li><a href="${context}/settings" title="Settings"><img src="${context}/img/settings-icon.png"
                                                                    class="img-responsive" alt="Settings"/></a></li>
        </ul>
    </div>

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
