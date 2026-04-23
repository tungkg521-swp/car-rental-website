<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Request Car Change</title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/style.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff-car-change.css?v=1">
    </head>
    <body>

        <div class="staff-layout">
            <%@ include file="sidebar.jsp" %>

            <div class="staff-content">
                <div class="car-change-page">
                    <div class="car-change-header">
                        <div>
                            <h1>Request Car Change</h1>
                            <p>
                                The current car cannot be delivered. Please choose a replacement car
                                with the same type and available schedule.
                            </p>
                        </div>

                        <a class="btn-back"
                           href="${pageContext.request.contextPath}/staff/contracts?action=detail&id=${contract.contractId}">
                            Back to Car Check
                        </a>
                    </div>

                    <c:if test="${not empty sessionScope.message}">
                        <div class="alert alert-success">${sessionScope.message}</div>
                        <c:remove var="message" scope="session"/>
                    </c:if>

                    <c:if test="${not empty sessionScope.error}">
                        <div class="alert alert-error">${sessionScope.error}</div>
                        <c:remove var="error" scope="session"/>
                    </c:if>

                    <div class="car-change-grid">

                        <!-- LEFT -->
                        <div class="change-card">
                            <h2>Current Booking Information</h2>

                            <div class="summary-list">
                                <div class="summary-item">
                                    <span>Booking ID</span>
                                    <p>#${booking.bookingId}</p>
                                </div>

                                <div class="summary-item">
                                    <span>Contract ID</span>
                                    <p>#${contract.contractId}</p>
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
                                    <span>Rental Period</span>
                                    <p>
                                        <fmt:formatDate value="${booking.startTime}" pattern="dd/MM/yyyy HH:mm"/>
                                        →
                                        <fmt:formatDate value="${booking.endTime}" pattern="dd/MM/yyyy HH:mm"/>
                                    </p>
                                </div>

                                <div class="summary-item">
                                    <span>Current Car</span>
                                    <p>${currentCar.modelName}</p>
                                </div>

                                <div class="summary-item">
                                    <span>Current Plate Number</span>
                                    <p>${currentCar.plateNumber}</p>
                                </div>

                                <div class="summary-item">
                                    <span>Car Type</span>
                                    <p>${currentCar.typeName}</p>
                                </div>

                                <div class="summary-item">
                                    <span>Daily Price</span>
                                    <p><fmt:formatNumber value="${currentCar.pricePerDay}" type="number"/> VND</p>
                                </div>
                            </div>

                            <c:if test="${not empty currentCar.imageUrl}">
                                <div class="car-image-box">
                                    <img src="${pageContext.request.contextPath}/${currentCar.imageUrl}"
                                         alt="${currentCar.modelName}">
                                </div>
                            </c:if>

                            <div class="check-fail-box">
                                <h3>Latest Check Result</h3>
                                <p>
                                    <strong>Result:</strong>
                                    <span class="status-badge status-bad">${latestCarCheck.checkResult}</span>
                                </p>
                                <p><strong>Note:</strong> ${latestCarCheck.note}</p>
                            </div>
                        </div>

                        <!-- RIGHT -->
                        <div class="change-card">
                            <h2>Select Replacement Car</h2>

                            <c:choose>
                                <c:when test="${not empty replacementCars}">
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/car-change"
                                          class="change-form">

                                        <input type="hidden" name="action" value="create"/>
                                        <input type="hidden" name="bookingId" value="${booking.bookingId}"/>

                                        <div class="form-group">
                                            <label for="newCarId">Available Cars</label>
                                            <select name="newCarId" id="newCarId" required onchange="showSelectedCarInfo()">
                                                <option value="">-- Select replacement car --</option>
                                                <c:forEach var="car" items="${replacementCars}">
                                                    <option value="${car.carId}"
                                                            data-name="${car.modelName}"
                                                            data-plate="${car.plateNumber}"
                                                            data-type="${car.typeName}"
                                                            data-price="${car.pricePerDay}"
                                                            data-seat="${car.seatCount}"
                                                            data-fuel="${car.fuelType}"
                                                            data-trans="${car.transmission}"
                                                            data-image="${car.imageUrl}">
                                                        ${car.modelName} - ${car.plateNumber} - ${car.typeName}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                        <div id="selectedCarPreview" class="selected-car-preview" style="display:none;">
                                            <h3>Selected Replacement Car</h3>

                                            <div class="preview-image-wrap">
                                                <img id="previewCarImage" src="" alt="Replacement Car">
                                            </div>

                                            <div class="preview-info">
                                                <p><strong>Model:</strong> <span id="previewCarName"></span></p>
                                                <p><strong>Plate Number:</strong> <span id="previewCarPlate"></span></p>
                                                <p><strong>Type:</strong> <span id="previewCarType"></span></p>
                                                <p><strong>Price/Day:</strong> <span id="previewCarPrice"></span></p>
                                                <p><strong>Seats:</strong> <span id="previewCarSeat"></span></p>
                                                <p><strong>Fuel:</strong> <span id="previewCarFuel"></span></p>
                                                <p><strong>Transmission:</strong> <span id="previewCarTrans"></span></p>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label for="reason">Reason for Change</label>
                                            <textarea id="reason"
                                                      name="reason"
                                                      rows="5"
                                                      required
                                                      placeholder="Explain why the current car must be replaced..."></textarea>
                                        </div>

                                        <div class="form-actions">
                                            <button type="submit" class="btn-submit">
                                                Create Car Change Request
                                            </button>

                                            <a class="btn-cancel"
                                               href="${pageContext.request.contextPath}/staff/contracts?action=detail&id=${contract.contractId}">
                                                Cancel
                                            </a>
                                        </div>
                                    </form>
                                </c:when>

                                <c:otherwise>
                                    <div class="empty-state">
                                        <h3>No available replacement car</h3>
                                        <p>
                                            There is currently no car of the same type available
                                            in the selected rental period.
                                        </p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script>
            function showSelectedCarInfo() {
                const select = document.getElementById("newCarId");
                const selected = select.options[select.selectedIndex];
                const preview = document.getElementById("selectedCarPreview");

                if (!selected.value) {
                    preview.style.display = "none";
                    return;
                }

                document.getElementById("previewCarName").textContent = selected.dataset.name || "";
                document.getElementById("previewCarPlate").textContent = selected.dataset.plate || "";
                document.getElementById("previewCarType").textContent = selected.dataset.type || "";
                document.getElementById("previewCarPrice").textContent = selected.dataset.price || "";
                document.getElementById("previewCarSeat").textContent = selected.dataset.seat || "";
                document.getElementById("previewCarFuel").textContent = selected.dataset.fuel || "";
                document.getElementById("previewCarTrans").textContent = selected.dataset.trans || "";

                const imagePath = selected.dataset.image || "";
                const img = document.getElementById("previewCarImage");
                if (imagePath) {
                    img.src = "${pageContext.request.contextPath}/" + imagePath;
                    img.style.display = "block";
                } else {
                    img.src = "";
                    img.style.display = "none";
                }

                preview.style.display = "block";
            }
        </script>

    </body>
</html>