<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Danh sách xe</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style-base.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/car-list.css">
    </head>
    <body>
        <!-- HEADER -->
        <jsp:include page="includes/header.jsp"/>

        <!-- MAIN CONTENT -->
        <section class="car-page">
            <div class="container car-layout">
                <!-- FILTER SIDEBAR -->
                <aside class="filter">

                    <!-- FORM SEARCH -->
                    <div class="search-box">
                        <form action="${pageContext.request.contextPath}/cars" method="get">
                            <input type="hidden" name="action" value="search"/>
                            <input type="text" name="keyword" placeholder="Tìm kiếm xe..." value="${param.keyword}">
                            <button type="submit">Tìm</button>
                        </form>
                    </div>

                    <!-- FORM FILTER RIÊNG (các filter + nút Apply) -->
                    <form action="${pageContext.request.contextPath}/cars" method="get">
                        <input type="hidden" name="action" value="filter"/>
                        <!-- Giữ keyword từ search để kết hợp -->
                        <input type="hidden" name="keyword" value="${param.keyword}"/>

                        <div class="filter-header">
                            <h3>Lọc theo</h3>
                            <a href="${pageContext.request.contextPath}/cars" class="reset">Xóa tất cả</a>
                        </div>

                        <!-- AVAILABLE -->
                        <div class="filter-group inline">
                            <label>Chỉ xe sẵn sàng ngay</label>
                            <input type="checkbox" name="availableOnly" ${param.availableOnly != null ? 'checked' : ''} onchange="this.form.submit();">
                        </div>

                        <!-- PRICE RANGE / DAY (VND thực tế) -->
                        <div class="filter-group">
                            <label>Giá thuê/ngày (VND)</label>
                            <div class="price-range">
                                <span>100.000</span>
                                <input type="range" name="maxPrice" min="100000" max="5000000" step="100000"
                                       value="${param.maxPrice != null ? param.maxPrice : '5000000'}"
                                       oninput="document.getElementById('maxPriceDisplay').innerText = new Intl.NumberFormat('vi-VN').format(this.value) + ' VND';">
                                <span id="maxPriceDisplay">
                                    <fmt:formatNumber value="${param.maxPrice != null ? param.maxPrice : 5000000}" pattern="#,##0" /> VND
                                </span>
                            </div>
                        </div>

                        <!-- BRAND -->
                        <div class="filter-group">
                            <label>Hãng xe</label>
                            <div class="checkbox-list">
                                <label><input type="checkbox" name="brand" value="Toyota" 
                                    ${fn:contains(fn:join(paramValues.brand, ','), 'Toyota') ? 'checked' : ''} onchange="this.form.submit();"> Toyota</label>
                                <label><input type="checkbox" name="brand" value="Mazda" 
                                    ${fn:contains(fn:join(paramValues.brand, ','), 'Mazda') ? 'checked' : ''} onchange="this.form.submit();"> Mazda</label>
                                <label><input type="checkbox" name="brand" value="BMW" 
                                    ${fn:contains(fn:join(paramValues.brand, ','), 'BMW') ? 'checked' : ''} onchange="this.form.submit();"> BMW</label>
                                <label><input type="checkbox" name="brand" value="VinFast" 
                                    ${fn:contains(fn:join(paramValues.brand, ','), 'VinFast') ? 'checked' : ''} onchange="this.form.submit();"> VinFast</label>
                            </div>
                        </div>

                        <!-- BODY TYPE -->
                        <div class="filter-group">
                            <label>Loại xe</label>
                            <div class="checkbox-list">
                                <label><input type="checkbox" name="type" value="Sedan" 
                                    ${fn:contains(fn:join(paramValues.type, ','), 'Sedan') ? 'checked' : ''} onchange="this.form.submit();"> Sedan</label>
                                <label><input type="checkbox" name="type" value="SUV" 
                                    ${fn:contains(fn:join(paramValues.type, ','), 'SUV') ? 'checked' : ''} onchange="this.form.submit();"> SUV</label>
                                <label><input type="checkbox" name="type" value="Hatchback" 
                                    ${fn:contains(fn:join(paramValues.type, ','), 'Hatchback') ? 'checked' : ''} onchange="this.form.submit();"> Hatchback</label>
                                <label><input type="checkbox" name="type" value="Coupe" 
                                    ${fn:contains(fn:join(paramValues.type, ','), 'Coupe') ? 'checked' : ''} onchange="this.form.submit();"> Coupe</label>
                            </div>
                        </div>

                        <!-- SEATS -->
                        <div class="filter-group">
                            <label>Số chỗ ngồi</label>
                            <div class="filter-options">                              
                                <button type="button" class="${param.seats == '4' ? 'active' : ''}" 
                                        onclick="document.getElementById('seats').value = '4'; this.form.submit();">4</button>
                                <button type="button" class="${param.seats == '5' ? 'active' : ''}" 
                                        onclick="document.getElementById('seats').value = '5'; this.form.submit();">5</button>
                                <button type="button" class="${param.seats == '7' ? 'active' : ''}" 
                                        onclick="document.getElementById('seats').value = '7'; this.form.submit();">7+</button>
                            </div>
                            <input type="hidden" id="seats" name="seats" value="${param.seats}">
                        </div>

                        <!-- TRANSMISSION -->
                        <div class="filter-group">
                            <label>Hộp số</label>
                            <div class="filter-options">
                                <button type="button" class="${empty param.transmission || param.transmission == 'Any' ? 'active' : ''}" 
                                        onclick="document.getElementById('trans').value = 'Any'; this.form.submit();">Bất kỳ</button>
                                <button type="button" class="${param.transmission == 'Automatic' ? 'active' : ''}" 
                                        onclick="document.getElementById('trans').value = 'Automatic'; this.form.submit();">Tự động</button>
                                <button type="button" class="${param.transmission == 'Manual' ? 'active' : ''}" 
                                        onclick="document.getElementById('trans').value = 'Manual'; this.form.submit();">Số sàn</button>
                            </div>
                            <input type="hidden" id="trans" name="transmission" value="${param.transmission}">
                        </div>

                        <!-- FUEL -->
                        <div class="filter-group">
                            <label>Nhiên liệu</label>
                            <div class="checkbox-list">
                                <label><input type="checkbox" name="fuel" value="Gasoline" 
                                    ${fn:contains(fn:join(paramValues.fuel, ','), 'Gasoline') ? 'checked' : ''} onchange="this.form.submit();"> Xăng (Gasoline)</label>
                                <label><input type="checkbox" name="fuel" value="Diesel" 
                                    ${fn:contains(fn:join(paramValues.fuel, ','), 'Diesel') ? 'checked' : ''} onchange="this.form.submit();"> Dầu diesel</label>
                                <label><input type="checkbox" name="fuel" value="Electric" 
                                    ${fn:contains(fn:join(paramValues.fuel, ','), 'Electric') ? 'checked' : ''} onchange="this.form.submit();"> Điện</label>
                                <label><input type="checkbox" name="fuel" value="Hybrid" 
                                    ${fn:contains(fn:join(paramValues.fuel, ','), 'Hybrid') ? 'checked' : ''} onchange="this.form.submit();"> Hybrid</label>
                            </div>
                        </div>

                        <!-- YEAR -->
                        <div class="filter-group">
                            <label>Năm sản xuất</label>
                            <select name="yearRange" class="filter-select" onchange="this.form.submit();">
                                <option value="Any" ${empty param.yearRange || param.yearRange == 'Any' ? 'selected' : ''}>Bất kỳ</option>
                                <option value="2024+" ${param.yearRange == '2024+' ? 'selected' : ''}>2024 trở lên</option>
                                <option value="2020-2023" ${param.yearRange == '2020-2023' ? 'selected' : ''}>2020 – 2023</option>
                                <option value="Before2020" ${param.yearRange == 'Before2020' ? 'selected' : ''}>Trước 2020</option>
                            </select>
                        </div>

                        <!-- NÚT APPLY (nếu không dùng onchange tự submit) -->
                        <!-- <button type="submit" class="apply-filter">Áp dụng bộ lọc</button> -->
                    </form>
                </aside>

                <!-- CAR LIST -->
                <main class="car-list">
                    <div class="list-header">
                        <h2>${cars.size()} xe có sẵn để thuê</h2>
                        <div class="list-actions">
                            <select>
                                <option>Giá thấp nhất</option>
                                <option>Giá cao nhất</option>
                                <option>Mới nhất</option>
                            </select>
                            <button type="button">Xem bản đồ</button>
                        </div>
                    </div>
                    <div class="car-grid">
                        <c:forEach var="car" items="${cars}">
                            <a href="${pageContext.request.contextPath}/cars?action=detail&carId=${car.carId}" class="car-card-link">
                                <div class="car-card">
                                    <div class="car-img">
                                        <img src="${car.imageUrl}" alt="${car.modelName}">
                                    </div>
                                    <div class="car-info">
                                        <h4>${car.modelName}</h4>
                                        <p>${car.brandName} • ${car.typeName}</p>
                                        <div class="price">
                                            <fmt:formatNumber value="${car.pricePerDay}" pattern="#,##0" /> VND / ngày
                                        </div>
                                    </div>
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </main>
            </div>
        </section>
    </body>
</html>