<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="context" value="${pageContext.request.contextPath}"/>

<%@ page import="java.util.Map" %>

<script>
    function goToDailyPageWithDaysAfterSelectedDate() {
        var date = $("#dateAfterWhichToShow").val();
        window.location.href =  "${context}/daily-after-date?date=" + date;
    }

    function goToDailyPageForSelectedMonthAndYear() {
        var month = $("#month").val();
        var year = $("#year").val();

        window.location.href =  "${context}/app/daily-for-month-and-year?month=" + month + "&year=" + year;
    }

    chartWasAlreadyDisplayed = false;
    function showHideCategoryChart() {
        if ($("#container").is(":visible")) {
            $("#container").hide();
        } else {
            $("#container").show();
            if (!chartWasAlreadyDisplayed) {
                displayCategoryChart()
                chartWasAlreadyDisplayed = true;
            }
        }
    }

    function displayCategoryChart() {
        // Radialize the colors
        Highcharts.getOptions().colors = Highcharts.map(Highcharts.getOptions().colors, function(color) {
            return {
                radialGradient: { cx: 0.5, cy: 0.3, r: 0.7 },
                stops: [
                    [0, color],
                    [1, Highcharts.Color(color).brighten(-0.3).get('rgb')] // darken
                ]
            };
        });

        // Build the chart
        $('#container').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: 'By categories'
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{<point class="percentage"></point>:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {y:.1f} грн.',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        },
                        connectorColor: 'silver'
                    }
                }
            },
            series: [{
                type: 'pie',
                name: 'Category',
                data: [
                    <%
                    Map<String, Double> categoryData =  (Map<String, Double>) request.getAttribute("categoryData");
                    for (Map.Entry<String, Double> entry:  categoryData.entrySet()) {
                        out.println("['" + entry.getKey() + "', " + entry.getValue() + "],");
                    }
                    %>

                ]
            }]
        });
    }
</script>

<!-- EDIT ITEM POPUP -->
<jsp:include page="edit-item-popup.jsp"/>
<!-- END OF EDIT ITEM POPUP -->

<div class="row">
    <!-- LEFT MENU -->
    <div class="col-md-2">
        <ul class="list-group">
            <li class="list-group-item">
                <button type="button" class="btn btn-warning btn-sm" onclick="showHideCategoryChart()">Show Category Chart</button>
            </li>
            <li class="list-group-item">
                <div class="form-group">
                    <input type="text" class="form-control input-sm datepicker" id="dateAfterWhichToShow"/>
                </div>
                <button type="button" class="btn btn-info btn-sm" onclick="goToDailyPageWithDaysAfterSelectedDate()">Get Days After Date</button>
            </li>
            <li class="list-group-item">
                <div class="form-group">
                    <label for="month" class="add-on">Month</label>
                    <select id="month" name="month" class="form-control input-sm">
                        <c:forEach items="${months}" var="month">
                            <option value="<fmt:formatDate value="${month}" type="both" pattern="MM" />">
                                <fmt:formatDate value="${month}" type="both" pattern="MMMM" />
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="year" class="add-on">Year</label>
                    <select id="year" name="year" class="form-control input-sm">
                        <c:forEach items="${years}" var="year">
                            <option value="${year}">${year}</option>
                        </c:forEach>
                    </select>
                </div>

                <button type="button" class="btn btn-success btn-sm" onclick="goToDailyPageForSelectedMonthAndYear()">Get Data</button>
            </li>
        </ul>
    </div>
    <!-- END OF LEFT MENU -->

    <div class="col-md-10">
        <!-- CATEGORY CHART -->
        <div id="container" style="min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto; display: none"></div>
        <!-- END OF CATEGORY CHART -->

        <!-- DAILY DATA TABLE PANEL -->
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title" style="">Daily Data Starting From: <fmt:formatDate value="${periodStartDate}" type="both" pattern="dd-MM-yyyy EEEE" /></h3>
            </div>
            <table class="table table-bordered table-hover table-striped">
                <c:forEach items="${days}" var="day">
                    <tr>
                        <td><fmt:formatDate value="${day.date}" type="both" pattern="dd-MM-yyyy EEEE"/></td>
                        <td class="td-item">
                            <c:forEach items="${day.incomes}" var="income">
                                <div id="p-${income.id}" class="record-row-block">
                                    <span id="item-${income.id}-amount">${income.amount}</span>
                                    <span id="item-${income.id}-description">${income.description}</span>
                                    <img src="${context}/img/delete-icon.png" id="delete-item-${income.id}" class="delete-item element-right"
                                         onclick="deleteItem(${income.id})"/>
                                    <img src="${context}/img/edit-icon.png" id="edit-item-${income.id}" class="edit-item element-right"/>
                                    <span class="element-right">${income.subCategory.name}</span>
                                </div>
                            </c:forEach>
                            <c:if test="${fn:length(day.incomes) > 1}">
                                <div class="total-for-day text-info">Total: ${day.totalIncomeAmount}</div>
                            </c:if>
                        </td>
                        <td class="td-item">
                            <c:forEach items="${day.outcomes}" var="outcome">
                                <div id="p-${outcome.id}" class="record-row-block">
                                    <span id="item-${outcome.id}-amount">${outcome.amount}</span>
                                    <span id="item-${outcome.id}-description">${outcome.description}</span>
                                    <img src="${context}/img/delete-icon.png" id="delete-item-${outcome.id}" class="delete-item element-right"
                                         onclick="deleteItem(${outcome.id})"/>
                                    <img src="${context}/img/edit-icon.png" id="edit-item-${outcome.id}" class="edit-item element-right"/>
                                    <span class="element-right">${outcome.subCategory.name}</span>
                                </div>
                            </c:forEach>
                            <c:if test="${fn:length(day.outcomes) > 1}">
                                <div class="total-for-day text-info">Total: ${day.totalOutcomeAmount}</div>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        <!-- END OF DAILY DATA TABLE PANEL -->
    </div>
</div>
