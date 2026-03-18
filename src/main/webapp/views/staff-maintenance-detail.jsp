<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Maintenance Detail #${maintenance.maintenanceId}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/maintenance.css">
</head>
<body>
    <div class="staff-layout">
        <%@ include file="sidebar.jsp" %>

        <div class="staff-content">
            <div class="maintenance-detail-card">
                <h1>Maintenance Detail #${maintenance.maintenanceId}</h1>

                <div class="detail-header">
                    <div class="car-image">
                        <c:choose>
                            <c:when test="${not empty maintenance.carImageUrl}">
                                <img src="${pageContext.request.contextPath}/${maintenance.carImageUrl}" 
                                     alt="${maintenance.modelName}">
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/assets/images/placeholder-car.jpg" alt="No image">
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="detail-info">
                        <p><strong>Car:</strong> ${maintenance.modelName} - ${maintenance.licensePlate}</p>
                        <p><strong>Type:</strong> ${maintenance.maintenanceType}</p>
                        <p><strong>Scheduled Date:</strong> ${maintenance.scheduledDate}</p>
                        <p><strong>Mileage:</strong> ${maintenance.mileageScheduled} km</p>
                        <p><strong>Estimated Cost:</strong> 
                            <fmt:formatNumber value="${maintenance.estimatedCost}" type="number" groupingUsed="true"/> VND
                        </p>
                        <p><strong>Status:</strong> 
                            <span class="status-badge status-${maintenance.status.toLowerCase()}">
                                ${maintenance.status}
                            </span>
                        </p>
                    </div>
                </div>

                <div class="maintenance-description">
                    <h3>Description / Reason</h3>
                    <p>${maintenance.description != null ? maintenance.description : 'Không có mô tả'}</p>
                </div>

                <div style="margin-top: 2rem; border-top: 1px solid #e2e8f0; padding-top: 1.5rem; font-size: 0.95rem;">
                    <p><strong>Created By:</strong> Staff ID ${maintenance.createdBy}</p>
                    <p><strong>Created At:</strong> ${maintenance.createdAt}</p>
                    <p><strong>Last Updated:</strong> ${maintenance.updatedAt != null ? maintenance.updatedAt : 'Chưa cập nhật'}</p>
                </div>
            </div>

            <div style="margin-top: 2rem; display: flex; gap: 15px;">
                <a href="${pageContext.request.contextPath}/staff/maintenance" class="btn-back-maintenance">
                    ← Back to List
                </a>
                <a href="${pageContext.request.contextPath}/staff/maintenance?action=edit&id=${maintenance.maintenanceId}" 
                   class="btn-add-maintenance" style="background: #f59e0b;">
                    ✏️ Edit Maintenance
                </a>
            </div>
        </div>
    </div>
</body>
</html>