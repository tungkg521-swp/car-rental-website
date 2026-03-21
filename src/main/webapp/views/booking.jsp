<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Đặt xe</title>

        <!-- Bootstrap LOCAL -->
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">

        <!-- CSS riêng cho booking -->
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/booking.css">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    </head>
    <body>

        <div class="container my-4">

            <div class="container my-5">
                <c:if test="${not empty errorMessage}">
                    <div id="errorAlert" class="alert alert-danger">
                        ${errorMessage}
                    </div>
                </c:if>

                <!-- BACK -->
                <a href="${pageContext.request.contextPath}/cars?action=detail&carId=${car.carId}"
                   class="back-link">
                    <span class="bi bi-arrow-left">Quay lại</span> 
                </a>

                <form action="${pageContext.request.contextPath}/booking"
                      method="post"
                      id="bookingForm"
                      onsubmit="return validateBooking()">

                    <input type="hidden" name="action" value="create">
                    <input type="hidden" name="carId" value="${car.carId}">

                    <div class="row g-4">

                        <!-- LEFT -->
                        <div class="col-lg-8">

                            <!-- USER -->
                            <div class="card p-4">
                                <h5 class="section-title">
                                    <i class="bi bi-person-circle"></i> Thông tin người đặt
                                </h5>

                                <div class="row">
                                    <div class="col-md-6">
                                        <p><strong>Họ tên:</strong> ${customer.fullName}</p>
                                        <p ><strong>Email:</strong><span class="text-muted"> ${customer.email}</span></p>
                                    </div>
                                    <div class="col-md-6">
                                        <p><strong>Số điện thoại:</strong> ${customer.phone}</p>
                                    </div>
                                </div>
                            </div>

                            <!-- CAR -->
                            <div class="card p-4 mt-4">

                                <img src="${pageContext.request.contextPath}/assets/images/cars/${car.imageFolder}/${car.imageFolder}_1.jpg"
                                     class="car-img mb-3">
                                <h1>${car.imageFolder}</h1>


                                <p class="text-muted">
                                    ${car.brandName} • ${car.typeName}
                                </p>

                                <div class="car-specs">
                                    <span><i class="bi bi-people"></i> ${car.seatCount} chỗ</span>
                                    <span><i class="bi bi-lightning"></i> ${car.fuelType}</span>
                                    <span><i class="bi bi-gear"></i> ${car.transmission}</span>
                                    <span><i class="bi bi-calendar"></i> ${car.modelYear}</span>
                                </div>

                                <div class="price-box mt-3">
                                    <span >
                                        Đơn giá thuê
                                        <i class="info-icon" onclick="openModal()">?</i>
                                    </span>
                                    <strong>
                                        <fmt:formatNumber value="${car.pricePerDay}" pattern="#,###"/> VND/ngày
                                    </strong>
                                </div>

                                <input type="hidden" id="pricePerDayRaw" value="${car.pricePerDay}">
                            </div>

                            <!-- DATE -->
                            <div class="card p-4 mt-4">
                                <h5 class="section-title">
                                    <i class="bi bi-calendar-check"></i> Thời gian thuê
                                </h5>

                                <div class="row">
                                    <div class="col-md-6">
                                        <label>Từ ngày</label>
                                        <input type="date" name="startDate"id="startDate" class="form-control" required>
                                    </div>
                                    <div class="col-md-6">
                                        <label>Đến ngày</label>
                                        <input type="date" name="endDate" id="endDate" class="form-control" required>
                                    </div>
                                </div>
                            </div>

                            <!-- NOTE -->
                            <div class="card p-4 mt-4">
                                <h5 class="section-title">
                                    <i class="bi bi-chat-left-text"></i> Ghi chú
                                </h5>

                                <textarea class="form-control"
                                          id="bookingNote"
                                          name="note"
                                          rows="3"
                                          placeholder="Ghi chú thêm..."></textarea>
                            </div>

                        </div>

                        <!-- RIGHT -->
                        <div class="col-lg-4">

                            <div class="card summary p-4">

                                <h5 class="section-title">
                                    <i class="bi bi-receipt"></i> Tóm tắt
                                </h5>

                                <div class="summary-row">
                                    <span>Giá/ngày</span>
                                    <span id="priceText">0 VND</span>
                                </div>

                                <div class="summary-row">
                                    <span>Số ngày</span>
                                    <span id="daysText">0 ngày</span>
                                </div>

                                <hr>

                                <div class="summary-total">
                                    <span>Tổng</span>
                                    <span id="totalText">0 VND</span>
                                </div>

                                <button type="button" id="openConfirmBtn" class="btn btn-primary w-100 mt-3">
                                    Xác nhận đặt xe
                                </button>

                            </div>

                        </div>

                    </div>
                </form>
            </div>
        </div>

        <!-- ================= CONFIRM BOOKING MODAL ================= -->
        <div id="confirmBookingModal" class="confirm-modal-overlay">
            <div class="confirm-modal-box">

                <button type="button" class="confirm-close" onclick="closeConfirmModal()">×</button>

                <div class="confirm-topbar">
                    <div>
                        <p class="confirm-subtitle">Booking Preview</p>
                        <h2 class="confirm-title">Xác nhận đặt xe</h2>
                    </div>
                    <div class="confirm-badge">Sẵn sàng đặt xe</div>
                </div>

                <div class="confirm-layout">
                    <!-- LEFT -->
                    <div class="confirm-left-panel">
                        <div class="confirm-image-wrap">
                            <img src="${pageContext.request.contextPath}/assets/images/cars/${car.imageFolder}/${car.imageFolder}_1.jpg"
                                 alt="${car.imageFolder}"
                                 class="confirm-car-img">
                        </div>

                        <div class="car-main-info">
                            <h3>${car.imageFolder}</h3>
                            <p>${car.brandName} • ${car.typeName}</p>
                        </div>

                        <div class="mini-info-grid">
                            <div class="mini-info-card">
                                <span class="mini-label">Bắt đầu</span>
                                <strong id="confirmStartDate">--/--/----</strong>
                            </div>
                            <div class="mini-info-card">
                                <span class="mini-label">Kết thúc</span>
                                <strong id="confirmEndDate">--/--/----</strong>
                            </div>
                        </div>

                        <div class="price-highlight-card">
                            <div class="price-highlight-top">Tổng thanh toán</div>
                            <div class="price-highlight-main" id="confirmTotal">0 VND</div>
                            <div class="price-highlight-sub">
                                <span id="confirmDays">0 ngày</span>
                                <span>•</span>
                                <span id="confirmPricePerDay">0 VND</span>
                            </div>
                        </div>
                    </div>

                    <!-- RIGHT -->
                    <div class="confirm-right-panel">
                        <div class="confirm-card">
                            <div class="section-heading">
                                <span class="section-dot"></span>
                                <h4>Thông tin người đặt</h4>
                            </div>

                            <div class="info-list">
                                <div class="info-row">
                                    <span>Họ tên</span>
                                    <strong>${customer.fullName}</strong>
                                </div>
                                <div class="info-row">
                                    <span>Email</span>
                                    <strong>${customer.email}</strong>
                                </div>
                                <div class="info-row">
                                    <span>Số điện thoại</span>
                                    <strong>${customer.phone}</strong>
                                </div>
                            </div>
                        </div>

                        <div class="confirm-card">
                            <div class="section-heading">
                                <span class="section-dot"></span>
                                <h4>Ghi chú cho chủ xe</h4>
                            </div>

                            <div id="confirmNoteBox" class="confirm-note-box">
                                Không có ghi chú
                            </div>
                        </div>

                        <div class="confirm-card">
                            <div class="section-heading">
                                <span class="section-dot"></span>
                                <h4>Tóm tắt chi phí</h4>
                            </div>

                            <div class="summary-modern">
                                <div class="summary-modern-row">
                                    <span>Giá thuê mỗi ngày</span>
                                    <strong id="confirmPricePerDay2">0 VND</strong>
                                </div>
                                <div class="summary-modern-row">
                                    <span>Số ngày thuê</span>
                                    <strong id="confirmDays2">0 ngày</strong>
                                </div>
                                <div class="summary-modern-row total">
                                    <span>Tổng cộng</span>
                                    <strong id="confirmTotal2">0 VND</strong>
                                </div>
                            </div>
                        </div>

                        <div class="confirm-card policy-card">
                            <div class="section-heading">
                                <span class="section-dot"></span>
                                <h4>Điều khoản & chính sách hủy chuyến</h4>
                            </div>

                            <div class="policy-content">
                                <p>
                                    Quý khách vui lòng không hút thuốc trên xe hoặc mang các thực phẩm có mùi.
                                    Nếu thay đổi thời gian đón/trả hoặc lộ trình, vui lòng báo trước để được hỗ trợ tốt hơn.
                                </p>

                                <div class="policy-table-wrap">
                                    <table class="policy-table">
                                        <thead>
                                            <tr>
                                                <th>Thời điểm hủy</th>
                                                <th>Phí hủy</th>
                                                <th>Hoàn cọc</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>Trong 1 giờ sau đặt cọc</td>
                                                <td>0% tiền cọc</td>
                                                <td>100% tiền cọc</td>
                                            </tr>
                                            <tr>
                                                <td>&gt; 7 ngày trước khởi hành</td>
                                                <td>30% tiền cọc</td>
                                                <td>70% tiền cọc</td>
                                            </tr>
                                            <tr>
                                                <td>&le; 7 ngày trước khởi hành</td>
                                                <td>100% tiền cọc</td>
                                                <td>0% tiền cọc</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>

                                <label class="policy-check-modern">
                                    <input type="checkbox" id="agreePolicy">
                                    <span class="custom-check"></span>
                                    <span>Tôi đã đọc và đồng ý với chính sách hủy chuyến.</span>
                                </label>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="confirm-actions modern-actions">
                    <button type="button" class="btn-cancel-confirm" onclick="closeConfirmModal()">Hủy</button>
                    <button type="button" class="btn-submit-confirm" id="finalSubmitBtn">Đặt xe ngay</button>
                </div>
            </div>
        </div>
        <!-- JS -->
        <script src="${pageContext.request.contextPath}/assets/js/booking.js?v=4"></script>

    </body>
</html>
