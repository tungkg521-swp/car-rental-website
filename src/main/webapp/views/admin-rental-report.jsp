<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Báo cáo Chuyến Thuê</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/report.css">
</head>
<body>
    <c:if test="${empty rentalList}">
        <p class="no-data">Không có dữ liệu chuyến thuê nào.</p>
    </c:if>

    <c:if test="${not empty rentalList}">
        <table class="report-table">
            <thead>
                <tr>
                    <th>Mã hợp đồng / Booking</th>
                    <th>Khách hàng</th>
                    <th>SĐT</th>
                    <th>Biển số</th>
                    <th>Xe</th>
                    <th>Nhận - Trả (dự kiến)</th>
                    <th>Số ngày</th>
                    <th>Tổng tiền</th>
                    <th>Trạng thái</th>
                    <th>Nhân viên</th>
                    <th>Ghi chú</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${rentalList}">
                    <tr>
                        <td>${r.contractId != null ? r.contractId : r.bookingId}</td>
                        <td>${r.customerName}</td>
                        <td>${r.customerPhone}</td>
                        <td>${r.plateNumber}</td>
                        <td>${r.modelName} (${r.brandName} - ${r.typeName})</td>
                        <td>
                            <fmt:formatDate value="${r.startDate}" pattern="dd/MM/yyyy"/> →
                            <fmt:formatDate value="${r.endDate}" pattern="dd/MM/yyyy"/>
                        </td>
                        <td>${r.rentalDays}</td>
                    <td><fmt:formatNumber value="${r.totalPrice}" pattern="#,##0 ₫"/></td>
                    <td>${r.status}</td>
                        <td>${r.staffName != null ? r.staffName : 'Chưa có'}</td>
                        <td>${r.note != null ? r.note : '-'}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <br>
    <button class="back-btn" onclick="loadReportsOverview()">
        Quay lại Reports Overview
    </button>

</body>
</html>