<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="context" value="${pageContext.request.contextPath}"/>

<script>
    $(function () {
        var ctx = document.getElementById("weeklyChart").getContext("2d");

        var data = {
            labels: [<c:forEach items="${mondayDates}" var="mondayDate">
                "${mondayDate}",
                </c:forEach>
            ],
            datasets: [
                {
                    fillColor: "rgba(220,220,220,0.5)",
                    strokeColor: "#1AD151",
                    pointColor: "rgba(220,220,220,1)",
                    pointStrokeColor: "#fff",
                    data: [
                        <c:forEach items="${weeks}" var="week">
                        ${week.incomeAmount},
                        </c:forEach>
                    ]
                },
                {
                    fillColor: "rgba(151,187,205,0.5)",
                    strokeColor: "#D11E1A",
                    pointColor: "rgba(151,187,205,1)",
                    pointStrokeColor: "#fff",
                    data: [
                        <c:forEach items="${weeks}" var="week">
                        ${week.outcomeAmount},
                        </c:forEach>
                    ]
                }
            ]
        };

        var monthlyChart = new Chart(ctx).Line(data);
    });
</script>

<div class="col-md-12">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Weekly Chart Data</h3>
        </div>
        <canvas id="weeklyChart" width="990" height="400"></canvas>
    </div>
</div>

<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Weekly Data</h3>
    </div>
    <table class="table table-bordered table-hover table-striped">
        <tr>
            <td>Week</td>
            <td>In amount</td>
            <td>Out amount</td>
        </tr>

        <c:forEach items="${weeks}" var="week">
            <tr>
                <td>${week.mondayDate}</td>
                <td>${week.incomeAmount}</td>
                <td>${week.outcomeAmount}</td>
            </tr>
        </c:forEach>
    </table>
</div>

