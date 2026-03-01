
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Staff Dashboard</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/staff.css">
</head>
<body>

<div class="staff-layout">

    <%@ include file="sidebar.jsp" %>

    <div class="staff-content">

        <h1 class="dashboard-title">Staff Dashboard</h1>

        <!-- ===== FIRST ROW ===== -->
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

        <!-- ===== SECOND ROW ===== -->
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
                <h3>Available Cars</h3>
                <p>${totalCars - maintenanceCars}</p>
            </div>

        </div>

    </div>

</div>

</body>
</html>
