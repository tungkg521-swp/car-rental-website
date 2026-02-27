<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="models.BookingModel" %>

<%
    BookingModel booking = (BookingModel) request.getAttribute("booking");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Booking Detail</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/staff.css">
</head>

<body class="detail-page">

<div class="detail-container">

    <div class="detail-card">

        <div class="detail-info">

            <h1>Booking #<%= booking.getBookingId() %></h1>

            <span class="status-badge <%= booking.getStatus().toLowerCase() %>">
                <%= booking.getStatus() %>
            </span>

            <div class="specs">
                <div><strong>Booking Date:</strong> <%= booking.getBookingDate() %></div>
                <div><strong>Start Date:</strong> <%= booking.getStartDate() %></div>
                <div><strong>End Date:</strong> <%= booking.getEndDate() %></div>
                <div><strong>Total:</strong> $<%= booking.getTotalEstimatedPrice() %></div>
            </div>

        </div>
    </div>

    <!-- CUSTOMER INFO -->
    <div class="detail-description">
        <h2>Customer Information</h2>
        <p><strong>Name:</strong> <%= booking.getCustomerName() %></p>
        <p><strong>Email:</strong> <%= booking.getCustomerEmail() %></p>
        <p><strong>Phone:</strong> <%= booking.getCustomerPhone() %></p>
    </div>

    <!-- CAR INFO -->
    <div class="detail-description">
        <h2>Car Information</h2>
        <p><strong>Model:</strong> <%= booking.getCarName() %></p>
        <p><strong>Price/Day:</strong> $<%= booking.getPricePerDay() %></p>
    </div>

    <div class="detail-actions">
        <a href="${pageContext.request.contextPath}/staff/bookings"
           class="btn-back">Back</a>
    </div>

</div>

</body>
</html>
