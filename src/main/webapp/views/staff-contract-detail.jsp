<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Contract Detail</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/contract-detail.css?v=4">
    </head>

    <body>
        <fmt:parseNumber var="remainingAmount" value="${remainingRentalAmount}" type="number" />

        <div class="staff-layout">
            <%@ include file="sidebar.jsp" %>

            <div class="staff-content">
                <div class="contract-detail-wrapper">
                    <div class="contract-detail-card">

                        <!-- Header -->
                        <div class="contract-header">
                            <div class="contract-header-left">
                                <h1>Contract #${contract.contractId}</h1>
                                <span class="contract-badge">${contract.contractStatus}</span>
                            </div>

                            <div class="contract-header-right">
                                <div>
                                    <strong>Booking ID:</strong> #${contract.bookingId}
                                </div>
                                <div>
                                    <strong>Created:</strong>
                                    <fmt:formatDate value="${contract.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                </div>
                                <div>
                                    <strong>Start Date:</strong>
                                    <fmt:formatDate value="${contract.contractStartTime}" pattern="yyyy-MM-dd HH:mm"/>
                                </div>
                                <div>
                                    <strong>End Date:</strong>
                                    <fmt:formatDate value="${contract.contractEndTime}" pattern="yyyy-MM-dd HH:mm"/>
                                </div>
                            </div>
                        </div>

                        <!-- Customer Information -->
                        <div class="contract-section">
                            <h2>Customer Information</h2>
                            <div class="info-table">
                                <div class="info-row">
                                    <div class="info-label">Name</div>
                                    <div class="info-value">${customer.fullName}</div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">Email</div>
                                    <div class="info-value">${customer.email}</div>
                                </div>

                                <div class="info-row">
                                    <div class="info-label">Số điện thoại</div>
                                    <div class="info-value">${customer.phone}</div>
                                </div>

                                <div class="info-row">
                                    <div class="info-label">Số căn cước</div>
                                    <div class="info-value">${customer.citizen_id}</div>
                                </div>
                            </div>
                        </div>

                        <!-- Car Information -->
                        <div class="contract-section">
                            <h2>Car Information</h2>
                            <div class="info-table">
                                <div class="info-row">
                                    <div class="info-label">Brand</div>
                                    <div class="info-value">${car.brandName}</div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">Name</div>
                                    <div class="info-value">${car.modelName}</div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">Daily Price</div>
                                    <div class="info-value">
                                        <fmt:formatNumber value="${contract.dailyPrice}" type="number"/> VND
                                    </div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">Plate Number</div>
                                    <div class="info-value">${car.plateNumber}</div>
                                </div>
                            </div>
                        </div>

                        <!-- Rental Information -->
                        <div class="contract-section">
                            <h2>Rental Information</h2>
                            <div class="info-table">
                                <div class="info-row">
                                    <div class="info-label">Start Date</div>
                                    <div class="info-value">
                                        <fmt:formatDate value="${contract.contractStartTime}" pattern="yyyy-MM-dd HH:mm"/>
                                    </div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">End Date</div>
                                    <div class="info-value">
                                        <fmt:formatDate value="${contract.contractEndTime}" pattern="yyyy-MM-dd HH:mm"/>
                                    </div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">Rental Duration</div>
                                    <div class="info-value">${rentalDurationText}</div>
                                </div>
                            </div>
                        </div>

                        <!-- Check Information -->
                        <!-- Pre-Delivery Check Information -->
                        <div class="contract-section">
                            <h2>Pre-Delivery Check Information</h2>

                            <c:choose>
                                <c:when test="${not empty preDeliveryCheck}">
                                    <div class="info-table">
                                        <div class="info-row">
                                            <div class="info-label">Check Result</div>
                                            <div class="info-value">${preDeliveryCheck.checkResult}</div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-label">Fuel Level</div>
                                            <div class="info-value">${preDeliveryCheck.fuelLevel}</div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-label">Odometer</div>
                                            <div class="info-value">
                                                <fmt:formatNumber value="${preDeliveryCheck.odometerKm}" type="number"/> km
                                            </div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-label">Exterior Note</div>
                                            <div class="info-value">${preDeliveryCheck.exteriorNote}</div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-label">Interior Note</div>
                                            <div class="info-value">${preDeliveryCheck.interiorNote}</div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-label">Staff Note</div>
                                            <div class="info-value">${preDeliveryCheck.note}</div>
                                        </div>
                                    </div>
                                </c:when>

                                <c:otherwise>
                                    <div class="empty-check-state">
                                        <div class="empty-check-icon">📄</div>
                                        <div class="empty-check-title">No pre-delivery inspection information yet</div>
                                        <div class="empty-check-subtitle">Chưa có thông tin kiểm tra trước giao xe</div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="contract-section">
                            <h2>Car Change Request</h2>

                            <c:choose>
                                <c:when test="${not empty carChangeRequest}">
                                    <div class="info-table">
                                        <div class="info-row">
                                            <div class="info-label">Request ID</div>
                                            <div class="info-value">#${carChangeRequest.requestId}</div>
                                        </div>

                                        <div class="info-row">
                                            <div class="info-label">Status</div>
                                            <div class="info-value">${carChangeRequest.status}</div>
                                        </div>

                                        <div class="info-row">
                                            <div class="info-label">Requested By</div>
                                            <div class="info-value">${carChangeRequest.requestedBy}</div>
                                        </div>

                                        <div class="info-row">
                                            <div class="info-label">Reason</div>
                                            <div class="info-value">${carChangeRequest.reason}</div>
                                        </div>

                                        <div class="info-row">
                                            <div class="info-label">Requested At</div>
                                            <div class="info-value">
                                                <fmt:formatDate value="${carChangeRequest.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                            </div>
                                        </div>

                                        <c:if test="${not empty carChangeRequest.resolvedAt}">
                                            <div class="info-row">
                                                <div class="info-label">Resolved At</div>
                                                <div class="info-value">
                                                    <fmt:formatDate value="${carChangeRequest.resolvedAt}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>

                                    <div class="booking-bottom-grid" style="margin-top:16px;">
                                        <c:if test="${not empty oldCarChangeCar}">
                                            <div class="booking-card">
                                                <h3 class="card-title">Current Car</h3>
                                                <div class="car-info-grid">
                                                    <div class="meta-item">
                                                        <span class="meta-label">Model</span>
                                                        <p>${oldCarChangeCar.modelName}</p>
                                                    </div>
                                                    <div class="meta-item">
                                                        <span class="meta-label">Type</span>
                                                        <p>${oldCarChangeCar.typeName}</p>
                                                    </div>
                                                    <div class="meta-item">
                                                        <span class="meta-label">Plate Number</span>
                                                        <p>${oldCarChangeCar.plateNumber}</p>
                                                    </div>
                                                    <div class="meta-item">
                                                        <span class="meta-label">Seats</span>
                                                        <p>${oldCarChangeCar.seatCount}</p>
                                                    </div>
                                                    <div class="meta-item">
                                                        <span class="meta-label">Transmission</span>
                                                        <p>${oldCarChangeCar.transmission}</p>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>

                                        <c:if test="${not empty newCarChangeCar}">
                                            <div class="booking-card">
                                                <h3 class="card-title">Replacement Car</h3>
                                                <div class="car-info-grid">
                                                    <div class="meta-item">
                                                        <span class="meta-label">Model</span>
                                                        <p>${newCarChangeCar.modelName}</p>
                                                    </div>
                                                    <div class="meta-item">
                                                        <span class="meta-label">Type</span>
                                                        <p>${newCarChangeCar.typeName}</p>
                                                    </div>
                                                    <div class="meta-item">
                                                        <span class="meta-label">Plate Number</span>
                                                        <p>${newCarChangeCar.plateNumber}</p>
                                                    </div>
                                                    <div class="meta-item">
                                                        <span class="meta-label">Seats</span>
                                                        <p>${newCarChangeCar.seatCount}</p>
                                                    </div>
                                                    <div class="meta-item">
                                                        <span class="meta-label">Transmission</span>
                                                        <p>${newCarChangeCar.transmission}</p>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>

                                    <c:if test="${carChangeRequest.status == 'PENDING' && carChangeRequest.requestedBy == 'STAFF'}">
                                        <div class="status-note status-note-info" style="margin-top:16px;">
                                            Waiting for customer response to the replacement car request.
                                        </div>
                                    </c:if>

                                    <c:if test="${carChangeRequest.status == 'APPROVED'}">
                                        <div class="status-note status-note-success" style="margin-top:16px;">
                                            Customer accepted the replacement car.
                                        </div>
                                    </c:if>

                                    <c:if test="${carChangeRequest.status == 'REJECTED'}">
                                        <div class="status-note status-note-danger" style="margin-top:16px;">
                                            Customer rejected the replacement car. Refund processing may be required.
                                        </div>
                                    </c:if>

                                    <c:if test="${carChangeRequest.status == 'CANCELLED'}">
                                        <div class="status-note status-note-warning" style="margin-top:16px;">
                                            This replacement request is no longer valid.
                                        </div>
                                    </c:if>
                                </c:when>

                                <c:otherwise>
                                    <div class="empty-check-state">
                                        <div class="empty-check-icon">🚗</div>
                                        <div class="empty-check-title">No car change request yet</div>
                                        <div class="empty-check-subtitle">Chưa có yêu cầu đổi xe cho hợp đồng này</div>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <c:if test="${canRequestCarChange}">
                                <div class="car-change-action-wrap">
                                    <a href="${pageContext.request.contextPath}/car-change?action=form&bookingId=${booking.bookingId}"
                                       class="btn-action btn-check request-change-btn">
                                        Request Replacement Car
                                    </a>
                                </div>
                            </c:if>
                        </div>

                        <!-- Return Check Information -->
                        <c:if test="${contract.contractStatus eq 'ACTIVE' or contract.contractStatus eq 'COMPLETED'}">
                            <div class="contract-section">
                                <h2>Return Check Information</h2>

                                <c:choose>
                                    <c:when test="${not empty returnCheck}">

                                        <div class="info-table" style="margin-bottom: 16px;">
                                            <div class="info-row">
                                                <div class="info-label">Return Odometer</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${not empty returnCheck.odometerKm ? returnCheck.odometerKm : 0}" type="number"/> km
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Actual KM</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${not empty contract.actualKm ? contract.actualKm : 0}" type="number"/> km
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Allowed KM</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${not empty contract.allowedKm ? contract.allowedKm : 0}" type="number"/> km
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Extra KM</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${not empty contract.extraKm ? contract.extraKm : 0}" type="number"/> km
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Extra KM Fee</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${not empty contract.extraKmFee ? contract.extraKmFee : 0}" type="number"/> VND
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Extra KM Fee Rule</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${extraKmFeePerKm}" type="number"/> VND / km
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Late Return Hourly Fee</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${lateHourlyFee}" type="number"/> VND / hour
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Return Status</div>
                                                <div class="info-value">
                                                    <c:choose>
                                                        <c:when test="${empty returnCheck.exteriorNote}">
                                                            Vehicle returned in normal condition
                                                        </c:when>
                                                        <c:otherwise>
                                                            Issues detected
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Actual Return Time</div>
                                                <div class="info-value">
                                                    <c:choose>
                                                        <c:when test="${not empty actualReturnTime}">
                                                            <fmt:formatDate value="${actualReturnTime}" pattern="yyyy-MM-dd HH:mm"/>
                                                        </c:when>
                                                        <c:otherwise>Not available</c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Return Timing Status</div>
                                                <div class="info-value">${returnTimingStatus}</div>
                                            </div>
                                        </div>

                                        <c:choose>
                                            <c:when test="${not empty returnCheck.exteriorNote}">
                                                <div style="margin-bottom: 10px; font-weight: 600;">Detected Issues</div>
                                                <ul class="check-list">
                                                    <c:forTokens items="${returnCheck.exteriorNote}" delims="|" var="issue">
                                                        <li><c:out value="${issue}"/></li>
                                                        </c:forTokens>
                                                </ul>
                                            </c:when>

                                            <c:otherwise>
                                                <div class="empty-check-state">
                                                    <div class="empty-check-icon">✅</div>
                                                    <div class="empty-check-title">Vehicle returned in normal condition</div>
                                                    <div class="empty-check-subtitle">Không phát hiện vấn đề phát sinh khi trả xe</div>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>

                                    </c:when>

                                    <c:otherwise>
                                        <div class="empty-check-state">
                                            <div class="empty-check-icon">📄</div>
                                            <div class="empty-check-title">No return inspection information yet</div>
                                            <div class="empty-check-subtitle">Chưa có thông tin hậu kiểm trả xe</div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <!-- Payment Summary -->
                        <div class="contract-section">
                            <h2>Payment Summary</h2>

                            <div class="payment-table">
                                <div class="payment-row">
                                    <div class="payment-label">Daily Price</div>
                                    <div class="payment-value">
                                        <fmt:formatNumber value="${contract.dailyPrice}" type="number"/> VND
                                    </div>
                                </div>

                                <div class="payment-row">
                                    <div class="payment-label">Rental Duration</div>
                                    <div class="payment-value">${rentalDurationText}</div>
                                </div>

                                <div class="payment-row">
                                    <div class="payment-label">Rental Subtotal</div>
                                    <div class="payment-value">
                                        <fmt:formatNumber value="${contract.totalAmount}" type="number"/> VND
                                    </div>
                                </div>

                                <div class="payment-row">
                                    <div class="payment-label">Minus Deposit</div>
                                    <div class="payment-value payment-negative">
                                        <fmt:formatNumber value="${contract.depositAmount}" type="number"/> VND
                                    </div>
                                </div>

                                <div class="payment-divider"></div>

                                <c:if test="${not empty extraChargeTypes}">
                                    <div class="extra-charge-title">Extra Charges:</div>

                                    <c:forEach var="feeType" items="${extraChargeTypes}" varStatus="loop">
                                        <div class="payment-row payment-extra-row">
                                            <div class="payment-label payment-bullet">• ${feeType}</div>
                                            <div class="payment-value">
                                                <fmt:formatNumber value="${extraChargeAmounts[loop.index]}" type="number"/> VND
                                            </div>
                                        </div>
                                    </c:forEach>

                                    <div class="payment-row payment-extra-total">
                                        <div class="payment-label">Extra Charges Total</div>
                                        <div class="payment-value">
                                            <fmt:formatNumber value="${extraChargeTotal}" type="number"/> VND
                                        </div>
                                    </div>

                                    <div class="payment-divider"></div>
                                </c:if>


                                <div class="payment-row">
                                    <div class="payment-label">Extra KM Fee</div>
                                    <div class="payment-value">
                                        <fmt:formatNumber value="${not empty contract.extraKmFee ? contract.extraKmFee : 0}" type="number"/> VND
                                    </div>
                                </div>

                                <div class="payment-row">
                                    <div class="payment-label">Extra KM</div>
                                    <div class="payment-value">
                                        <fmt:formatNumber value="${not empty contract.extraKm ? contract.extraKm : 0}" type="number"/> km
                                    </div>
                                </div>

                                <div class="payment-row">
                                    <div class="payment-label">Extra Time Fee</div>
                                    <div class="payment-value">
                                        <fmt:formatNumber value="${not empty extraTimeFee ? extraTimeFee : 0}" type="number"/> VND
                                    </div>
                                </div>



                                <c:if test="${finalAmountDue < 0}">
                                    <div class="payment-row payment-final">
                                        <div class="payment-label">Số tiền cần thanh toán khi trả xe</div>

                                        <div class="payment-value payment-final-value">
                                            <fmt:formatNumber value="${0}" type="number"/> VND
                                        </div>

                                    </div>
                                    <div class="payment-row payment-final">
                                        <div class="payment-label">Số tiền hoàn trả khi trả xe</div>
                                        <div class="payment-value payment-final-value">
                                            <fmt:formatNumber value="${finalAmountDue*(-1)}" type="number"/> VND
                                        </div>
                                    </div>
                                </c:if>




                                <c:if test="${finalAmountDue > 0}">
                                    <div class="payment-row payment-final">
                                        <div class="payment-label">Số tiền cần thanh toán khi trả xe</div>
                                        <div class="payment-value payment-final-value">
                                            <fmt:formatNumber value="${finalAmountDue}" type="number"/> VND
                                        </div>
                                    </div>

                                    <div class="payment-row payment-final">
                                        <div class="payment-label">Số tiền hoàn trả khi trả xe</div>
                                        <div class="payment-value payment-final-value">
                                            <fmt:formatNumber value="${0}" type="number"/> VND
                                        </div>
                                    </div>
                                </c:if>



                            </div>
                        </div>

                        <c:if test="${canProcessRefund}">
                            <div class="status-note status-note-warning" style="margin-top:16px;">
                                Customer rejected the vehicle handover. This booking is waiting for staff to complete the refund.
                            </div>
                        </c:if>

                        <!-- Actions -->
                        <div class="contract-actions">
                            <a href="${pageContext.request.contextPath}/staff/contracts" class="btn-action btn-back">
                                Back
                            </a>

                            <c:choose>
                                <c:when test="${contract.contractStatus eq 'CREATED' and empty carChangeRequest}">
                                    <button type="button"
                                            class="btn-action btn-check"
                                            onclick="openBeforeCheckModal()">
                                        Check Car
                                    </button>


                                    <form method="post"
                                          action="${pageContext.request.contextPath}/staff/contracts"
                                          style="display:inline-block;"
                                          onsubmit="return validateSendToCustomer(${not empty preDeliveryCheck and preDeliveryCheck.checkResult eq 'OK'});">
                                        <input type="hidden" name="action" value="sendToCustomer">
                                        <input type="hidden" name="contractId" value="${contract.contractId}">

                                        <button type="submit"
                                                class="btn-action btn-complete">
                                            Send to Customer
                                        </button>
                                    </form>


                                    <form method="post"
                                          action="${pageContext.request.contextPath}/staff/contracts"
                                          style="display:inline-block;">
                                        <input type="hidden" name="action" value="cancel">
                                        <input type="hidden" name="contractId" value="${contract.contractId}">

                                        <button type="submit"
                                                class="btn-action btn-back"
                                                onclick="return confirm('Cancel this contract?');">
                                            Cancel Contract
                                        </button>
                                    </form>
                                </c:when>

                                <c:when test="${canProcessRefund}">
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/car-change"
                                          style="display:inline-block;">
                                        <input type="hidden" name="action" value="refund">
                                        <input type="hidden" name="bookingId" value="${booking.bookingId}">

                                        <button type="submit"
                                                class="btn-action btn-complete"
                                                onclick="return confirm('Confirm that refund has been completed for this booking?');">
                                            Complete Refund
                                        </button>
                                    </form>
                                </c:when>

                                <c:when test="${contract.contractStatus eq 'CREATED' and not empty carChangeRequest and carChangeRequest.status eq 'PENDING'}">
                                    <div class="status-note status-note-info" style="margin-bottom:12px;">
                                        Replacement request is in progress. Normal pre-delivery actions are temporarily locked until the customer responds.
                                    </div>
                                </c:when>

                                <c:when test="${contract.contractStatus eq 'WAITING_CUSTOMER_CONFIRM'}">

                                    <c:choose>
                                        <c:when test="${contract.customerConfirmed eq true}">
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/staff/contracts"
                                                  style="display:inline-block;"
                                                  onsubmit="return confirm('Deliver this car now?');">
                                                <input type="hidden" name="action" value="deliverCar">
                                                <input type="hidden" name="contractId" value="${contract.contractId}">

                                                <button type="submit"
                                                        class="btn-action btn-complete">
                                                    Deliver Car
                                                </button>
                                            </form>
                                        </c:when>

                                        <c:otherwise>
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/staff/contracts"
                                                  style="display:inline-block;">
                                                <input type="hidden" name="action" value="markNoShow">
                                                <input type="hidden" name="contractId" value="${contract.contractId}">

                                                <button type="submit"
                                                        class="btn-action btn-complete"
                                                        onclick="return confirm('Mark this customer as no-show?');">
                                                    Mark No-show
                                                </button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>

                                </c:when>

                                <c:when test="${contract.contractStatus eq 'ACTIVE'}">
                                    <button type="button" class="btn-action btn-check" onclick="openCheckFeesModal()">
                                        Check - Fees
                                    </button>

                                    <button type="button"
                                            class="btn-action btn-complete"
                                            onclick="handleCompleteReturn(${hasReturnCheck})">
                                        Complete Return
                                    </button>

                                    <form id="completeReturnForm"
                                          method="post"
                                          action="${pageContext.request.contextPath}/staff/contracts"
                                          style="display:none;">
                                        <input type="hidden" name="action" value="complete">
                                        <input type="hidden" name="contractId" value="${contract.contractId}">
                                        <input type="hidden" name="carNextStatus" value="AVAILABLE">
                                    </form>
                                </c:when>
                            </c:choose>
                        </div>

                    </div>
                </div>
            </div>
        </div>

        <div id="beforeCheckModal" class="cf-modal-overlay">
            <div class="cf-modal">
                <div class="cf-modal-header">
                    <h3>Check Car Before Delivery</h3>
                </div>

                <form method="post"
                      action="${pageContext.request.contextPath}/staff/contracts"
                      id="beforeCheckForm">

                    <input type="hidden" name="action" value="saveCheck">
                    <input type="hidden" name="contractId" value="${contract.contractId}">

                    <div class="cf-modal-body">

                        <div class="cf-section">
                            <h4>Exterior Check</h4>
                            <div class="cf-issues-grid">
                                <label class="cf-checkbox-item"><input type="checkbox" name="exteriorIssues" value="Scratch"><span>Scratch</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="exteriorIssues" value="Dent"><span>Dent</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="exteriorIssues" value="Broken light"><span>Broken light</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="exteriorIssues" value="Dirty exterior"><span>Dirty exterior</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="exteriorIssues" value="Mirror issue"><span>Mirror issue</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="exteriorIssues" value="Tire issue"><span>Tire issue</span></label>
                            </div>
                        </div>

                        <div class="cf-section">
                            <h4>Interior Check</h4>
                            <div class="cf-issues-grid">
                                <label class="cf-checkbox-item"><input type="checkbox" name="interiorIssues" value="Dirty interior"><span>Dirty interior</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="interiorIssues" value="Bad smell"><span>Bad smell</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="interiorIssues" value="Seat damage"><span>Seat damage</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="interiorIssues" value="Dashboard issue"><span>Dashboard issue</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="interiorIssues" value="Missing accessories"><span>Missing accessories</span></label>
                            </div>
                        </div>

                        <div class="cf-section">
                            <h4>Fuel Level</h4>
                            <select name="fuelLevel" class="cf-input">
                                <option value="EMPTY">EMPTY</option>
                                <option value="1/4">1/4</option>
                                <option value="1/2">1/2</option>
                                <option value="3/4">3/4</option>
                                <option value="FULL">FULL</option>
                            </select>
                        </div>

                        <div class="cf-section">
                            <h4>Current Odometer</h4>
                            <input type="number"
                                   class="cf-input"
                                   value="${car.currentOdometerKm}"
                                   readonly>
                        </div>

                        <div class="cf-section">
                            <h4>System Check</h4>
                            <div class="info-table">
                                <div class="info-row">
                                    <div class="info-label">Maintenance Blocked</div>
                                    <div class="info-value">
                                        <c:choose>
                                            <c:when test="${maintenanceBlocked}">Yes</c:when>
                                            <c:otherwise>No</c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">Schedule Conflict</div>
                                    <div class="info-value">
                                        <c:choose>
                                            <c:when test="${scheduleConflict}">Yes</c:when>
                                            <c:otherwise>No</c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="cf-section">
                            <h4>Final Status</h4>
                            <div class="cf-issues-grid">
                                <label class="cf-checkbox-item">
                                    <input type="radio" name="physicalStatus" value="OK" checked>
                                    <span>OK</span>
                                </label>
                                <label class="cf-checkbox-item">
                                    <input type="radio" name="physicalStatus" value="NOT_OK">
                                    <span>NOT OK</span>
                                </label>
                            </div>
                        </div>

                        <div class="cf-section">
                            <h4>Note</h4>
                            <textarea name="note" class="cf-input" style="height:100px; padding:12px;"></textarea>
                        </div>

                    </div>

                    <div class="cf-modal-footer">
                        <button type="button" class="cf-btn cf-btn-cancel" onclick="closeBeforeCheckModal()">
                            Cancel
                        </button>
                        <button type="submit" class="cf-btn cf-btn-save">
                            Save Check
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <div id="checkFeesModal" class="cf-modal-overlay">
            <div class="cf-modal">
                <div class="cf-modal-header">
                    <h3>Check - Fees</h3>
                </div>

                <form method="post"
                      action="${pageContext.request.contextPath}/staff/contracts"
                      id="checkFeesForm">

                    <input type="hidden" name="action" value="saveReturnCheck">
                    <input type="hidden" name="contractId" value="${contract.contractId}">

                    <div class="cf-modal-body">

                        <div class="cf-section">
                            <label class="cf-checkbox-item">
                                <input type="checkbox" id="noIssuesFound" name="noIssuesFound" value="true">
                                <span>No issues found / Vehicle returned in normal condition</span>
                            </label>
                        </div>

                        <div class="cf-section">
                            <h4>Return Odometer</h4>
                            <input type="number"
                                   name="odometerKm"
                                   class="cf-input"
                                   min="${preDeliveryCheck.odometerKm}"
                                   value="${not empty returnCheck ? returnCheck.odometerKm : ''}"
                                   placeholder="Enter current odometer"
                                   required>
                            <small style="color:#666;">
                                Pre-check odometer:
                                <fmt:formatNumber value="${preDeliveryCheck.odometerKm}" type="number"/> km
                            </small>
                        </div>

                        <div class="cf-section">
                            <h4>Actual Return Time</h4>

                            <div class="datetime-split-row">
                                <div class="datetime-col">
                                    <label class="cf-sub-label">Return Date</label>
                                    <input type="date"
                                           name="actualReturnDate"
                                           class="cf-input"
                                           value="${not empty actualReturnDateValue ? actualReturnDateValue : ''}"
                                           required>
                                </div>

                                <div class="datetime-col">
                                    <label class="cf-sub-label">Return Hour</label>
                                    <select name="actualReturnHour" class="cf-input" required>
                                        <option value="">Select hour</option>
                                        <c:forEach begin="0" end="23" var="hour">
                                            <fmt:formatNumber value="${hour}" pattern="00" var="hourText"/>
                                            <option value="${hourText}:00"
                                                    ${actualReturnHourValue == (hourText.concat(':00')) ? 'selected' : ''}>
                                                ${hourText}:00
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <small style="color:#666;">
                                Contract end time:
                                <fmt:formatDate value="${contract.contractEndTime}" pattern="yyyy-MM-dd HH:mm"/>
                            </small>
                        </div>

                        <div class="cf-section">
                            <h4>Check Information</h4>

                            <div class="cf-issues-grid">
                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Dirty exterior">
                                    <span>Dirty exterior</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Dirty interior">
                                    <span>Dirty interior</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Bad smell">
                                    <span>Bad smell</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Fuel shortage">
                                    <span>Fuel shortage</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Exterior scratch">
                                    <span>Exterior scratch</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Interior damage">
                                    <span>Interior damage</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Missing accessories">
                                    <span>Missing accessories</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Tire issue">
                                    <span>Tire issue</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Other damage">
                                    <span>Other damage</span>
                                </label>
                            </div>
                        </div>

                        <div class="cf-section">
                            <div class="cf-selected-box">
                                <div class="cf-selected-title">Selected Issues</div>
                                <div id="selectedIssuesPreview" class="cf-selected-tags"></div>
                            </div>
                        </div>

                        <div class="cf-section">
                            <h4>Fees</h4>

                            <div class="cf-fee-table">
                                <div class="cf-fee-head">
                                    <div>Type</div>
                                    <div>Description</div>
                                    <div>Amount</div>
                                </div>

                                <div id="feeRowsContainer" class="cf-fee-body">
                                    <!-- rows render bằng JS -->
                                </div>
                            </div>
                        </div>

                    </div>

                    <div class="cf-modal-footer">
                        <button type="button" class="cf-btn cf-btn-cancel" onclick="closeCheckFeesModal()">
                            Cancel
                        </button>
                        <button type="submit" class="cf-btn cf-btn-save">
                            Save
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <div id="savedReturnCheckDataContainer" style="display:none;">
            <c:forEach var="issue" items="${savedIssueTypes}" varStatus="loop">
                <input type="hidden"
                       class="saved-return-item"
                       data-issue="${issue}"
                       data-description="${savedDescriptions[loop.index]}"
                       data-amount="${savedAmounts[loop.index]}">
            </c:forEach>
        </div>

        <div id="returnCheckMetaData"
             data-has-return-check="${not empty returnCheck}"
             data-normal-return="${not empty returnCheck and empty returnCheck.exteriorNote and returnCheck.note eq 'No issues found'}"
             style="display:none;">
        </div>

        <div id="returnCheckSavedOdometer"
             data-odometer="${not empty returnCheck ? returnCheck.odometerKm : ''}"
             style="display:none;">
        </div>

        <div id="returnCheckSavedActualReturnTime"
             data-return-time="${not empty actualReturnTimeValue ? actualReturnTimeValue : ''}"
             style="display:none;">
        </div>

        <div id="savedBeforeCheckData"
             data-exterior-note="${not empty preDeliveryCheck ? preDeliveryCheck.exteriorNote : ''}"
             data-interior-note="${not empty preDeliveryCheck ? preDeliveryCheck.interiorNote : ''}"
             data-fuel-level="${not empty preDeliveryCheck ? preDeliveryCheck.fuelLevel : ''}"
             data-check-result="${not empty preDeliveryCheck ? preDeliveryCheck.checkResult : ''}"
             data-note="${not empty preDeliveryCheck ? preDeliveryCheck.note : ''}"
             style="display:none;">
        </div>

        <div id="preDeliveryIssueSeedData"
             data-exterior-note="${not empty preDeliveryCheck ? preDeliveryCheck.exteriorNote : ''}"
             data-interior-note="${not empty preDeliveryCheck ? preDeliveryCheck.interiorNote : ''}"
             style="display:none;">
        </div>

        <input type="hidden" id="preCheckOdometerValue" value="${not empty preDeliveryCheck ? preDeliveryCheck.odometerKm : 0}">

        <script src="${pageContext.request.contextPath}/assets/js/contract-check-fees.js?v=9"></script>
    </body>
</html>