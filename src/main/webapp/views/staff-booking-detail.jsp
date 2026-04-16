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

                            <!-- HERO SUMMARY -->
                            <div class="booking-hero-card">
                                <div class="booking-hero-left">
                                    <div class="booking-id-row">
                                        <h2>Booking #${booking.bookingId}</h2>
                                        <span class="status-badge ${fn:toLowerCase(booking.status)}">
                                            ${booking.status}
                                        </span>
                                    </div>

                                    <p class="booking-hero-text">
                                        Review booking progress, customer details, selected car, and rental duration.
                                    </p>
                                </div>

                                <div class="booking-hero-right">
                                    <div class="hero-stat">
                                        <span class="hero-stat-label">Booking Date</span>
                                        <span class="hero-stat-value">${booking.bookingDate}</span>
                                    </div>

                                    <div class="hero-stat">
                                        <span class="hero-stat-label">Start Date</span>
                                        <span class="hero-stat-value">${booking.startDate}</span>
                                    </div>

                                    <div class="hero-stat">
                                        <span class="hero-stat-label">End Date</span>
                                        <span class="hero-stat-value">${booking.endDate}</span>
                                    </div>

                                    <div class="hero-stat">
                                        <span class="hero-stat-label">Total</span>
                                        <span class="hero-stat-value">
                                            <fmt:formatNumber value="${booking.totalEstimatedPrice}" pattern="#,###"/>
                                            VND
                                        </span>
                                    </div>
                                </div>
                            </div>

                            <!-- MAIN GRID -->
                            <div class="booking-detail-grid">

                                <div class="detail-panel">
                                    <h3>Booking Status</h3>

                                    <c:if test="${booking.status == 'REJECTED'}">
                                        <p class="status-message rejected">
                                            This booking has been rejected.
                                        </p>
                                    </c:if>

                                    <c:if test="${booking.status == 'AWAITING_PAYMENT'}">
                                        <div class="info-message">
                                            This booking has been approved and is waiting for customer payment.
                                        </div>
                                    </c:if>

                                    <c:if test="${booking.status == 'PENDING_APPROVAL'}">
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/staff/bookings"
                                              class="action-form">

                                            <input type="hidden" name="bookingId" value="${booking.bookingId}"/>

                                            <button type="submit"
                                                    name="action"
                                                    value="approve"
                                                    class="btn btn-success"
                                                    onclick="return confirm('Approve this booking request?');">
                                                Approve
                                            </button>

                                            <button type="submit"
                                                    name="action"
                                                    value="reject"
                                                    class="btn btn-danger"
                                                    onclick="return confirm('Reject this booking request?');">
                                                Reject
                                            </button>
                                        </form>
                                    </c:if>

                                    <div class="info-list compact-list">
                                        <div class="info-item-block">
                                            <strong>Booking Date</strong>
                                            <p>${booking.bookingDate}</p>
                                        </div>

                                        <div class="info-item-block">
                                            <strong>Start Date</strong>
                                            <p>${booking.startDate}</p>
                                        </div>

                                        <div class="info-item-block">
                                            <strong>End Date</strong>
                                            <p>${booking.endDate}</p>
                                        </div>

                                        <div class="info-item-block">
                                            <strong>Total</strong>
                                            <p class="price-highlight">
                                                <fmt:formatNumber value="${booking.totalEstimatedPrice}" pattern="#,###"/>
                                                VND
                                            </p>
                                        </div>
                                    </div>
                                </div>

                                <div class="detail-panel">
                                    <h3>Customer Information</h3>

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

                                <div class="detail-panel">
                                    <h3>Car Information</h3>

                                    <div class="car-info-block">
                                        <img src="${pageContext.request.contextPath}/assets/images/cars/${booking.imageFolder}/${booking.imageFolder}_1.jpg"
                                             class="car-preview-large"
                                             alt="Car Image">

                                        <div class="info-grid">
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
                                </div>

                                <div class="detail-panel">
                                    <h3>Rental Summary</h3>

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

                            </div>

                            <!-- CAR CHANGE REQUEST -->
                            <c:if test="${booking.status == 'AWAITING_PAYMENT' || booking.status == 'CONFIRMED'}">
                                <div class="detail-panel full-width-panel">
                                    <h3>Car Change Request</h3>
                                    <p class="section-note">Manage replacement vehicle requests for this booking.</p>

                                    <c:if test="${param.changeRequest == 'success'}">
                                        <div class="message-success">
                                            Car change request created successfully.
                                        </div>
                                    </c:if>

                                    <c:if test="${param.changeRequest == 'fail'}">
                                        <div class="message-error">
                                            Failed to create car change request.
                                        </div>
                                    </c:if>

                                    <c:choose>
                                        <c:when test="${not empty pendingCarChangeRequest}">
                                            <div class="info-grid">
                                                <div>
                                                    <strong>Request ID</strong>
                                                    <p>#${pendingCarChangeRequest.requestId}</p>
                                                </div>

                                                <div>
                                                    <strong>Status</strong>
                                                    <p>${pendingCarChangeRequest.status}</p>
                                                </div>

                                                <div>
                                                    <strong>Old Car ID</strong>
                                                    <p>${pendingCarChangeRequest.oldCarId}</p>
                                                </div>

                                                <div>
                                                    <strong>New Car ID</strong>
                                                    <p>${pendingCarChangeRequest.newCarId}</p>
                                                </div>

                                                <div>
                                                    <strong>Reason</strong>
                                                    <p>${pendingCarChangeRequest.reason}</p>
                                                </div>

                                                <div>
                                                    <strong>Created At</strong>
                                                    <p>${pendingCarChangeRequest.createdAt}</p>
                                                </div>
                                            </div>

                                            <p class="status-message pending-note">
                                                There is already a pending car change request for this booking.
                                            </p>
                                        </c:when>

                                        <c:otherwise>
                                            <c:choose>
                                                <c:when test="${not empty replacementCars}">
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/car-change"
                                                          class="change-request-form">

                                                        <input type="hidden" name="action" value="create"/>
                                                        <input type="hidden" name="bookingId" value="${booking.bookingId}"/>

                                                        <div class="form-field">
                                                            <label for="newCarId"><strong>Select replacement car</strong></label>
                                                            <select name="newCarId" id="newCarId" required class="form-control">
                                                                <option value="">-- Select car --</option>
                                                                <c:forEach var="car" items="${replacementCars}">
                                                                    <option value="${car.carId}">
                                                                        ID: ${car.carId} - ${car.modelName}
                                                                        - ${car.plateNumber}
                                                                        - <fmt:formatNumber value="${car.pricePerDay}" pattern="#,###"/> VND/day
                                                                    </option>
                                                                </c:forEach>
                                                            </select>
                                                        </div>

                                                        <div class="form-field">
                                                            <label for="reason"><strong>Reason</strong></label>
                                                            <textarea name="reason"
                                                                      id="reason"
                                                                      rows="4"
                                                                      required
                                                                      class="form-textarea"
                                                                      placeholder="Enter reason for changing car..."></textarea>
                                                        </div>

                                                        <button type="submit"
                                                                class="btn btn-warning"
                                                                onclick="return confirm('Create car change request for this booking?');">
                                                            Send Car Change Request
                                                        </button>
                                                    </form>
                                                </c:when>

                                                <c:otherwise>
                                                    <p class="status-message rejected">
                                                        No suitable replacement car is currently available.
                                                    </p>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>

                            <!-- FOOTER ACTIONS -->
                            <div class="page-footer-actions">

                                <div class="left-actions">
                                    <c:if test="${booking.status == 'REFUND_PENDING'}">
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/car-change"
                                              class="inline-form">
                                            <input type="hidden" name="action" value="refund"/>
                                            <input type="hidden" name="bookingId" value="${booking.bookingId}"/>

                                            <button type="submit"
                                                    class="btn btn-danger"
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
                                                    class="btn btn-warning"
                                                    onclick="return confirm('Reject on behalf of customer and mark refunded at counter?');">
                                                Reject & Refund at Counter
                                            </button>
                                        </form>
                                    </c:if>
                                </div>

                                <div class="right-actions">
                                    <a href="${pageContext.request.contextPath}/staff/bookings" class="btn-back">
                                        Back to Booking List
                                    </a>
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