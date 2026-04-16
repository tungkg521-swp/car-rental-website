<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Báo cáo Chi Tiết</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/report.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
</head>
<body>

    <button onclick="loadReportsOverview()" class="back-btn" style="margin-bottom:15px;">
        ← Quay lại Report Dashboard
    </button>

    <c:choose>
        <%-- ====================== RENTAL (TRIP REPORT) ====================== --%>
        <c:when test="${reportType == 'RENTAL'}">
            <h2 class="report-title">🚗 Trip Report - Báo cáo Chuyến Thuê</h2>
            
            <!-- Mini Bar Chart -->
            <canvas id="rentalMiniChart" height="80"></canvas>

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
                            <td>${r.contractId != null ? r.contractId : r.bookingId}</td>
                            <td>${r.customerName}</td>
                            <td>${r.plateNumber}</td>
                            <td><fmt:formatDate value="${r.startDate}" pattern="dd/MM/yyyy"/></td>
                            <td><fmt:formatDate value="${r.endDate}" pattern="dd/MM/yyyy"/></td>
                            <td>${r.rentalDays}</td>
                            <td><fmt:formatNumber value="${r.totalPrice}" pattern="#,##0 ₫"/></td>
                            <td>${r.status}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>

        <%-- ====================== REVENUE REPORT ====================== --%>
        <c:when test="${reportType == 'REVENUE'}">
            <h2 class="report-title">💰 Revenue Report - Báo cáo Doanh Thu</h2>
            
            <canvas id="revenueLineChart" height="140"></canvas>

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
                            <td>${r.contractId != null ? r.contractId : r.bookingId}</td>
                            <td>${r.customerName}</td>
                            <td>${r.plateNumber}</td>
                            <td>
                                <fmt:formatDate value="${r.startDate}" pattern="dd/MM"/> →
                                <fmt:formatDate value="${r.endDate}" pattern="dd/MM/yyyy"/>
                            </td>
                            <td>${r.rentalDays}</td>
                            <td><fmt:formatNumber value="${r.totalPrice}" pattern="#,##0 ₫"/></td>
                            <td><fmt:formatDate value="${r.revenueDate}" pattern="dd/MM/yyyy"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>

        <%-- ====================== USAGE REPORT ====================== --%>
        <c:when test="${reportType == 'USAGE'}">
            <h2 class="report-title">⚙️ Vehicle Utilization Report</h2>
            
            <div style="display: flex; gap: 30px; flex-wrap: wrap;">
                <div style="flex: 1; min-width: 300px;">
                    <h3>Phân bổ trạng thái xe</h3>
                    <canvas id="usagePieChart" height="200"></canvas>
                </div>
                <div style="flex: 2; min-width: 400px;">
                    <h3>Tỷ lệ sử dụng theo xe</h3>
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
                            <td><fmt:formatNumber value="${r.totalRevenue}" pattern="#,##0 ₫"/></td>
                            <td><fmt:formatDate value="${r.lastRentalDate}" pattern="dd/MM/yyyy"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
    </c:choose>

    <!-- CHARTS SCRIPT -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const type = '${reportType}';
            
            if (type === 'RENTAL') renderRentalMiniChart();
            if (type === 'REVENUE') renderRevenueLineChart();
            if (type === 'USAGE') {
                renderUsagePieChart();
                renderUsageBarChart();
            }
        });

        function renderRentalMiniChart() {
            // Mini bar chart số chuyến theo ngày (có thể cải tiến sau)
            new Chart(document.getElementById('rentalMiniChart'), {
                type: 'bar',
                data: { labels: ['Tổng'], datasets: [{ label: 'Số chuyến', data: [${reportList.size()}], backgroundColor: '#3498db' }] },
                options: { responsive: true, plugins: { legend: { display: false }}}
            });
        }

        function renderRevenueLineChart() {
            const labels = [<c:forEach var="r" items="${reportList}" varStatus="s">'<fmt:formatDate value="${r.revenueDate}" pattern="dd/MM"/>'${!s.last ? ',' : ''}</c:forEach>];
            const data  = [<c:forEach var="r" items="${reportList}" varStatus="s">${r.totalPrice != null ? r.totalPrice : 0}${!s.last ? ',' : ''}</c:forEach>];

            new Chart(document.getElementById('revenueLineChart'), {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Doanh thu (₫)',
                        data: data,
                        borderColor: '#27ae60',
                        tension: 0.3,
                        fill: true
                    }]
                },
                options: { responsive: true }
            });
        }

        function renderUsagePieChart() {
            // Pie chart giả lập (bạn có thể cải tiến sau bằng cách query thêm trạng thái)
            new Chart(document.getElementById('usagePieChart'), {
                type: 'pie',
                data: {
                    labels: ['Đang thuê', 'Rảnh', 'Bảo trì'],
                    datasets: [{ data: [60, 30, 10], backgroundColor: ['#e74c3c', '#2ecc71', '#f1c40f'] }]
                }
            });
        }

        function renderUsageBarChart() {
            const labels = [<c:forEach var="r" items="${reportList}" varStatus="s">'${r.plateNumber}'${!s.last ? ',' : ''}</c:forEach>];
            const values = [<c:forEach var="r" items="${reportList}" varStatus="s">${r.totalRentalDays}${!s.last ? ',' : ''}</c:forEach>];

            new Chart(document.getElementById('usageBarChart'), {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Tổng ngày thuê',
                        data: values,
                        backgroundColor: '#3498db'
                    }]
                },
                options: { indexAxis: 'y', responsive: true }
            });
        }
    </script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</body>
</html>