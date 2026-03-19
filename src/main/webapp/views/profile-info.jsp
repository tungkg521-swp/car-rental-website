<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- ================= PROFILE CARD ================= -->
<div class="profile-card">

    <c:if test="${param.msg == 'success'}">
        <div class="alert success">
            Cập nhật thành công!
        </div>
    </c:if>

    <c:if test="${param.msg == 'error'}">
        <div class="alert error">
            Cập nhật thất bại!
        </div>
    </c:if>

    <div class="card-header">
        <h2>Thông tin tài khoản</h2>
        <button class="edit-main-btn" onclick="openModal()">✏</button>
    </div>

    <div class="profile-layout">

        <!-- LEFT -->
        <div class="profile-left">

            <div class="avatar-circle">
                ${fn:substring(CUSTOMER_PROFILE.fullName,0,1)}
            </div>

            <h3>${CUSTOMER_PROFILE.fullName}</h3>

            <p class="join-date">
                Tham gia:
                ${fn:substring(CUSTOMER_PROFILE.createdAt,0,10)}
            </p>

        </div>

        <!-- RIGHT -->
        <div class="profile-right">

            <div class="info-row">
                <span>Ngày sinh</span>
                <span>${CUSTOMER_PROFILE.dob}</span>
            </div>

            <div class="info-row">
                <span>Số điện thoại</span>
                <span>${CUSTOMER_PROFILE.phone}</span>
            </div>

            <div class="info-row">
                <span>Email</span>
                <span>${CUSTOMER_PROFILE.email}</span>
            </div>

        </div>

    </div>

</div>


<!-- ================= DRIVER LICENSE ================= -->
<div class="profile-card license-card">

    <div class="card-header">
        <h2>Giấy phép lái xe</h2>

        <c:choose>

            <c:when test="${LICENSE != null && LICENSE.status == 'APPROVED'}">
                <span class="badge green">Đã xác thực</span>
            </c:when>

            <c:when test="${LICENSE != null && LICENSE.status == 'REQUESTED'}">
                <span class="badge orange">Đang chờ duyệt</span>
            </c:when>

            <c:when test="${LICENSE != null && LICENSE.status == 'REJECTED'}">
                <span class="badge red">Bị từ chối</span>
            </c:when>

            <c:otherwise>
                <span class="badge gray">Chưa gửi xác thực</span>
            </c:otherwise>

        </c:choose>

        <c:if test="${LICENSE == null || LICENSE.status != 'APPROVED'}">
            <button type="button"
                    class="edit-btn"
                    onclick="enableLicenseEdit()">
                Chỉnh sửa
            </button>
        </c:if>

        <c:if test="${LICENSE != null && (LICENSE.status == null || LICENSE.status == 'REJECTED')}">
            <form method="post"
                  action="${pageContext.request.contextPath}/customer/profile"
                  style="margin-top:10px;">
                <input type="hidden" name="action" value="requestVerification">
                <button type="submit" class="verify-btn">
                    Gửi xác thực
                </button>
            </form>
        </c:if>

    </div>


    <form method="post"
          action="${pageContext.request.contextPath}/customer/profile"
          enctype="multipart/form-data">

        <input type="hidden" name="action" value="updateLicense">

        <!-- IMAGE -->
        <div class="license-images">

            <div class="image-box">
                <label>Ảnh mặt trước</label>

                <c:choose>
                    <c:when test="${LICENSE != null && LICENSE.imageFront != null}">
                        <img src="${pageContext.request.contextPath}/license-image?name=${LICENSE.imageFront}"
                             class="preview-img">
                    </c:when>
                    <c:otherwise>
                        <div class="empty-img">Chưa có ảnh</div>
                    </c:otherwise>
                </c:choose>

                <input type="file"
                       name="imageFront"
                       class="file-input"
                       accept="image/*"
                       disabled>
            </div>

            <div class="image-box">
                <label>Ảnh mặt sau</label>

                <c:choose>
                    <c:when test="${LICENSE != null && LICENSE.imageBack != null}">
                        <img src="${pageContext.request.contextPath}/license-image?name=${LICENSE.imageBack}"
                             class="preview-img">
                    </c:when>
                    <c:otherwise>
                        <div class="empty-img">Chưa có ảnh</div>
                    </c:otherwise>
                </c:choose>

                <input type="file"
                       name="imageBack"
                       class="file-input"
                       accept="image/*"
                       disabled>
            </div>

        </div>


        <!-- FORM -->
        <div class="form-group">
            <label>Số GPLX</label>
            <input type="text"
                   name="licenseNumber"
                   value="${LICENSE != null ? LICENSE.licenseNumber : ''}"
                   readonly>
        </div>

        <div class="form-group">
            <label>Họ và tên</label>
            <input type="text"
                   name="fullName"
                   value="${LICENSE != null ? LICENSE.fullName : ''}"
                   readonly>
        </div>

        <div class="form-group">
            <label>Ngày sinh</label>
            <input type="date"
                   name="dob"
                   value="${LICENSE != null && LICENSE.dob != null ? LICENSE.dob : ''}"
                   readonly>
        </div>

        <div class="form-group">
            <label>Ngày cấp</label>
            <input type="date"
                   name="issueDate"
                   value="${LICENSE != null && LICENSE.issueDate != null ? LICENSE.issueDate : ''}"
                   readonly>
        </div>

        <div class="form-group">
            <label>Ngày hết hạn</label>
            <input type="date"
                   name="expiryDate"
                   value="${LICENSE != null && LICENSE.expiryDate != null ? LICENSE.expiryDate : ''}"
                   readonly>
        </div>

        <button type="submit"
                class="save-btn"
                style="display:none">
            Lưu
        </button>

    </form>

</div>


<!-- ================= MODAL UPDATE PROFILE ================= -->
<div id="updateModal" class="modal-overlay">

    <div class="modal-box">

        <button class="modal-close" onclick="closeModal()">✕</button>

        <h2>Cập nhật thông tin</h2>

        <form action="${pageContext.request.contextPath}/customer/profile"
              method="post">

            <input type="hidden" name="action" value="update">

            <div class="form-group">
                <label>Tên tài khoản</label>
                <input type="text"
                       name="fullName"
                       value="${CUSTOMER_PROFILE.fullName}" required>
            </div>

            <div class="form-group">
                <label>Ngày sinh</label>
                <input type="date"
                       name="dob"
                       value="${CUSTOMER_PROFILE.dob}">
            </div>

            <div class="form-group">
                <label>Số điện thoại</label>
                <input type="text"
                       name="phone"
                       value="${CUSTOMER_PROFILE.phone}">
            </div>

            <div class="form-group">
                <label>Email</label>
                <input type="email"
                       name="email"
                       value="${CUSTOMER_PROFILE.email}">
            </div>

            <button type="submit" class="modal-submit">
                Cập nhật
            </button>

        </form>

    </div>

</div>
            
