<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Báo cáo Chuyến Thuê</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h2 { color: #333; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background-color: #f2f2f2; font-weight: bold; }
        tr:nth-child(even) { background-color: #f9f9f9; }
    </style>
</head>
<body>

<h2>Báo cáo Chuyến Thuê Xe</h2>

<c:if test="${empty rentalList}">
    <p style="color: red; font-weight: bold;">Không có dữ liệu chuyến thuê nào.</p>
</c:if>

<c:if test="${not empty rentalList}">
    <table>
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

</body>
</html>