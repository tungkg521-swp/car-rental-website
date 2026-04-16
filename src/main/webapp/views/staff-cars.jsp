
<!DOCTYPE html>
<html>
    <head>
        <title>Manage Cars</title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff.css">
    </head>
    <body>

        <div class="staff-layout">

            <%@ include file="sidebar.jsp" %>

            <div class="staff-content">

                <h1 class="dashboard-title">Manage Cars</h1>

                <!-- ===== SEARCH + FILTER ===== -->
                <div class="car-filter">

                    <input type="text" placeholder="Search by model name..." />

                    <select>
                        <option value="">All Status</option>
                        <option>AVAILABLE</option>
                        <option>RENTED</option>
                        <option>MAINTENANCE</option>
                    </select>

                    <button>Search</button>

                </div>

                <!-- ===== CAR TABLE ===== -->
                <div class="dashboard-table">

                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Model</th>
                                <th>Brand</th>
                                <th>Year</th>
                                <th>Price/Day</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>

                        <tbody>

                            <c:forEach var="car" items="${carList}">
                                <tr>
                                    <td>${car.carId}</td>
                                    <td>${car.modelName}</td>
                                    <td>${car.brandName}</td>
                                    <td>${car.modelYear}</td>
                                    <td>$${car.pricePerDay}</td>

                                    <td>
                                        <span class="status ${car.status.toLowerCase()}">
                                            ${car.status}
                                        </span>
                                    </td>

                                    <td>
                                        <a class="btn-view"
                                           href="${pageContext.request.contextPath}/staff/cars?action=detail&id=${car.carId}">
                                            View
                                        </a>


                                        <a href="${pageContext.request.contextPath}/staff/cars/edit?id=${car.carId}" 
                                           class="btn-edit">Edit</a>
                                    </td>

                                </tr>
                            </c:forEach>

                        </tbody>
                    </table>

                </div>

            </div>

        </div>

    </body>
</html>
