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
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff-booking.css?v=1">
    </head>

    <body>

        <div class="staff-layout">

            <%@ include file="sidebar.jsp" %>

            <div class="staff-content booking-detail-page">
                <div class="booking-detail-shell">



                    <c:choose>
                        <c:when test="${not empty booking}">
                            <div class="booking-detail-v2">

                                <!-- TOP BAR -->
                                <div class="booking-top-card">
                                    <div class="booking-top-left">
                                        <h2>Booking #${booking.bookingId}</h2>
                                    </div>

                                    <div class="booking-top-right">
                                        <span class="status-chip ${fn:toLowerCase(booking.status)}">
                                            ${booking.status}
                                        </span>
                                    </div>
                                </div>

                                <!-- CUSTOMER INFORMATION -->
                                <div class="booking-card customer-card">
                                    <h3 class="card-title">Customer Information</h3>

                                    <div class="customer-meta-grid">
                                        <div class="meta-item">
                                            <span class="meta-label">Name</span>
                                            <p>${booking.customerName}</p>
                                        </div>

                                        <div class="meta-item">
                                            <span class="meta-label">Email</span>
                                            <p>${booking.customerEmail}</p>
                                        </div>

                                        <div class="meta-item">
                                            <span class="meta-label">Phone</span>
                                            <p>${booking.customerPhone}</p>
                                        </div>
                                    </div>

                                    <div class="doc-section-title">Customer Documents</div>

                                    <div class="document-grid">
                                        <!-- Driver License Front -->
                                        <div class="document-item">
                                            <div class="document-thumb">
                                                <c:choose>
                                                    <c:when test="${not empty license.imageFront}">
                                                        <img src="${pageContext.request.contextPath}/license-image?name=${license.imageFront}" alt="Driver License Front">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="doc-empty">No image</div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <p>Driver License Front</p>
                                        </div>

                                        <!-- Driver License Back -->
                                        <div class="document-item">
                                            <div class="document-thumb">
                                                <c:choose>
                                                    <c:when test="${not empty license.imageBack}">
                                                        <img src="${pageContext.request.contextPath}/license-image?name=${license.imageBack}" alt="Driver License Back">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="doc-empty">No image</div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <p>Driver License Back</p>
                                        </div>

                                        <!-- Selfie -->
                                        <div class="document-item">
                                            <div class="document-thumb">
                                                <c:choose>
                                                    <c:when test="${not empty license.selfieImage}">
                                                        <img src="${pageContext.request.contextPath}/license-image?name=${license.selfieImage}" alt="Selfie with Driver License">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="doc-empty">No image</div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <p>Selfie with Driver License</p>
                                        </div>

                                        <!-- Citizen ID Front -->
                                        <div class="document-item">
                                            <div class="document-thumb">
                                                <c:choose>
                                                    <c:when test="${not empty license.nationalIdFront}">
                                                        <img src="${pageContext.request.contextPath}/license-image?name=${license.nationalIdFront}" alt="Citizen ID Front">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="doc-empty">No image</div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <p>Citizen ID Front</p>
                                        </div>

                                        <!-- Citizen ID Back -->
                                        <div class="document-item">
                                            <div class="document-thumb">
                                                <c:choose>
                                                    <c:when test="${not empty license.nationalIdBack}">
                                                        <img src="${pageContext.request.contextPath}/license-image?name=${license.nationalIdBack}" alt="Citizen ID Back">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="doc-empty">No image</div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <p>Citizen ID Back</p>
                                        </div>
                                    </div>
                                </div>

                                <!-- BOTTOM 2 COLUMNS -->
                                <div class="booking-bottom-grid">

                                    <!-- CAR INFORMATION -->
                                    <div class="booking-card">
                                        <h3 class="card-title">Car Information</h3>

                                        <div class="car-photo-wrap">
                                            <img src="${pageContext.request.contextPath}/assets/images/cars/${booking.imageFolder}/${booking.imageFolder}_1.jpg"
                                                 class="car-photo"
                                                 alt="Car Image">
                                        </div>

                                        <div class="car-info-grid">
                                            <div class="meta-item">
                                                <span class="meta-label">Model</span>
                                                <p>${booking.carName}</p>
                                            </div>

                                            <div class="meta-item">
                                                <span class="meta-label">Plate Number</span>
                                                <p>${booking.plateNumber}</p>
                                            </div>

                                            <div class="meta-item">
                                                <span class="meta-label">Price / Day</span>
                                                <p>
                                                    <fmt:formatNumber value="${booking.pricePerDay}" pattern="#,###"/> VND
                                                </p>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- BOOKING STATUS -->
                                    <div class="booking-card">
                                        <h3 class="card-title">Booking Status</h3>

                                        <c:if test="${booking.status == 'PENDING_APPROVAL'}">
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/staff/bookings"
                                                  class="booking-action-row">
                                                <input type="hidden" name="bookingId" value="${booking.bookingId}"/>

                                                <button type="submit"
                                                        name="action"
                                                        value="approve"
                                                        class="btn-status btn-approve"
                                                        onclick="return confirm('Approve this booking request?');">
                                                    ✓ Approve
                                                </button>

                                                <button type="submit"
                                                        name="action"
                                                        value="reject"
                                                        class="btn-status btn-reject"
                                                        onclick="return confirm('Reject this booking request?');">
                                                    ✕ Reject
                                                </button>
                                            </form>
                                        </c:if>

                                        <c:if test="${booking.status == 'REJECTED'}">
                                            <div class="status-note status-note-danger">
                                                This booking has been rejected.
                                            </div>
                                        </c:if>

                                        <c:if test="${booking.status == 'AWAITING_PAYMENT'}">
                                            <div class="status-note status-note-info">
                                                This booking has been approved and is waiting for customer payment.
                                            </div>
                                        </c:if>

                                        <div class="status-info-list">
                                            <div class="status-info-item">
                                                <span class="meta-label">Booking Date</span>
                                                <p><fmt:formatDate value="${booking.bookingDate}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
                                            </div>

                                            <div class="status-info-item">
                                                <span class="meta-label">Start Date</span>
                                                <p><fmt:formatDate value="${booking.startTime}" pattern="dd-MM-yyyy HH:mm:ss"/></p>
                                            </div>

                                            <div class="status-info-item">
                                                <span class="meta-label">End Date</span>
                                                <p><fmt:formatDate value="${booking.endTime}" pattern="dd-MM-yyyy HH:mm:ss"/></p>
                                            </div>

                                            <div class="status-info-item">
                                                <span class="meta-label">Total</span>
                                                <p class="total-price">
                                                    <fmt:formatNumber value="${booking.totalEstimatedPrice}" pattern="#,###"/> VND
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- FOOTER -->
                                <div class="booking-footer-row">
                                    <div class="footer-left">
                                        <c:if test="${booking.status == 'REFUND_PENDING'}">
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/car-change"
                                                  class="inline-form">
                                                <input type="hidden" name="action" value="refund"/>
                                                <input type="hidden" name="bookingId" value="${booking.bookingId}"/>

                                                <button type="submit"
                                                        class="btn-status btn-reject"
                                                        onclick="return confirm('Confirm that refund has been completed?');">
                                                    Mark Refund Completed
                                                </button>
                                            </form>
                                        </c:if>

                                        <c:if test="${booking.status == 'CONFIRMED' && not empty pendingCarChangeRequest}">
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/car-change"
                                                  class="inline-form">
                                                <input type="hidden" name="action" value="staffRejectRefund"/>
                                                <input type="hidden" name="bookingId" value="${booking.bookingId}"/>

                                                <button type="submit"
                                                        class="btn-status btn-warning"
                                                        onclick="return confirm('Reject on behalf of customer and mark refunded at counter?');">
                                                    Reject & Refund at Counter
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>

                                    <div class="footer-right">
                                        <a href="${pageContext.request.contextPath}/staff/bookings" class="btn-back-v2">
                                            Back to Booking List
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:when>

                        <c:otherwise>
                            <div class="detail-panel empty-panel">
                                <p class="empty-message">Booking not found.</p>

                                <div class="page-footer-actions">
                                    <div class="right-actions">
                                        <a href="${pageContext.request.contextPath}/staff/bookings" class="btn-back">
                                            Back to Booking List
                                        </a>
                                    </div>
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