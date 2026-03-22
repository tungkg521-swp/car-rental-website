<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Manage Reviews</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
        <link href="${pageContext.request.contextPath}/assets/css/admin.css" rel="stylesheet" type="text/css"/>
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
                <h1 class="dashboard-title">Manage Reviews</h1>

                <c:if test="${not empty sessionScope.success}">
                    <div class="alert success">${sessionScope.success}</div>
                    <c:remove var="success" scope="session"/>
                </c:if>

                <c:if test="${not empty sessionScope.error}">
                    <div class="alert error">${sessionScope.error}</div>
                    <c:remove var="error" scope="session"/>
                </c:if>

                <div class="table-wrapper">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Review ID</th>
                                <th>Customer</th>
                                <th>Car ID</th>
                                <th>Booking ID</th>
                                <th>Rating</th>
                                <th>Comment</th>
                                <th>Created At</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="r" items="${reviews}">
                                <tr>
                                    <td>${r.reviewId}</td>
                                    <td>${r.customerName}</td>
                                    <td>${r.carId}</td>
                                    <td>${r.bookingId}</td>
                                    <td>${r.rating}</td>
                                    <td class="comment-cell">${r.comment}</td>
                                    <td>
                                        <fmt:formatDate value="${r.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/admin/review" method="post"
                                              onsubmit="return confirm('Are you sure you want to delete this review?');">
                                            <input type="hidden" name="action" value="delete"/>
                                            <input type="hidden" name="reviewId" value="${r.reviewId}"/>
                                            <button type="submit" class="btn-delete">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty reviews}">
                                <tr>
                                    <td colspan="8">No reviews found.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

    </body>
</html>