<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>${pageTitle}</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/access-denied.css">
    </head>
    <body>

        <jsp:include page="includes/header.jsp"/>

        <main class="not-found-page">
            <section class="not-found-section">
                <div class="container">
                    <div class="not-found-box">
                        <div class="not-found-code">404</div>

                        <h1 class="not-found-title">
                            Trang không tồn tại
                        </h1>

                        <p class="not-found-message">
                            ${message}
                        </p>

                        <a href="${pageContext.request.contextPath}/home" class="not-found-btn">
                            Quay về trang chủ
                        </a>
                    </div>
                </div>
            </section>
        </main>

        <footer class="site-footer">
            <div class="container">
                <div class="footer-grid">

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

    </body>
</html>