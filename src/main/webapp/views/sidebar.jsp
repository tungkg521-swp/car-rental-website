<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<div class="staff-sidebar">

    <!-- LOGO / TITLE -->
    <div class="sidebar-header">
        <div class="brand">
            <span class="top">AUTOMOBILI</span>
            <span class="bottom">Rental Car</span>
        </div>
    </div>


    <!-- MENU -->
    <ul class="sidebar-menu">

        <!-- DASHBOARD -->
        <li>
            <a href="${pageContext.request.contextPath}/staff/dashboard">
                <i class="icon">📊</i>
                <span>Dashboard</span>
            </a>
        </li>

        <!-- USERS -->
        <li>
            <a href="${pageContext.request.contextPath}/staff/users">
                <i class="icon">👥</i>
                <span>Manage Users</span>
            </a>
        </li>

        <!-- CARS -->
        <li>
            <a href="${pageContext.request.contextPath}/staff/cars">
                <i class="icon">🚗</i>
                <span>Manage Cars</span>
            </a>
        </li>

        <!-- MAINTENANCE -->
        <li>
            <a href="${pageContext.request.contextPath}/staff/maintenance">
                <i class="icon">🛠</i>
                <span>Maintenance</span>
            </a>
        </li>

        <!-- VOUCHERS -->
        <li>
            <a href="${pageContext.request.contextPath}/staff/vouchers">
                <i class="icon">🎟</i>
                <span>Manage Vouchers</span>
            </a>
        </li>

        <!-- BOOKINGS -->
        <li>
            <a href="${pageContext.request.contextPath}/staff/bookings">
                <i class="icon">📅</i>
                <span>Manage Bookings</span>
            </a>
        </li>

    </ul>

    <!-- FOOTER -->
    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/logout">
            🚪 Logout
        </a>
    </div>

</div>
