<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Danh sách xe</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/style-base.css">

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/car-list.css">

    </head>
    <body>

        <!-- HEADER -->
        <jsp:include page="includes/header.jsp"/>

        <!-- MAIN CONTENT -->
        <section class="car-page">
            <div class="container car-layout">

                <!-- FILTER SIDEBAR -->
                <aside class="filter">
                    <div class="filter-header">
                        <h3>Filter by</h3>
                        <button class="reset">Reset all</button>
                    </div>

                    <!-- RENTAL TYPE -->
                    <div class="filter-group">
                        <label>Rental type</label>
                        <div class="filter-options">
                            <button class="active">Any</button>
                            <button>Per day</button>
                            <button>Per hour</button>
                        </div>
                    </div>

                    <!-- AVAILABLE -->
                    <div class="filter-group inline">
                        <label>Available now only</label>
                        <input type="checkbox">
                    </div>

                    <!-- PRICE -->
                    <div class="filter-group">
                        <label>Price range / day</label>
                        <div class="price-range">
                            <span>$10</span>
                            <input type="range" min="10" max="100">
                            <span>$100</span>
                        </div>
                    </div>

                    <!-- BRAND -->
                    <div class="filter-group">
                        <label>Car brand</label>
                        <div class="checkbox-list">
                            <label><input type="checkbox"> Toyota</label>
                            <label><input type="checkbox"> Mazda</label>
                            <label><input type="checkbox"> BMW</label>
                            <label><input type="checkbox"> Mercedes</label>
                        </div>
                    </div>

                    <!-- BODY TYPE -->
                    <div class="filter-group">
                        <label>Body type</label>
                        <div class="checkbox-list">
                            <label><input type="checkbox"> Sedan</label>
                            <label><input type="checkbox"> SUV</label>
                            <label><input type="checkbox"> Hatchback</label>
                            <label><input type="checkbox"> Coupe</label>
                        </div>
                    </div>

                    <!-- SEATS -->
                    <div class="filter-group">
                        <label>Seats</label>
                        <div class="filter-options">
                            <button>2</button>
                            <button>4</button>
                            <button>5</button>
                            <button>7+</button>
                        </div>
                    </div>

                    <!-- TRANSMISSION -->
                    <div class="filter-group">
                        <label>Transmission</label>
                        <div class="filter-options">
                            <button class="active">Any</button>
                            <button>Automatic</button>
                            <button>Manual</button>
                        </div>
                    </div>

                    <!-- FUEL -->
                    <div class="filter-group">
                        <label>Fuel type</label>
                        <div class="checkbox-list">
                            <label><input type="checkbox"> Gasoline</label>
                            <label><input type="checkbox"> Diesel</label>
                            <label><input type="checkbox"> Electric</label>
                            <label><input type="checkbox"> Hybrid</label>
                        </div>
                    </div>

                    <!-- MODEL YEAR -->
                    <div class="filter-group">
                        <label>Model year</label>
                        <select class="filter-select">
                            <option>Any</option>
                            <option>2024+</option>
                            <option>2020 – 2023</option>
                            <option>Before 2020</option>
                        </select>
                    </div>
                </aside>


                <!-- CAR LIST -->
                <main class="car-list">

                    <div class="list-header">
                        <h2>${cars.size()} vehicles to rent</h2>

                        <div class="list-actions">
                            <select>
                                <option>Closest to me</option>
                                <option>Lowest price</option>
                                <option>Highest rating</option>
                            </select>
                            <button type="button">Show map</button>
                        </div>
                    </div>

                    <div class="car-grid">

                        <c:forEach var="car" items="${cars}">
                            <a href="${pageContext.request.contextPath}/cars?action=detail&carId=${car.carId}"
                               class="car-card-link">


                                <div class="car-card">

                                    <div class="car-img">
                                        <img src="${car.imageUrl}" alt="${car.modelName}">
                                    </div>

                                    <div class="car-info">
                                        <h4>${car.modelName}</h4>
                                        <p>${car.brandName} • ${car.typeName}</p>
                                        <div class="price">$${car.pricePerDay} / day</div>
                                    </div>

                                </div>

                            </a>
                        </c:forEach>

                    </div>

                </main>



            </div>
        </section>

    </body>
</html>
