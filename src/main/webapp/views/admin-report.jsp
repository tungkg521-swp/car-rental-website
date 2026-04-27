<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Báo cáo Chi Tiết</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/report.css">

        <script>
            window.contextPath = '${pageContext.request.contextPath}';
            window.reportType = '${reportType}';

            window.reportChartData = {
                rentalLabels: [
                    <c:forEach var="r" items="${reportList}" varStatus="s">
                        '<fmt:formatDate value="${r.revenueDate != null ? r.revenueDate : r.endDate}" pattern="dd/MM/yyyy"/>'
                        ${!s.last ? ',' : ''}
                    </c:forEach>
                ],

                revenueLabels: [
                    <c:forEach var="r" items="${reportList}" varStatus="s">
                        '<fmt:formatDate value="${r.revenueDate}" pattern="dd/MM/yyyy"/>'
                        ${!s.last ? ',' : ''}
                    </c:forEach>
                ],

                revenueValues: [
                    <c:forEach var="r" items="${reportList}" varStatus="s">
                        ${r.totalPrice != null ? r.totalPrice : 0}
                        ${!s.last ? ',' : ''}
                    </c:forEach>
                ],

                usageLabels: [
                    <c:forEach var="r" items="${reportList}" varStatus="s">
                        '${r.plateNumber}'
                        ${!s.last ? ',' : ''}
                    </c:forEach>
                ],

                usageValues: [
                    <c:forEach var="r" items="${reportList}" varStatus="s">
                        ${r.totalRentalDays != null ? r.totalRentalDays : 0}
                        ${!s.last ? ',' : ''}
                    </c:forEach>
                ]
            };
        </script>

        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
    </head>

    <body>

        <button onclick="goBackToDashboard()" class="back-btn" style="margin-bottom:15px;">
            ← Quay lại Report Dashboard
        </button>

        <c:choose>

            <%-- ====================== RENTAL REPORT ====================== --%>
            <c:when test="${reportType == 'RENTAL'}">
                <h2 class="report-title">🚗 Trip Report - Báo cáo Chuyến Thuê</h2>

                <c:choose>
                    <c:when test="${empty reportList}">
                        <div class="empty-message">
                            Không có dữ liệu chuyến thuê trong khoảng thời gian này.
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="chart-box">
                            <canvas id="rentalMiniChart" height="80"></canvas>
                        </div>

                        <table class="report-table">
                            <thead>
                                <tr>
                                    <th>Mã Booking/HĐ</th>
                                    <th>Khách hàng</th>
                                    <th>Biển số</th>
                                    <th>Nhận xe</th>
                                    <th>Trả xe</th>
                                    <th>Số ngày</th>
                                    <th>Tổng tiền</th>
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="r" items="${reportList}">
                                    <tr>
                                        <td>
                                            <c:choose>
                                                <c:when test="${r.contractId != null}">
                                                    ${r.contractId}
                                                </c:when>
                                                <c:otherwise>
                                                    ${r.bookingId}
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>${r.customerName}</td>
                                        <td>${r.plateNumber}</td>

                                        <td>
                                            <fmt:formatDate value="${r.startDate}" pattern="dd/MM/yyyy"/>
                                        </td>

                                        <td>
                                            <fmt:formatDate value="${r.endDate}" pattern="dd/MM/yyyy"/>
                                        </td>

                                        <td>${r.rentalDays}</td>

                                        <td>
                                            <fmt:formatNumber value="${r.totalPrice}" pattern="#,##0"/> ₫
                                        </td>

                                        <td>${r.status}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </c:when>

            <%-- ====================== REVENUE REPORT ====================== --%>
            <c:when test="${reportType == 'REVENUE'}">
                <h2 class="report-title">💰 Revenue Report - Báo cáo Doanh Thu</h2>

                <c:choose>
                    <c:when test="${empty reportList}">
                        <div class="empty-message">
                            Không có dữ liệu doanh thu trong khoảng thời gian này.
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="chart-box">
                            <canvas id="revenueLineChart" height="140"></canvas>
                        </div>

                        <table class="report-table">
                            <thead>
                                <tr>
                                    <th>Mã HĐ/Booking</th>
                                    <th>Khách hàng</th>
                                    <th>Biển số</th>
                                    <th>Thời gian</th>
                                    <th>Số ngày</th>
                                    <th>Doanh thu</th>
                                    <th>Ngày ghi nhận</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="r" items="${reportList}">
                                    <tr>
                                        <td>
                                            <c:choose>
                                                <c:when test="${r.contractId != null}">
                                                    ${r.contractId}
                                                </c:when>
                                                <c:otherwise>
                                                    ${r.bookingId}
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>${r.customerName}</td>
                                        <td>${r.plateNumber}</td>

                                        <td>
                                            <fmt:formatDate value="${r.startDate}" pattern="dd/MM"/>
                                            →
                                            <fmt:formatDate value="${r.endDate}" pattern="dd/MM/yyyy"/>
                                        </td>

                                        <td>${r.rentalDays}</td>

                                        <td>
                                            <fmt:formatNumber value="${r.totalPrice}" pattern="#,##0"/> ₫
                                        </td>

                                        <td>
                                            <fmt:formatDate value="${r.revenueDate}" pattern="dd/MM/yyyy"/>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </c:when>

            <%-- ====================== USAGE REPORT ====================== --%>
            <c:when test="${reportType == 'USAGE'}">
                <h2 class="report-title">⚙️ Vehicle Utilization Report</h2>

                <c:choose>
                    <c:when test="${empty reportList}">
                        <div class="empty-message">
                            Không có dữ liệu sử dụng xe trong khoảng thời gian này.
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div style="display:flex; gap:30px; flex-wrap:wrap;">
                            <div style="flex:1; min-width:300px;">
                                <h3>Tỷ lệ xe theo số ngày thuê</h3>
                                <canvas id="usagePieChart" height="200"></canvas>
                            </div>

                            <div style="flex:2; min-width:400px;">
                                <h3>Tổng ngày thuê theo xe</h3>
                                <canvas id="usageBarChart" height="300"></canvas>
                            </div>
                        </div>

                        <table class="report-table" style="margin-top:30px;">
                            <thead>
                                <tr>
                                    <th>Biển số</th>
                                    <th>Xe</th>
                                    <th>Số lần thuê</th>
                                    <th>Tổng ngày thuê</th>
                                    <th>Tổng doanh thu</th>
                                    <th>Lần thuê gần nhất</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="r" items="${reportList}">
                                    <tr>
                                        <td>${r.plateNumber}</td>
                                        <td>${r.modelName} (${r.brandName})</td>
                                        <td>${r.rentalCount}</td>
                                        <td>${r.totalRentalDays}</td>

                                        <td>
                                            <fmt:formatNumber value="${r.totalRevenue}" pattern="#,##0"/> ₫
                                        </td>

                                        <td>
                                            <fmt:formatDate value="${r.lastRentalDate}" pattern="dd/MM/yyyy"/>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </c:when>

            <%-- ====================== DEFAULT ====================== --%>
            <c:otherwise>
                <h2 class="report-title">Không tìm thấy loại báo cáo</h2>
            </c:otherwise>

        </c:choose>

        <script src="${pageContext.request.contextPath}/assets/js/report.js"></script>
    </body>
</html>