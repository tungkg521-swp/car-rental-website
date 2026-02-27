<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Booking Detail</title>

        <!-- Global CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style-base.css">

        <!-- Booking Detail CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/booking-detail.css">
    </head>

    <body>

        <jsp:include page="includes/header.jsp"/>

        <section class="booking-detail-page">
            <div class="container">

                <div class="detail-card">

                    <!-- LEFT IMAGE -->
                    <div class="detail-left">
                        <img src="${pageContext.request.contextPath}/assets/images/cars/${booking.imageFolder}/${booking.imageFolder}_1.jpg"
                             alt="${booking.carName}">
                    </div>

                    <!-- RIGHT INFO -->
                    <div class="detail-right">

                        <h2>Booking #${booking.bookingId}</h2>

                        <!-- STATUS BADGE -->
                        <div class="status ${booking.status}">
                            ${booking.status}
                        </div>

                        <!-- BOOKING INFO -->
                        <ul class="detail-info">
                            <li><strong>Car:</strong> ${booking.carName}</li>

                            <li><strong>Customer:</strong> ${booking.customerName}</li>
                            <li><strong>Email:</strong> ${booking.customerEmail}</li>
                            <li><strong>Phone:</strong> ${booking.customerPhone}</li>

                            <li>
                                <strong>Booking date:</strong>
                                <fmt:formatDate value="${booking.bookingDate}" pattern="dd/MM/yyyy HH:mm"/>
                            </li>

                            <li>
                                <strong>Start date:</strong>
                                <fmt:formatDate value="${booking.startDate}" pattern="dd/MM/yyyy"/>
                            </li>

                            <li>
                                <strong>End date:</strong>
                                <fmt:formatDate value="${booking.endDate}" pattern="dd/MM/yyyy"/>
                            </li>

                            <li>
                                <strong>Total price:</strong>
                                <span class="price">
                                    <fmt:formatNumber value="${booking.totalEstimatedPrice}" type="number" groupingUsed="true"/>
                                    VND
                                </span>
                            </li>
                        </ul>

                        <!-- NOTE -->
                        <c:if test="${not empty booking.note}">
                            <div class="note-box">
                                <strong>Note:</strong>
                                <p>${booking.note}</p>
                            </div>
                        </c:if>

                        <!-- ACTION BUTTON -->
                        <div class="action-buttons">

                            <!-- BACK BUTTON -->
                            <a href="${pageContext.request.contextPath}/customer/bookings?action=list"
                               class="btn-back">
                                ← Back to My Bookings
                            </a>

                            <!-- CANCEL BUTTON (only when PENDING) -->
                            <c:if test="${booking.status == 'PENDING'}">
                                <form method="post"
                                      action="${pageContext.request.contextPath}/booking"
                                      style="display:inline-block;">

                                    <input type="hidden" name="action" value="cancel"/>
                                    <input type="hidden" name="bookingId"
                                           value="${booking.bookingId}"/>

                                    <button type="submit"
                                            class="btn-cancel"
                                            onclick="return confirm('Are you sure you want to cancel this booking?');">
                                        Cancel Booking
                                    </button>
                                </form>
                            </c:if>

                        </div>


                    </div>

                </div>

            </div>
        </section>

    </body>
</html>
