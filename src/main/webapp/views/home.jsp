<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Car Rental</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=21">
    </head>
    <body>

        <jsp:include page="includes/header.jsp"/>

        <section class="hero">
            <div class="container">
                <div class="grid-12 hero-main">
                    <div class="col-6 hero-left">
                        <h1 class="hero-title">
                            Elevate Your Travel<br>
                            Experience
                        </h1>

                        <div class="mini-cars">
                            <div class="mini-card image-card">
                                <img src="${pageContext.request.contextPath}/assets/images/cars/vf3/vf3_1.jpg" alt="VF3">
                            </div>
                            <div class="mini-card image-card">
                                <img src="${pageContext.request.contextPath}/assets/images/cars/vf6_eco/vf6_eco_1.jpg" alt="VF6 Eco">
                            </div>
                        </div>
                    </div>

                    <div class="col-6 hero-right">
                        <div class="big-car image-card">
                            <img src="${pageContext.request.contextPath}/assets/images/cars/vf7_plus/vf7_plus_1.jpg" alt="VF7 Plus">
                        </div>
                    </div>
                </div>


                <div class="hero-booking-panel">
                    <p class="hero-kicker">AUTOMOBILI RENTAL CAR</p>

                    <h2 class="hero-booking-title">
                        THUÊ XE DỄ DÀNG<br>
                        CHO MỌI HÀNH TRÌNH
                    </h2>

                    <p class="hero-booking-subtitle">
                        Chọn ngày thuê và trả xe để tìm những mẫu xe đang khả dụng cho lịch trình của bạn.
                    </p>

                    <form action="${pageContext.request.contextPath}/cars" method="get" class="hero-booking-form">
                        <input type="hidden" name="action" value="list">

                        <div class="hero-booking-grid">
                            <div class="hero-field">
                                <label for="startDate">Pick-up date</label>
                                <input type="date" id="startDate" name="startDate" required>
                            </div>

                            <div class="hero-field">
                                <label for="endDate">Return date</label>
                                <input type="date" id="endDate" name="endDate" required>
                            </div>

                            <div class="hero-field hero-field-submit">
                                <button type="submit" class="hero-search-btn">Tìm xe ngay</button>
                            </div>
                        </div>
                    </form>
                </div>
                        <!-- Row 2 -->

                <div class="grid-12 hero-summary">
                    <div class="col-4">
                        <h3>Luxury Meets<br>Performance</h3>
                    </div>



                    <div class="col-4 cta">
                        <a href="${pageContext.request.contextPath}/cars" class="btn-primary">
                            BOOK NOW
                        </a>
                    </div>
                </div>
            </div>
        </section>

        <section class="promo-section">
            <div class="promo-header">
                <h2>Chương Trình Khuyến Mãi</h2>
                <p>Nhận nhiều ưu đãi hấp dẫn dành cho khách hàng đặt xe online</p>
            </div>

            <div class="promo-grid">

                <!-- PROMO 1 -->
                <div class="promo-card promo-dark"
                     data-title="Giảm 10% cho khách hàng mới"
                     data-subtitle="Ưu đãi dành riêng cho người dùng mới"

                     data-content="Chương trình ưu đãi dành cho khách hàng lần đầu đăng ký tài khoản và thực hiện đặt xe trên hệ thống. Bạn sẽ được giảm ngay 10% giá trị đơn thuê xe đầu tiên. Ưu đãi này giúp người dùng mới dễ dàng trải nghiệm dịch vụ thuê xe với chi phí tiết kiệm hơn. Chương trình chỉ áp dụng một lần duy nhất trên mỗi tài khoản hợp lệ.">
                    <span class="promo-badge">NEW USER</span>
                    <h3>Giảm 10% cho khách hàng mới</h3>
                    <p>Áp dụng cho lần đặt xe đầu tiên khi đăng ký tài khoản mới.</p>
                </div>

                <!-- PROMO 2 -->
                <div class="promo-card promo-blue"
                     data-title="Ưu đãi cuối tuần"
                     data-subtitle="Tiết kiệm hơn cho những chuyến đi ngắn ngày"

                     data-content="Chương trình ưu đãi cuối tuần được áp dụng cho các đơn đặt xe vào thứ Bảy và Chủ nhật. Đây là lựa chọn phù hợp cho những chuyến đi ngắn ngày cùng gia đình hoặc bạn bè. Khách hàng sẽ nhận được mức giá tốt hơn trong thời gian cuối tuần, giúp việc lên kế hoạch cho các chuyến đi trở nên linh hoạt và tiết kiệm hơn.">
                    <span class="promo-badge">WEEKEND</span>
                    <h3>Ưu đãi cuối tuần</h3>
                    <p>Tiết kiệm hơn cho những chuyến đi ngắn ngày cùng gia đình.</p>
                </div>

                <!-- PROMO 3 -->
                <div class="promo-card promo-orange"
                     data-title="Thuê dài ngày giá tốt"
                     data-subtitle="Ưu đãi hấp dẫn cho hành trình dài ngày"

                     data-content="Đối với những khách hàng có nhu cầu thuê xe từ 3 ngày trở lên, hệ thống áp dụng mức giá ưu đãi tốt hơn so với thuê ngắn ngày. Chương trình này phù hợp cho các chuyến du lịch dài ngày, công tác hoặc về quê. Việc thuê xe trong thời gian dài không chỉ giúp bạn chủ động di chuyển mà còn tối ưu chi phí đáng kể.">
                    <span class="promo-badge">LONG TRIP</span>
                    <h3>Thuê dài ngày giá tốt</h3>
                    <p>Đặt từ 3 ngày trở lên để nhận mức giá ưu đãi hơn.</p>
                </div>

            </div>
        </section>
        <div id="promoModal" class="promo-modal">
            <div class="promo-modal-overlay"></div>

            <div class="promo-modal-box">
                <button type="button" class="promo-modal-close" id="closePromoModal">&times;</button>

                <div class="promo-modal-body">
                    <h3 id="promoModalTitle"></h3>
                    <h4 id="promoModalSubtitle"></h4>
                    <div class="promo-line"></div>
                    <p id="promoModalContent"></p>
                </div>
            </div>
        </div>

        <section class="recommend">
            <div class="container">
                <h2 class="recommend-title">Xe Dành Cho Bạn</h2>

                <div class="grid-12 recommend-grid">
                    <a class="col-3 car-info-card"
                    href="${pageContext.request.contextPath}/cars">
                        <img src="${pageContext.request.contextPath}/assets/images/cars/vf3/vf3_1.jpg" alt="VF3">
                        <div class="car-card-body">
                            <h4>VinFast VF3</h4>
                            <p>500.000đ / ngày</p>
                        </div>
                    </a>

                    <a class="col-3 car-info-card"
               href="${pageContext.request.contextPath}/cars">
                        <img src="${pageContext.request.contextPath}/assets/images/cars/vf6_eco/vf6_eco_1.jpg" alt="VF6 Eco">
                        <div class="car-card-body">
                            <h4>VinFast VF6 Eco</h4>
                            <p>800.000đ / ngày</p>
                        </div>
                    </a>

                    <a class="col-3 car-info-card"
                   href="${pageContext.request.contextPath}/cars">
                        <img src="${pageContext.request.contextPath}/assets/images/cars/vf6_plus/vf6_plus_1.jpg" alt="VF6 Plus">
                        <div class="car-card-body">
                            <h4>VinFast VF6 Plus</h4>
                            <p>900.000đ / ngày</p>
                        </div>
                    </a>

                    <a class="col-3 car-info-card"
                     href="${pageContext.request.contextPath}/cars">
                        <img src="${pageContext.request.contextPath}/assets/images/cars/vf7_plus/vf7_plus_1.jpg" alt="VF7 Plus">
                        <div class="car-card-body">
                            <h4>VinFast VF7 Plus</h4>
                            <p>1.300.000đ / ngày</p>
                        </div>
                    </a>
                </div>
            </div>
        </section>

        <footer class="site-footer">
            <div class="container">
                <div class="footer-grid">
                    <div class="footer-col company">
                        <div class="footer-logo">
                            <span class="logo-text">Rental Car</span>
                            <span class="logo-desc">Dịch vụ<br>cho thuê xe<br>linh hoạt</span>
                        </div>

                        <h4>CÔNG TY DỊCH VỤ CHO THUÊ XE</h4>

                        <p class="small">
                            MST/MSDN: 010771284 do Sở KHĐT TP Hà Nội cấp lần đầu ngày 28/02/2025
                        </p>

                        <p class="small">
                            Địa chỉ: 600, Nguyễn văn Cừ nối dài, Ninh Kiều, Cần Thơ
                        </p>

                        <div class="cert">
                            <div class="cert-box">ĐÃ THÔNG BÁO<br>BỘ CÔNG THƯƠNG</div>
                        </div>
                    </div>

                    <div class="footer-col">
                        <h4>Đặt xe</h4>
                        <ul>
                            <li><a href="#">Ngắn hạn</a></li>
                            <li><a href="#">Dài hạn</a></li>
                            <li><a href="#">Doanh nghiệp</a></li>
                        </ul>
                    </div>

                    <div class="footer-col">
                        <h4>Giới thiệu</h4>
                        <ul>
                            <li><a href="#">Về chúng tôi</a></li>
                            <li><a href="#">Tin tức</a></li>
                        </ul>
                    </div>

                    <div class="footer-col">
                        <h4>Liên hệ</h4>
                        <p class="hotline">📞 1900 1877</p>
                        <p>✉️ support@greenfuture.tech</p>
                        <p>✉️ car.rental@greenfuture.tech</p>
                    </div>
                </div>

                <div class="footer-bottom">
                    <span>©2025 Green Future. All rights reserved.</span>
                    <a href="#">Điều khoản sử dụng</a>
                </div>
            </div>
        </footer>


        <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/home.js"></script>

    </body>
</html>