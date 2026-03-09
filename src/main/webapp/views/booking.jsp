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
    </head>
    <body>

        <div class="container my-4">

            <c:if test="${not empty errorMessage}">
                <div id="errorAlert" class="alert alert-danger">
                    ${errorMessage}
                </div>
            </c:if>

            <!-- BACK LINK -->
            <a href="${pageContext.request.contextPath}/cars?action=detail&id=${car.carId}"
               class="back-link mb-3 d-inline-block">
                ← Quay lại trang chi tiết xe
            </a>



            <form action="${pageContext.request.contextPath}/booking"
                  method="post"
                  id="bookingForm"
                  onsubmit="return validateBooking()">

                <!-- HIDDEN -->
                <input type="hidden" name="action" value="create">
                <input type="hidden" name="carId" value="${car.carId}">

                <div class="row g-4">

                    <!-- ================= LEFT COLUMN ================= -->
                    <div class="col-md-8">

                        <!-- USER INFO -->
                        <div class="card mb-4">
                            <div class="card-body">
                                <h5 class="section-title">👤 Thông tin người đặt xe</h5>

                                <div class="row">
                                    <div class="col-md-6">
                                        <p><strong>Họ tên:</strong> ${customer.fullName}</p>
                                        <p><strong>Email:</strong> ${customer.email}</p>
                                    </div>
                                    <div class="col-md-6">
                                        <p><strong>Số điện thoại:</strong> ${customer.phone}</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- CAR INFO -->
                        <!-- CAR INFO -->
                        <div class="card mb-4">
                            <div class="card-body">

                                <h5 class="section-title">🚗 Thông tin xe</h5>

                                <h6 class="car-title">${car.modelName}</h6>

                                <p class="text-muted mb-2">
                                    ${car.brandName} • ${car.typeName}
                                </p>

                                <ul class="car-specs">
                                    <li>🚗 ${car.seatCount} chỗ</li>
                                    <li>⚡ ${car.fuelType}</li>
                                    <li>⚙️ ${car.transmission}</li>
                                    <li>📅 ${car.modelYear}</li>
                                </ul>

                                <p class="price-text">
                                    Giá thuê:
                                    <strong>
                                        <fmt:formatNumber value="${car.pricePerDay}" pattern="#,###"/>
                                    </strong>
                                    VND / ngày
                                </p>

                                <input type="hidden" id="pricePerDayRaw" value="${car.pricePerDay}">

                                <span class="badge bg-success">Còn xe</span>

                            </div>
                        </div>


                        <!-- RENTAL DATE -->
                        <div class="card mb-4">
                            <div class="card-body">
                                <h5 class="section-title">📅 Thời gian thuê</h5>

                                <div class="row">
                                    <div class="col-md-6">
                                        <label class="form-label">Từ ngày</label>
                                        <input type="date"
                                               name="startDate"
                                               id="startDate"
                                               class="form-control"
                                               required>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label">Đến ngày</label>
                                        <input type="date"
                                               name="endDate"
                                               id="endDate"
                                               class="form-control"
                                               required>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- NOTE -->
                        <div class="card mb-4">
                            <div class="card-body">
                                <h5 class="section-title">📝 Ghi chú</h5>

                                <textarea name="note"
                                          class="form-control"
                                          rows="3"
                                          placeholder="Ghi chú thêm cho chủ xe (nếu có)"></textarea>
                            </div>
                        </div>

                        <!-- PAYMENT METHOD -->
                        <div class="card">
                            <div class="card-body">
                                <h5 class="section-title">💳 Phương thức thanh toán</h5>

                                <div class="form-check">
                                    <input class="form-check-input"
                                           type="radio"
                                           checked>
                                    <label class="form-check-label">
                                        Thanh toán khi nhận xe (Khuyến nghị)
                                    </label>
                                </div>

                                <div class="form-check text-muted">
                                    <input class="form-check-input"
                                           type="radio"
                                           disabled>
                                    <label class="form-check-label">
                                        Chuyển khoản ngân hàng (Coming soon)
                                    </label>
                                </div>

                                <div class="form-check text-muted">
                                    <input class="form-check-input"
                                           type="radio"
                                           disabled>
                                    <label class="form-check-label">
                                        Thanh toán online (Coming soon)
                                    </label>
                                </div>
                            </div>
                        </div>

                    </div>

                    <!-- ================= RIGHT COLUMN ================= -->
                    <div class="col-md-4">

                        <!-- ORDER SUMMARY -->
                        <div class="card sticky-summary">
                            <div class="card-body">
                                <h5 class="section-title">🧾 Tóm tắt đơn hàng</h5>

                                <div class="d-flex justify-content-between">
                                    <span>Giá thuê</span>
                                    <span id="priceText">0 VND</span>
                                </div>

                                <div class="d-flex justify-content-between">
                                    <span>Số ngày</span>
                                    <span id="daysText">0 ngày</span>
                                </div>

                                <hr>

                                <div class="d-flex justify-content-between total">
                                    <span>Tổng tiền</span>
                                    <span id="totalText">0 VND</span>
                                </div>

                                <button type="submit"
                                        class="btn btn-primary w-100 mt-4">
                                    Xác nhận đặt xe
                                </button>
                            </div>
                        </div>

                    </div>

                </div>
            </form>
        </div>

        <!-- JS -->
        <script src="assets/js/booking.js?v=3"></script>

    </body>
</html>
