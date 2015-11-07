<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="context" value="${pageContext.request.contextPath}"/>

<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Specify settings</h3>
    </div>

    <div class="panel-body">
        <p>Categories which included in accounting</p>
        <form action="${context}/save-settings" method="post">
            <div class="col-md-3">
                <p>Income categories</p>
                <c:forEach items="${inCategories}" var="inCategory">
                    <p>${inCategory.name}
                        <input type="checkbox" name="inCategories[]" value="${inCategory.id}"
                               <c:if test="${inCategory.active}">
                                   checked="checked"
                               </c:if>
                        />
                    </p>
                </c:forEach>
            </div>

            <div class="col-md-3">
                <p>Outcome categories</p>
                <c:forEach items="${outCategories}" var="outCategory">
                    <p>${outCategory.name}
                        <input type="checkbox" name="outCategories[]" value="${outCategory.id}"
                                <c:if test="${outCategory.active}">
                                    checked="checked"
                                </c:if>
                                />
                    </p>
                </c:forEach>
            </div>

            <button type="submit">Save settings</button>
        </form>
    </div>
</div>
