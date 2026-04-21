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
            <div class="maintenance-page-shell">
                <div class="maintenance-detail-card modern-detail-card">
                    <div class="maintenance-detail-main car-style-detail">
                        <div class="maintenance-detail-media">
                            <c:choose>
                                <c:when test="${not empty maintenance.carImageUrl}">
                                    <img src="${pageContext.request.contextPath}/${maintenance.carImageUrl}"
                                         alt="${maintenance.modelName}" class="maintenance-car-image">
                                </c:when>
                                <c:otherwise>
                                    <div class="maintenance-no-image">No image available</div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="maintenance-detail-info">
                            <h1 class="maintenance-car-title">${maintenance.modelName}</h1>

                            <div class="maintenance-top-status">
                                <span class="status-badge status-${maintenance.status.toLowerCase().replace('_','-')}">
                                    ${maintenance.status}
                                </span>
                            </div>

                            <div class="maintenance-price-like">
                                <c:choose>
                                    <c:when test="${maintenance.estimatedCost != null}">
                                        <fmt:formatNumber value="${maintenance.estimatedCost}" type="number" groupingUsed="true"/> VND
                                    </c:when>
                                    <c:otherwise>
                                        0 VND
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="maintenance-info-grid car-detail-like-grid">
                                <div class="maintenance-info-item">
                                    <span class="label">Plate Number</span>
                                    <span class="value">${maintenance.licensePlate}</span>
                                </div>

                                <div class="maintenance-info-item">
                                    <span class="label">Type</span>
                                    <span class="value">${maintenance.maintenanceType}</span>
                                </div>

                                <div class="maintenance-info-item">
                                    <span class="label">Start Date</span>
                                    <span class="value">${maintenance.startDate}</span>
                                </div>

                                <div class="maintenance-info-item">
                                    <span class="label">End Date</span>
                                    <span class="value">${maintenance.endDate}</span>
                                </div>

                                <div class="maintenance-info-item">
                                    <span class="label">Mileage</span>
                                    <span class="value">${maintenance.mileageScheduled} km</span>
                                </div>

                                <div class="maintenance-info-item">
                                    <span class="label">Maintenance ID</span>
                                    <span class="value">#${maintenance.maintenanceId}</span>
                                </div>
                            </div>

                            <div class="maintenance-description-box">
                                <h3>Description</h3>
                                <p>
                                    <c:choose>
                                        <c:when test="${not empty maintenance.description}">
                                            ${maintenance.description}
                                        </c:when>
                                        <c:otherwise>
                                            Không có mô tả
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </div>

                    <div class="maintenance-detail-sections">
                        <div class="maintenance-section-block meta-block">
                            <h3>Additional Information</h3>
                            <div class="maintenance-meta-grid">
                                <div class="maintenance-meta-item">
                                    <span class="label">Created By</span>
                                    <span class="value">
                                        <c:choose>
                                            <c:when test="${maintenance.createdBy != null}">
                                                ${maintenance.createdBy}
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>

                                <div class="maintenance-meta-item">
                                    <span class="label">Last Updated</span>
                                    <span class="value">
                                        <c:choose>
                                            <c:when test="${maintenance.updatedAt != null}">
                                                ${maintenance.updatedAt}
                                            </c:when>
                                            <c:otherwise>Chưa cập nhật</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="maintenance-detail-actions">
                    <a href="${pageContext.request.contextPath}/staff/maintenance" class="btn-back-maintenance">
                        ← Back to List
                    </a>

                    <a href="${pageContext.request.contextPath}/staff/maintenance?action=edit&id=${maintenance.maintenanceId}" class="btn-edit-maintenance">
                        ✏ Edit Maintenance
                    </a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>