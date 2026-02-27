<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Manage Bookings</title>

    <!-- LINK CSS -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/staff.css">
</head>

<body>


<div class="staff-layout">

    <%@ include file="sidebar.jsp" %>

    <div class="staff-content">

        <h1 class="dashboard-title">Manage Bookings</h1>

        <div class="dashboard-table">

            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Customer</th>
                        <th>Car</th>
                        <th>Start</th>
                        <th>End</th>
                        <th>Total</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody>
                <c:forEach var="b" items="${bookingList}">
                    <tr>
                        <td>${b.bookingId}</td>
                        <td>${b.customerName}</td>
                        <td>${b.carName}</td>
                        <td>${b.startDate}</td>
                        <td>${b.endDate}</td>
                        <td>$${b.totalEstimatedPrice}</td>

                        <td>
                            <span class="status-badge ${b.status.toLowerCase()}">
                                ${b.status}
                            </span>
                        </td>

                        <td>
                            <a href="${pageContext.request.contextPath}/staff/bookings?action=detail&id=${b.bookingId}"
                               class="btn-view">
                                View
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>

            </table>

        </div>

    </div>
</div>
</body>
</html>