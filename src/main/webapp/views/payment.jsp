<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Thanh toán tiền cọc</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/payment.css">

        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    </head>
    <body>
        <div class="payment-page">

            <a href="${pageContext.request.contextPath}/booking?action=list" class="back-link">
                <span>←</span>
                <span>Quay lại danh sách booking</span>
            </a>

            <c:if test="${param.result == 'success'}">
                <div class="alert alert-success mb-4">
                    Thanh toán cọc thành công. Booking của bạn đã được xác nhận.
                </div>
            </c:if>

            <c:if test="${param.result == 'fail'}">
                <div class="alert alert-danger mb-4">
                    Thanh toán thất bại. Booking vẫn đang ở trạng thái chờ thanh toán.
                </div>
            </c:if>

            <c:if test="${param.result == 'invalid'}">
                <div class="alert alert-warning mb-4">
                    Giao dịch không hợp lệ hoặc booking không còn ở trạng thái chờ thanh toán.
                </div>
            </c:if>

            <div class="payment-layout">

                <!-- LEFT -->
                <div class="payment-left">
                    <div class="payment-badge">Bước 2/2: Thanh toán cọc</div>

                    <h2>Giữ xe bằng tiền cọc</h2>
                    <p>
                        Hoàn tất thanh toán tiền cọc để booking được xác nhận và tạo hợp đồng thuê xe.
                    </p>

                    <div class="price-box-main">
                        <div class="label">Tiền cọc và dằn xe cần thanh toán</div>
                        <div class="value">
                            <fmt:formatNumber value="${depositAmount}" pattern="#,###"/> VND
                        </div>
                        <div class="sub">Thanh toán trước tiền cọc để giữ xe</div>
                    </div>

                    <div class="mini-summary">
                        <div class="mini-item">
                            <span>Tổng tiền thuê</span>
                            <strong>
                                <fmt:formatNumber value="${booking.totalEstimatedPrice}" pattern="#,###"/> VND
                            </strong>
                        </div>

                      

                        <div class="mini-item">
                            <span>Trạng thái hiện tại</span>
                            <strong>
                                ${booking.status}

                            </strong>
                        </div>
                    </div>
                </div>

                <!-- RIGHT -->
                <div class="payment-right">

                    <div class="payment-card">
                        <div class="section-head">
                            <h3>Thông tin booking</h3>
                            <span class="status-badge other">${booking.status}</span>

                        </div>

                        <div class="info-grid">
                            <div class="info-item">
                                <span style="color: #16a34a">Mã booking</span>
                                <strong>#${booking.bookingId}</strong>
                            </div>

                            <div class="info-item">
                                <span style="color: #16a34a">Tên xe</span>
                                <strong>${booking.carName}</strong>
                            </div>

                            <div class="info-item">
                                <span style="color: #16a34a">Ngày bắt đầu</span>
                                <strong>
                                    <fmt:formatDate value="${booking.startTime}" pattern="dd/MM/yyyy HH:mm"/>
                                </strong>
                            </div>

                            <div class="info-item">
                                <span style="color: #16a34a">Ngày kết thúc</span>
                                <strong>
                                    <fmt:formatDate value="${booking.endTime}" pattern="dd/MM/yyyy HH:mm"/>
                                </strong>
                            </div>
                        </div>
                    </div>

                    <div class="payment-card">
                        <div class="section-head">
                            <h3>Tóm tắt thanh toán</h3>
                        </div>

                        <div class="payment-summary">
                            <div class="summary-row">
                                <span>Tổng tiền thuê</span>
                                <strong>
                                    <fmt:formatNumber value="${booking.totalEstimatedPrice}" pattern="#,###"/> VND
                                </strong>
                            </div>

                            <div class="summary-row deposit">
                                <span>Tiền cọc cần thanh toán ngay</span>
                                <strong>
                                    <fmt:formatNumber value="${depositAmount}" pattern="#,###"/> VND
                                </strong>
                            </div>


                         
 
                               
                            <div class="summary-row total">
                                <span>Xác nhận thanh toán</span>
                                <strong>
                                    <fmt:formatNumber value="${depositAmount}" pattern="#,###"/> VND
                                </strong>
                            </div>

                            <div class="summary-row">
                                <span>Hạn thanh toán Cọc</span>
                                <strong>
                                    <fmt:formatDate value="${booking.paymentDeadline}" pattern="dd/MM/yyyy HH:mm"/>
                                </strong>
                            </div>
                        </div>
                    </div>

                    <div class="payment-card">
                        <div class="section-head">
                            <h3>Phương thức thanh toán</h3>
                        </div>

                        <div class="note-box">
                            Đây là bước mô phỏng. Bạn có thể thử cả trường hợp thanh toán thành công, thất bại hoặc hủy thanh toán để kiểm tra luồng nghiệp vụ.
                        </div>

                        <c:if test="${booking.status == 'AWAITING_PAYMENT'}">
                            <form action="${pageContext.request.contextPath}/payment" method="post" class="payment-methods">
                                <input type="hidden" name="bookingId" value="${booking.bookingId}">

                                <div class="mb-3">
                                    <label class="form-label">Chọn cổng thanh toán</label>
                                    <select name="paymentMethod" class="form-select">
                                        <option value="VNPAY">VNPAY</option>
                                        <option value="MOMO">MoMo</option>
                                    </select>
                                </div>

                                <div class="action-grid">
                                    <button type="submit"
                                            name="action"
                                            value="sandbox-success"
                                            class="btn-pay-success">
                                        Thanh toán thành công
                                    </button>

                                    <button type="submit"
                                            name="action"
                                            value="sandbox-fail"
                                            class="btn-pay-fail">
                                        Thanh toán thất bại
                                    </button>

                                    <button type="submit"
                                            name="action"
                                            value="cancel-payment"
                                            class="btn-pay-cancel">
                                        Hủy thanh toán
                                    </button>
                                </div>
                            </form>
                        </c:if>


                    </div>

                </div>
            </div>
        </div>
    </body>
</html>