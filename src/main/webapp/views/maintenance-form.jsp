<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>${maintenance != null ? 'Edit Maintenance' : 'Add New Maintenance'}</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/maintenance.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
        <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
    </head>
    <body>
        <div class="staff-layout">
            <%@ include file="sidebar.jsp" %>

            <div class="staff-content">
                <div class="maintenance-form-shell">
                    <div class="maintenance-form-topbar">
                        <h1 class="maintenance-page-title">
                            ${maintenance != null ? 'Edit Maintenance' : 'Add New Maintenance'}
                        </h1>

                        <a href="${pageContext.request.contextPath}/staff/maintenance" class="maintenance-back-btn">
                            ← Back to List
                        </a>
                    </div>
                    <c:if test="${not empty error}">
                        <div class="maintenance-alert error-alert auto-dismiss-alert">
                            ${error}
                        </div>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/staff/maintenance" method="post" class="maintenance-form">
                        <input type="hidden" name="action" value="${maintenance != null ? 'update' : 'add'}">
                        <c:if test="${maintenance != null}">
                            <input type="hidden" name="maintenanceId" value="${maintenance.maintenanceId}">
                        </c:if>

                        <div class="maintenance-form-grid">
                            <div class="maintenance-field full-width">
                                <label for="carId">Chọn Xe</label>
                                <select name="carId" id="carId" required>
                                    <option value="">-- Chọn xe --</option>
                                    <c:forEach var="car" items="${carList}">
                                        <option value="${car.carId}"
                                                <c:if test="${(maintenance != null && maintenance.carId == car.carId) || (maintenance == null && formCarId == car.carId.toString())}">
                                                    selected
                                                </c:if>>
                                            ${car.modelName} - ${car.plateNumber}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="maintenance-field full-width">
                                <label for="maintenanceType">Loại Bảo Dưỡng</label>
                                <select name="maintenanceType" id="maintenanceType" required>
                                    <option value="">-- Chọn loại bảo dưỡng --</option>
                                    <option value="Bảo dưỡng định kỳ"
                                            <c:if test="${(maintenance != null && maintenance.maintenanceType == 'Bảo dưỡng định kỳ') || (maintenance == null && formMaintenanceType == 'Bảo dưỡng định kỳ')}">selected</c:if>>
                                                Bảo dưỡng định kỳ
                                            </option>
                                            <option value="Sửa chữa"
                                            <c:if test="${(maintenance != null && maintenance.maintenanceType == 'Sửa chữa') || (maintenance == null && formMaintenanceType == 'Sửa chữa')}">selected</c:if>>
                                                Sửa chữa
                                            </option>
                                            <option value="Khẩn cấp"
                                            <c:if test="${(maintenance != null && maintenance.maintenanceType == 'Khẩn cấp') || (maintenance == null && formMaintenanceType == 'Khẩn cấp')}">selected</c:if>>
                                                Khẩn cấp
                                            </option>
                                            <option value="Thay dầu"
                                            <c:if test="${(maintenance != null && maintenance.maintenanceType == 'Thay dầu') || (maintenance == null && formMaintenanceType == 'Thay dầu')}">selected</c:if>>
                                                Thay dầu
                                            </option>
                                    </select>
                                </div>

                                <div class="maintenance-field">
                                    <label for="startDate">Ngày bắt đầu</label>
                                    <input type="text" id="startDate" name="startDate"
                                           value="${maintenance != null ? maintenance.startDate : formStartDate}" required>
                            </div>

                            <div class="maintenance-field">
                                <label for="endDate">Ngày kết thúc</label>
                                <input type="text" id="endDate" name="endDate"
                                       value="${maintenance != null ? maintenance.endDate : formEndDate}" required>
                            </div>

                            <div class="maintenance-field">
                                <label for="mileageScheduled">Số Km Lên Lịch (km)</label>
                                <input type="number" id="mileageScheduled" name="mileageScheduled" min="0"
                                       value="${maintenance != null ? maintenance.mileageScheduled : (empty formMileageScheduled ? '0' : formMileageScheduled)}" required>
                            </div>

                            <div class="maintenance-field">
                                <label for="estimatedCost">Chi phí ước tính (VND)</label>
                                <input type="number" id="estimatedCost" name="estimatedCost" min="0" step="1000"
                                       value="${maintenance != null ? maintenance.estimatedCost : (empty formEstimatedCost ? '0' : formEstimatedCost)}" required>
                            </div>

                            <div class="maintenance-field full-width">
                                <label for="description">Mô tả / Lý do</label>
                                <textarea id="description" name="description" placeholder="Nhập lý do bảo dưỡng...">${maintenance != null ? maintenance.description : formDescription}</textarea>
                            </div>

                            <c:if test="${maintenance != null}">
                                <div class="maintenance-field full-width">
                                    <label for="status">Trạng thái</label>
                                    <select name="status" id="status" required>
                                        <option value="SCHEDULED" ${maintenance.status == 'SCHEDULED' ? 'selected' : ''}>SCHEDULED</option>
                                        <option value="IN_PROGRESS" ${maintenance.status == 'IN_PROGRESS' ? 'selected' : ''}>IN_PROGRESS</option>
                                        <option value="COMPLETED" ${maintenance.status == 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                                        <option value="CANCELLED" ${maintenance.status == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                                    </select>
                                </div>
                            </c:if>
                        </div>

                        <div class="maintenance-form-actions">
                            <button type="submit" class="maintenance-submit-btn">
                                ${maintenance != null ? 'Update Maintenance' : 'Create Maintenance'}
                            </button>

                            <a href="${pageContext.request.contextPath}/staff/maintenance" class="maintenance-cancel-btn">
                                Cancel
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script>
        window.maintenanceConfig = {
            contextPath: '${pageContext.request.contextPath}'
        };
    </script>
    <script src="${pageContext.request.contextPath}/assets/js/maintenance.js"></script>
</body>
</html>