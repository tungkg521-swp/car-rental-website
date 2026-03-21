<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="currentUri" value="${pageContext.request.requestURI}" />
<c:set var="currentQuery" value="${pageContext.request.queryString}" />
<c:set var="account" value="${sessionScope.ACCOUNT}" />
<c:set var="isAdmin" value="${account != null and account.roleId == 3}" />

<div class="staff-sidebar">

    <div class="sidebar-header">
        <a href="${pageContext.request.contextPath}/guest-home"
           style="text-decoration:none; color:inherit;">
            <div class="brand">
                <span class="top">AUTOMOBILI</span>
                <span class="bottom">
                    <c:choose>
                        <c:when test="${isAdmin}">Admin Panel</c:when>
                        <c:otherwise>Rental Car</c:otherwise>
                    </c:choose>
                </span>
            </div>
        </a>
    </div>

    <ul class="sidebar-menu">

        <c:choose>

            <c:when test="${isAdmin}">
                <li class="menu-section">ADMIN ONLY</li>

                <li>
                    <a class="${fn:contains(currentUri, '/dashboard/admin') and empty param.view ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/dashboard/admin">
                        <i class="icon">🛡️</i>
                        <span>Admin Dashboard</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/admin/review') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/review?action=list">
                        <i class="icon">💬</i>
                        <span>Manage Reviews</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/dashboard/admin') and param.view == 'reports' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/dashboard/admin?view=reports">
                        <i class="icon">📈</i>
                        <span>Reports</span>
                    </a>
                </li>

                <li class="menu-section">STAFF FEATURES</li>

                <li>
                    <a class="${fn:contains(currentUri, '/staff/users') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/users">
                        <i class="icon">👥</i>
                        <span>Manage Users</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/staff/cars') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/cars">
                        <i class="icon">🚗</i>
                        <span>Manage Cars</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/staff/maintenance') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/maintenance">
                        <i class="icon">🛠</i>
                        <span>Maintenance</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/staff/vouchers') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/vouchers">
                        <i class="icon">🎟</i>
                        <span>Manage Vouchers</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/staff/bookings') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/bookings">
                        <i class="icon">📅</i>
                        <span>Manage Bookings</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/staff/licenses') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/licenses">
                        <i class="icon">🪪</i>
                        <span>Manage Driver Licenses</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/staff/contracts') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/contracts">
                        <i class="icon">📄</i>
                        <span>Manage Contracts</span>
                    </a>
                </li>
            </c:when>

            <c:otherwise>
                <li>
                    <a class="${fn:contains(currentUri, '/dashboard/staff') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/dashboard/staff">
                        <i class="icon">📊</i>
                        <span>Dashboard</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/staff/users') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/users">
                        <i class="icon">👥</i>
                        <span>Manage Users</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/staff/cars') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/cars">
                        <i class="icon">🚗</i>
                        <span>Manage Cars</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/staff/maintenance') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/maintenance">
                        <i class="icon">🛠</i>
                        <span>Maintenance</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/staff/vouchers') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/vouchers">
                        <i class="icon">🎟</i>
                        <span>Manage Vouchers</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/staff/bookings') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/bookings">
                        <i class="icon">📅</i>
                        <span>Manage Bookings</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/staff/licenses') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/licenses">
                        <i class="icon">🪪</i>
                        <span>Manage Driver Licenses</span>
                    </a>
                </li>

                <li>
                    <a class="${fn:contains(currentUri, '/staff/contracts') ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/staff/contracts">
                        <i class="icon">📄</i>
                        <span>Manage Contracts</span>
                    </a>
                </li>
            </c:otherwise>

        </c:choose>

    </ul>

    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/logout">
            🚪 Logout
        </a>
    </div>

</div>