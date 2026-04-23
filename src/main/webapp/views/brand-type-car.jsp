<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Brand & Type Car Management</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/brand-type-car.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
    </head>
    <body>
        <div class="page-wrapper">

            <%-- Replace this include path if your sidebar file is in another folder --%>
            <jsp:include page="sidebar.jsp"></jsp:include>

                <div class="main-content">

                <c:if test="${not empty sessionScope.success}">
                    <div class="flash-message flash-success">${sessionScope.success}</div>
                    <c:remove var="success" scope="session"/>
                </c:if>

                <c:if test="${not empty sessionScope.error}">
                    <div class="flash-message flash-error">${sessionScope.error}</div>
                    <c:remove var="error" scope="session"/>
                </c:if>

                <div class="page-header">
                    <div class="page-title">
                        <h1>Brand & Type Car Management</h1>
                    </div>
                </div>

                <div class="hero-card">
                    <div>
                        <h2>Catalog Configuration</h2>
                        <p>Control brand and type data for vehicles in the system.</p>
                    </div>
                    <div class="admin-only-badge">Admin Only</div>
                </div>

                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="label">Total Brands</div>
                        <div class="number">${totalBrands}</div>
                    </div>

                    <div class="stat-card">
                        <div class="label">Total Car Types</div>
                        <div class="number">${totalTypes}</div>
                    </div>

                    <div class="stat-card">
                        <div class="label">Active Catalog Items</div>
                        <div class="number">${activeCatalogItems}</div>
                    </div>
                </div>

                <%-- BRAND MANAGEMENT --%>
                <div class="accordion-card active" id="brandAccordion">
                    <button type="button" class="accordion-header" onclick="toggleAccordion('brandAccordion')">
                        <div class="accordion-title-wrap">
                            <div class="accordion-left">
                                <h3>Brand Management</h3>
                                <p>Create, update, and remove car brands</p>
                            </div>
                        </div>

                        <div class="accordion-right">
                            <span class="count-badge">${totalBrands} brands</span>
                            <span class="accordion-icon">⌃</span>
                        </div>
                    </button>

                    <div class="accordion-body">
                        <div class="section-toolbar">
                            <form action="${pageContext.request.contextPath}/admin/brand-type-cars" method="get" class="search-form">
                                <input type="hidden" name="action" value="search">
                                <input type="hidden" name="typeKeyword" value="${typeKeyword}">
                                <div class="search-input-wrap">
                                    <input type="text" name="brandKeyword" placeholder="Search brand name..." value="${brandKeyword}">
                                </div>
                                <button type="submit" class="btn btn-search">Search</button>
                            </form>

                            <button type="button" class="btn btn-primary btn-add" onclick="openModal('addBrandModal')">
                                + Add Brand
                            </button>
                        </div>

                        <div class="table-card">
                            <div class="table-wrapper">
                                <table>
                                    <thead>
                                        <tr>
                                            <th class="col-id">ID</th>
                                            <th>Brand Name</th>
                                            <th>Status</th>
                                            <th>Created Date</th>
                                            <th class="col-actions">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:choose>
                                            <c:when test="${not empty brandList}">
                                                <c:forEach var="brand" items="${brandList}">
                                                    <tr>
                                                        <td>${brand.brandId}</td>
                                                        <td class="name-cell">${brand.brandName}</td>
                                                        <td>
                                                            <span class="status-badge ${brand.status == 'ACTIVE' ? 'status-active' : 'status-inactive'}">
                                                                ${brand.status}
                                                            </span>
                                                        </td>
                                                        <td>
                                                            <fmt:formatDate value="${brand.createdAt}" pattern="dd/MM/yyyy"/>
                                                        </td>
                                                        <td>
                                                            <div class="action-group">
                                                                <button type="button"
                                                                        class="action-btn btn-edit"
                                                                        onclick="openEditBrandModal('${brand.brandId}', '${brand.brandName}', '${brand.status}')">
                                                                    Edit
                                                                </button>

                                                                <button type="button"
                                                                        class="action-btn btn-delete"
                                                                        onclick="openDeleteBrandModal('${brand.brandId}', '${brand.brandName}')">
                                                                    Delete
                                                                </button>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <tr>
                                                    <td colspan="5" class="empty-state">No brand data found.</td>
                                                </tr>
                                            </c:otherwise>
                                        </c:choose>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <%-- TYPE CAR MANAGEMENT --%>
                <div class="accordion-card active" id="typeAccordion">
                    <button type="button" class="accordion-header" onclick="toggleAccordion('typeAccordion')">
                        <div class="accordion-title-wrap">
                            <div class="accordion-left">
                                <h3>Type Car Management</h3>
                                <p>Manage available vehicle type categories.</p>
                            </div>
                        </div>

                        <div class="accordion-right">
                            <span class="count-badge">${totalTypes} types</span>
                            <span class="accordion-icon">⌃</span>
                        </div>
                    </button>

                    <div class="accordion-body">
                        <div class="section-toolbar">
                            <form action="${pageContext.request.contextPath}/admin/brand-type-cars" method="get" class="search-form">
                                <input type="hidden" name="action" value="search">
                                <input type="hidden" name="brandKeyword" value="${brandKeyword}">
                                <div class="search-input-wrap">
                                    <input type="text" name="typeKeyword" placeholder="Search type name..." value="${typeKeyword}">
                                </div>
                                <button type="submit" class="btn btn-search">Search</button>
                            </form>

                            <button type="button" class="btn btn-primary btn-add" onclick="openModal('addTypeModal')">
                                + Add Type Car
                            </button>
                        </div>

                        <div class="table-card">
                            <div class="table-wrapper">
                                <table>
                                    <thead>
                                        <tr>
                                            <th class="col-id">ID</th>
                                            <th>Type Name</th>
                                            <th>Status</th>
                                            <th>Created Date</th>
                                            <th class="col-actions">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:choose>
                                            <c:when test="${not empty typeList}">
                                                <c:forEach var="type" items="${typeList}">
                                                    <tr>
                                                        <td>${type.typeId}</td>
                                                        <td class="name-cell">${type.typeName}</td>
                                                        <td>
                                                            <span class="status-badge ${type.status == 'ACTIVE' ? 'status-active' : 'status-inactive'}">
                                                                ${type.status}
                                                            </span>
                                                        </td>
                                                        <td>
                                                            <fmt:formatDate value="${type.createdAt}" pattern="dd/MM/yyyy"/>
                                                        </td>
                                                        <td>
                                                            <div class="action-group">
                                                                <button type="button"
                                                                        class="action-btn btn-edit"
                                                                        onclick="openEditTypeModal('${type.typeId}', '${type.typeName}', '${type.status}')">
                                                                    Edit
                                                                </button>

                                                                <button type="button"
                                                                        class="action-btn btn-delete"
                                                                        onclick="openDeleteTypeModal('${type.typeId}', '${type.typeName}')">
                                                                    Delete
                                                                </button>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <tr>
                                                    <td colspan="5" class="empty-state">No type car data found.</td>
                                                </tr>
                                            </c:otherwise>
                                        </c:choose>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <%-- ADD BRAND MODAL --%>
                <div class="modal" id="addBrandModal">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3>Add New Brand</h3>
                        </div>
                        <form action="${pageContext.request.contextPath}/admin/brand-type-cars" method="post">
                            <input type="hidden" name="action" value="createBrand">
                            <div class="modal-body">
                                <div class="form-group">
                                    <label>Brand Name</label>
                                    <input type="text" name="brandName" required>
                                </div>

                                <div class="form-group">
                                    <label>Status</label>
                                    <select name="brandStatus">
                                        <option value="ACTIVE">ACTIVE</option>
                                        <option value="INACTIVE">INACTIVE</option>
                                    </select>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" onclick="closeModal('addBrandModal')">Cancel</button>
                                <button type="submit" class="btn btn-primary">Save Brand</button>
                            </div>
                        </form>
                    </div>
                </div>

                <%-- EDIT BRAND MODAL --%>
                <div class="modal" id="editBrandModal">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3>Edit Brand</h3>
                        </div>
                        <form action="${pageContext.request.contextPath}/admin/brand-type-cars" method="post">
                            <input type="hidden" name="action" value="updateBrand">
                            <input type="hidden" name="brandId" id="editBrandId">
                            <div class="modal-body">
                                <div class="form-group">
                                    <label>Brand Name</label>
                                    <input type="text" name="brandName" id="editBrandName" required>
                                </div>

                                <div class="form-group">
                                    <label>Status</label>
                                    <select name="brandStatus" id="editBrandStatus">
                                        <option value="ACTIVE">ACTIVE</option>
                                        <option value="INACTIVE">INACTIVE</option>
                                    </select>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" onclick="closeModal('editBrandModal')">Cancel</button>
                                <button type="submit" class="btn btn-primary">Update Brand</button>
                            </div>
                        </form>
                    </div>
                </div>
                <%-- DELETE BRAND MODAL --%>
                <div class="modal" id="deleteBrandModal">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3>Confirm Delete</h3>
                        </div>
                        <form action="${pageContext.request.contextPath}/admin/brand-type-cars" method="post">
                            <input type="hidden" name="action" value="deleteBrand">
                            <input type="hidden" name="brandId" id="deleteBrandId">
                            <div class="modal-body">
                                <p>Are you sure you want to delete this brand?</p>
                                <div class="readonly-box" id="deleteBrandName"></div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" onclick="closeModal('deleteBrandModal')">Cancel</button>
                                <button type="submit" class="btn btn-danger">Delete</button>
                            </div>
                        </form>
                    </div>
                </div>

                <%-- ADD TYPE MODAL --%>
                <div class="modal" id="addTypeModal">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3>Add New Type Car</h3>
                        </div>
                        <form action="${pageContext.request.contextPath}/admin/brand-type-cars" method="post">
                            <input type="hidden" name="action" value="createType">
                            <div class="modal-body">
                                <div class="form-group">
                                    <label>Type Name</label>
                                    <input type="text" name="typeName" required>
                                </div>

                                <div class="form-group">
                                    <label>Status</label>
                                    <select name="typeStatus">
                                        <option value="ACTIVE">ACTIVE</option>
                                        <option value="INACTIVE">INACTIVE</option>
                                    </select>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" onclick="closeModal('addTypeModal')">Cancel</button>
                                <button type="submit" class="btn btn-primary">Save Type</button>
                            </div>
                        </form>
                    </div>
                </div>

                <%-- EDIT TYPE MODAL --%>
                <div class="modal" id="editTypeModal">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3>Edit Type Car</h3>
                        </div>
                        <form action="${pageContext.request.contextPath}/admin/brand-type-cars" method="post">
                            <input type="hidden" name="action" value="updateType">
                            <input type="hidden" name="typeId" id="editTypeId">
                            <div class="modal-body">
                                <div class="form-group">
                                    <label>Type Name</label>
                                    <input type="text" name="typeName" id="editTypeName" required>
                                </div>

                                <div class="form-group">
                                    <label>Status</label>
                                    <select name="typeStatus" id="editTypeStatus">
                                        <option value="ACTIVE">ACTIVE</option>
                                        <option value="INACTIVE">INACTIVE</option>
                                    </select>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" onclick="closeModal('editTypeModal')">Cancel</button>
                                <button type="submit" class="btn btn-primary">Update Type</button>
                            </div>
                        </form>
                    </div>
                </div>


                <%-- DELETE TYPE MODAL --%>
                <div class="modal" id="deleteTypeModal">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3>Confirm Delete</h3>
                        </div>
                        <form action="${pageContext.request.contextPath}/admin/brand-type-cars" method="post">
                            <input type="hidden" name="action" value="deleteType">
                            <input type="hidden" name="typeId" id="deleteTypeId">
                            <div class="modal-body">
                                <p>Are you sure you want to delete this type car?</p>
                                <div class="readonly-box" id="deleteTypeName"></div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" onclick="closeModal('deleteTypeModal')">Cancel</button>
                                <button type="submit" class="btn btn-danger">Delete</button>
                            </div>
                        </form>
                    </div>
                </div>
                <script>
                    function toggleAccordion(id) {
                        const card = document.getElementById(id);
                        card.classList.toggle('active');
                    }

                    function openModal(id) {
                        document.getElementById(id).classList.add('show');
                    }

                    function closeModal(id) {
                        document.getElementById(id).classList.remove('show');
                    }

                    function openEditBrandModal(id, name, status) {
                        document.getElementById('editBrandId').value = id;
                        document.getElementById('editBrandName').value = name;
                        document.getElementById('editBrandStatus').value = status;
                        openModal('editBrandModal');
                    }

                    function openDeleteBrandModal(id, name) {
                        document.getElementById('deleteBrandId').value = id;
                        document.getElementById('deleteBrandName').innerText = name;
                        openModal('deleteBrandModal');
                    }

                    function openEditTypeModal(id, name, status) {
                        document.getElementById('editTypeId').value = id;
                        document.getElementById('editTypeName').value = name;
                        document.getElementById('editTypeStatus').value = status;
                        openModal('editTypeModal');
                    }

                    function openDeleteTypeModal(id, name) {
                        document.getElementById('deleteTypeId').value = id;
                        document.getElementById('deleteTypeName').innerText = name;
                        openModal('deleteTypeModal');
                    }

                    window.onclick = function (event) {
                        document.querySelectorAll('.modal').forEach(function (modal) {
                            if (event.target === modal) {
                                modal.classList.remove('show');
                            }
                        });
                    };
                </script>
                </body>
                </html>