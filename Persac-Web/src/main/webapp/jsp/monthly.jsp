<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="context" value="${pageContext.request.contextPath}"/>

<script>
$(function() {
    var ctx = document.getElementById("monthlyChart").getContext("2d");

    var data = {
        labels: [<c:forEach items="${months}" var="month">
            "<fmt:formatDate value="${month.beginDate}" type="both" pattern="MMMM" />",
            </c:forEach>
        ],
        datasets: [
            {
                fillColor: "rgba(220,220,220,0.5)",
                strokeColor: "#1AD151" /*"rgba(220,220,220,1)"*/,
                pointColor: "rgba(220,220,220,1)",
                pointStrokeColor: "#fff",
                data: [
                    <c:forEach items="${months}" var="month">
                    ${month},
                    </c:forEach>
                ]
            },
            {
                fillColor: "rgba(151,187,205,0.5)",
                strokeColor: "#D11E1A" /*"rgba(151,187,205,1)"*/,
                pointColor: "rgba(151,187,205,1)",
                pointStrokeColor: "#fff",
                data: [
                    <c:forEach items="${months}" var="month">
                    ${month},
                    </c:forEach>
                ]
            }
        ]
    }

    var monthlyChart = new Chart(ctx).Line(data);
});
</script>

<div class="col-md-12">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Monthly Chart Data</h3>
        </div>
        <canvas id="monthlyChart" width="990" height="400"></canvas>
    </div>
</div>

<table class="table table-bordered table-hover table-striped">
    <tr>
        <th>Year</th>
        <th>Month</th>
        <th>In amount</th>
        <th>Out amount</th>
    </tr>

    <c:forEach items="${months}" var="month">
        <td>${month.year}</td>
            <td><fmt:formatDate value="${month.beginDate}" type="both" pattern="MMMM" /></td>
            <td>${month}</td>
            <td>${month}</td>
        </tr>
    </c:forEach>
</table>
