<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="models.AccountModel" %>
<%@ page import="Utils.RoleConstants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");

    boolean isAdmin = account != null && account.getRoleId() == RoleConstants.ADMIN;
    boolean isStaff = account != null && account.getRoleId() == RoleConstants.STAFF;

    String contextPath = request.getContextPath();
    String requestUri = request.getRequestURI();
    String currentPath = requestUri.substring(contextPath.length());

    boolean activeAdminDashboard = currentPath.equals("/dashboard/admin");
    boolean activeStaffDashboard = currentPath.equals("/dashboard/staff");
    boolean activeUsers = currentPath.startsWith("/staff/users");
    boolean activeCars = currentPath.startsWith("/staff/cars");
    boolean activeMaintenance = currentPath.startsWith("/staff/maintenance");
    boolean activeVouchers = currentPath.startsWith("/staff/vouchers");
    boolean activeBookings = currentPath.startsWith("/staff/bookings");
    boolean activeLicenses = currentPath.startsWith("/staff/licenses");
    boolean activeContracts = currentPath.startsWith("/staff/contracts");

    String dashboardLink = isAdmin ? contextPath + "/dashboard/admin" : contextPath + "/dashboard/staff";
    String panelName = isAdmin ? "Admin Panel" : "Rental Car";

    String activeClassAdminDashboard = activeAdminDashboard ? "active" : "";
    String activeClassStaffDashboard = activeStaffDashboard ? "active" : "";
    String activeClassUsers = activeUsers ? "active" : "";
    String activeClassCars = activeCars ? "active" : "";
    String activeClassMaintenance = activeMaintenance ? "active" : "";
    String activeClassVouchers = activeVouchers ? "active" : "";
    String activeClassBookings = activeBookings ? "active" : "";
    String activeClassLicenses = activeLicenses ? "active" : "";
    String activeClassContracts = activeContracts ? "active" : "";
%>

<div class="staff-sidebar">

    <div class="sidebar-header">
        <a href="<%= dashboardLink %>" class="sidebar-brand-link">
            <div class="brand">
                <span class="top">AUTOMOBILI</span>
                <span class="bottom"><%= panelName %></span>
            </div>
        </a>
    </div>

    <ul class="sidebar-menu">

        <% if (isAdmin) { %>
            <li class="menu-section">ADMIN ONLY</li>

            <li>
                <a href="<%= contextPath %>/dashboard/admin" class="<%= activeClassAdminDashboard %>">
                    <i class="icon">🛡</i>
                    <span>Admin Dashboard</span>
                </a>
            </li>

            <li>
                <a href="javascript:void(0)">
                    <i class="icon">👨‍💼</i>
                    <span>Manage Staff Accounts</span>
                </a>
            </li>

            <li>
                <a href="javascript:void(0)">
                    <i class="icon">🔐</i>
                    <span>Roles & Permissions</span>
                </a>
            </li>

            <li>
                <a href="javascript:void(0)">
                    <i class="icon">📈</i>
                    <span>System Reports</span>
                </a>
            </li>

            <li class="menu-section">STAFF FEATURES</li>
        <% } %>

        <% if (isStaff) { %>
            <li>
                <a href="<%= contextPath %>/dashboard/staff" class="<%= activeClassStaffDashboard %>">
                    <i class="icon">📊</i>
                    <span>Dashboard</span>
                </a>
            </li>
        <% } %>

        <li>
            <a href="<%= contextPath %>/staff/users" class="<%= activeClassUsers %>">
                <i class="icon">👥</i>
                <span>Manage Users</span>
            </a>
        </li>

        <li>
            <a href="<%= contextPath %>/staff/cars" class="<%= activeClassCars %>">
                <i class="icon">🚗</i>
                <span>Manage Cars</span>
            </a>
        </li>

        <li>
            <a href="<%= contextPath %>/staff/maintenance" class="<%= activeClassMaintenance %>">
                <i class="icon">🛠</i>
                <span>Maintenance</span>
            </a>
        </li>

        <li>
            <a href="<%= contextPath %>/staff/vouchers" class="<%= activeClassVouchers %>">
                <i class="icon">🎟</i>
                <span>Manage Vouchers</span>
            </a>
        </li>

        <li>
            <a href="<%= contextPath %>/staff/bookings" class="<%= activeClassBookings %>">
                <i class="icon">📅</i>
                <span>Manage Bookings</span>
            </a>
        </li>

        <li>
            <a href="<%= contextPath %>/staff/licenses" class="<%= activeClassLicenses %>">
                <i class="icon">🪪</i>
                <span>Manage Driver Licenses</span>
            </a>
        </li>

        <li>
            <a href="<%= contextPath %>/staff/contracts" class="<%= activeClassContracts %>">
                <i class="icon">📄</i>
                <span>Manage Contracts</span>
            </a>
        </li>

    </ul>

    <div class="sidebar-footer">
        <a href="<%= contextPath %>/logout">
            🚪 Logout
        </a>
    </div>

</div>