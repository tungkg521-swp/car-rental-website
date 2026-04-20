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
                 data-cancel-status="${requestScope.cancelStatus}"
                 data-handover-status="${sessionScope.handoverStatus}">
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
                                    <span class="info-label">Start Time</span>
                                    <span class="info-value">
                                        <fmt:formatDate value="${booking.startTime}" pattern="dd/MM/yyyy HH:mm"/>
                                    </span>
                                </div>

                                <div class="info-row">
                                    <span class="info-label">End Time</span>
                                    <span class="info-value">
                                        <fmt:formatDate value="${booking.endTime}" pattern="dd/MM/yyyy HH:mm"/>
                                    </span>
                                </div>

                                <div class="info-row">
                                    <span class="info-label">Rental Duration</span>
                                    <span class="info-value">${rentalDurationText}</span>
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
                                <div class="car-change-customer-card">
                                    <div class="car-change-header">
                                        <div>
                                            <h3>Replacement Car Request</h3>
                                            <p>
                                                Your original car cannot be delivered. Our staff has prepared a replacement
                                                car for the same rental period.
                                            </p>
                                        </div>

                                        <span class="change-status-badge ${pendingCarChangeRequest.status == 'PENDING' ? 'status-pending' : 'status-done'}">
                                            ${pendingCarChangeRequest.status}
                                        </span>
                                    </div>

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

                                    <div class="change-meta-grid">
                                        <div class="change-meta-item">
                                            <span>Request ID</span>
                                            <p>#${pendingCarChangeRequest.requestId}</p>
                                        </div>

                                        <div class="change-meta-item">
                                            <span>Booking ID</span>
                                            <p>#${booking.bookingId}</p>
                                        </div>

                                        <div class="change-meta-item full-width">
                                            <span>Reason</span>
                                            <p>${pendingCarChangeRequest.reason}</p>
                                        </div>
                                    </div>

                                    <div class="change-car-compare">
                                        <div class="change-car-box">
                                            <div class="change-car-title">Current Car</div>

                                            <c:if test="${not empty oldCarChangeCar and not empty oldCarChangeCar.imageUrl}">
                                                <div class="change-car-image-wrap">
                                                    <img src="${pageContext.request.contextPath}/${oldCarChangeCar.imageUrl}"
                                                         alt="${oldCarChangeCar.modelName}">
                                                </div>
                                            </c:if>

                                            <div class="change-car-info">
                                                <p><strong>Model:</strong> ${oldCarChangeCar.modelName}</p>
                                                <p><strong>Type:</strong> ${oldCarChangeCar.typeName}</p>
                                                <p><strong>Seats:</strong> ${oldCarChangeCar.seatCount}</p>
                                                <p><strong>Fuel:</strong> ${oldCarChangeCar.fuelType}</p>
                                                <p><strong>Transmission:</strong> ${oldCarChangeCar.transmission}</p>
                                                <p><strong>Price/Day:</strong> ${oldCarChangeCar.pricePerDay}</p>
                                            </div>
                                        </div>

                                        <div class="change-car-box recommended">
                                            <div class="change-car-title">Replacement Car</div>

                                            <c:if test="${not empty newCarChangeCar and not empty newCarChangeCar.imageUrl}">
                                                <div class="change-car-image-wrap">
                                                    <img src="${pageContext.request.contextPath}/${newCarChangeCar.imageUrl}"
                                                         alt="${newCarChangeCar.modelName}">
                                                </div>
                                            </c:if>

                                            <div class="change-car-info">
                                                <p><strong>Model:</strong> ${newCarChangeCar.modelName}</p>
                                                <p><strong>Type:</strong> ${newCarChangeCar.typeName}</p>
                                                <p><strong>Seats:</strong> ${newCarChangeCar.seatCount}</p>
                                                <p><strong>Fuel:</strong> ${newCarChangeCar.fuelType}</p>
                                                <p><strong>Transmission:</strong> ${newCarChangeCar.transmission}</p>
                                                <p><strong>Price/Day:</strong> ${newCarChangeCar.pricePerDay}</p>
                                            </div>
                                        </div>
                                    </div>

                                    <c:if test="${pendingCarChangeRequest.status == 'PENDING'}">
                                        <div class="change-action-row">
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/car-change"
                                                  class="inline-form">
                                                <input type="hidden" name="action" value="respond"/>
                                                <input type="hidden" name="requestId" value="${pendingCarChangeRequest.requestId}"/>
                                                <input type="hidden" name="bookingId" value="${booking.bookingId}"/>
                                                <input type="hidden" name="decision" value="accept"/>

                                                <button type="submit"
                                                        class="btn btn-primary"
                                                        onclick="return confirm('Do you want to accept this replacement car?');">
                                                    Accept Replacement
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
                                                        onclick="return confirm('Do you want to reject this replacement car?');">
                                                    Reject Replacement
                                                </button>
                                            </form>
                                        </div>
                                    </c:if>
                                </div>
                            </c:if>

                            <c:if test="${not empty contract and (contract.contractStatus eq 'WAITING_CUSTOMER_CONFIRM' or contract.contractStatus eq 'ACTIVE' or contract.contractStatus eq 'COMPLETED')}">
                                <div class="note-box">
                                    <div class="note-title">Pre-Delivery Check Information</div>

                                    <c:choose>
                                        <c:when test="${not empty handoverCheck}">
                                            <div class="info-card" style="margin-top: 12px;">
                                                <div class="info-row">
                                                    <span class="info-label">Check Result</span>
                                                    <span class="info-value">${handoverCheck.checkResult}</span>
                                                </div>

                                                <div class="info-row">
                                                    <span class="info-label">Fuel Level</span>
                                                    <span class="info-value">${handoverCheck.fuelLevel}</span>
                                                </div>

                                                <div class="info-row">
                                                    <span class="info-label">Exterior Note</span>
                                                    <span class="info-value">${handoverCheck.exteriorNote}</span>
                                                </div>

                                                <div class="info-row">
                                                    <span class="info-label">Interior Note</span>
                                                    <span class="info-value">${handoverCheck.interiorNote}</span>
                                                </div>

                                                <div class="info-row">
                                                    <span class="info-label">Staff Note</span>
                                                    <span class="info-value">${handoverCheck.note}</span>
                                                </div>
                                            </div>
                                        </c:when>

                                        <c:otherwise>
                                            <p>No pre-delivery inspection information yet.</p>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>

                            <c:if test="${canCustomerConfirm}">
                                <div class="note-box">
                                    <div class="note-title">Vehicle Handover Confirmation</div>

                                    <form method="post"
                                          action="${pageContext.request.contextPath}/booking"
                                          id="customerHandoverForm">

                                        <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                        <input type="hidden" name="contractId" value="${contract.contractId}">

                                        <div style="margin: 14px 0;">
                                            <label style="display:block; margin-bottom:10px;">
                                                <input type="checkbox" id="reviewedCheck">
                                                I have reviewed the vehicle condition.
                                            </label>

                                            <label style="display:block; margin-bottom:10px;">
                                                <input type="checkbox" id="agreeReceive">
                                                I agree to receive this vehicle.
                                            </label>
                                        </div>

                                        <div class="note-title" style="margin-bottom:8px;">Customer Note</div>
                                        <textarea name="customerNote"
                                                  style="width:100%; min-height:100px; border:1px solid #ddd; border-radius:8px; padding:12px; resize:vertical;"></textarea>

                                        <div class="action-buttons" style="margin-top:16px;">
                                            <button type="submit"
                                                    name="action"
                                                    value="confirmHandover"
                                                    class="btn btn-primary"
                                                    onclick="return validateCustomerConfirm();">
                                                Confirm Vehicle Handover
                                            </button>

                                            <button type="submit"
                                                    name="action"
                                                    value="rejectHandover"
                                                    class="btn btn-danger"
                                                    onclick="return confirm('Are you sure you want to reject this vehicle?');">
                                                Reject Vehicle
                                            </button>
                                        </div>
                                    </form>
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