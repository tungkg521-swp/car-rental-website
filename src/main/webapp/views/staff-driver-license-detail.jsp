<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Driver License Detail</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css?v=2">
</head>
<body>

<div class="staff-layout">

    <%@ include file="sidebar.jsp" %>

    <div class="staff-content">

        <c:if test="${license != null}">

            <h1 class="dashboard-title">Driver License Detail</h1>

            <a class="btn back-btn"
               href="${pageContext.request.contextPath}/staff/licenses">
                ← Back to list
            </a>

            <div class="license-info">
                <p><b>License ID:</b> ${license.licenseId}</p>
                <p><b>Customer ID:</b> ${license.customerId}</p>
                <p><b>Số GPLX:</b> ${license.licenseNumber}</p>
                <p><b>Họ tên:</b> ${license.fullName}</p>
                <p><b>Ngày sinh:</b> ${license.dob}</p>
                <p><b>Ngày cấp:</b> ${license.issueDate}</p>
                <p><b>Ngày hết hạn:</b> ${license.expiryDate}</p>
                <p><b>Status:</b>
                    <span class="status-badge ${license.status}">
                        ${license.status}
                    </span>
                </p>
            </div>

            <div class="license-images">

                <div class="image-box">
                    <p><b>Ảnh mặt trước</b></p>
                    <img src="${pageContext.request.contextPath}/license-image?name=${license.imageFront}"
                         alt="Front">
                </div>

                <div class="image-box">
                    <p><b>Ảnh mặt sau</b></p>
                    <img src="${pageContext.request.contextPath}/license-image?name=${license.imageBack}"
                         alt="Back">
                </div>

            </div>

            <c:if test="${license.status == 'REQUESTED'}">
                <form method="post"
                      action="${pageContext.request.contextPath}/staff/licenses"
                      class="license-action-form">

                    <input type="hidden"
                           name="licenseId"
                           value="${license.licenseId}" />

                    <button class="btn btn-primary"
                            type="submit"
                            name="action"
                            value="approve">
                        Approve
                    </button>

                    <button class="btn btn-danger"
                            type="submit"
                            name="action"
                            value="reject"
                            onclick="return confirm('Bạn có chắc muốn từ chối GPLX này?');">
                        Reject
                    </button>

                </form>
            </c:if>

            <c:if test="${license.status != 'REQUESTED'}">
                <p class="license-processed-msg">
                    GPLX đã được xử lý (APPROVED / REJECTED).
                </p>
            </c:if>

        </c:if>

        <c:if test="${license == null}">
            <p>License not found.</p>
        </c:if>

    </div>
</div>

</body>
</html>