
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

                                    <c:if test="${booking.status == 'PENDING_APPROVAL'}">
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/staff/bookings"
                                              class="inline-form">

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
                                    <c:if test="${booking.status == 'AWAITING_PAYMENT'}">
                                        <div class="info-message">
                                            This booking has been approved and is waiting for customer payment.
                                        </div>
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

                            <c:if test="${booking.status == 'AWAITING_PAYMENT' || booking.status == 'CONFIRMED'}">
                                <div class="detail-description">
                                    <h2>🔄 Car Change Request</h2>

                                    <c:if test="${param.changeRequest == 'success'}">
                                        <div class="info-message" style="color: green; font-weight: 600;">
                                            Car change request created successfully.
                                        </div>
                                    </c:if>

                                    <c:if test="${param.changeRequest == 'fail'}">
                                        <div class="info-message" style="color: red; font-weight: 600;">
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

                                            <p class="status-message" style="color: #b26a00;">
                                                There is already a pending car change request for this booking.
                                            </p>
                                        </c:when>

                                        <c:otherwise>
                                            <c:choose>
                                                <c:when test="${not empty replacementCars}">
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/car-change"
                                                          class="inline-form"
                                                          style="display:block; margin-top:16px;">

                                                        <input type="hidden" name="action" value="create"/>
                                                        <input type="hidden" name="bookingId" value="${booking.bookingId}"/>

                                                        <div style="margin-bottom: 16px;">
                                                            <label for="newCarId"><strong>Select replacement car</strong></label><br/>
                                                            <select name="newCarId" id="newCarId" required
                                                                    style="width:100%; max-width:420px; padding:10px; margin-top:8px;">
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

                                                        <div style="margin-bottom: 16px;">
                                                            <label for="reason"><strong>Reason</strong></label><br/>
                                                            <textarea name="reason"
                                                                      id="reason"
                                                                      rows="4"
                                                                      required
                                                                      style="width:100%; max-width:520px; padding:10px; margin-top:8px;"
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

                            <div class="detail-actions">

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