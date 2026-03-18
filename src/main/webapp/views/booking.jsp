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

                                <button class="btn btn-primary w-100 mt-3">
                                    Xác nhận đặt xe
                                </button>

                            </div>

                        </div>

                    </div>
                </form>
            </div>
        </div>
                            
                             <div id="priceModal" class="modal">
            <div class="modal-content">

                <button class="modal-close" onclick="closeModal()">×</button>

                <h2 class="modal-title">Đơn giá thuê</h2>

                <div class="modal-body">

                    <ul>
                        <li>
                            Giá thuê xe được tính theo ngày, thời gian thuê xe ít hơn
                            24 giờ sẽ được tính tròn 1 ngày.
                        </li>

                        <li>
                            Giá thuê xe không bao gồm tiền xăng/tiền sạc pin. Khi kết thúc chuyến đi,
                            bạn vui lòng đổ xăng/sạc pin về lại mức ban đầu như khi nhận xe,
                            hoặc thanh toán lại chi phí xăng xe sạc pin cho chủ xe.
                        </li>

                        <li>
                            Giá thuê xe đã bao gồm phí dịch vụ của Mioto. Phí dịch vụ giúp duy trì
                            ứng dụng & chăm sóc khách hàng, bao gồm:
                        </li>
                    </ul>

                    <ul class="sub-list">
                        <li>Dịch vụ tổng đài hỗ trợ khách hàng đặt xe.</li>
                        <li>Tìm xe thay thế / hoàn tiền nếu chuyến bị huỷ.</li>
                        <li>Hỗ trợ tranh chấp phát sinh với chủ xe.</li>
                    </ul>

                </div>
            </div>
        <!-- JS -->
        <script src="assets/js/booking.js?v=3"></script>

    </body>
</html>
