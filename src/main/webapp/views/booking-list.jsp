<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>My Bookings</title>

    <!-- CSS RIÊNG CHO BOOKING LIST -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/booking-list.css">
</head>

<body>

<div class="booking-container">
    <div class="back-home">
    <a href="${pageContext.request.contextPath}/home"
       class="back-link">
        ← Back to Home
    </a>
</div>


    <h1 class="page-title">My Bookings</h1>

    <!-- EMPTY STATE -->
    <c:if test="${empty BOOKINGS}">
        <div class="empty-state">
            <h3>No bookings found</h3>
            <p>You haven’t booked any cars yet.</p>
        </div>
    </c:if>

    <!-- BOOKING LIST -->
    <c:if test="${not empty BOOKINGS}">
    <div class="booking-grid">
        <c:forEach var="b" items="${BOOKINGS}">

            <a href="${pageContext.request.contextPath}/customer/bookings?action=detail&bookingId=${b.bookingId}"

               class="booking-card-link">

                <div class="booking-card">

                    <img class="car-image"
                         src="${pageContext.request.contextPath}/assets/images/cars/${b.imageFolder}/${b.imageFolder}_1.jpg"
                         alt="${b.carName}">

                    <div class="card-body">

                        <div class="car-name">${b.carName}</div>

                        <div class="booking-dates">
                            <fmt:formatDate value="${b.startDate}" pattern="dd/MM/yyyy"/>
                            →
                            <fmt:formatDate value="${b.endDate}" pattern="dd/MM/yyyy"/>
                        </div>

                        <div class="price">
                            <fmt:formatNumber value="${b.totalEstimatedPrice}"
                                              type="currency"
                                              currencySymbol="₫"/>
                        </div>

                        <span class="status ${b.status}">
                            ${b.status}
                        </span>

                    </div>
                </div>

            </a>

        </c:forEach>
    </div>
</c:if>


</div>

</body>
</html>
