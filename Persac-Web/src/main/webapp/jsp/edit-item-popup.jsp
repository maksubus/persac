<%@ page language="java" pageEncoding="UTF-8" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="context" value="${pageContext.request.contextPath}"/>

<script>
    incomeCategories = [
        <c:forEach items="${incomeCategories}" var="incomeCategory">
        {id: ${incomeCategory.id}, name: '${incomeCategory.name}'},
        </c:forEach>
    ];

    outcomeCategories = [
        <c:forEach items="${outcomeCategories}" var="outcomeCategory">
        {id: ${outcomeCategory.id}, name: '${outcomeCategory.name}'},
        </c:forEach>
    ];

    function changeSubCategoriesListInSelectForEdit() {
        var categoriesForSelect;
        if ($("#edit-category").val() == 'Income') {
            categoriesForSelect = incomeCategories;
        } else {
            categoriesForSelect = outcomeCategories;
        }

        $('#edit-subCategory')
                .find('option')
                .remove()
                .end();

        $.each(categoriesForSelect, function (index, value) {
            $("#edit-subCategory").append("<option value=" + value.id + ">" + value.name + "</option>");
        });
    }

    $(function () {
        var dialog,
                form,
                id = $("#edit-id"),
                category = $("#edit-category"),
                subCategory = $("#edit-subCategory"),
                assetType = $("#edit-assetType"),
                amount = $("#edit-amount"),
                description = $("#edit-description"),
                date = $("#edit-date"),
                allFields = $([]).add(category).add(subCategory).add(assetType).add(amount).add(description).add(date),
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
                updateTips(n + ". Invalid format");
                return false;
            } else {
                return true;
            }
        }

        function updateItem() {
            var valid = true;
            allFields.removeClass("ui-state-error");

            valid = valid && checkNotEmpty(category, "Category");
            valid = valid && checkNotEmpty(subCategory, "Subcategory");
            valid = valid && checkNotEmpty(assetType, "Asset Type");
            valid = valid && checkNotEmpty(amount, "Amount");
            valid = valid && checkNotEmpty(description, "Description");
            valid = valid && checkLength(description, "Description", 1, 250);
            valid = valid && checkRegexp(date, /[0-9]{2}-[0-9]{2}-[0-9]{4}/, "Invalid format of date");

            if (valid) {
                var item = {
                    id: id.val(),
                    categoryName: category.val(),
                    subCategoryId: subCategory.val(),
                    assetTypeId: assetType.val(),
                    amount: amount.val(),
                    description: description.val(),
                    date: date.val()
                };

                $.post("${context}/item/update", item, function () {
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
                "Update an item": updateItem,
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
            updateItem();
            window.location.reload(true);
        });

        $(".edit-item").button().on("click", function (events, selector) {
            itemIdToUpdate = $(this).attr("id").substr(10);

            dialog.dialog("open");

            $.get("${context}/item/get?id=" + itemIdToUpdate, function (data) {
                console.log("Item: " + data);

                if (data.subCategory.parentCategory.name == 'Income') {
                    categoriesForSelect = incomeCategories;
                } else {
                    categoriesForSelect = outcomeCategories;
                }

                $.each(categoriesForSelect, function (index, value) {
                    $("#edit-subCategory").append("<option value=" + value.id + ">" + value.name + "</option>");
                });

                $("#edit-id").val(data.id);
                $("#edit-category").val(data.subCategory.parentCategory.name);
                $("#edit-subCategory").val(data.subCategory.id);

                if (data.assetType == null) {
                    $("#edit-assetType").parent().hide();
                } else {
                    $("#edit-assetType").parent().show();
                    $("#edit-assetType").val(data.assetType.id);
                }

                $("#edit-amount").val(data.amount);
                $("#edit-description").val(data.description);
                var monthString = data.monthOfYear < 10 ? "0" + data.monthOfYear : data.monthOfYear;
                var dayString = data.dayOfMonth < 10 ? "0" + data.dayOfMonth : data.dayOfMonth;
                $("#edit-date").val(dayString + "-" + monthString + "-" + data.year);
            });
        });

    });

    function deleteItem(itemId) {
        console.log("Item id to delete: " + itemId);

        $.get("${context}/item/delete?id=" + itemId, function (data) {
            window.location.reload(true);
        });
    }
</script>

<!-- EDIT ITEM POPUP -->
<div id="dialog-form" title="Edit Item">
    <p class="validateTips">All form fields are required.</p>

    <form>
        <fieldset>
            <input type="hidden" name="id" id="edit-id">

            <div>
                <label for="edit-category">Category</label>
                <select name="category" id="edit-category" class="text ui-widget-content ui-corner-all"
                        onchange="changeSubCategoriesListInSelectForEdit()">
                    <option value="Outcome">Outcome</option>
                    <option value="Income">Income</option>
                </select>
            </div>
            <div>
                <label for="edit-subCategory">Subcategory</label>
                <select name="subCategory" id="edit-subCategory" class="text ui-widget-content ui-corner-all">
                </select>
            </div>
            <div>
                <label for="edit-assetType">Asset Type</label>
                <select name="assetType" id="edit-assetType" class="text ui-widget-content ui-corner-all">
                    <c:forEach items="${assetTypes}" var="assetType">
                        <option value="${assetType.id}">${assetType.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div>
                <label for="edit-amount">Amount</label>
                <input type="text" name="amount" id="edit-amount" class="text ui-widget-content ui-corner-all">
            </div>
            <div>
                <label for="edit-description">Description</label>
                <input type="text" name="description" id="edit-description" class="text ui-widget-content ui-corner-all">
            </div>
            <div>
                <label for="edit-date">Date</label>
                <input type="text" name="date" id="edit-date" class="datepicker text ui-widget-content ui-corner-all">
            </div>
            <!-- Allow form submission with keyboard without duplicating the dialog button -->
            <input type="submit" tabindex="-1" style="position:absolute; top:-1000px">
        </fieldset>
    </form>
</div>
<!-- END OF EDIT ITEM POPUP -->