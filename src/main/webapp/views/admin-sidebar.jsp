<%@page contentType="text/html" pageEncoding="UTF-8"%>

<div class="staff-sidebar">

    <div class="sidebar-header">
        <a href="${pageContext.request.contextPath}/guest-home"
           style="text-decoration:none; color:inherit;">
            <div class="brand">
                <span class="top">AUTOMOBILI</span>
                <span class="bottom">Admin Panel</span>
            </div>
        </a>
    </div>

    <ul class="sidebar-menu">

        <li class="menu-section">ADMIN ONLY</li>

        <li>
            <a href="${pageContext.request.contextPath}/dashboard/admin">
                <i class="icon">🛡</i>
                <span>Admin Dashboard</span>
            </a>
        </li>

        <li>
            <a href="#">
                <i class="icon">👨‍💼</i>
                <span>Manage Staff Accounts</span>
            </a>
        </li>

        <li>
            <a href="#">
                <i class="icon">🔐</i>
                <span>Roles & Permissions</span>
            </a>
        </li>

        <li>
            <a href="#">
                <i class="icon">📈</i>
                <span>System Reports</span>
            </a>
        </li>

        <li>
            <a href="${pageContext.request.contextPath}/admin/review?action=list">
                <i class="icon">💬</i>
                <span>Manage Reviews</span>
            </a>
        </li>

        <li class="menu-section">STAFF FEATURES</li>

        <li>
            <a href="${pageContext.request.contextPath}/staff/users">
                <i class="icon">👥</i>
                <span>Manage Users</span>
            </a>
        </li>

        <li>
            <a href="${pageContext.request.contextPath}/staff/cars">
                <i class="icon">🚗</i>
                <span>Manage Cars</span>
            </a>
        </li>

        <li>
            <a href="${pageContext.request.contextPath}/staff/maintenance">
                <i class="icon">🛠</i>
                <span>Maintenance</span>
            </a>
        </li>

        <li>
            <a href="${pageContext.request.contextPath}/staff/vouchers">
                <i class="icon">🎟</i>
                <span>Manage Vouchers</span>
            </a>
        </li>

        <li>
            <a href="${pageContext.request.contextPath}/staff/bookings">
                <i class="icon">📅</i>
                <span>Manage Bookings</span>
            </a>
        </li>

        <li>
            <a href="${pageContext.request.contextPath}/staff/licenses">
                <i class="icon">🪪</i>
                <span>Manage Driver Licenses</span>
            </a>
        </li>

        <li>
            <a href="${pageContext.request.contextPath}/staff/contracts">
                <i class="icon">📄</i>
                <span>Manage Contracts</span>
            </a>
        </li>

    </ul>

    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/logout">
            🚪 Logout
        </a>
    </div>
</div>