<%@ page language="java" pageEncoding="UTF-8" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="context" value="${pageContext.request.contextPath}"/>

<script>
    $(function () {
        $('#assetsChartContainer').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: 'My Assets'
            },
            subtitle: {
                text: ''
            },
            xAxis: {
                type: 'category'
            },
            tooltip: {
                headerFormat: '',
                pointFormat: '{series.name}: {point.y:.1f}',
                shared: false,
                useHTML: true
            },
            plotOptions: {
                series: {
                    pointWidth: 50,
                    dataLabels: {
                        enabled: true,
                        format: '{point.y:.1f}'
                    }
                }
            },
            series: [
                <c:forEach items="${assets}" var="asset">
                {
                    name: "${asset.assetType.name}(${asset.assetType.currencyCode})",
                    data: [${asset.amount}]
                },
                </c:forEach>
            ]
        });

        var dialog,
                form,
                assetId = $("#assetId"),
                assetTypeName = $("#assetTypeName"),
                amount = $("#amount"),
                allFields = $([]).add(assetId).add(name).add(amount),
                tips = $(".validateTips");

        function updateTips(t) {
            tips
                    .text(t)
                    .addClass("ui-state-highlight");
            setTimeout(function () {
                tips.removeClass("ui-state-highlight", 1500);
            }, 500);
        }

        function checkLength(o, n, min, max) {
            if (o.val().length > max || o.val().length < min) {
                o.addClass("ui-state-error");
                updateTips("Length of " + n + " must be between " +
                min + " and " + max + ".");
                return false;
            } else {
                return true;
            }
        }

        function checkNotEmpty(o, n) {
            if (o.val().length == 0) {
                o.addClass("ui-state-error");
                updateTips(n + " must be filled.");
                return false;
            } else {
                return true;
            }
        }

        function checkRegexp(o, regexp, n) {
            if (!( regexp.test(o.val()) )) {
                o.addClass("ui-state-error");
                updateTips(n);
                return false;
            } else {
                return true;
            }
        }

        function updateAsset() {
            var valid = true;
            allFields.removeClass("ui-state-error");

            valid = valid && checkNotEmpty(assetTypeName, "Asset Name");
            valid = valid && checkNotEmpty(amount, "Amount");

            if (valid) {
                var asset = {
                    id: assetId.val(),
                    assetTypeName: assetTypeName.val(),
                    amount: amount.val()

                };

                $.post("${context}/assets/update", asset, function () {
                    window.location.reload(true);
                });

                dialog.dialog("close");
            }
            return valid;
        }

        dialog = $("#dialog-form").dialog({
            autoOpen: false,
            height: 350,
            width: 350,
            modal: true,
            buttons: {
                "Update an Asset": updateAsset,
                Cancel: function () {
                    dialog.dialog("close");
                }
            },
            close: function () {
                form[0].reset();
                allFields.removeClass("ui-state-error");
            }
        });

        form = dialog.find("form").on("submit", function (event) {
            event.preventDefault();
            updateAsset();
            window.location.reload(true);
        });

        $(".edit-asset").button().on("click", function (events, selector) {
            assetIdToUpdate = $(this).attr("id").substr(11)

            dialog.dialog("open");

            $.get("${context}/assets/get?id=" + assetIdToUpdate, function (data) {
                console.log("asset: " + data);

                $("#assetId").val(data.id);
                $("#assetTypeName").val(data.assetType.name);
                $("#amount").val(data.amount);
            });
        });

    });

    function deactivateAsset(id) {
        $.get("${context}/assets/deactivate?id=" + id, function (data) {
            console.log("Asset deactivated: " + id + " : " + data);
            window.location.reload(true);
        });
    }

    function recalculateAfterAmountForExchangeChanged() {
        var rate = $("#rate");
        var amountForExchange = $("#amountForExchange");
        var resultedAmount = $("#resultedAmount");

        var rateVal = rate.val();
        var rateOperationVal = $("input[name=rateOperation]:checked").val();

        if (rateVal == "") {
            resultedAmount.val("");
        } else {
            if (rateOperationVal == "buy") {
                resultedAmount.val(amountForExchange.val() / rateVal);
            } else {
                resultedAmount.val(amountForExchange.val() * rateVal);
            }
        }
    }

    function recalculateAfterResultedAmountChanged() {
        var rate = $("#rate");
        var amountForExchange = $("#amountForExchange");
        var resultedAmount = $("#resultedAmount");

        var rateVal = rate.val();
        var rateOperationVal = $("input[name=rateOperation]:checked").val();

        if (rateVal == "") {
            resultedAmount.val("");
        } else {
            if (rateOperationVal == "buy") {
                amountForExchange.val(resultedAmount.val() * rateVal);
            } else {
                amountForExchange.val(resultedAmount.val() / rateVal);
            }
        }
    }

    function recalculateAfterRateChanged() {
        var rate = $("#rate");
        var amountForExchange = $("#amountForExchange");
        var resultedAmount = $("#resultedAmount");

        if (amountForExchange.val() != "" && resultedAmount.val() != "") {
            amountForExchange.val("");
            resultedAmount.val("");
        } else if (amountForExchange.val() != "" || resultedAmount.val() == "") {
            recalculateAfterAmountForExchangeChanged();
        } else if (amountForExchange.val() == "" || resultedAmount.val() != "") {
            recalculateAfterResultedAmountChanged();
        }
    }

    var assetTypes = [
        <c:forEach items="${assetTypes}" var="assetType" varStatus="status">
        {
            id:             ${assetType.id},
            name: "${assetType.name}",
            currencyCode: "${assetType.currencyCode}"
        }<c:if test="${!status.last}">, </c:if>
        </c:forEach>
    ];

    function isCurrencyCodeEqual(element) {
        var selectedCurrencyCodeVal = $("#currencyCode").val();
        return element.currencyCode == selectedCurrencyCodeVal;
    }

    function filterAssetTypesAccordingToCurrencySelected() {
        var filteredAssetTypes = assetTypes.filter(isCurrencyCodeEqual);

        var destAssetForExchange = $("#destAssetForExchange");
        destAssetForExchange.html("<option> ----- Destination Asset ----- </option>");
        for (index in filteredAssetTypes) {
            destAssetForExchange.append(new Option(filteredAssetTypes[index].name, filteredAssetTypes[index].id));
        }
    }
</script>


<!-- START EDIT ASSET FORM -->
<div id="dialog-form" title="Edit Asset">
    <p class="validateTips">All form fields are required.</p>

    <form id="assetForm">
        <fieldset>
            <input type="hidden" name="assetId" id="assetId">

            <div>
                <label for="assetTypeName">Asset Name</label>
                <input type="text" name="amount" id="assetTypeName"
                       class="text ui-widget-content ui-corner-all input-sm">
            </div>

            <div>
                <label for="amount">Amount</label>
                <input type="text" name="amount" id="amount" class="text ui-widget-content ui-corner-all input-sm">
            </div>

            <!-- Allow form submission with keyboard without duplicating the dialog button -->
            <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
        </fieldset>
    </form>
</div>
<!-- END EDIT ASSET FORM -->

<div class="row">
    <div class="col-md-3">
        <div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
            <!-- START OF CREATE ASSET PANEL -->
            <div class="panel panel-default">
                <div class="panel-heading" role="tab" id="headingOne">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true"
                           aria-controls="collapseOne">
                            Create Asset
                        </a>
                    </h4>
                </div>
                <div id="collapseOne" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
                    <form action="${context}/assets/create-asset" method="post">
                        <div class="panel-body">
                            <label for="newAssetTypeName">New Asset Name:</label>
                            <input type="text" name="assetTypeName" id="newAssetTypeName"
                                   class="form-control input-sm"/>


                            <label for="newAssetAmount">Amount (can be empty)</label>
                            <input type="text" name="amount" id="newAssetAmount" class="form-control input-sm"/>

                            <div class="form-group">
                                <label for="newAssetCurrencyCode">Currency:</label>
                                <select name="currencyCode" id="newAssetCurrencyCode" class="form-control input-sm">
                                    <c:forEach items="${currencies}" var="currency">
                                        <option value="${currency.currencyCode}">${currency.currencyCode}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <input type="submit" value="Create" class="btn btn-warning btn-sm"/>
                        </div>
                    </form>
                </div>
            </div>
            <!-- END OF CREATE ASSET PANEL -->

            <!-- START OF TRANSFER PANEL -->
            <div class="panel panel-default">
                <div class="panel-heading" role="tab" id="headingTwo">
                    <h4 class="panel-title">
                        <a class="collapsed" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo"
                           aria-expanded="false" aria-controls="collapseTwo">
                            Transfer Between Assets
                        </a>
                    </h4>
                </div>
                <div id="collapseTwo" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingTwo">
                    <form action="${context}/assets/transfer" method="post">
                        <div class="panel-body">
                            <label for="sourceAssetTypeId">Source Asset:</label>
                            <select name="sourceAssetTypeId" id="sourceAssetTypeId" class="form-control input-sm">
                                <c:forEach items="${assetTypes}" var="assetType">
                                    <option value="${assetType.id}">${assetType.name}</option>
                                </c:forEach>
                            </select>

                            <label for="transferAmount">Amount:</label>
                            <input type="text" name="amount" id="transferAmount" class="form-control input-sm"/>

                            <div class="form-group">
                                <label for="destAssetTypeId">Destination Asset:</label>
                                <select name="destAssetTypeId" id="destAssetTypeId" class="form-control input-sm">
                                    <c:forEach items="${assetTypes}" var="assetType">
                                        <option value="${assetType.id}">${assetType.name}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <input type="submit" value="Transfer" class="btn btn-warning btn-sm"/>
                        </div>
                    </form>
                </div>
            </div>
            <!-- END OF TRANSFER PANEL -->

            <!-- START OF CURRENCY EXCHANGE PANEL -->
            <div class="panel panel-default">
                <div class="panel-heading" role="tab" id="headingThree">
                    <h4 class="panel-title">
                        <a class="collapsed" data-toggle="collapse" data-parent="#accordion" href="#collapseThree"
                           aria-expanded="false" aria-controls="collapseThree">
                            Currency Exchange
                        </a>
                    </h4>
                </div>
                <div id="collapseThree" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingThree">
                    <div class="panel-body">
                        <form action="${context}/assets/exchange" method="post">
                            <label for="sourceAssetForExchange">Source Asset:</label>
                            <select name="sourceAssetTypeIdForExchange" id="sourceAssetForExchange"
                                    class="form-control input-sm">
                                <c:forEach items="${assetTypes}" var="assetType">
                                    <option value="${assetType.id}">${assetType.name}</option>
                                </c:forEach>
                            </select>

                            <label for="amountForExchange">Amount:</label>
                            <input type="text" name="amount" id="amountForExchange"
                                   onchange="recalculateAfterAmountForExchangeChanged()" class="form-control input-sm">

                            <div class="form-group">
                                <label for="rate">Rate:</label>
                                <input type="text" name="rate" id="rate" onchange="recalculateAfterRateChanged()"
                                       class="form-control input-sm">
                            </div>

                            <div class="form-group">
                                <label>
                                    <input type="radio" name="rateOperation" value="buy" checked>
                                    Buying rate for me
                                </label>
                                <label>
                                    <input type="radio" name="rateOperation" value="sell">
                                    Selling rate for me
                                </label>
                            </div>

                            <label for="resultedAmount">Resulted Amount:</label>
                            <input type="text" name="resultedAmount" id="resultedAmount"
                                   onchange="recalculateAfterResultedAmountChanged()" class="form-control input-sm">

                            <label for="currencyCode">Currency Exchange To:</label>
                            <select name="currencyCode" id="currencyCode"
                                    onchange="filterAssetTypesAccordingToCurrencySelected()"
                                    class="form-control input-sm">
                                <option value=""> ---- Currency ----</option>
                                <c:forEach items="${currencies}" var="currency">
                                    <option value="${currency.currencyCode}">${currency.currencyCode}</option>
                                </c:forEach>
                            </select>

                            <div class="form-group">
                                <label for="destAssetForExchange">Destination Asset If Any:</label>
                                <select name="destAssetTypeIdForExchange" id="destAssetForExchange"
                                        class="form-control input-sm">
                                    <option value=""> ----- Destination Asset -----</option>
                                </select>
                            </div>

                            <input type="submit" value="Submit" class="btn btn-warning btn-sm"/>
                        </form>
                    </div>
                </div>
            </div>
            <!-- END OF CURRENCY EXCHANGE PANEL -->
        </div>

        <br/>

        <!-- START OF ASSET TABLE -->
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">Assets Table</h3>
            </div>
            <table class="table table-bordered table-hover table-striped">
                <tr>
                    <td>Name</td>
                    <td>Amount</td>
                    <td>Edit</td>
                    <td>Deactivate</td>
                </tr>

                <c:forEach items="${assets}" var="asset">
                    <tr>
                        <td>
                                ${asset.assetType.name}
                        </td>
                        <td>
                                ${asset.amount}&nbsp;${asset.assetType.currencyCode}
                        </td>
                        <td>
                            <img src="${context}/img/edit-icon.png" id="edit-asset-${asset.id}" class="edit-asset"
                                 onclick="updateAsset(${asset.id}, '${asset.assetType.name}', ${asset.amount})"/>
                        </td>
                        <td>
                            <img src="${context}/img/deactivate-icon.png" id="deactivate-asset-${asset.id}"
                                 class="deactivate-asset"
                                 onclick="deactivateAsset(${asset.id})"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        <!-- END OF ASSET TABLE -->
    </div>

    <div class="col-md-9">
        <!-- START OF CURRENCIES PANEL -->
        <div class="col-md-6">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Total Amounts Owned</h3>
                </div>
                <table class="table table-bordered table-hover table-striped">
                    <tr>
                        <td>Currency</td>
                        <td>Amount</td>
                    </tr>

                    <c:forEach items="${month.currencyAssets}" var="currencyAsset">
                        <tr>
                            <td>
                                    ${currencyAsset.currencyCode}
                            </td>
                            <td>
                                    ${currencyAsset.amount}
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </div>
        <!-- END OF CURRENCIES PANEL -->

        <!-- START OF All CAPITAL PANEL -->
        <div class="col-md-6">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Overall Capital in Different Currencies</h3>
                </div>
                <table class="table table-bordered table-hover table-striped">
                    <tr>
                        <td>USD</td>
                        <td>${capital}</td>
                    </tr>
                </table>
            </div>
        </div>
        <!-- END OF All CAPITAL PANEL -->

        <!-- START OF ASSETS CHART PANEL -->
        <div id="assetsChartContainer"></div>
        <!-- END OF ASSETS CHART PANEL -->
    </div>
</div>