<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Car Rental</title>
        <meta charset="UTF-8">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

    </head>
    <body>

        <jsp:include page="includes/header.jsp"/>

        <!-- HERO -->
        <section class="hero">
            <div class="container">

                <!-- Row 1 -->
                <div class="grid-12 hero-main">

                    <!-- Left -->
                    <div class="col-6 hero-left">
                        <h1 class="hero-title">
                            Elevate Your Travel<br>
                            Experience
                        </h1>

                        <div class="mini-cars">
                            <div class="mini-card"></div>
                            <div class="mini-card"></div>
                        </div>
                    </div>

                    <!-- Right -->
                    <div class="col-6 hero-right">
                        <div class="big-car"></div>
                    </div>

                </div>

                <!-- Row 2 -->
                <div class="grid-12 hero-summary">
                    <div class="col-4">
                        <h3>Luxury Meets<br>Performance</h3>
                    </div>

                    <div class="col-4">
                        <p>
                            Trải nghiệm thuê xe cao cấp với giao diện hiện đại,
                            đặt xe nhanh chóng và minh bạch.
                        </p>
                    </div>

                    <div class="col-4 cta">
                        <a href="${pageContext.request.contextPath}/cars" class="btn-primary">
                            BOOK NOW
                        </a>

                    </div>
                </div>

            </div>
        </section>

        <section class="promotion">
            <div class="container">

                <h2 class="promotion-title">Chương Trình Khuyến Mãi</h2>
                <p class="promotion-subtitle">
                    Nhận nhiều ưu đãi hấp dẫn từ Mioto
                </p>

                <div class="promotion-wrapper">

                    <!-- Arrow trái -->
                    <button class="promo-arrow left" id="promoPrev">‹</button>

                    <!-- Carousel -->
                    <div class="promotion-carousel" id="promoCarousel">
                        <div class="promo-card"></div>
                        <div class="promo-card"></div>
                        <div class="promo-card"></div>
                        <div class="promo-card"></div> <!-- card dư để test scroll -->
                    </div>

                    <!-- Arrow phải -->
                    <button class="promo-arrow right" id="promoNext">›</button>

                </div>

            </div>
        </section>

        <section class="recommend">
            <div class="container">
                <h2 class="recommend-title">Xe Dành Cho Bạn</h2>

                <div class="grid-12 recommend-grid">
                    <!-- 8 KHUNG -->
                    <div class="col-3 car-card"></div>
                    <div class="col-3 car-card"></div>
                    <div class="col-3 car-card"></div>
                    <div class="col-3 car-card"></div>

                    <div class="col-3 car-card"></div>
                    <div class="col-3 car-card"></div>
                    <div class="col-3 car-card"></div>
                    <div class="col-3 car-card"></div>
                </div>
            </div>
        </section>


        <footer class="site-footer">
            <div class="container">
                <div class="footer-grid">

                    <!-- CỘT 1: LOGO + CÔNG TY -->
                    <div class="footer-col company">
                        <div class="footer-logo">
                            <span class="logo-text">Rental Car</span>
                            <span class="logo-desc">Dịch vụ<br>cho thuê xe<br>linh hoạt</span>
                        </div>

                        <h4>CÔNG TY CỔ PHẦN THƯƠNG MẠI<br>VÀ DỊCH VỤ GREEN FUTURE</h4>

                        <p class="small">
                            MST/MSDN: 010771284 do Sở KHĐT TP Hà Nội cấp lần đầu ngày 28/02/2025
                        </p>

                        <p class="small">
                            Địa chỉ: Tòa văn phòng Symphony, Đường Chu Huy Mân, 
                            Khu đô thị Vinhomes Riverside, Phường Phúc Lợi, 
                            Quận Long Biên, Thành phố Hà Nội, Việt Nam
                        </p>

                        <div class="cert">
                            <div class="cert-box">ĐÃ THÔNG BÁO<br>BỘ CÔNG THƯƠNG</div>
                        </div>
                    </div>

                    <!-- CỘT 2: ĐẶT XE -->
                    <div class="footer-col">
                        <h4>Đặt xe</h4>
                        <ul>
                            <li><a href="#">Ngắn hạn</a></li>
                            <li><a href="#">Dài hạn</a></li>
                            <li><a href="#">Doanh nghiệp</a></li>
                        </ul>
                    </div>

                    <!-- CỘT 3: GIỚI THIỆU -->
                    <div class="footer-col">
                        <h4>Giới thiệu</h4>
                        <ul>
                            <li><a href="#">Về chúng tôi</a></li>
                            <li><a href="#">Tin tức</a></li>
                        </ul>
                    </div>

                    <!-- CỘT 4: LIÊN HỆ -->
                    <div class="footer-col">
                        <h4>Liên hệ</h4>
                        <p class="hotline">📞 1900 1877</p>
                        <p>✉️ support@greenfuture.tech</p>
                        <p>✉️ car.rental@greenfuture.tech</p>
                    </div>

                </div>

                <!-- BOTTOM -->
                <div class="footer-bottom">
                    <span>©2025 Green Future. All rights reserved.</span>
                    <a href="#">Điều khoản sử dụng</a>
                </div>
            </div>
        </footer>


        <script>
            function toggleNotification() {
                const popup = document.querySelector(".notification-popup");
                popup.classList.toggle("show");
            }
        </script>
        <script src="assets/js/main.js"></script>
    </body>
</html>