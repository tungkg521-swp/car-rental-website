<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Booking Detail</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style-base.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/booking-detail.css">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=99">
    </head>

    <body>
        <jsp:include page="/views/includes/header.jsp"/>


        <section class="booking-detail-page"
                 data-cancel-status="${requestScope.cancelStatus}">
            <div class="container">

                <div class="booking-detail-shell">

                    <!-- TOP BAR -->
                    <div class="detail-topbar">
                        <div class="topbar-left">
                            <p class="page-kicker">Customer Booking</p>
                            <h1>Booking Detail</h1>
                        </div>
                        <div class="topbar-right">
                            <div class="booking-code-card">
                                <span class="booking-code-label">Booking ID</span>
                                <strong>#${booking.bookingId}</strong>
                            </div>
                        </div>
                    </div>
                    <c:if test="${param.created == '1'}">
                        <div class="alert alert-success">
                            Booking request created successfully. Please wait for staff approval.
                        </div>
                    </c:if>
                    <!-- MAIN CARD -->
                    <div class="detail-card">

                        <!-- LEFT -->
                        <div class="detail-left">
                            <div class="car-gallery">

                                <div class="main-image-box">
                                    <button type="button" class="gallery-nav prev" onclick="changeImage(-1)">‹</button>

                                    <img id="mainCarImage"
                                         src="${pageContext.request.contextPath}/assets/images/cars/${booking.imageFolder}/${booking.imageFolder}_1.jpg"
                                         alt="${booking.carName}">

                                    <button type="button" class="gallery-nav next" onclick="changeImage(1)">›</button>
                                </div>

                                <div class="thumbnail-list">
                                    <img class="thumb active-thumb"
                                         src="${pageContext.request.contextPath}/assets/images/cars/${booking.imageFolder}/${booking.imageFolder}_1.jpg"
                                         alt="Thumbnail 1"
                                         onclick="setMainImage(this, 0)">

                                    <img class="thumb"
                                         src="${pageContext.request.contextPath}/assets/images/cars/${booking.imageFolder}/${booking.imageFolder}_2.jpg"
                                         alt="Thumbnail 2"
                                         onclick="setMainImage(this, 1)">

                                    <img class="thumb"
                                         src="${pageContext.request.contextPath}/assets/images/cars/${booking.imageFolder}/${booking.imageFolder}_3.jpg"
                                         alt="Thumbnail 3"
                                         onclick="setMainImage(this, 2)">

                                    <img class="thumb"
                                         src="${pageContext.request.contextPath}/assets/images/cars/${booking.imageFolder}/${booking.imageFolder}_4.jpg"
                                         alt="Thumbnail 4"
                                         onclick="setMainImage(this, 3)">

                                    <img class="thumb"
                                         src="${pageContext.request.contextPath}/assets/images/cars/${booking.imageFolder}/${booking.imageFolder}_5.jpg"
                                         alt="Thumbnail 5"
                                         onclick="setMainImage(this, 4)">
                                </div>

                            </div>
                        </div>

                        <!-- RIGHT -->
                        <div class="detail-right">

                            <div class="detail-header">
                                <div>
                                    <h2>${booking.carName}</h2>
                                </div>

                                <!-- STATUS BADGE -->
                                <c:choose>
                                    <c:when test="${booking.status == 'PENDING_APPROVAL'}">
                                        <span class="status-badge pending">Waiting for Staff Approval</span>
                                    </c:when>

                                    <c:when test="${booking.status == 'AWAITING_PAYMENT'}">
                                        <span class="status-badge awaiting">Awaiting Payment</span>
                                    </c:when>

                                    <c:when test="${booking.status == 'CONFIRMED'}">
                                        <span class="status-badge confirmed">Confirmed</span>
                                    </c:when>

                                    <c:when test="${booking.status == 'ACTIVE'}">
                                        <span class="status-badge active">Renting</span>
                                    </c:when>

                                    <c:when test="${booking.status == 'COMPLETED'}">
                                        <span class="status-badge completed">Completed</span>
                                    </c:when>

                                    <c:when test="${booking.status == 'REJECTED'}">
                                        <span class="status-badge rejected">Rejected</span>
                                    </c:when>

                                    <c:when test="${booking.status == 'CANCELLED'}">
                                        <span class="status-badge cancelled">Cancelled</span>
                                    </c:when>

                                    <c:otherwise>
                                        <span class="status-badge default">${booking.status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <!-- INFO GRID -->
                            <div class="info-card">
                                <div class="info-row">
                                    <span class="info-label">Customer</span>
                                    <span class="info-value">${booking.customerName}</span>
                                </div>

                                <div class="info-row">
                                    <span class="info-label">Email</span>
                                    <span class="info-value">${booking.customerEmail}</span>
                                </div>

                                <div class="info-row">
                                    <span class="info-label">Phone</span>
                                    <span class="info-value">${booking.customerPhone}</span>
                                </div>

                                <div class="info-row">
                                    <span class="info-label">Booking Date</span>
                                    <span class="info-value">
                                        <fmt:formatDate value="${booking.bookingDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </span>
                                </div>

                                <div class="info-row">
                                    <span class="info-label">Start Date</span>
                                    <span class="info-value">
                                        <fmt:formatDate value="${booking.startDate}" pattern="dd/MM/yyyy"/>
                                    </span>
                                </div>

                                <div class="info-row">
                                    <span class="info-label">End Date</span>
                                    <span class="info-value">
                                        <fmt:formatDate value="${booking.endDate}" pattern="dd/MM/yyyy"/>
                                    </span>
                                </div>

                                <div class="info-row total-row">
                                    <span class="info-label">Total Price</span>
                                    <span class="info-value total-price">
                                        <fmt:formatNumber value="${booking.totalEstimatedPrice}" type="number" groupingUsed="true"/>
                                        VND
                                    </span>
                                </div>
                            </div>

                            <!-- NOTE -->
                            <c:if test="${not empty booking.note}">
                                <div class="note-box">
                                    <div class="note-title">Customer Note</div>
                                    <p>${booking.note}</p>
                                </div>
                            </c:if>

                            <c:if test="${not empty pendingCarChangeRequest}">
                                <div class="info-card" style="margin-top: 20px;">
                                    <h3 style="margin-bottom: 12px;">Car Change Request</h3>

                                    <c:if test="${param.changeResponse == 'success'}">
                                        <div class="alert alert-success">
                                            Your response to the car change request was submitted successfully.
                                        </div>
                                    </c:if>

                                    <c:if test="${param.changeResponse == 'fail'}">
                                        <div class="alert alert-danger">
                                            Failed to process your response. The replacement car may no longer be available.
                                        </div>
                                    </c:if>

                                    <div class="info-row">
                                        <span class="info-label">Request ID</span>
                                        <span class="info-value">#${pendingCarChangeRequest.requestId}</span>
                                    </div>

                                    <div class="info-row">
                                        <span class="info-label">Status</span>
                                        <span class="info-value">${pendingCarChangeRequest.status}</span>
                                    </div>

                                    <div class="info-row">
                                        <span class="info-label">Old Car ID</span>
                                        <span class="info-value">${pendingCarChangeRequest.oldCarId}</span>
                                    </div>

                                    <div class="info-row">
                                        <span class="info-label">New Car ID</span>
                                        <span class="info-value">${pendingCarChangeRequest.newCarId}</span>
                                    </div>

                                    <div class="info-row">
                                        <span class="info-label">Reason</span>
                                        <span class="info-value">${pendingCarChangeRequest.reason}</span>
                                    </div>

                                    <c:if test="${pendingCarChangeRequest.status == 'PENDING'}">
                                        <div style="display:flex; gap:12px; margin-top:16px; flex-wrap:wrap;">
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/car-change"
                                                  class="inline-form">
                                                <input type="hidden" name="action" value="respond"/>
                                                <input type="hidden" name="requestId" value="${pendingCarChangeRequest.requestId}"/>
                                                <input type="hidden" name="bookingId" value="${booking.bookingId}"/>
                                                <input type="hidden" name="decision" value="accept"/>

                                                <button type="submit"
                                                        class="btn btn-primary"
                                                        onclick="return confirm('Do you want to accept this car change request?');">
                                                    Accept Car Change
                                                </button>
                                            </form>

                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/car-change"
                                                  class="inline-form">
                                                <input type="hidden" name="action" value="respond"/>
                                                <input type="hidden" name="requestId" value="${pendingCarChangeRequest.requestId}"/>
                                                <input type="hidden" name="bookingId" value="${booking.bookingId}"/>
                                                <input type="hidden" name="decision" value="reject"/>

                                                <button type="submit"
                                                        class="btn btn-danger"
                                                        onclick="return confirm('Do you want to reject this car change request?');">
                                                    Reject Car Change
                                                </button>
                                            </form>
                                        </div>
                                    </c:if>
                                </div>
                            </c:if>

                            <!-- ACTIONS -->
                            <div class="action-buttons">
                                <a href="${pageContext.request.contextPath}/booking?action=list"
                                   class="btn btn-secondary">
                                    ← Back to My Bookings
                                </a>

                                <c:if test="${booking.status == 'PENDING_APPROVAL' or booking.status == 'AWAITING_PAYMENT'}">
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/booking"
                                          class="inline-form">

                                        <input type="hidden" name="action" value="cancel"/>
                                        <input type="hidden" name="bookingId" value="${booking.bookingId}"/>

                                        <button type="submit"
                                                class="btn btn-danger"
                                                onclick="return confirm('Are you sure you want to cancel this booking?');">
                                            Cancel Booking
                                        </button>
                                    </form>
                                </c:if>


                                <c:if test="${booking.status == 'AWAITING_PAYMENT' && empty pendingCarChangeRequest}">
                                    <a href="${pageContext.request.contextPath}/payment?action=create&bookingId=${booking.bookingId}"
                                       class="btn btn-primary">
                                        Thanh toán ngay
                                    </a>
                                </c:if>
                                <c:if test="${booking.status == 'CANCELLED'}">
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/booking"
                                          class="inline-form">

                                        <input type="hidden" name="action" value="delete"/>
                                        <input type="hidden" name="bookingId" value="${booking.bookingId}"/>

                                        <button type="submit"
                                                class="btn btn-delete"
                                                onclick="return confirm('Are you sure you want to delete this cancelled booking?');">
                                            Delete Booking
                                        </button>
                                    </form>
                                </c:if>
                            </div>

                        </div>
                    </div>

                </div>
            </div>
        </section>

        <div id="popupOverlay" class="popup-overlay">
            <div class="popup-box" id="popupBox">
                <div class="popup-icon" id="popupIcon">✓</div>
                <h3 id="popupTitle">Notification</h3>
                <p id="popupMessage">Message here</p>
                <button type="button" class="popup-btn" onclick="closePopup()">OK</button>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/assets/js/booking-detail.js?v=1"></script>
    </body>
</html>