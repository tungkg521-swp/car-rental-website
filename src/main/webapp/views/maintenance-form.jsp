<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>${isEdit ? 'Edit' : 'Add New'} Maintenance</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/maintenance.css">
    <style>
        .form-group { margin-bottom: 1.5rem; }
        .form-group label { 
            display: block; 
            margin-bottom: 6px; 
            font-weight: 600; 
            color: #1e293b; 
        }
        .form-group input, .form-group select, .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
            font-size: 1rem;
        }
        .form-group textarea { min-height: 120px; resize: vertical; }
    </style>
</head>
<body>
    <div class="staff-layout">
        <%@ include file="sidebar.jsp" %>

        <div class="staff-content">
            <h1 class="maintenance-title">${isEdit ? 'Edit Maintenance' : 'Add New Maintenance'}</h1>

            <form action="${pageContext.request.contextPath}/staff/maintenance" method="post" style="max-width: 700px;">
                <input type="hidden" name="action" value="${isEdit ? 'update' : 'add'}">
                <c:if test="${isEdit}">
                    <input type="hidden" name="maintenanceId" value="${maintenance.maintenanceId}">
                </c:if>

                <div class="form-group">
                    <label>Chọn Xe</label>
                    <select name="carId" required>
                        <c:forEach var="car" items="${cars}">
                            <option value="${car.carId}" 
                                ${maintenance != null && maintenance.carId == car.carId ? 'selected' : ''}>
                                ${car.modelName} - ${car.plateNumber != null ? car.plateNumber : 'No plate'}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label>Loại Bảo Dưỡng</label>
                    <select name="maintenanceType" required>
                        <option value="Bảo dưỡng định kỳ" ${maintenance != null && maintenance.maintenanceType == 'Periodic' ? 'selected' : ''}>Bảo dưỡng định kỳ</option>
                        <option value="Sữa chữa" ${maintenance != null && maintenance.maintenanceType == 'Repair' ? 'selected' : ''}>Sửa chữa</option>
                        <option value="Khẩn cấp" ${maintenance != null && maintenance.maintenanceType == 'Emergency' ? 'selected' : ''}>Khẩn cấp</option>
                        <option value="Thay dầu" ${maintenance != null && maintenance.maintenanceType == 'Oil Change' ? 'selected' : ''}>Thay dầu</option>
                    </select>
                </div>

                <div class="form-group">
                    <label>Ngày Lên Lịch</label>
                    <input type="date" name="scheduledDate" 
                           value="${maintenance != null ? maintenance.scheduledDate : ''}" required>
                </div>

                <div class="form-group">
                    <label>Số Km Lên Lịch (km)</label>
                    <input type="number" name="mileageScheduled" 
                           value="${maintenance != null ? maintenance.mileageScheduled : '0'}" required>
                </div>

                <div class="form-group">
                    <label>Chi phí ước tính (VND)</label>
                    <input type="number" name="estimatedCost" step="1000" 
                           value="${maintenance != null ? maintenance.estimatedCost : '0'}" required>
                </div>

                <div class="form-group">
                    <label>Mô tả / Lý do</label>
                    <textarea name="description" placeholder="Nhập lý do bảo dưỡng...">${maintenance != null ? maintenance.description : ''}</textarea>
                </div>

                <c:if test="${isEdit}">
                    <div class="form-group">
                        <label>Trạng thái</label>
                        <select name="status">
                            <option value="SCHEDULED" ${maintenance.status == 'SCHEDULED' ? 'selected' : ''}>Scheduled</option>
                            <option value="IN_PROGRESS" ${maintenance.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                            <option value="COMPLETED" ${maintenance.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                            <option value="CANCELLED" ${maintenance.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                            <option value="OVERDUE" ${maintenance.status == 'OVERDUE' ? 'selected' : ''}>Overdue</option>
                        </select>
                    </div>
                </c:if>

                <div style="margin-top: 30px;">
                    <button type="submit" class="btn-add-maintenance" style="padding: 12px 32px; font-size: 1.1rem;">
                        ${isEdit ? 'Cập nhật lịch bảo dưỡng' : 'Tạo lịch bảo dưỡng mới'}
                    </button>
                    <a href="${pageContext.request.contextPath}/staff/maintenance" 
                       style="margin-left: 20px; color: #64748b; text-decoration: none; font-weight: 500;">
                        Hủy
                    </a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>