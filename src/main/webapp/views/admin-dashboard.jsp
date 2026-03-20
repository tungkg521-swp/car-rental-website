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

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff.css">
    </head>
    <body>

        <div class="staff-layout">

            <jsp:include page="admin-sidebar.jsp"/>

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