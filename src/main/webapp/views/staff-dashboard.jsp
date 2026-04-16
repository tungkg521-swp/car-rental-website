<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Staff Dashboard</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff-dashboard.css">
    </head>
    <body>


        <div class="staff-layout">

            <%@ include file="sidebar.jsp" %>

            <div class="staff-content">

                <%@ include file="includes/topbar.jsp" %>

               
                <div class="dashboard-section-title">System Statistics</div>
                <div class="dashboard-note">Quick overview of current staff operations.</div>

                <div class="dashboard-cards">
                    <div class="card users">
                        <h3>Total Users</h3>
                        <p>${totalUsers}</p>
                    </div>

                    <div class="card cars">
                        <h3>Total Cars</h3>
                        <p>${totalCars}</p>
                    </div>

                    <div class="card active-bookings">
                        <h3>Active Bookings</h3>
                        <p>${activeBookings}</p>
                    </div>

                    <div class="card maintenance">
                        <h3>Cars in Maintenance</h3>
                        <p>${maintenanceCars}</p>
                    </div>
                </div>

                <div class="dashboard-cards">
                    <div class="card pending">
                        <h3>Pending Bookings</h3>
                        <p>${pendingBookings}</p>
                    </div>

                    <div class="card completed">
                        <h3>Completed Bookings</h3>
                        <p>${completedBookings}</p>
                    </div>

                    <div class="card revenue">
                        <h3>Total Revenue</h3>
                        <p>$${totalRevenue}</p>
                    </div>

                    <div class="card available">
                        <h3>Available Cars</h3>
                        <p>${totalCars - maintenanceCars}</p>
                    </div>
                </div>

            </div>

        </div>

    </body>
</html>
