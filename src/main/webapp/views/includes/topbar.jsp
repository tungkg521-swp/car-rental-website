<%-- 
    Document   : topbar
    Created on : Apr 12, 2026, 6:24:55 PM
    Author     : Admin
--%>


<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="account" value="${sessionScope.ACCOUNT}" />
<c:set var="isAdmin" value="${account != null and account.roleId == 3}" />

<div class="page-topbar">
    <div>
        <h1 class="page-title">Staff Dashboard</h1>
        <div class="page-subtitle">
            <c:choose>
                <c:when test="${isAdmin}">Admin Panel</c:when>
                <c:otherwise>Staff Panel</c:otherwise>
            </c:choose>
            / Overview
        </div>
    </div>

    <div class="topbar-user">
        <span class="role-badge ${isAdmin ? 'admin' : 'staff'}">
            <c:choose>
                <c:when test="${isAdmin}">Admin</c:when>
                <c:otherwise>Staff</c:otherwise>
            </c:choose>
        </span>

        <div class="topbar-user-info">
            <div class="topbar-user-name">
                <c:out value="${sessionScope.ACCOUNT.email}" default="staff@system" />
            </div>
            <div class="topbar-user-text">System Management</div>
        </div>
    </div>
</div>