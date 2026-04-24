<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Chi tiết hợp đồng</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/contract-detail.css?v=5">
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
                                <h1>Hợp đồng #${contract.contractId}</h1>

                                <span class="contract-badge">
                                    <c:choose>
                                        <c:when test="${contract.contractStatus eq 'CREATED'}">Đã tạo</c:when>
                                        <c:when test="${contract.contractStatus eq 'WAITING_CUSTOMER_CONFIRM'}">Chờ khách xác nhận</c:when>
                                        <c:when test="${contract.contractStatus eq 'ACTIVE'}">Đang thuê</c:when>
                                        <c:when test="${contract.contractStatus eq 'COMPLETED'}">Hoàn tất</c:when>
                                        <c:when test="${contract.contractStatus eq 'CANCELLED'}">Đã hủy</c:when>
                                        <c:otherwise>${contract.contractStatus}</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>

                            <div class="contract-header-right">
                                <div>
                                    <strong>Mã booking:</strong> #${contract.bookingId}
                                </div>
                                <div>
                                    <strong>Trạng thái booking:</strong>
                                    <c:choose>
                                        <c:when test="${booking.status eq 'PENDING_APPROVAL'}">Chờ duyệt</c:when>
                                        <c:when test="${booking.status eq 'AWAITING_PAYMENT'}">Chờ thanh toán</c:when>
                                        <c:when test="${booking.status eq 'CONFIRMED'}">Đã xác nhận</c:when>
                                        <c:when test="${booking.status eq 'ACTIVE'}">Đang thuê</c:when>
                                        <c:when test="${booking.status eq 'COMPLETED'}">Hoàn tất</c:when>
                                        <c:when test="${booking.status eq 'CANCELLED'}">Đã hủy</c:when>
                                        <c:when test="${booking.status eq 'REFUND_PENDING'}">Chờ hoàn tiền</c:when>
                                        <c:when test="${booking.status eq 'REFUNDED'}">Đã hoàn tiền</c:when>
                                        <c:otherwise>${booking.status}</c:otherwise>
                                    </c:choose>
                                </div>
                                <div>
                                    <strong>Ngày tạo hợp đồng:</strong>
                                    <fmt:formatDate value="${contract.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                </div>
                                <div>
                                    <strong>Ngày nhận xe:</strong>
                                    <fmt:formatDate value="${contract.contractStartTime}" pattern="dd/MM/yyyy HH:mm"/>
                                </div>
                                <div>
                                    <strong>Ngày trả xe:</strong>
                                    <fmt:formatDate value="${contract.contractEndTime}" pattern="dd/MM/yyyy HH:mm"/>
                                </div>
                            </div>
                        </div>

                        <!-- Thông tin khách hàng -->
                        <div class="contract-section">
                            <h2>Thông tin khách hàng</h2>
                            <div class="info-table">
                                <div class="info-row">
                                    <div class="info-label">Họ và tên</div>
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

                        <!-- Thông tin xe -->
                        <div class="contract-section">
                            <h2>Thông tin xe</h2>
                            <div class="info-table">
                                <div class="info-row">
                                    <div class="info-label">Hãng xe</div>
                                    <div class="info-value">${car.brandName}</div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">Tên xe</div>
                                    <div class="info-value">${car.modelName}</div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">Biển số</div>
                                    <div class="info-value">${car.plateNumber}</div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">Đơn giá / ngày</div>
                                    <div class="info-value">
                                        <fmt:formatNumber value="${contract.dailyPrice}" type="number"/> VND
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Thông tin thuê xe -->
                        <div class="contract-section">
                            <h2>Thông tin thuê xe</h2>
                            <div class="info-table">
                                <div class="info-row">
                                    <div class="info-label">Thời gian nhận xe</div>
                                    <div class="info-value">
                                        <fmt:formatDate value="${contract.contractStartTime}" pattern="dd/MM/yyyy HH:mm"/>
                                    </div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">Thời gian trả xe</div>
                                    <div class="info-value">
                                        <fmt:formatDate value="${contract.contractEndTime}" pattern="dd/MM/yyyy HH:mm"/>
                                    </div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">Tổng thời gian thuê</div>
                                    <div class="info-value">${rentalDurationText}</div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">Giá trị hợp đồng</div>
                                    <div class="info-value">
                                        <fmt:formatNumber value="${contract.totalAmount}" type="number"/> VND
                                    </div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">Tiền cọc</div>
                                    <div class="info-value">
                                        <fmt:formatNumber value="${contract.depositAmount}" type="number"/> VND
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Ghi chú khách hàng -->
                        <c:if test="${not empty booking.note}">
                            <div class="contract-section">
                                <h2>Ghi chú của khách hàng</h2>
                                <div class="note-box">
                                    <p>${booking.note}</p>
                                </div>
                            </div>
                        </c:if>

                        <!-- Kiểm tra trước giao xe -->
                        <div class="contract-section">
                            <h2>Thông tin kiểm tra trước giao xe</h2>

                            <c:choose>
                                <c:when test="${not empty preDeliveryCheck}">
                                    <div class="info-table">
                                        <div class="info-row">
                                            <div class="info-label">Kết quả kiểm tra</div>
                                            <div class="info-value">${preDeliveryCheck.checkResult}</div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-label">Mức nhiên liệu</div>
                                            <div class="info-value">${preDeliveryCheck.fuelLevel}</div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-label">Số km hiện tại</div>
                                            <div class="info-value">
                                                <fmt:formatNumber value="${preDeliveryCheck.odometerKm}" type="number"/> km
                                            </div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-label">Ghi chú ngoại thất</div>
                                            <div class="info-value">${preDeliveryCheck.exteriorNote}</div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-label">Ghi chú nội thất</div>
                                            <div class="info-value">${preDeliveryCheck.interiorNote}</div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-label">Ghi chú của nhân viên</div>
                                            <div class="info-value">${preDeliveryCheck.note}</div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-label">Bị chặn do bảo dưỡng</div>
                                            <div class="info-value">
                                                <c:choose>
                                                    <c:when test="${maintenanceBlocked}">Có</c:when>
                                                    <c:otherwise>Không</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-label">Xung đột lịch</div>
                                            <div class="info-value">
                                                <c:choose>
                                                    <c:when test="${scheduleConflict}">Có</c:when>
                                                    <c:otherwise>Không</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                </c:when>

                                <c:otherwise>
                                    <div class="empty-check-state">
                                        <div class="empty-check-icon">📄</div>
                                        <div class="empty-check-title">Chưa có thông tin kiểm tra trước giao xe</div>
                                        <div class="empty-check-subtitle">Nhân viên cần kiểm tra xe trước khi giao cho khách</div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Hậu kiểm trả xe -->
                        <c:if test="${contract.contractStatus eq 'ACTIVE' or contract.contractStatus eq 'COMPLETED'}">
                            <div class="contract-section">
                                <h2>Thông tin hậu kiểm khi trả xe</h2>

                                <c:choose>
                                    <c:when test="${not empty returnCheck}">
                                        <div class="info-table" style="margin-bottom:16px;">
                                            <div class="info-row">
                                                <div class="info-label">Số km khi trả xe</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${not empty returnCheck.odometerKm ? returnCheck.odometerKm : 0}" type="number"/> km
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Số km trước khi giao</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${not empty preDeliveryCheck and preDeliveryCheck.odometerKm != null ? preDeliveryCheck.odometerKm : 0}" type="number"/> km
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Tổng km đã đi</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${not empty contract.actualKm ? contract.actualKm : 0}" type="number"/> km
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Km được sử dụng</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${not empty contract.allowedKm ? contract.allowedKm : 0}" type="number"/> km
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Km vượt mức</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${not empty contract.extraKm ? contract.extraKm : 0}" type="number"/> km
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Phí vượt km</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${not empty contract.extraKmFee ? contract.extraKmFee : 0}" type="number"/> VND
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Đơn giá vượt km</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${extraKmFeePerKm}" type="number"/> VND / km
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Phí trễ theo giờ</div>
                                                <div class="info-value">
                                                    <fmt:formatNumber value="${lateHourlyFee}" type="number"/> VND / giờ
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Thời gian trả thực tế</div>
                                                <div class="info-value">
                                                    <c:choose>
                                                        <c:when test="${not empty actualReturnTime}">
                                                            <fmt:formatDate value="${actualReturnTime}" pattern="dd/MM/yyyy HH:mm"/>
                                                        </c:when>
                                                        <c:otherwise>Chưa có</c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>

                                            <div class="info-row">
                                                <div class="info-label">Trạng thái trả xe</div>
                                                <div class="info-value">${returnTimingStatus}</div>
                                            </div>
                                        </div>

                                        <c:choose>
                                            <c:when test="${not empty returnCheck.exteriorNote}">
                                                <div style="margin-bottom:10px; font-weight:600;">Các vấn đề phát hiện</div>
                                                <ul class="check-list">
                                                    <c:forTokens items="${returnCheck.exteriorNote}" delims="|" var="issue">
                                                        <li><c:out value="${issue}"/></li>
                                                        </c:forTokens>
                                                </ul>
                                            </c:when>

                                            <c:otherwise>
                                                <div class="empty-check-state">
                                                    <div class="empty-check-icon">✅</div>
                                                    <div class="empty-check-title">Xe được trả trong tình trạng bình thường</div>
                                                    <div class="empty-check-subtitle">Không phát hiện vấn đề phát sinh khi trả xe</div>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>

                                    <c:otherwise>
                                        <div class="empty-check-state">
                                            <div class="empty-check-icon">📄</div>
                                            <div class="empty-check-title">Chưa có thông tin hậu kiểm trả xe</div>
                                            <div class="empty-check-subtitle">Nhân viên cần kiểm tra xe khi khách trả xe</div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <!-- Tổng kết thanh toán -->
                        <div class="contract-section">
                            <h2>Tổng kết thanh toán</h2>

                            <div class="payment-table">
                                <div class="payment-row">
                                    <div class="payment-label">Đơn giá thuê / ngày</div>
                                    <div class="payment-value">
                                        <fmt:formatNumber value="${contract.dailyPrice}" type="number"/> VND
                                    </div>
                                </div>

                                <div class="payment-row">
                                    <div class="payment-label">Tổng thời gian thuê</div>
                                    <div class="payment-value">${rentalDurationText}</div>
                                </div>

                                <div class="payment-row">
                                    <div class="payment-label">Tiền thuê xe</div>
                                    <div class="payment-value">
                                        <fmt:formatNumber value="${contract.totalAmount}" type="number"/> VND
                                    </div>
                                </div>

                                <div class="payment-row">
                                    <div class="payment-label">Tiền cọc đã thu</div>
                                    <div class="payment-value payment-negative">
                                        <fmt:formatNumber value="${contract.depositAmount}" type="number"/> VND
                                    </div>
                                </div>

                                

                                <div class="payment-divider"></div>

                                <div class="payment-row">
                                    <div class="payment-label">Số km trước khi giao</div>
                                    <div class="payment-value">
                                        <fmt:formatNumber value="${not empty preDeliveryCheck and preDeliveryCheck.odometerKm != null ? preDeliveryCheck.odometerKm : 0}" type="number"/> km
                                    </div>
                                </div>

                                <div class="payment-row">
                                    <div class="payment-label">Số km khi trả xe</div>
                                    <div class="payment-value">
                                        <fmt:formatNumber value="${not empty returnCheck and returnCheck.odometerKm != null ? returnCheck.odometerKm : 0}" type="number"/> km
                                    </div>
                                </div>

                                <div class="payment-row">
                                    <div class="payment-label">Km được sử dụng</div>
                                    <div class="payment-value">
                                        <fmt:formatNumber value="${not empty contract.allowedKm ? contract.allowedKm : 0}" type="number"/> km
                                    </div>
                                </div>

                                <div class="payment-row">
                                    <div class="payment-label">Thời gian trả thực tế</div>
                                    <div class="payment-value">
                                        <c:choose>
                                            <c:when test="${not empty actualReturnTime}">
                                                <fmt:formatDate value="${actualReturnTime}" pattern="dd/MM/yyyy HH:mm"/>
                                            </c:when>
                                            <c:otherwise>Chưa có</c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                                <div class="payment-row">
                                    <div class="payment-label">Trạng thái trả xe</div>
                                    <div class="payment-value">
                                        <c:choose>
                                            <c:when test="${returnTimingStatus eq 'EARLY'}">Trả sớm</c:when>
                                            <c:when test="${returnTimingStatus eq 'ON_TIME'}">Đúng giờ</c:when>
                                            <c:when test="${returnTimingStatus eq 'LATE'}">Trả trễ</c:when>
                                            <c:otherwise>${returnTimingStatus}</c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                                <div class="payment-divider"></div>

                                <c:if test="${not empty extraChargeTypes}">
                                    <div class="extra-charge-title">Phí phát sinh khác:</div>

                                    <c:forEach var="feeType" items="${extraChargeTypes}" varStatus="loop">
                                        <div class="payment-row payment-extra-row">
                                            <div class="payment-label payment-bullet">• ${feeType}</div>
                                            <div class="payment-value">
                                                <fmt:formatNumber value="${extraChargeAmounts[loop.index]}" type="number"/> VND
                                            </div>
                                        </div>
                                    </c:forEach>

                                    <div class="payment-row payment-extra-total">
                                        <div class="payment-label">Tổng phí phát sinh</div>
                                        <div class="payment-value">
                                            <fmt:formatNumber value="${extraChargeTotal}" type="number"/> VND
                                        </div>
                                    </div>

                                    <div class="payment-divider"></div>
                                </c:if>

                                <div class="payment-row">
                                    <div class="payment-label">Phí vượt km</div>
                                    <div class="payment-value">
                                        <fmt:formatNumber value="${not empty contract.extraKmFee ? contract.extraKmFee : 0}" type="number"/> VND
                                    </div>
                                </div>

                                <div class="payment-row">
                                    <div class="payment-label">Km vượt mức</div>
                                    <div class="payment-value">
                                        <fmt:formatNumber value="${not empty contract.extraKm ? contract.extraKm : 0}" type="number"/> km
                                    </div>
                                </div>

                                <div class="payment-row">
                                    <div class="payment-label">Phí trả xe trễ</div>
                                    <div class="payment-value">
                                        <fmt:formatNumber value="${not empty extraTimeFee ? extraTimeFee : 0}" type="number"/> VND
                                    </div>
                                </div>

                                <c:if test="${finalAmountDue < 0}">
                                    <div class="payment-row payment-final">
                                        <div class="payment-label">Số tiền khách cần thanh toán thêm</div>
                                        <div class="payment-value payment-final-value">
                                            <fmt:formatNumber value="${0}" type="number"/> VND
                                        </div>
                                    </div>

                                    <div class="payment-row payment-final">
                                        <div class="payment-label">Số tiền cần hoàn lại cho khách</div>
                                        <div class="payment-value payment-final-value">
                                            <fmt:formatNumber value="${finalAmountDue * (-1)}" type="number"/> VND
                                        </div>
                                    </div>
                                </c:if>

                                <c:if test="${finalAmountDue > 0}">
                                    <div class="payment-row payment-final">
                                        <div class="payment-label">Số tiền khách cần thanh toán thêm</div>
                                        <div class="payment-value payment-final-value">
                                            <fmt:formatNumber value="${finalAmountDue}" type="number"/> VND
                                        </div>
                                    </div>

                                    <div class="payment-row payment-final">
                                        <div class="payment-label">Số tiền cần hoàn lại cho khách</div>
                                        <div class="payment-value payment-final-value">
                                            <fmt:formatNumber value="${0}" type="number"/> VND
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </div>


                        <c:if test="${canProcessRefund}">
                            <div class="status-note status-note-warning" style="margin-top:16px;">
                                Khách hàng đã từ chối nhận xe. Đơn này đang chờ nhân viên xác nhận hoàn tiền.
                            </div>
                        </c:if>

                        <!-- Actions -->
                        <div class="contract-actions">
                            <a href="${pageContext.request.contextPath}/staff/contracts" class="btn-action btn-back">
                                Quay lại
                            </a>

                            <c:choose>
                                <c:when test="${contract.contractStatus eq 'CREATED' and empty carChangeRequest}">
                                    <button type="button"
                                            class="btn-action btn-check"
                                            onclick="openBeforeCheckModal()">
                                        Kiểm tra xe
                                    </button>

                                    <form method="post"
                                          action="${pageContext.request.contextPath}/staff/contracts"
                                          style="display:inline-block;"
                                          onsubmit="return validateSendToCustomer(${not empty preDeliveryCheck and preDeliveryCheck.checkResult eq 'OK'});">
                                        <input type="hidden" name="action" value="sendToCustomer">
                                        <input type="hidden" name="contractId" value="${contract.contractId}">

                                        <button type="submit" class="btn-action btn-complete">
                                            Gửi khách xác nhận
                                        </button>
                                    </form>

                                    <form method="post"
                                          action="${pageContext.request.contextPath}/staff/contracts"
                                          style="display:inline-block;">
                                        <input type="hidden" name="action" value="cancel">
                                        <input type="hidden" name="contractId" value="${contract.contractId}">

                                        <button type="submit"
                                                class="btn-action btn-back"
                                                onclick="return confirm('Bạn có chắc muốn hủy hợp đồng này?');">
                                            Hủy hợp đồng
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
                                                onclick="return confirm('Xác nhận đã hoàn tiền cho booking này?');">
                                            Xác nhận hoàn tiền
                                        </button>
                                    </form>
                                </c:when>

                                <c:when test="${contract.contractStatus eq 'WAITING_CUSTOMER_CONFIRM'}">
                                    <c:choose>
                                        <c:when test="${contract.customerConfirmed eq true}">
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/staff/contracts"
                                                  style="display:inline-block;"
                                                  onsubmit="return confirm('Xác nhận giao xe cho khách?');">
                                                <input type="hidden" name="action" value="deliverCar">
                                                <input type="hidden" name="contractId" value="${contract.contractId}">

                                                <button type="submit" class="btn-action btn-complete">
                                                    Giao xe
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
                                                        onclick="return confirm('Xác nhận khách không đến nhận xe?');">
                                                    Khách không đến
                                                </button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>

                                <c:when test="${contract.contractStatus eq 'ACTIVE'}">
                                    <button type="button" class="btn-action btn-check" onclick="openCheckFeesModal()">
                                        Kiểm tra - Phí phát sinh
                                    </button>

                                    <button type="button"
                                            class="btn-action btn-complete"
                                            onclick="handleCompleteReturn(${hasReturnCheck})">
                                        Hoàn tất trả xe
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
                    <c:if test="${canRequestCarChange or (not empty carChangeRequest and carChangeRequest.status eq 'PENDING')}">
                        <div class="contract-detail-wrapper" style="margin-top:16px;">
                            <div class="contract-detail-card">
                                <div class="contract-section">
                                    <h2>Yêu cầu đổi xe</h2>

                                    <c:if test="${not empty carChangeRequest and carChangeRequest.status eq 'PENDING'}">
                                        <div class="status-note status-note-info" style="margin-bottom:16px;">
                                            Đang có yêu cầu đổi xe chờ khách hàng phản hồi. Vui lòng xử lý tại màn hình đổi xe riêng.
                                        </div>
                                    </c:if>

                                    <div class="contract-actions" style="justify-content:flex-start;">
                                        <c:if test="${canRequestCarChange}">
                                            <a href="${pageContext.request.contextPath}/car-change?action=form&bookingId=${booking.bookingId}"
                                               class="btn-action btn-check request-change-btn">
                                                Yêu cầu đổi xe
                                            </a>
                                        </c:if>

                                        <c:if test="${not empty carChangeRequest and carChangeRequest.status eq 'PENDING'}">
                                            <a href="${pageContext.request.contextPath}/car-change?action=form&bookingId=${booking.bookingId}"
                                               class="btn-action btn-check request-change-btn">
                                                Xem xử lý đổi xe
                                            </a>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Modal kiểm tra trước giao xe -->
        <div id="beforeCheckModal" class="cf-modal-overlay">
            <div class="cf-modal">
                <div class="cf-modal-header">
                    <h3>Kiểm tra xe trước khi giao</h3>
                </div>

                <form method="post"
                      action="${pageContext.request.contextPath}/staff/contracts"
                      id="beforeCheckForm">

                    <input type="hidden" name="action" value="saveCheck">
                    <input type="hidden" name="contractId" value="${contract.contractId}">

                    <div class="cf-modal-body">

                        <div class="cf-section">
                            <h4>Kiểm tra ngoại thất</h4>
                            <div class="cf-issues-grid">
                                <label class="cf-checkbox-item"><input type="checkbox" name="exteriorIssues" value="Scratch"><span>Trầy xước</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="exteriorIssues" value="Dent"><span>Móp méo</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="exteriorIssues" value="Broken light"><span>Đèn hỏng</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="exteriorIssues" value="Dirty exterior"><span>Bẩn ngoại thất</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="exteriorIssues" value="Mirror issue"><span>Lỗi gương</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="exteriorIssues" value="Tire issue"><span>Lỗi lốp</span></label>
                            </div>
                        </div>

                        <div class="cf-section">
                            <h4>Kiểm tra nội thất</h4>
                            <div class="cf-issues-grid">
                                <label class="cf-checkbox-item"><input type="checkbox" name="interiorIssues" value="Dirty interior"><span>Bẩn nội thất</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="interiorIssues" value="Bad smell"><span>Mùi khó chịu</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="interiorIssues" value="Seat damage"><span>Ghế hư hỏng</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="interiorIssues" value="Dashboard issue"><span>Lỗi taplo</span></label>
                                <label class="cf-checkbox-item"><input type="checkbox" name="interiorIssues" value="Missing accessories"><span>Thiếu phụ kiện</span></label>
                            </div>
                        </div>

                        <div class="cf-section">
                            <h4>Mức nhiên liệu</h4>
                            <select name="fuelLevel" class="cf-input">
                                <option value="EMPTY">Rỗng</option>
                                <option value="1/4">1/4</option>
                                <option value="1/2">1/2</option>
                                <option value="3/4">3/4</option>
                                <option value="FULL">Đầy</option>
                            </select>
                        </div>

                        <div class="cf-section">
                            <h4>Số km hiện tại</h4>
                            <input type="number"
                                   class="cf-input"
                                   value="${car.currentOdometerKm}"
                                   readonly>
                        </div>

                        <div class="cf-section">
                            <h4>Kiểm tra hệ thống</h4>
                            <div class="info-table">
                                <div class="info-row">
                                    <div class="info-label">Bị chặn do bảo dưỡng</div>
                                    <div class="info-value">
                                        <c:choose>
                                            <c:when test="${maintenanceBlocked}">Có</c:when>
                                            <c:otherwise>Không</c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                <div class="info-row">
                                    <div class="info-label">Xung đột lịch</div>
                                    <div class="info-value">
                                        <c:choose>
                                            <c:when test="${scheduleConflict}">Có</c:when>
                                            <c:otherwise>Không</c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="cf-section">
                            <h4>Kết luận</h4>
                            <div class="cf-issues-grid">
                                <label class="cf-checkbox-item">
                                    <input type="radio" name="physicalStatus" value="OK" checked>
                                    <span>Đạt</span>
                                </label>
                                <label class="cf-checkbox-item">
                                    <input type="radio" name="physicalStatus" value="NOT_OK">
                                    <span>Không đạt</span>
                                </label>
                            </div>
                        </div>

                        <div class="cf-section">
                            <h4>Ghi chú</h4>
                            <textarea name="note" class="cf-input" style="height:100px; padding:12px;"></textarea>
                        </div>

                    </div>

                    <div class="cf-modal-footer">
                        <button type="button" class="cf-btn cf-btn-cancel" onclick="closeBeforeCheckModal()">
                            Hủy
                        </button>
                        <button type="submit" class="cf-btn cf-btn-save">
                            Lưu kiểm tra
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Modal hậu kiểm -->
        <div id="checkFeesModal" class="cf-modal-overlay">
            <div class="cf-modal">
                <div class="cf-modal-header">
                    <h3>Kiểm tra xe - Phí phát sinh</h3>
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
                                <span>Không phát hiện vấn đề / Xe được trả trong tình trạng bình thường</span>
                            </label>
                        </div>

                        <div class="cf-section">
                            <h4>Số km khi trả xe</h4>
                            <input type="number"
                                   name="odometerKm"
                                   class="cf-input"
                                   min="${preDeliveryCheck.odometerKm}"
                                   value="${not empty returnCheck ? returnCheck.odometerKm : ''}"
                                   placeholder="Nhập số km hiện tại"
                                   required>
                            <small style="color:#666;">
                                Số km lúc giao xe:
                                <fmt:formatNumber value="${preDeliveryCheck.odometerKm}" type="number"/> km
                            </small>
                        </div>

                        <div class="cf-section">
                            <h4>Thời gian trả thực tế</h4>

                            <div class="datetime-split-row">
                                <div class="datetime-col">
                                    <label class="cf-sub-label">Ngày trả</label>
                                    <input type="date"
                                           name="actualReturnDate"
                                           class="cf-input"
                                           value="${not empty actualReturnDateValue ? actualReturnDateValue : ''}"
                                           required>
                                </div>

                                <div class="datetime-col">
                                    <label class="cf-sub-label">Giờ trả</label>
                                    <select name="actualReturnHour" class="cf-input" required>
                                        <option value="">Chọn giờ</option>
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
                                Thời gian kết thúc hợp đồng:
                                <fmt:formatDate value="${contract.contractEndTime}" pattern="dd/MM/yyyy HH:mm"/>
                            </small>
                        </div>

                        <div class="cf-section">
                            <h4>Thông tin kiểm tra</h4>

                            <div class="cf-issues-grid">
                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Dirty exterior">
                                    <span>Bẩn ngoại thất</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Dirty interior">
                                    <span>Bẩn nội thất</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Bad smell">
                                    <span>Mùi khó chịu</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Fuel shortage">
                                    <span>Thiếu nhiên liệu</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Exterior scratch">
                                    <span>Trầy xước ngoại thất</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Interior damage">
                                    <span>Hư hỏng nội thất</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Missing accessories">
                                    <span>Thiếu phụ kiện</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Tire issue">
                                    <span>Lỗi lốp</span>
                                </label>

                                <label class="cf-checkbox-item">
                                    <input type="checkbox" name="issueTypes" value="Other damage">
                                    <span>Hư hỏng khác</span>
                                </label>
                            </div>
                        </div>

                        <div class="cf-section">
                            <div class="cf-selected-box">
                                <div class="cf-selected-title">Các lỗi đã chọn</div>
                                <div id="selectedIssuesPreview" class="cf-selected-tags"></div>
                            </div>
                        </div>

                        <div class="cf-section">
                            <h4>Phí phát sinh</h4>

                            <div class="cf-fee-table">
                                <div class="cf-fee-head">
                                    <div>Loại phí</div>
                                    <div>Mô tả</div>
                                    <div>Số tiền</div>
                                </div>

                                <div id="feeRowsContainer" class="cf-fee-body">
                                </div>
                            </div>
                        </div>

                    </div>

                    <div class="cf-modal-footer">
                        <button type="button" class="cf-btn cf-btn-cancel" onclick="closeCheckFeesModal()">
                            Hủy
                        </button>
                        <button type="submit" class="cf-btn cf-btn-save">
                            Lưu
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