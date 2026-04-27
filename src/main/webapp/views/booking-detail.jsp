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

        <section class="booking-detail-page">
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
                            Tạo đơn đặt xe thành công.
                        </div>
                    </c:if>

                    <c:if test="${not empty handoverMessage}">
                        <div class="alert alert-success">
                            ${handoverMessage}
                        </div>
                    </c:if>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">
                            ${errorMessage}
                        </div>
                    </c:if>

                    <c:if test="${cancelStatus == 'success'}">
                        <div class="alert alert-success">
                            Hủy đơn đặt xe thành công.
                        </div>
                    </c:if>

                    <c:if test="${cancelStatus == 'fail'}">
                        <div class="alert alert-danger">
                            Hủy đơn đặt xe thất bại. Vui lòng thử lại.
                        </div>
                    </c:if>

                    <c:if test="${param.deleteStatus == 'fail'}">
                        <div class="alert alert-danger">
                            Xóa đơn đặt xe thất bại. Đơn này có thể không còn được phép xóa.
                        </div>
                    </c:if>

                    <c:if test="${param.paymentStatus == 'success'}">
                        <div class="alert alert-success">
                            Thanh toán tiền cọc thành công.
                        </div>
                    </c:if>

                    <c:if test="${param.paymentStatus == 'expired'}">
                        <div class="alert alert-danger">
                            Thanh toán đã hết hạn. Đơn đặt xe đã bị hủy.
                        </div>
                    </c:if>

                    <c:if test="${param.paymentStatus == 'invalid'}">
                        <div class="alert alert-danger">
                            Không thể thanh toán đơn này. Trạng thái đơn đặt xe không hợp lệ.
                        </div>
                    </c:if>

                    <c:if test="${param.paymentStatus == 'cancelled'}">
                        <div class="alert alert-warning">
                            Bạn đã hủy thanh toán.
                        </div>
                    </c:if>

                    <!-- MAIN CARD -->
                    <div class="detail-card">

                        <!-- TOP ROW -->
                        <div class="detail-top-grid">

                            <!-- LEFT: GALLERY -->
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

                            <!-- RIGHT: BOOKING INFO -->
                            <div class="detail-right">
                                <div class="detail-main-card">

                                    <div class="detail-header">
                                        <div class="detail-header-main">
                                            <h2>${booking.carName}</h2>
                                        </div>

                                        <div class="detail-header-status">
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

                                                <c:when test="${booking.status == 'REFUND_PENDING'}">
                                                    <span class="status-badge awaiting">Waiting for Refund</span>
                                                </c:when>

                                                <c:when test="${booking.status == 'REFUNDED'}">
                                                    <span class="status-badge completed">Refunded</span>
                                                </c:when>

                                                <c:otherwise>
                                                    <span class="status-badge default">${booking.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>

                                    <div class="section-card booking-info-section">
                                        <div class="section-heading">
                                            <div>
                                                <h3>Booking Information</h3>
                                                <span>Main booking details</span>
                                            </div>
                                        </div>

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

                                            <div class="info-row">
                                                <span class="info-label">Tiền cọc và dằn xe</span>

                                                <fmt:formatNumber value="${booking.depositAmount+10000000}" type="number" groupingUsed="true"/>
                                                VND
                                            </div>

                                            <div class="info-row total-row">
                                                <span class="info-label">Total Price</span>
                                                <span class="info-value total-price">
                                                    <fmt:formatNumber value="${booking.totalEstimatedPrice}" type="number" groupingUsed="true"/>
                                                    VND
                                                </span>
                                            </div>
                                        </div>
                                    </div>

                                    <c:if test="${not empty booking.note}">
                                        <div class="section-card customer-note-section">
                                            <div class="section-heading">
                                                <div>
                                                    <h3>Customer Note</h3>
                                                    <span>Additional message from customer</span>
                                                </div>
                                            </div>

                                            <div class="note-box">
                                                <p>${booking.note}</p>
                                            </div>
                                        </div>
                                    </c:if>

                                </div>
                            </div>
                        </div>

                        <!-- BOTTOM ROW -->
                        <div class="detail-bottom-full">

                            <!-- PRE DELIVERY CHECK -->
                            <div class="section-card precheck-section">
                                <div class="section-heading">
                                    <div>
                                        <h3>Pre-Delivery Check</h3>
                                        <span>Vehicle condition before handover</span>
                                    </div>
                                </div>

                                <c:choose>
                                    <c:when test="${not empty contract and (contract.contractStatus eq 'WAITING_CUSTOMER_CONFIRM' or contract.contractStatus eq 'ACTIVE' or contract.contractStatus eq 'COMPLETED')}">
                                        <c:choose>
                                            <c:when test="${not empty handoverCheck}">
                                                <div class="info-card precheck-card">
                                                    <div class="info-row">
                                                        <span class="info-label">Check Result</span>
                                                        <span class="info-value">${handoverCheck.checkResult}</span>
                                                    </div>

                                                    <div class="info-row">
                                                        <span class="info-label">Fuel Level</span>
                                                        <span class="info-value">${handoverCheck.fuelLevel}</span>
                                                    </div>

                                                    <div class="info-row">
                                                        <div class="info-label">Odometer</div>
                                                        <div class="info-value">
                                                            <c:choose>
                                                                <c:when test="${not empty handoverCheck and handoverCheck.odometerKm != null}">
                                                                    <fmt:formatNumber value="${handoverCheck.odometerKm}" type="number"/> km
                                                                </c:when>
                                                                <c:otherwise>Not available</c:otherwise>
                                                            </c:choose>
                                                        </div>
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
                                                <div class="precheck-empty">
                                                    <div class="precheck-empty-icon">🛠</div>
                                                    <div class="precheck-empty-content">
                                                        <strong>No pre-delivery inspection information yet.</strong>
                                                        <p>Inspection information will appear here after staff processes the vehicle handover.</p>
                                                    </div>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>

                                    <c:otherwise>
                                        <div class="precheck-empty">
                                            <div class="precheck-empty-icon">🛠</div>
                                            <div class="precheck-empty-content">
                                                <strong>Pre-check not available yet.</strong>
                                                <p>This area is reserved for the staff pre-delivery inspection in the next step.</p>
                                            </div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>



                            <!-- NOTE -->
                            <c:if test="${not empty booking.note}">
                                <div class="note-box">
                                    <div class="note-title">Customer Note</div>
                                    <p>${booking.note}</p>
                                </div>
                            </c:if>

                            <c:if test="${not empty carChangeRequest}">
                                <div class="section-card car-change-section">
                                    <div class="section-heading">
                                        <div>
                                            <h3>Replacement Car Request</h3>
                                            <span>Replacement vehicle proposed by staff</span>
                                        </div>
                                    </div>

                                    <div class="car-change-customer-card">
                                        <div class="car-change-header">
                                            <div>
                                                <h3>Replacement Car Request</h3>
                                                <p>
                                                    Your original car cannot be delivered. Our staff has prepared a replacement
                                                    car for the same rental period.
                                                </p>
                                            </div>

                                            <span class="change-status-badge ${carChangeRequest.status == 'PENDING' ? 'status-pending' : 'status-done'}">
                                                ${carChangeRequest.status}
                                            </span>
                                        </div>

                                        <c:if test="${param.changeResponse == 'success'}">
                                            <div class="alert alert-success">
                                                Phản hồi yêu cầu đổi xe thành công.
                                            </div>
                                        </c:if>

                                        <c:if test="${param.changeResponse == 'fail'}">
                                            <div class="alert alert-danger">
                                                Xử lý phản hồi thất bại. Xe thay thế có thể không còn khả dụng.
                                            </div>
                                        </c:if>

                                        <div class="change-meta-grid">
                                            <div class="change-meta-item">
                                                <span>Request ID</span>
                                                <p>#${carChangeRequest.requestId}</p>
                                            </div>

                                            <div class="change-meta-item">
                                                <span>Booking ID</span>
                                                <p>#${booking.bookingId}</p>
                                            </div>

                                            <div class="change-meta-item full-width">
                                                <span>Reason</span>
                                                <p>${carChangeRequest.reason}</p>
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
                                                    <p><strong>Plate Number:</strong> ${oldCarChangeCar.plateNumber}</p>
                                                    <p><strong>Type:</strong> ${oldCarChangeCar.typeName}</p>
                                                    <p><strong>Seats:</strong> ${oldCarChangeCar.seatCount}</p>
                                                    <p><strong>Fuel:</strong> ${oldCarChangeCar.fuelType}</p>
                                                    <p><strong>Transmission:</strong> ${oldCarChangeCar.transmission}</p>
                                                    <p><strong>Price/Day:</strong> ${oldCarChangeCar.pricePerDay}</p>
                                                </div>
                                            </div>

                                            <c:if test="${not empty newCarChangeCar}">
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
                                                        <p><strong>Plate Number:</strong> ${newCarChangeCar.plateNumber}</p>
                                                        <p><strong>Type:</strong> ${newCarChangeCar.typeName}</p>
                                                        <p><strong>Seats:</strong> ${newCarChangeCar.seatCount}</p>
                                                        <p><strong>Fuel:</strong> ${newCarChangeCar.fuelType}</p>
                                                        <p><strong>Transmission:</strong> ${newCarChangeCar.transmission}</p>
                                                        <p><strong>Price/Day:</strong> ${newCarChangeCar.pricePerDay}</p>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </div>

                                        <c:if test="${carChangeRequest.status == 'PENDING' && carChangeRequest.requestedBy == 'STAFF'}">
                                            <div class="change-action-row">
                                                <form method="post"
                                                      action="${pageContext.request.contextPath}/car-change"
                                                      class="inline-form">
                                                    <input type="hidden" name="action" value="respond"/>
                                                    <input type="hidden" name="requestId" value="${carChangeRequest.requestId}"/>
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
                                                    <input type="hidden" name="requestId" value="${carChangeRequest.requestId}"/>
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


                                        <c:if test="${carChangeRequest.status == 'APPROVED'}">
                                            <div class="alert alert-success" style="margin-top:16px;">
                                                Bạn đã chấp nhận xe thay thế. Đơn đặt xe đã được cập nhật.
                                            </div>
                                        </c:if>

                                        <c:if test="${carChangeRequest.status == 'REJECTED'}">
                                            <div class="alert alert-danger" style="margin-top:16px;">
                                                Bạn đã từ chối xe thay thế. Nhân viên sẽ xử lý hoàn tiền.
                                            </div>
                                        </c:if>

                                        <c:if test="${carChangeRequest.status == 'CANCELLED'}">
                                            <div class="alert alert-warning" style="margin-top:16px;">
                                                Yêu cầu đổi xe này không còn khả dụng.
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </c:if>

                            <!-- CUSTOMER HANDOVER CONFIRM -->
                            <c:if test="${canCustomerConfirm}">
                                <div class="section-card handover-section">
                                    <div class="section-heading">
                                        <div>
                                            <h3>Vehicle Handover Confirmation</h3>
                                            <span>Customer confirmation before receiving vehicle</span>
                                        </div>
                                    </div>

                                    <div class="note-box">
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/booking"
                                              id="customerHandoverForm">

                                            <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                            <input type="hidden" name="contractId" value="${contract.contractId}">

                                            <div class="handover-check-list" style="margin: 14px 0;">
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
                                                        onclick="return confirm('Are you sure you want to cancel this booking and request refund?');">
                                                    Cancel Booking & Refund
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </c:if>

                            <!-- ACTION FOOTER -->
                            <div class="detail-action-bar">
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

                                    <c:if test="${booking.status == 'AWAITING_PAYMENT' && empty carChangeRequest}">
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