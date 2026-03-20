<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Car Manager</title>
    <link rel="stylesheet" 
          href="${pageContext.request.contextPath}/assets/css/staff-cars-manager.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="staff-layout">
        <!-- Include sidebar -->
        <jsp:include page="/views/sidebar.jsp" />

        <div class="staff-content">
            <div class="container">
                
                <!-- Hiển thị thông báo -->
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

                <!-- ==================== MODE: LIST ==================== -->
            <c:if test="${param.action == 'list' || param.action == 'search' || empty param.action}">
                    <!-- Header với nút Add -->
                    <div class="header-actions">
                        <h1 class="dashboard-title">Manage Cars</h1>
                        <a href="?action=add" class="btn-add">
                            <i class="fas fa-plus"></i> Add New Car
                        </a>
                    </div>

                    <!-- Search Form -->
                    <div class="search-filter">
                        <form action="${pageContext.request.contextPath}/staff/cars" method="get">
                            <input type="hidden" name="action" value="search">
                            <input type="text" name="keyword" placeholder="Search by model name..." 
                                   value="${param.keyword}" class="search-input">
                            <select name="status" class="status-select">
                                <option value="">All Status</option>
                                <option value="AVAILABLE" ${param.status == 'AVAILABLE' ? 'selected' : ''}>AVAILABLE</option>
                                <option value="BOOKED" ${param.status == 'BOOKED' ? 'selected' : ''}>BOOKED</option>
                                <option value="MAINTENANCE" ${param.status == 'MAINTENANCE' ? 'selected' : ''}>MAINTENANCE</option>
                            </select>
                            <button type="submit" class="btn-search">
                                <i class="fas fa-search"></i> Search
                            </button>
                            <a href="?action=list" class="btn-reset">
                                <i class="fas fa-undo"></i> Reset
                            </a>
                        </form>
                    </div>

                    <!-- Car Table -->
                    <div class="dashboard-table">
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Image</th>
                                    <th>Model</th>
                                    <th>Brand</th>
                                    <th>Year</th>
                                    <th>Price/Day</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty carList}">
                                        <c:forEach var="car" items="${carList}">
                                            <tr>
                                                <td>${car.carId}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty car.imageUrl}">
                                                            <img src="${pageContext.request.contextPath}/${car.imageUrl}" 
                                                                 alt="${car.modelName}" class="table-image">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="no-image">No Image</div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td><strong>${car.modelName}</strong></td>
                                                <td>${car.brandName}</td>
                                                <td>${car.modelYear}</td>
                                                <td><fmt:formatNumber value="${car.pricePerDay}" type="currency" currencySymbol="$"/></td>
                                                <td>
                                                    <span class="status-badge ${car.status.toLowerCase()}">
                                                        ${car.status}
                                                    </span>
                                                </td>
                                                <td class="action-buttons">
                                                    <a href="?action=detail&id=${car.carId}" class="btn-view" title="View">
                                                        <i class="fas fa-eye"></i>
                                                    </a>
                                                    <a href="?action=edit&id=${car.carId}" class="btn-edit" title="Edit">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                    <form class="delete-form" method="POST" 
                                                          action="${pageContext.request.contextPath}/staff/cars"
                                                          onsubmit="return confirm('Are you sure you want to delete this car?');">
                                                        <input type="hidden" name="action" value="delete">
                                                        <input type="hidden" name="carId" value="${car.carId}">
                                                        <button type="submit" class="btn-delete" title="Delete">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="8" class="no-data">
                                                No cars found. 
                                                <a href="?action=add">Add your first car</a>
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </c:if>

                <!-- ==================== MODE: ADD ==================== -->
                <c:if test="${param.action == 'add'}">
                    <div class="header-actions">
                        <h1>Add New Car</h1>
                        <a href="?action=list" class="btn-back">
                            <i class="fas fa-arrow-left"></i> Back to List
                        </a>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="error-message">${error}</div>
                    </c:if>

                    <form method="POST" action="${pageContext.request.contextPath}/staff/cars" 
                          enctype="multipart/form-data" class="car-form">
                        <input type="hidden" name="action" value="create">

                        <div class="form-grid">
                            <div class="form-group">
                                <label>Model Name *</label>
                                <input type="text" name="modelName" required 
                                       placeholder="e.g., VinFast VF8" value="${car.modelName}">
                            </div>

                            <div class="form-group">
                                <label>Model Year *</label>
                                <input type="number" name="modelYear" required 
                                       min="1900" max="2026" placeholder="2024" value="${car.modelYear}">
                            </div>

                            <div class="form-group">
                                <label>Price Per Day ($) *</label>
                                <input type="number" name="pricePerDay" step="0.01" min="0" required
                                       placeholder="50.00" value="${car.pricePerDay}">
                            </div>

                            <div class="form-group">
                                <label>Seat Count *</label>
                                <input type="number" name="seatCount" required min="2" max="50"
                                       placeholder="5" value="${car.seatCount}">
                            </div>

                            <div class="form-group">
                                <label>Fuel Type *</label>
                                <select name="fuelType" required>
                                    <option value="">Select fuel type</option>
                                    <option value="Gasoline" ${car.fuelType == 'Gasoline' ? 'selected' : ''}>Gasoline</option>
                                    <option value="Diesel" ${car.fuelType == 'Diesel' ? 'selected' : ''}>Diesel</option>
                                    <option value="Electric" ${car.fuelType == 'Electric' ? 'selected' : ''}>Electric</option>
                                    <option value="Hybrid" ${car.fuelType == 'Hybrid' ? 'selected' : ''}>Hybrid</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label>Transmission *</label>
                                <select name="transmission" required>
                                    <option value="">Select transmission</option>
                                    <option value="Automatic" ${car.transmission == 'Automatic' ? 'selected' : ''}>Automatic</option>
                                    <option value="Manual" ${car.transmission == 'Manual' ? 'selected' : ''}>Manual</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label>Brand *</label>
                                <input type="text" name="brandName" required 
                                       placeholder="e.g., VinFast, Toyota" value="${car.brandName}">
                            </div>

                            <div class="form-group">
                                <label>Type *</label>
                                <input type="text" name="typeName" required 
                                       placeholder="e.g., SUV, Sedan" value="${car.typeName}">
                            </div>

                            <div class="form-group full-width">
                                <label>Car Images *</label>
                                <input type="file" name="carImages" multiple required accept="image/*" 
                                       class="file-input">
                                <small class="help-text">
                                    <i class="fas fa-info-circle"></i> 
                                    Hold Ctrl/Cmd to select multiple files. First image will be used as primary.
                                </small>
                            </div>

                            <div class="form-group full-width">
                                <label>Status *</label>
                                <select name="status" required>
                                    <option value="AVAILABLE" ${car.status == 'AVAILABLE' ? 'selected' : ''}>AVAILABLE</option>
                                    <option value="BOOKED" ${car.status == 'BOOKED' ? 'selected' : ''}>BOOKED</option>
                                    <option value="MAINTENANCE" ${car.status == 'MAINTENANCE' ? 'selected' : ''}>MAINTENANCE</option>
                                </select>
                            </div>

                            <div class="form-group full-width">
                                <label>Description</label>
                                <textarea name="description" rows="5" placeholder="Enter car description, features, etc.">${car.description}</textarea>
                            </div>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn-save">
                                <i class="fas fa-save"></i> Create Car
                            </button>
                            <a href="?action=list" class="btn-cancel">
                                <i class="fas fa-times"></i> Cancel
                            </a>
                        </div>
                    </form>
                </c:if>

                <!-- ==================== MODE: DETAIL ==================== -->
                <c:if test="${param.action == 'detail' && not empty car}">
                    <div class="header-actions">
                        <h1>Car Details</h1>
                        <div>
                            <a href="?action=list" class="btn-back">
                                <i class="fas fa-arrow-left"></i> Back
                            </a>
                            <a href="?action=edit&id=${car.carId}" class="btn-edit">
                                <i class="fas fa-edit"></i> Edit
                            </a>
                        </div>
                    </div>

                    <div class="detail-wrapper">
                        <div class="detail-container">
                            <!-- Image Gallery -->
                            <div class="detail-image-gallery">
                                <div class="main-image">
                                    <img src="${pageContext.request.contextPath}/assets/images/cars/${car.imageFolder}/${car.imageFolder}_1.jpg" 
                                         alt="${car.modelName}" id="mainCarImage">
                                </div>
                                <div class="thumbnail-list" id="thumbnailList">
                                    <!-- Thumbnails sẽ được load bằng JavaScript -->
                                </div>
                            </div>

                            <!-- Car Info -->
                            <div class="detail-info">
                                <h2>${car.brandName} ${car.modelName}</h2>
                                <span class="status-badge ${car.status.toLowerCase()}">${car.status}</span>
                                
                                <div class="price-tag">
                                    <fmt:formatNumber value="${car.pricePerDay}" type="currency" currencySymbol="$"/> / day
                                </div>

                                <div class="specs-grid">
                                    <div class="spec-item">
                                        <i class="fas fa-calendar"></i>
                                        <strong>Year:</strong> ${car.modelYear}
                                    </div>
                                    <div class="spec-item">
                                        <i class="fas fa-users"></i>
                                        <strong>Seats:</strong> ${car.seatCount}
                                    </div>
                                    <div class="spec-item">
                                        <i class="fas fa-gas-pump"></i>
                                        <strong>Fuel:</strong> ${car.fuelType}
                                    </div>
                                    <div class="spec-item">
                                        <i class="fas fa-cog"></i>
                                        <strong>Transmission:</strong> ${car.transmission}
                                    </div>
                                    <div class="spec-item">
                                        <i class="fas fa-tag"></i>
                                        <strong>Type:</strong> ${car.typeName}
                                    </div>
                                </div>

                                <div class="description-section">
                                    <h3>Description</h3>
                                    <p>${car.description}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <!-- ==================== MODE: EDIT ==================== -->
                <c:if test="${param.action == 'edit' && not empty car}">
                    <div class="header-actions">
                        <h1>Edit Car</h1>
                        <a href="?action=list" class="btn-back">
                            <i class="fas fa-arrow-left"></i> Back to List
                        </a>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="error-message">${error}</div>
                    </c:if>

                    <form method="POST" action="${pageContext.request.contextPath}/staff/cars" 
                          enctype="multipart/form-data" class="car-form">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="carId" value="${car.carId}">

                        <div class="form-grid">
                            <div class="form-group">
                                <label>Model Name *</label>
                                <input type="text" name="modelName" value="${car.modelName}" required>
                            </div>

                            <div class="form-group">
                                <label>Model Year *</label>
                                <input type="number" name="modelYear" value="${car.modelYear}" required>
                            </div>

                            <div class="form-group">
                                <label>Price Per Day ($) *</label>
                                <input type="number" name="pricePerDay" step="0.01" value="${car.pricePerDay}" required>
                            </div>

                            <div class="form-group">
                                <label>Seat Count *</label>
                                <input type="number" name="seatCount" value="${car.seatCount}" required>
                            </div>

                            <div class="form-group">
                                <label>Fuel Type *</label>
                                <select name="fuelType" required>
                                    <option value="Gasoline" ${car.fuelType == 'Gasoline' ? 'selected' : ''}>Gasoline</option>
                                    <option value="Diesel" ${car.fuelType == 'Diesel' ? 'selected' : ''}>Diesel</option>
                                    <option value="Electric" ${car.fuelType == 'Electric' ? 'selected' : ''}>Electric</option>
                                    <option value="Hybrid" ${car.fuelType == 'Hybrid' ? 'selected' : ''}>Hybrid</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label>Transmission *</label>
                                <select name="transmission" required>
                                    <option value="Automatic" ${car.transmission == 'Automatic' ? 'selected' : ''}>Automatic</option>
                                    <option value="Manual" ${car.transmission == 'Manual' ? 'selected' : ''}>Manual</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label>Brand *</label>
                                <input type="text" name="brandName" value="${car.brandName}" required>
                            </div>

                            <div class="form-group">
                                <label>Type *</label>
                                <input type="text" name="typeName" value="${car.typeName}" required>
                            </div>
                        </div>

                        <!-- Current Images -->
                        <div class="form-group full-width">
                            <label>Current Images</label>
                            <div class="current-images" id="currentImages">
                                <!-- Images will be loaded here -->
                            </div>
                        </div>

                        <!-- Upload New Images -->
                        <div class="form-group full-width upload-section">
                            <label>Add More Images</label>
                            <input type="file" name="carImages" multiple accept="image/*" class="file-input">
                            <small class="help-text">
                                <i class="fas fa-info-circle"></i> 
                                Hold Ctrl/Cmd to select multiple files. New images will be added to existing ones.
                            </small>
                        </div>

                        <div class="form-group full-width">
                            <label>Status *</label>
                            <select name="status" required>
                                <option value="AVAILABLE" ${car.status == 'AVAILABLE' ? 'selected' : ''}>AVAILABLE</option>
                                <option value="BOOKED" ${car.status == 'BOOKED' ? 'selected' : ''}>BOOKED</option>
                                <option value="MAINTENANCE" ${car.status == 'MAINTENANCE' ? 'selected' : ''}>MAINTENANCE</option>
                            </select>
                        </div>

                        <div class="form-group full-width">
                            <label>Description</label>
                            <textarea name="description" rows="5">${car.description}</textarea>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn-save">
                                <i class="fas fa-save"></i> Update Car
                            </button>
                            <a href="?action=list" class="btn-cancel">
                                <i class="fas fa-times"></i> Cancel
                            </a>
                        </div>
                    </form>
                </c:if>
            </div>
        </div>
    </div>

    <!-- JavaScript -->
   <!-- JavaScript -->
<script>
    // Load current images for edit mode
    <c:if test="${param.action == 'edit' && not empty car}">
        fetch('${pageContext.request.contextPath}/staff/cars?action=getImages&id=${car.carId}')
            .then(response => response.json())
            .then(images => {
                const container = document.getElementById('currentImages');
                container.innerHTML = '';
                images.forEach((img, index) => {
                    const imgDiv = document.createElement('div');
                    imgDiv.className = 'current-image-item';
                    
                    // Tạo nội dung bằng cách nối chuỗi thay vì dùng template literal
                    let html = '<img src="${pageContext.request.contextPath}/' + img + '" alt="Car Image">';
                    if (index === 0) {
                        html += '<span class="primary-badge">Primary</span>';
                    }
                    
                    imgDiv.innerHTML = html;
                    container.appendChild(imgDiv);
                });
            });
    </c:if>

    // Load thumbnails for detail mode
    <c:if test="${param.action == 'detail' && not empty car}">
        const mainImage = document.getElementById('mainCarImage');
        const thumbnailList = document.getElementById('thumbnailList');
        
        fetch('${pageContext.request.contextPath}/staff/cars?action=getImages&id=${car.carId}')
            .then(response => response.json())
            .then(images => {
                thumbnailList.innerHTML = '';
                images.forEach((img, index) => {
                    const thumb = document.createElement('img');
                    thumb.src = '${pageContext.request.contextPath}/' + img;
                    thumb.alt = 'Thumbnail ' + (index + 1);
                    thumb.className = 'thumbnail' + (index === 0 ? ' active' : '');
                    thumb.onclick = () => {
                        mainImage.src = thumb.src;
                        document.querySelectorAll('.thumbnail').forEach(t => t.classList.remove('active'));
                        thumb.classList.add('active');
                    };
                    thumbnailList.appendChild(thumb);
                });
            });
    </c:if>
</script>
</body>
</html>