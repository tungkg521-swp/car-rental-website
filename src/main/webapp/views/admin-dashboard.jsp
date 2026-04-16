<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Admin Dashboard</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/report.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-dashboard.css">

        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
        <script>
            window.contextPath = '${pageContext.request.contextPath}';
        </script>
        <script src="${pageContext.request.contextPath}/assets/js/report.js"></script>
    </head>
    <body>
        <div class="staff-layout">

            <%@ include file="sidebar.jsp" %>

            <div class="staff-content" id="adminContentArea">

                <div class="page-topbar">
                    <div>
                        <h1 class="page-title">Admin Dashboard</h1>
                        <p class="page-subtitle">System overview, reports, revenue, and operational insights</p>
                    </div>

                    <div class="topbar-user">
                        <span class="role-badge admin">Admin</span>
                        <div class="topbar-user-info">
                            <div class="topbar-user-name">System Administrator</div>
                            <div class="topbar-user-text">Full access control</div>
                        </div>
                    </div>
                </div>

                <div class="admin-hero">
                    <div class="admin-hero-left">
                        <h2>Business Overview</h2>
                        <p>Track users, vehicles, bookings, maintenance, and revenue performance in one place.</p>
                    </div>
                    <div class="admin-hero-right">
                        <span class="hero-badge">Live Overview</span>
                    </div>
                </div>

                <div class="section-heading">
                    <h3>Key Statistics</h3>
                    <p>Summary of current system activities</p>
                </div>

                <div class="dashboard-cards admin-grid">
                    <div class="card metric-card users">
                        <div class="metric-label">Total Users</div>
                        <div class="metric-value">${totalUsers}</div>
                        <div class="metric-note">Registered accounts in system</div>
                    </div>

                    <div class="card metric-card cars">
                        <div class="metric-label">Total Cars</div>
                        <div class="metric-value">${totalCars}</div>
                        <div class="metric-note">Vehicles managed by platform</div>
                    </div>

                    <div class="card metric-card active">
                        <div class="metric-label">Active Bookings</div>
                        <div class="metric-value">${activeBookings}</div>
                        <div class="metric-note">Bookings currently in progress</div>
                    </div>

                    <div class="card metric-card maintenance">
                        <div class="metric-label">Cars in Maintenance</div>
                        <div class="metric-value">${maintenanceCars}</div>
                        <div class="metric-note">Temporarily unavailable vehicles</div>
                    </div>

                    <div class="card metric-card pending">
                        <div class="metric-label">Pending Bookings</div>
                        <div class="metric-value">${pendingBookings}</div>
                        <div class="metric-note">Awaiting staff/admin processing</div>
                    </div>

                    <div class="card metric-card completed">
                        <div class="metric-label">Completed Bookings</div>
                        <div class="metric-value">${completedBookings}</div>
                        <div class="metric-note">Successfully finished rentals</div>
                    </div>

                    <div class="card metric-card revenue">
                        <div class="metric-label">Total Revenue</div>
                        <div class="metric-value revenue-value">
                            $<fmt:formatNumber value="${totalRevenue}" type="number" maxFractionDigits="0"/>
                        </div>
                        <div class="metric-note">Revenue from completed payments</div>
                    </div>

                    <div class="card metric-card role">
                        <div class="metric-label">System Role</div>
                        <div class="metric-value">ADMIN</div>
                        <div class="metric-note">Full management privileges</div>
                    </div>
                </div>

                <div class="dashboard-bottom">
                    <div class="dashboard-panel chart-panel">
                        <div class="panel-header">
                            <div>
                                <h3>Reports Overview</h3>
                                <p>Visual summary from reporting module</p>
                            </div>
                        </div>

                        <div id="reportOverviewContainer">
                            <canvas id="reportChart" height="110"></canvas>
                        </div>
                    </div>

                    <div class="dashboard-panel quick-panel">
                        <div class="panel-header">
                            <div>
                                <h3>Quick Insights</h3>
                                <p>Operational highlights</p>
                            </div>
                        </div>

                        <div class="quick-insight-list">
                            <div class="quick-item">
                                <span class="quick-title">Available Cars</span>
                                <span class="quick-value">${totalCars - maintenanceCars}</span>
                            </div>

                            <div class="quick-item">
                                <span class="quick-title">Maintenance Ratio</span>
                                <span class="quick-value">
                                    <fmt:formatNumber value="${(maintenanceCars * 100.0) / (totalCars == 0 ? 1 : totalCars)}" maxFractionDigits="0"/>%
                                </span>
                            </div>

                            <div class="quick-item">
                                <span class="quick-title">Booking Completion</span>
                                <span class="quick-value">
                                    <fmt:formatNumber value="${(completedBookings * 100.0) / ((completedBookings + pendingBookings + activeBookings) == 0 ? 1 : (completedBookings + pendingBookings + activeBookings))}" maxFractionDigits="0"/>%
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const params = new URLSearchParams(window.location.search);
                const view = params.get("view");

                if (view === "reports" && typeof loadReportsOverview === "function") {
                    loadReportsOverview();
                }
            });
        </script>
    </body>
</html>