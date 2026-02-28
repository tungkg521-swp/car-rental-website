<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <title>${car.modelName}</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style-base.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/car-detail.css">
    </head>
    <body>

        <jsp:include page="includes/header.jsp"/>

        <section class="car-detail-page">
            <div class="container">

                <!-- TOP SECTION -->
                <!-- TOP SECTION -->
                <div class="car-top">

                    <!-- LEFT: IMAGE -->
                    <div class="car-gallery">

                        <!-- ẢNH LỚN -->
                        <div class="main-image">
                            <button class="nav prev" onclick="prevImage()">‹</button>

                            <img id="mainCarImage"
                                 src="${pageContext.request.contextPath}/assets/images/cars/${car.imageFolder}/${car.imageFolder}_1.jpg"
                                 alt="${car.modelName}">

                            <button class="nav next" onclick="nextImage()">›</button>
                        </div>

                        <!-- THUMBNAILS -->
                        <div class="thumbs">
                            <c:forEach var="i" begin="1" end="5">
                                <img src="${pageContext.request.contextPath}/assets/images/cars/${car.imageFolder}/${car.imageFolder}_${i}.jpg"
                                     onclick="changeImage(${i})">
                            </c:forEach>
                        </div>

                    </div>

                    <!-- RIGHT: INFO -->
                    <div class="car-summary">
                        <h1>
                            ${car.modelName}
                            <span class="status">${car.status}</span>
                        </h1>

                        <div class="price">
                            ${car.pricePerDay} <span>VND / Ngày</span>
                        </div>

                        <div class="badge">Miễn phí sạc tới 31/12/2027</div>

                        <ul class="specs">
                            <li>🚗 ${car.seatCount} chỗ</li>
                            <li>⚙️ ${car.transmission}</li>
                            <li>⛽ ${car.fuelType}</li>
                            <li>📅 Năm ${car.modelYear}</li>
                            <li>🚘 ${car.typeName}</li>
                            <li>🏷️ ${car.brandName}</li>
                        </ul>

                        <a href="${pageContext.request.contextPath}/booking?action=create&carId=${car.carId}"
                           class="btn ${car.status ne 'AVAILABLE' ? 'disabled' : ''}">
                            Đặt xe
                        </a>



                        <a href="#" class="consult">Nhận thông tin tư vấn</a>
                    </div>

                </div>


                <!-- OTHER FEATURES -->
                <div class="section">
                    <h2>Mô tả xe</h2>
                    <p>${car.description}</p>
                </div>

                <!-- CONDITIONS + SIMILAR -->
                <div class="bottom-grid">

                    <!-- CONDITIONS -->
                    <div class="conditions">
                        <h2>Điều kiện thuê xe</h2>

                        <div class="box">
                            <h4>Thông tin cần có khi nhận xe</h4>
                            <ul>
                                <li>CCCD hoặc Hộ chiếu còn thời hạn</li>
                                <li>Bằng lái hợp lệ</li>
                            </ul>
                        </div>

                        <div class="box">
                            <h4>Hình thức thanh toán</h4>
                            <ul>
                                <li>Trả trước</li>
                                <li>Thanh toán 100% khi nhận xe</li>
                            </ul>
                        </div>

                        <div class="box">
                            <h4>Chính sách đặt cọc</h4>
                            <p>Đặt cọc theo quy định của chủ xe</p>
                        </div>
                    </div>

                    <!-- SIMILAR CAR (để tạm – sẽ làm sau) -->
                    <div class="similar">
                        <h2>Xe tương tự</h2>
                        <p>Đang cập nhật...</p>
                    </div>

                </div>

            </div>
        </section>

        <script src="${pageContext.request.contextPath}/assets/js/car-detail.js"></script>

    </body>
</html>

