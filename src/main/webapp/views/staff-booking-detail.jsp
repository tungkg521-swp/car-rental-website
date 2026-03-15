<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="models.BookingModel" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    BookingModel booking = (BookingModel) request.getAttribute("booking");
%>

<!DOCTYPE html>
<html>

<head>
    <title>Booking Detail</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/staff.css?v=3">
</head>

<body class="detail-page">

<div class="detail-container">

    <!-- ================= HEADER ================= -->

    <div class="detail-card">

        <div class="detail-info">

            <h1>Booking #<%= booking.getBookingId() %></h1>

            <span class="status-badge <%= booking.getStatus().toLowerCase() %>">
                <%= booking.getStatus() %>
            </span>

            <c:if test="${booking.status == 'REJECTED'}">
                <p class="status-message rejected">
                    This booking has been rejected.
                </p>
            </c:if>

            <c:if test="${booking.status == 'CONFIRMED'}">
                <p class="status-message confirmed">
                    This booking has been approved.
                </p>
            </c:if>

            <div class="specs">

                <div>
                    <strong>Booking Date:</strong>
                    <fmt:formatDate value="<%= booking.getBookingDate() %>"
                                    pattern="dd/MM/yyyy HH:mm"/>
                </div>

                <div>
                    <strong>Start Date:</strong>
                    <fmt:formatDate value="<%= booking.getStartDate() %>"
                                    pattern="dd/MM/yyyy"/>
                </div>

                <div>
                    <strong>End Date:</strong>
                    <fmt:formatDate value="<%= booking.getEndDate() %>"
                                    pattern="dd/MM/yyyy"/>
                </div>

                <div>
                    <strong>Total:</strong>
                    <fmt:formatNumber value="<%= booking.getTotalEstimatedPrice() %>"
                                      pattern="#,###"/> VND
                </div>

            </div>

        </div>

    </div>


    <!-- ================= CUSTOMER ================= -->

    <div class="detail-description">

        <h2>👤 Customer Information</h2>

        <div class="info-grid">

            <div>
                <strong>Name</strong>
                <p><%= booking.getCustomerName() %></p>
            </div>

            <div>
                <strong>Email</strong>
                <p><%= booking.getCustomerEmail() %></p>
            </div>

            <div>
                <strong>Phone</strong>
                <p><%= booking.getCustomerPhone() %></p>
            </div>

        </div>

    </div>


    <!-- ================= CAR ================= -->

    <div class="detail-description">

        <h2>🚗 Car Information</h2>

        <div class="info-grid">

            <img src="${pageContext.request.contextPath}/assets/images/cars/<%= booking.getImageFolder() %>/<%= booking.getImageFolder() %>_1.jpg"
                 class="car-preview">

            <div>
                <strong>Model</strong>
                <p><%= booking.getCarName() %></p>
            </div>

            <div>
                <strong>Price / Day</strong>
                <p>
                    <fmt:formatNumber value="<%= booking.getPricePerDay() %>"
                                      pattern="#,###"/> VND
                </p>
            </div>

        </div>

    </div>


    <!-- ================= RENTAL ================= -->

    <div class="detail-description">

        <h2>📅 Rental Summary</h2>

        <div class="info-grid">

            <div>
                <strong>Start Date</strong>
                <p>
                    <fmt:formatDate value="<%= booking.getStartDate() %>"
                                    pattern="dd/MM/yyyy"/>
                </p>
            </div>

            <div>
                <strong>End Date</strong>
                <p>
                    <fmt:formatDate value="<%= booking.getEndDate() %>"
                                    pattern="dd/MM/yyyy"/>
                </p>
            </div>

            <div>
                <strong>Total Price</strong>
                <p class="price-highlight">
                    <fmt:formatNumber value="<%= booking.getTotalEstimatedPrice() %>"
                                      pattern="#,###"/> VND
                </p>
            </div>

        </div>

    </div>


    <!-- ================= ACTION ================= -->

            <div class="detail-actions">

                <% if ("PENDING".equals(booking.getStatus())) {%>

                <form action="${pageContext.request.contextPath}/staff/bookings" method="post">

                    <input type="hidden" name="bookingId" value="<%= booking.getBookingId()%>">

                    <button type="submit"
                            name="action"
                            value="approve"
                            class="btn-approve"
                            onclick="return confirmApprove()">
                        ✓ Approve
                    </button>

                    <button type="submit"
                            name="action"
                            value="reject"
                            class="btn-reject"
                            onclick="return confirmReject()">
                        ✕ Reject
                    </button>

                </form>

                <% }%>

                <a href="${pageContext.request.contextPath}/staff/bookings"
                   class="btn-back">
                    Back
                </a>

            </div>

        </div>

        <script src="${pageContext.request.contextPath}/assets/js/staff-booking.js"></script>

    </body>