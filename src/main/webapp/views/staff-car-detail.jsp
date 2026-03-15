<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Car Detail</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/staff.css">
</head>

<body class="detail-body">

<div class="car-detail-wrapper">

    <div class="car-detail-container">

        <!-- IMAGE -->
        <div class="car-detail-image">
            <img src="${pageContext.request.contextPath}/assets/images/cars/${car.imageFolder}/${car.imageFolder}_1.jpg"
                 alt="${car.modelName}">
        </div>

        <!-- INFO -->
        <div class="car-detail-info">

            <h1>${car.brandName} ${car.modelName}</h1>

            <span class="status-badge ${car.status.toLowerCase()}">
                ${car.status}
            </span>

            <div class="price">
                $${car.pricePerDay} / day
            </div>

            <div class="specs">
                <div><strong>Year:</strong> ${car.modelYear}</div>
                <div><strong>Seats:</strong> ${car.seatCount}</div>
                <div><strong>Fuel:</strong> ${car.fuelType}</div>
                <div><strong>Transmission:</strong> ${car.transmission}</div>
                <div><strong>Type:</strong> ${car.typeName}</div>
            </div>

            <div class="detail-actions">
                <a href="${pageContext.request.contextPath}/staff/cars"
                   class="btn-back">Back</a>

                <a href="${pageContext.request.contextPath}/staff/cars?action=edit&id=${car.carId}"
                   class="btn-edit">Edit</a>
            </div>

        </div>

    </div>

    <!-- DESCRIPTION -->
    <div class="car-detail-description">
        <h2>Description</h2>
        <p>${car.description}</p>
    </div>

</div>

</body>


</html>
