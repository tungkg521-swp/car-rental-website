<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Báo cáo</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/report.css">
    </head>
    <body>

        <c:choose>
            <c:when test="${reportType == 'RENTAL'}">
                <h2 class="report-title">Báo cáo Chuyến Thuê</h2>
                <c:if test="${empty reportList}">
                    <p class="no-data">Không có dữ liệu chuyến thuê nào.</p>
                </c:if>
                <c:if test="${not empty reportList}">
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
                            <c:forEach var="r" items="${reportList}">
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
            </c:when>

            <c:when test="${reportType == 'USAGE'}">
                <h2 class="report-title">Báo cáo Sử Dụng Xe</h2>
                <c:if test="${empty reportList}">
                    <p class="no-data">Không có dữ liệu sử dụng xe nào.</p>
                </c:if>
                <c:if test="${not empty reportList}">
                    <table class="report-table">
                        <thead>
                            <tr>
                                <th>Biển số</th>
                                <th>Xe</th>
                                <th>Số lần thuê</th>
                                <th>Tổng ngày thuê</th>
                                <th>Tổng doanh thu</th>
                                <th>Lần thuê gần nhất</th>
                                <th>Bảo dưỡng gần nhất</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="r" items="${reportList}">
                                <tr>
                                    <td>${r.plateNumber}</td>
                                    <td>${r.modelName} (${r.brandName} - ${r.typeName})</td>
                                    <td>${r.rentalCount != null ? r.rentalCount : 0}</td>
                                    <td>${r.totalRentalDays != null ? r.totalRentalDays : 0}</td>
                                    <td><fmt:formatNumber value="${r.totalRevenue}" pattern="#,##0 ₫"/></td>
                                    <td>
                                        <fmt:formatDate value="${r.lastRentalDate}" pattern="dd/MM/yyyy"/>
                                        <c:if test="${r.lastRentalDate == null}">-</c:if>
                                        </td>
                                        <td>
                                        <fmt:formatDate value="${r.lastMaintenanceDate}" pattern="dd/MM/yyyy"/>
                                        <c:if test="${r.lastMaintenanceDate == null}">-</c:if>
                                        </td>
                                    </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </c:when>

            <c:when test="${reportType == 'REVENUE'}">
                <h2 class="report-title">Báo cáo Doanh Thu</h2>

                <c:if test="${empty reportList}">
                    <p class="no-data">Chưa có doanh thu nào được ghi nhận từ các chuyến hoàn thành.</p>
                </c:if>

                <c:if test="${not empty reportList}">
                    <table class="report-table">
                        <thead>
                            <tr>
                                <th>Mã HĐ/Booking</th>
                                <th>Khách hàng</th>
                                <th>SĐT</th>
                                <th>Biển số</th>
                                <th>Xe</th>
                                <th>Thời gian thuê</th>
                                <th>Số ngày</th>
                                <th>Doanh thu</th>
                                <th>Ngày ghi nhận</th>
                                <th>Trạng thái</th>
                                <th>Nhân viên</th>
                                <th>Ghi chú</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="r" items="${reportList}">
                                <tr>
                                    <td>${r.contractId != null ? r.contractId : (r.bookingId != null ? r.bookingId : '-')}</td>
                                    <td>${r.customerName != null ? r.customerName : '-'}</td>
                                    <td>${r.customerPhone != null ? r.customerPhone : '-'}</td>
                                    <td>${r.plateNumber != null ? r.plateNumber : '-'}</td>
                                    <td>${r.modelName != null ? r.modelName : '-'} (${r.brandName != null ? r.brandName : '-'} - ${r.typeName != null ? r.typeName : '-'})</td>
                                    <td>
                                        <fmt:formatDate value="${r.startDate}" pattern="dd/MM/yyyy" /> →
                                        <fmt:formatDate value="${r.endDate}" pattern="dd/MM/yyyy" />
                                    </td>
                                    <td>${r.rentalDays}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty r.totalPrice}">
                                                <fmt:formatNumber value="${r.totalPrice}" pattern="#,##0 ₫"/>
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty r.revenueDate}">
                                                <fmt:formatDate value="${r.revenueDate}" pattern="dd/MM/yyyy"/>
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${r.status != null ? r.status : '-'}</td>
                                    <td>${r.staffName != null ? r.staffName : 'Chưa có'}</td>
                                    <td>${r.note != null ? r.note : '-'}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <!-- Tính tổng an toàn bằng c:set -->
                    <c:set var="totalRevenue" value="0" scope="page" />
                    <c:forEach var="r" items="${reportList}">
                        <c:if test="${not empty r.totalPrice}">
                            <c:set var="totalRevenue" value="${totalRevenue + r.totalPrice}" />
                        </c:if>
                    </c:forEach>

                    <div style="margin-top: 20px; font-weight: bold; text-align: right; font-size: 1.2em; color: #27ae60;">
                        Tổng doanh thu: <fmt:formatNumber value="${totalRevenue}" pattern="#,##0 ₫"/>
                    </div>
                </c:if>
            </c:when>
            <c:otherwise>
                <p class="no-data">Không xác định loại báo cáo.</p>
            </c:otherwise>
        </c:choose>

        <br>
        <button class="back-btn" onclick="loadReportsOverview()">
            ← Quay lại Reports Overview
        </button>

    </body>
</html>