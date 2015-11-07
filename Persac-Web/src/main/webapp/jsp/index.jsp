<%@ page language="java" pageEncoding="UTF-8" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="context" value="${pageContext.request.contextPath}"/>

<!-- EDIT ITEM POPUP WITH JS-->
<jsp:include page="edit-item-popup.jsp"/>
<!-- END OF EDIT ITEM POPUP WITH JS-->

<script>
    $(function () {
        $("#category").change(function () {
            var categoriesForSelect;
            if ($("#category").val() == 'Income') {
                categoriesForSelect = incomeCategories;
            } else {
                categoriesForSelect = outcomeCategories;
            }

            $('#subCategory')
                    .find('option')
                    .remove()
                    .end();

            $.each(categoriesForSelect, function (index, value) {
                $("#subCategory").append("<option value=" + value.id + ">" + value.name + "</option>");
            });
        });

        $('#monthlyChartContainer').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: '<spring:message code="index.monthly.chart.data" text="Last Months Chart Data" />'
            },
            xAxis: {
                categories: [
                    <c:forEach items="${monthBeginDates}" var="beginDate" varStatus="loop">
                    '<fmt:formatDate value="${beginDate}" type="both" pattern="MMMM" />'<c:if test="${!loop.last}">, </c:if>
                    </c:forEach>
                ]
            },
            credits: {
                enabled: false
            },
            plotOptions: {
                series: {
                    pointWidth: 50, //width of the column bars irrespective of the chart size
                    dataLabels: {
                        enabled: true,
                        format: '{point.y:.1f}'
                    }
                }
            },
            series: [
                <c:forEach items="${incomesChartData}" var="currencyIncomes" varStatus="loop">
                {
                    name: "${currencyIncomes.key}",
                    data: [<c:forEach items="${currencyIncomes.value}" var="value" varStatus="valueLoop">${value}<c:if test="${!valueLoop.last}">, </c:if></c:forEach>]
                }<c:if test="${!loop.last}">, </c:if>
                </c:forEach>

                <c:if test="${!empty outcomesChartData}">
                ,
                </c:if>

                <c:forEach items="${outcomesChartData}" var="currencyOutcomes" varStatus="loop">
                {
                    name: "${currencyOutcomes.key}",
                    data: [<c:forEach items="${currencyOutcomes.value}" var="value" varStatus="valueLoop">${value}<c:if test="${!valueLoop.last}">, </c:if></c:forEach>]
                }<c:if test="${!loop.last}">, </c:if>
                </c:forEach>
            ]
        });
    });
</script>

<div class="row">
    <div class="col-md-4">
        <!-- BEGIN ADDING INCOME OR OUTCOME PANEL -->
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><spring:message code="index.add.income.outcome"
                                                        text="Adding Income or Outcome"/></h3>
            </div>

            <div class="panel-body">
                <form id="addIncomeOutcomeForm" action="${context}/item/save" method="post" class="form-horizontal">
                    <div class="form-group">
                        <label for="category" class="col-sm-4 control-label"><spring:message
                                code="index.add.income.outcome.category" text="Category"/></label>

                        <div class="col-sm-8">
                            <select name="categoryName" id="category" class="form-control input-sm">
                                <option value="Outcome">Outcome</option>
                                <option value="Income">Income</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="subCategory" class="col-sm-4 control-label"><spring:message
                                code="index.add.income.outcome.subcategory" text="Subcategory"/></label>

                        <div class="col-sm-8">
                            <select name="subCategoryId" id="subCategory" class="form-control input-sm">
                                <c:forEach items="${outcomeCategories}" var="outcomeCategory">
                                    <option value="${outcomeCategory.id}">${outcomeCategory.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="assetTypeId" class="col-sm-4 control-label"><spring:message
                                code="index.add.income.outcome.asset.type" text="Asset Type"/></label>

                        <div class="col-sm-8">
                            <select name="assetTypeId" id="assetTypeId" class="form-control input-sm">
                                <c:forEach items="${assetTypes}" var="assetType">
                                    <option value="${assetType.id}">${assetType.name}
                                        - ${assetType.currencyCode}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="amount" class="col-sm-4 control-label"><spring:message
                                code="index.add.income.outcome.amount" text="Amount"/></label>

                        <div class="col-sm-8">
                            <input type="text" name="amount" id="amount" class="form-control input-sm">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="description" class="col-sm-4 control-label"><spring:message
                                code="index.add.income.outcome.description" text="Description"/></label>

                        <div class="col-sm-8">
                            <input type="text" name="description" id="description" class="form-control input-sm">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="date" class="col-sm-4 control-label"><spring:message
                                code="index.add.income.outcome.date" text="Date"/></label>

                        <div class="col-sm-8">
                            <input type="text" name="date" id="date" class="form-control input-sm datepicker">
                        </div>
                    </div>

                    <input type="submit" value="Submit" class="btn btn-sm btn-warning center-block">
                </form>
            </div>
        </div>
        <!-- END ADDING INCOME OR OUTCOME PANEL -->

        <!-- BEGIN LAST MONTHS DATA PANEL -->
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><spring:message code="index.last.months.data" text="Last Months Data"/></h3>
            </div>
            <table class="table table-bordered table-hover table-striped">
                <tr>
                    <td><spring:message code="index.last.months.data.year"/></td>
                    <td><spring:message code="index.last.months.data.month"/></td>
                    <td><spring:message code="income"/></td>
                    <td><spring:message code="outcome"/></td>
                    <%--<td><spring:message code="index.last.months.data.total.outcome"/></td>--%>
                    <%--<td><spring:message code="index.last.months.data.unrecorded.outcome" /></td>--%>
                </tr>

                <c:forEach items="${months}" var="month">
                    <tr>
                        <td>${month.year}</td>
                        <td>
                            <a href="${context}/daily-for-month-and-year?month=${month.monthOfYear}&year=${month.year}">
                                <fmt:formatDate value="${month.beginDate}" type="both" pattern="MMMM"/>
                            </a>
                        </td>

                        <td>
                            <c:forEach items="${month.incomes}" var="income" varStatus="loop">
                                ${income.amount} ${income.currencyCode}
                                <c:if test="${!loop.last}"><br/></c:if>
                            </c:forEach>
                        </td>
                        <td>
                            <c:forEach items="${month.outcomes}" var="outcome" varStatus="loop">
                                ${outcome.amount} ${outcome.currencyCode}
                                <c:if test="${!loop.last}"><br/></c:if>
                            </c:forEach>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        <!-- END LAST MONTHS DATA PANEL -->

        <!-- BEGIN LAST WEEKS DATA PANEL -->
        <%--<div class="panel panel-default">--%>
        <%--<div class="panel-heading">--%>
        <%--<h3 class="panel-title"><spring:message code="index.last.weeks.data" text="Last Weeks Data" /></h3>--%>
        <%--</div>--%>
        <%--<table class="table table-bordered table-hover table-striped">--%>
        <%--<tr>--%>
        <%--<td><spring:message code="index.last.weeks.data.first.day" /></td>--%>
        <%--<td><spring:message code="income" /></td>--%>
        <%--<td><spring:message code="outcome" /></td>--%>
        <%--</tr>--%>

        <%--<c:forEach items="${weeks}" var="week">--%>
        <%--<tr>--%>
        <%--<td><fmt:formatDate value="${week.mondayDate}" type="both" pattern="dd-MM-yyyy EEEE" /></td>--%>
        <%--<td><fmt:formatNumber value="${week}"  maxFractionDigits="2" /></td>--%>
        <%--<td><fmt:formatNumber value="${week}"  maxFractionDigits="2" /></td>--%>
        <%--</tr>--%>
        <%--</c:forEach>--%>
        <%--</table>--%>
        <%--</div>--%>
        <!-- END LAST WEEKS DATA PANEL -->

    </div>

    <div class="col-md-8">
        <!-- BEGIN LAST MONTHS CHART DATA PANEL -->
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><spring:message code="index.monthly.chart.data"
                                                        text="Last Months Chart Data"/></h3>
            </div>
            <div id="monthlyChartContainer"></div>
        </div>
        <!-- END LAST MONTHS CHART DATA PANEL -->

        <!-- BEGIN CURRENT WEEK DAYS PANEL -->
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><spring:message code="index.current.week.daily.data"
                                                        text="Current week daily"/></h3>
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
        <!-- END CURRENT WEEK DAYS PANEL PANEL -->
    </div>
</div>