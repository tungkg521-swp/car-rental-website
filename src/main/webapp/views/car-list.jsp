
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Danh sách xe</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style-base.css?v=2">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/car-list.css?v=4">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=22">
    </head>
    <body>
        <!-- HEADER -->
        <jsp:include page="includes/header.jsp"/>

        <!-- MAIN CONTENT -->
        <section class="car-page">
            <div class="container car-layout">
                <!-- FILTER SIDEBAR -->
                <aside class="filter">

                    <!-- FORM SEARCH RIÊNG (chỉ keyword) -->
                    <div class="search-box">
                        <form action="${pageContext.request.contextPath}/cars" method="get">
                            <input type="hidden" name="action" value="search"/>
                            <input type="hidden" name="startDate" value="${param.startDate}"/>
                            <input type="hidden" name="startHour" value="${param.startHour}"/>
                            <input type="hidden" name="endDate" value="${param.endDate}"/>
                            <input type="hidden" name="endHour" value="${param.endHour}"/>


                            <input type="text" name="keyword" placeholder="Tìm kiếm xe..." value="${not empty keyword ? keyword : param.keyword}">
                            <button type="submit">Tìm</button>
                        </form>
                    </div>

                    <!-- FORM FILTER RIÊNG (các filter + nút Apply) -->
                    <form action="${pageContext.request.contextPath}/cars" method="get">
                        <input type="hidden" name="action" value="filter"/>
                        <input type="hidden" name="keyword" value="${not empty keyword ? keyword : param.keyword}"/>
                        <input type="hidden" name="startDate" value="${param.startDate}"/>
                        <input type="hidden" name="startHour" value="${param.startHour}"/>
                        <input type="hidden" name="endDate" value="${param.endDate}"/>
                        <input type="hidden" name="endHour" value="${param.endHour}"/>
                        <div class="filter-header">
                            <h3>Lọc theo</h3>
                            <a href="${pageContext.request.contextPath}/cars?action=list&startDate=${param.startDate}&startHour=${param.startHour}&endDate=${param.endDate}&endHour=${param.endHour}"
                               class="reset">
                                Xóa tất cả
                            </a>
                        </div>



                        <!-- BRAND -->
                        <div class="filter-group">
                            <label>Hãng xe</label>
                            <div class="checkbox-list">
                                <select name="brand" onchange="this.form.submit();">
                                    <option value="">All brands</option>
                                    <c:forEach var="brandItem" items="${brandList}">
                                        <option value="${brandItem}"
                                                <c:if test="${param.brand eq brandItem}">selected="selected"</c:if>>
                                            ${brandItem}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <!-- BODY TYPE -->
                        <div class="filter-group">
                            <label>Loại xe</label>
                            <div class="checkbox-list">
                                <select name="type" onchange="this.form.submit();">
                                    <option value="">All types</option>
                                    <c:forEach var="typeItem" items="${typeList}">
                                        <option value="${typeItem}"
                                                <c:if test="${param.type eq typeItem}">selected="selected"</c:if>>
                                            ${typeItem}
                                        </option>
                                    </c:forEach>
                                </select>
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
                                        onclick="document.getElementById('seats').value = '7'; this.form.submit();">7</button>
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
                    <div class="modern-list-header">
                        <div class="list-header-top">
                            <div class="list-title-wrap">
                                <p class="list-kicker">AUTOMOBILI RENTAL CAR</p>
                                <h1 class="list-title">Danh sách xe cho hành trình của bạn</h1>
                                <p class="list-subtitle">
                                    Chọn thời gian thuê để xem những mẫu xe đang phù hợp và sẵn sàng.
                                </p>
                            </div>
                        </div>

                        <div id="carListDateTimeError"
                             class="car-list-date-error"
                             style="${not empty dateError ? '' : 'display:none;'}">
                            <div class="error-icon">!</div>
                            <div class="error-content">
                                <strong>Thời gian thuê không hợp lệ</strong>
                                <span>
                                    <c:choose>
                                        <c:when test="${not empty dateError}">
                                            ${dateError}
                                        </c:when>
                                        <c:otherwise>
                                            Vui lòng kiểm tra lại ngày giờ nhận xe và trả xe.
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                        </div>

                        <div class="booking-bar">
                            <form action="${pageContext.request.contextPath}/cars" method="get" class="booking-bar-form" id="carListBookingForm">
                                <input type="hidden" name="action" value="list">

                                <div class="booking-bar-item">
                                    <label>Ngày nhận xe</label>
                                    <input type="date" id="listStartDate" name="startDate"
                                           value="${not empty startDate ? fn:substring(startDate,0,10) : param.startDate}" required>
                                </div>

                                <div class="booking-bar-item">
                                    <label>Giờ nhận xe</label>
                                    <select id="listStartHour" name="startHour" required>
                                        <option value="">Chọn giờ</option>
                                        <c:forEach var="h" begin="0" end="23">
                                            <fmt:formatNumber value="${h}" pattern="00" var="hourText" />
                                            <option value="${hourText}:00"
                                                    ${param.startHour == hourText.concat(':00') ? 'selected' : ''}>
                                                ${hourText}:00
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="booking-bar-item">
                                    <label>Ngày trả xe</label>
                                    <input type="date" id="listEndDate" name="endDate"
                                           value="${not empty endDate ? fn:substring(endDate,0,10) : param.endDate}" required>
                                </div>

                                <div class="booking-bar-item">
                                    <label>Giờ trả xe</label>
                                    <select id="listEndHour" name="endHour" required>
                                        <option value="">Chọn giờ</option>
                                        <c:forEach var="h" begin="0" end="23">
                                            <fmt:formatNumber value="${h}" pattern="00" var="hourText" />
                                            <option value="${hourText}:00"
                                                    ${param.endHour == hourText.concat(':00') ? 'selected' : ''}>
                                                ${hourText}:00
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="booking-bar-item booking-bar-action">
                                    <button type="submit" class="booking-search-btn">Tìm xe</button>
                                </div>
                            </form>
                        </div>

                        <div class="car-result-toolbar">
                            <div class="car-result-count">
                                <h2>${cars.size()} xe có sẵn để thuê</h2>
                                <span>Hiển thị các mẫu xe phù hợp với thời gian bạn đã chọn</span>
                            </div>

                            <div class="list-actions modern-list-actions">
                                <select>
                                    <option>Giá thấp nhất</option>
                                    <option>Giá cao nhất</option>
                                    <option>Mới nhất</option>
                                </select>
                                <button type="button">Xem bản đồ</button>
                            </div>
                        </div>
                    </div>
                    <div class="car-grid">
                        <c:forEach var="car" items="${cars}">
                            <a href="${pageContext.request.contextPath}/cars?action=detail&carId=${car.carId}&startDate=${not empty startDate ? fn:substring(startDate,0,10) : param.startDate}&startHour=${param.startHour}&endDate=${not empty endDate ? fn:substring(endDate,0,10) : param.endDate}&endHour=${param.endHour}" class="car-card-link">

                                <div class="car-card">
                                    <div class="car-img">
                                        <img src="${car.imageUrl}" alt="${car.modelName}">
                                    </div>

                                    <div class="car-info">
                                        <h4 class="car-name-row">
                                            <span class="car-model-name">${car.modelName}</span>
                                            <span class="car-name-dot">•</span>
                                            <span class="car-plate-inline">${car.plateNumber}</span>
                                        </h4>

                                        <p class="car-sub-meta">${car.brandName} • ${car.typeName}</p>

                                        <div class="car-spec-line">
                                            <span class="car-spec-item">🚗 ${car.seatCount} chỗ</span>
                                            <span class="car-spec-separator">||</span>
                                            <span class="car-spec-item">⚙ ${car.transmission}</span>
                                            <span class="car-spec-separator">||</span>
                                            <span class="car-spec-item fuel-item">⛽ ${car.fuelType}</span>
                                        </div>

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

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const bookingForm = document.getElementById("carListBookingForm");
                const startDateInput = document.getElementById("listStartDate");
                const startHourInput = document.getElementById("listStartHour");
                const endDateInput = document.getElementById("listEndDate");
                const endHourInput = document.getElementById("listEndHour");
                const errorBox = document.getElementById("carListDateTimeError");

                let errorTimer = null;

                function showError(message) {
                    if (!errorBox) {
                        return;
                    }

                    const messageText = errorBox.querySelector(".error-content span");
                    if (messageText) {
                        messageText.textContent = message;
                    }

                    errorBox.style.display = "flex";

                    if (errorTimer) {
                        clearTimeout(errorTimer);
                    }

                    errorTimer = setTimeout(function () {
                        hideError();
                    }, 3500);
                }

                function hideError() {
                    if (!errorBox) {
                        return;
                    }

                    errorBox.style.display = "none";

                    if (errorTimer) {
                        clearTimeout(errorTimer);
                        errorTimer = null;
                    }
                }

                function buildDateTime(dateValue, hourValue) {
                    if (!dateValue || !hourValue) {
                        return null;
                    }

                    const dateTime = new Date(dateValue + "T" + hourValue + ":00");

                    if (isNaN(dateTime.getTime())) {
                        return null;
                    }

                    return dateTime;
                }

                function validateDateTime() {
                    const startDate = startDateInput ? startDateInput.value : "";
                    const startHour = startHourInput ? startHourInput.value : "";
                    const endDate = endDateInput ? endDateInput.value : "";
                    const endHour = endHourInput ? endHourInput.value : "";

                    if (!startDate || !endDate) {
                        showError("Vui lòng chọn đầy đủ ngày nhận xe và ngày trả xe.");
                        return false;
                    }

                    if (!startHour || !endHour) {
                        showError("Vui lòng chọn đầy đủ giờ nhận xe và giờ trả xe.");
                        return false;
                    }

                    const startDateTime = buildDateTime(startDate, startHour);
                    const endDateTime = buildDateTime(endDate, endHour);

                    if (!startDateTime || !endDateTime) {
                        showError("Ngày giờ thuê không hợp lệ. Vui lòng kiểm tra lại.");
                        return false;
                    }

                    const now = new Date();

                    if (startDateTime < now) {
                        showError("Thời gian nhận xe không được nhỏ hơn thời điểm hiện tại.");
                        return false;
                    }

                    if (endDateTime <= startDateTime) {
                        showError("Thời gian trả xe phải sau thời gian nhận xe.");
                        return false;
                    }

                    return true;
                }

                if (bookingForm) {
                    bookingForm.addEventListener("submit", function (event) {
                        if (!validateDateTime()) {
                            event.preventDefault();
                        }
                    });
                }

                [startDateInput, startHourInput, endDateInput, endHourInput].forEach(function (input) {
                    if (input) {
                        input.addEventListener("change", hideError);
                        input.addEventListener("input", hideError);
                    }
                });

                if (errorBox && errorBox.style.display !== "none") {
                    errorTimer = setTimeout(function () {
                        hideError();
                    }, 3500);
                }
            });
        </script>

    </body>

</html>