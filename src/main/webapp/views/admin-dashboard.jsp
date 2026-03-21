<%-- 
    Document   : admin-dashboard
    Created on : Mar 18, 2026, 2:37:24 PM
    Author     : HP
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Admin Dashboard</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
        <!-- DÒNG NÀY LÀ BẮT BUỘC - để report.js biết contextPath -->
        <script>
            window.contextPath = '${pageContext.request.contextPath}';
        </script>

        <script src="${pageContext.request.contextPath}/assets/js/report.js"></script>

    </head>
    <body>

        <div class="staff-layout">

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

                    <!-- ===== ADMIN ONLY ===== -->
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

                    <li> <a href="${pageContext.request.contextPath}/admin/review"> <i class="icon">💬</i> <span>Manage Reviews</span> </a> </li>

                    <li>
                        <a href="#" onclick="loadReportsOverview(); return false;">
                            <i class="icon">📈</i>
                            <span>Reports</span>
                        </a>
                    </li>
                    <!-- ===== STAFF FEATURES ===== -->
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

            <div class="staff-content">

                <h1 class="dashboard-title">Admin Dashboard</h1>

                <div class="dashboard-cards">
                    <div class="card">
                        <h3>Total Users</h3>
                        <p>${totalUsers}</p>
                    </div>

                    <div class="card">
                        <h3>Total Cars</h3>
                        <p>${totalCars}</p>
                    </div>

                    <div class="card">
                        <h3>Active Bookings</h3>
                        <p>${activeBookings}</p>
                    </div>

                    <div class="card">
                        <h3>Cars in Maintenance</h3>
                        <p>${maintenanceCars}</p>
                    </div>
                </div>

                <div class="dashboard-cards">
                    <div class="card">
                        <h3>Pending Bookings</h3>
                        <p>${pendingBookings}</p>
                    </div>

                    <div class="card">
                        <h3>Completed Bookings</h3>
                        <p>${completedBookings}</p>
                    </div>

                    <div class="card">
                        <h3>Total Revenue</h3>
                        <p>$${totalRevenue}</p>
                    </div>

                    <div class="card">
                        <h3>System Role</h3>
                        <p>ADMIN</p>
                    </div>
                </div>

            </div>
        </div>

    </body>
</html>