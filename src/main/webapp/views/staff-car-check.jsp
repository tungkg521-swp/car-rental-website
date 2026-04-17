<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Car Check Before Delivery</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/style.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff-car-check.css?v=2">
    </head>
    <body>

        <div class="staff-layout">
            <%@ include file="sidebar.jsp" %>

            <div class="staff-content">
                <div class="car-check-page">

                    <div class="car-check-header">
                        <div>
                            <h1>Car Check Before Delivery</h1>
                            <p>
                                Check vehicle condition, maintenance status, and availability
                                in the rental period before handover.
                            </p>
                        </div>

                        <a class="btn-back"
                           href="${pageContext.request.contextPath}/staff/contracts?action=detail&id=${contract.contractId}">
                            Back to Contract Detail
                        </a>
                    </div>

                    <c:if test="${not empty sessionScope.message}">
                        <div class="alert alert-success">
                            ${sessionScope.message}
                        </div>
                        <c:remove var="message" scope="session"/>
                    </c:if>

                    <c:if test="${not empty sessionScope.error}">
                        <div class="alert alert-error">
                            ${sessionScope.error}
                        </div>
                        <c:remove var="error" scope="session"/>
                    </c:if>

                    <div class="car-check-grid">
                        <div class="car-check-card">
                            <h2>Check Form</h2>

                            <form method="post"
                                  action="${pageContext.request.contextPath}/staff/contracts"
                                  class="car-check-form">

                                <input type="hidden" name="action" value="saveCheck"/>
                                <input type="hidden" name="contractId" value="${contract.contractId}"/>

                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="fuelLevel">Fuel Level</label>
                                        <select id="fuelLevel" name="fuelLevel">
                                            <option value="">-- Select --</option>
                                            <option value="FULL">Full</option>
                                            <option value="3/4">3/4</option>
                                            <option value="1/2">1/2</option>
                                            <option value="1/4">1/4</option>
                                            <option value="EMPTY">Empty</option>
                                        </select>
                                    </div>

                                    <div class="form-group">
                                        <label for="physicalStatus">Physical Condition</label>
                                        <select id="physicalStatus" name="physicalStatus" required>
                                            <option value="OK">OK</option>
                                            <option value="NOT_OK">NOT_OK</option>
                                        </select>
                                    </div>
                                </div>

                                <p class="form-hint">
                                    Final result will be determined based on physical condition,
                                    maintenance status, and car availability in the rental period.
                                </p>

                                <div class="form-group">
                                    <label for="exteriorNote">Exterior Note</label>
                                    <input type="text"
                                           id="exteriorNote"
                                           name="exteriorNote"
                                           placeholder="Example: body clean, no major scratches">
                                </div>

                                <div class="form-group">
                                    <label for="interiorNote">Interior Note</label>
                                    <input type="text"
                                           id="interiorNote"
                                           name="interiorNote"
                                           placeholder="Example: seats and dashboard are in good condition">
                                </div>

                                <div class="form-group">
                                    <label for="note">Additional Note</label>
                                    <textarea id="note"
                                              name="note"
                                              rows="4"
                                              placeholder="Write any extra note here..."></textarea>
                                </div>

                                <div class="form-actions">
                                    <button type="submit" class="btn-save">
                                        Save Check
                                    </button>

                                    <a class="btn-cancel"
                                       href="${pageContext.request.contextPath}/staff/contracts?action=detail&id=${contract.contractId}">
                                        Cancel
                                    </a>
                                </div>
                            </form>
                        </div>

                        <div class="car-check-card">
                            <h2>Contract Summary</h2>

                            <div class="summary-item">
                                <span>Contract ID</span>
                                <p>#${contract.contractId}</p>
                            </div>

                            <div class="summary-item">
                                <span>Contract Status</span>
                                <p>${contract.contractStatus}</p>
                            </div>

                            <div class="summary-item">
                                <span>Customer</span>
                                <p>${customer.fullName}</p>
                            </div>

                            <div class="summary-item">
                                <span>Phone</span>
                                <p>${customer.phone}</p>
                            </div>

                            <div class="summary-item">
                                <span>Car</span>
                                <p>${car.modelName}</p>
                            </div>

                            <div class="summary-item">
                                <span>Rental Period</span>
                                <p>${contract.contractStartDate} → ${contract.contractEndDate}</p>
                            </div>

                            <div class="system-check-box">
                                <h3>System Check</h3>

                                <p>
                                    <strong>Schedule Availability:</strong>
                                    <c:choose>
                                        <c:when test="${scheduleConflict}">
                                            <span class="status-badge status-bad">Conflict detected</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge status-good">Available</span>
                                        </c:otherwise>
                                    </c:choose>
                                </p>

                                <p>
                                    <strong>Maintenance Status:</strong>
                                    <c:choose>
                                        <c:when test="${maintenanceBlocked}">
                                            <span class="status-badge status-bad">Under maintenance</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge status-good">Normal</span>
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>

                            <div class="latest-check-box">
                                <h3>Latest Check</h3>

                                <c:choose>
                                    <c:when test="${not empty latestCarCheck}">
                                        <p>
                                            <strong>Final Result:</strong>
                                            <span class="status-badge ${latestCarCheck.checkResult == 'OK' ? 'status-good' : 'status-bad'}">
                                                ${latestCarCheck.checkResult}
                                            </span>
                                        </p>
                                        <p><strong>Time:</strong> ${latestCarCheck.checkTime}</p>
                                        <p><strong>Fuel:</strong> ${latestCarCheck.fuelLevel}</p>
                                        <p><strong>Exterior:</strong> ${latestCarCheck.exteriorNote}</p>
                                        <p><strong>Interior:</strong> ${latestCarCheck.interiorNote}</p>
                                        <p><strong>Note:</strong> ${latestCarCheck.note}</p>
                                        <p><strong>Checked By (Staff ID):</strong> ${latestCarCheck.checkedBy}</p>
                                    </c:when>
                                    <c:otherwise>
                                        <p>No check recorded yet.</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <!-- HISTORY -->
                    <div class="car-check-card history-card">
                        <h2>Check History</h2>

                        <c:choose>
                            <c:when test="${not empty carCheckList}">
                                <div class="history-list">
                                    <c:forEach var="item" items="${carCheckList}">
                                        <div class="history-item">
                                            <p>
                                                <strong>Result:</strong>
                                                <span class="status-badge ${item.checkResult == 'OK' ? 'status-good' : 'status-bad'}">
                                                    ${item.checkResult}
                                                </span>
                                            </p>
                                            <p><strong>Time:</strong> ${item.checkTime}</p>
                                            <p><strong>Fuel:</strong> ${item.fuelLevel}</p>
                                            <p><strong>Exterior:</strong> ${item.exteriorNote}</p>
                                            <p><strong>Interior:</strong> ${item.interiorNote}</p>
                                            <p><strong>Note:</strong> ${item.note}</p>
                                            <p><strong>Checked By (Staff ID):</strong> ${item.checkedBy}</p>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p>No history yet.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>

                </div>
            </div>
        </div>

    </body>
</html>