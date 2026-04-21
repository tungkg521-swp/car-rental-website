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

                        <!-- Return Check Information -->
                        <c:if test="${contract.contractStatus eq 'ACTIVE' or contract.contractStatus eq 'COMPLETED'}">
                            <div class="contract-section">
                                <h2>Return Check Information</h2>

                                <c:choose>
                                    <c:when test="${not empty returnCheck}">
                                        <ul class="check-list">
                                            <c:if test="${not empty returnCheck.exteriorNote}">
                                                <c:forTokens items="${returnCheck.exteriorNote}" delims="|" var="issue">
                                                    <li>${issue}</li>
                                                    </c:forTokens>
                                                </c:if>
                                        </ul>
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

                        <!-- Actions -->
                        <div class="contract-actions">
                            <a href="${pageContext.request.contextPath}/staff/contracts" class="btn-action btn-back">
                                Back
                            </a>

                            <c:choose>
                                <c:when test="${contract.contractStatus eq 'CREATED'}">
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
                                    <input type="checkbox" name="issueTypes" value="Returned late">
                                    <span>Returned late</span>
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

        <script src="${pageContext.request.contextPath}/assets/js/contract-check-fees.js"></script>
    </body>
</html>