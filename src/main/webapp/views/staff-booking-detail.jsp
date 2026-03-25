
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Booking Detail</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/staff.css?v=3">
</head>

<body>

<div class="staff-layout">

    <%@ include file="sidebar.jsp" %>

    <div class="staff-content detail-page">
        <div class="detail-container">

            <c:choose>
                <c:when test="${not empty booking}">

                    <div class="detail-card">
                        <div class="detail-info">

                            <h1>Booking #${booking.bookingId}</h1>

                            <span class="status-badge ${fn:toLowerCase(booking.status)}">
                                ${booking.status}
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
                                    ${booking.bookingDate}
                                </div>

                                <div>
                                    <strong>Start Date:</strong>
                                    ${booking.startDate}
                                </div>

                                <div>
                                    <strong>End Date:</strong>
                                    ${booking.endDate}
                                </div>

                                <div>
                                    <strong>Total:</strong>
                                    <fmt:formatNumber value="${booking.totalEstimatedPrice}" pattern="#,###"/>
                                    VND
                                </div>
                            </div>

                        </div>
                    </div>

                    <div class="detail-description">
                        <h2>👤 Customer Information</h2>

                        <div class="info-grid">
                            <div>
                                <strong>Name</strong>
                                <p>${booking.customerName}</p>
                            </div>

                            <div>
                                <strong>Email</strong>
                                <p>${booking.customerEmail}</p>
                            </div>

                            <div>
                                <strong>Phone</strong>
                                <p>${booking.customerPhone}</p>
                            </div>
                        </div>
                    </div>

                    <div class="detail-description">
                        <h2>🚗 Car Information</h2>

                        <div class="info-grid">
                            <img src="${pageContext.request.contextPath}/assets/images/cars/${booking.imageFolder}/${booking.imageFolder}_1.jpg"
                                 class="car-preview"
                                 alt="Car Image">

                            <div>
                                <strong>Model</strong>
                                <p>${booking.carName}</p>
                            </div>

                            <div>
                                <strong>Price / Day</strong>
                                <p>
                                    <fmt:formatNumber value="${booking.pricePerDay}" pattern="#,###"/>
                                    VND
                                </p>
                            </div>
                        </div>
                    </div>

                    <div class="detail-description">
                        <h2>📅 Rental Summary</h2>

                        <div class="info-grid">
                            <div>
                                <strong>Start Date</strong>
                                <p>${booking.startDate}</p>
                            </div>

                            <div>
                                <strong>End Date</strong>
                                <p>${booking.endDate}</p>
                            </div>

                            <div>
                                <strong>Total Price</strong>
                                <p class="price-highlight">
                                    <fmt:formatNumber value="${booking.totalEstimatedPrice}" pattern="#,###"/>
                                    VND
                                </p>
                            </div>
                        </div>
                    </div>

                    <div class="detail-actions">

                        <c:if test="${booking.status == 'DEPOSIT_PAID'}">
                            <form action="${pageContext.request.contextPath}/staff/bookings" method="post">
                                <input type="hidden" name="bookingId" value="${booking.bookingId}">

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
                        </c:if>

                        <a href="${pageContext.request.contextPath}/staff/bookings" class="btn-back">
                            Back
                        </a>
                    </div>

                </c:when>

                <c:otherwise>
                    <div class="detail-card">
                        <p style="text-align:center; padding:20px;">Booking not found.</p>
                        <div class="detail-actions">
                            <a href="${pageContext.request.contextPath}/staff/bookings" class="btn-back">
                                Back
                            </a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>

        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/staff-booking.js"></script>

</body>
</html>