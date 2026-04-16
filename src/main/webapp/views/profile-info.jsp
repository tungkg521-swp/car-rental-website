<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="profile-card">

    <div class="card-header">
        <h2>Thông tin tài khoản</h2>
        <button class="edit-main-btn" onclick="openModal()">✏</button>
    </div>

    <div class="profile-layout">
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
            <button type="button" class="edit-btn" onclick="enableLicenseEdit()">
                Chỉnh sửa
            </button>
        </c:if>

        <c:if test="${LICENSE != null && (LICENSE.status == null || LICENSE.status == 'REJECTED')}">
            <form method="post"
                  action="${pageContext.request.contextPath}/customer/profile"
                  style="margin-top:10px;">
                <input type="hidden" name="action" value="requestVerification">
                <button type="submit" class="verify-btn">Gửi xác thực</button>
            </form>
        </c:if>
    </div>

    <form method="post"
          action="${pageContext.request.contextPath}/customer/profile"
          enctype="multipart/form-data">

        <input type="hidden" name="action" value="updateLicense">

        <c:set var="formData" value="${licenseForm != null ? licenseForm : LICENSE}" />
        <c:set var="errs" value="${licenseErrors}" />




        <h3 style="margin-top:20px;">Ảnh giấy phép lái xe</h3>
        <div class="license-images">
            <div class="image-box">
                <label>GPLX mặt trước</label>
                <c:choose>
                    <c:when test="${formData != null && formData.imageFront != null}">
                        <img src="${pageContext.request.contextPath}/license-image?name=${formData.imageFront}"
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
                       ${formData == null || empty formData.imageFront ? 'required' : ''}
                       disabled
                       onchange="previewImage(this)">

                <c:if test="${errs.imageFront != null}">
                    <small style="display:block;margin-top:6px;color:#e53935;font-size:13px;font-weight:600;">
                        ${errs.imageFront}
                    </small>
                </c:if>
            </div>

            <div class="image-box">
                <label>GPLX mặt sau</label>
                <c:choose>
                    <c:when test="${formData != null && formData.imageBack != null}">
                        <img src="${pageContext.request.contextPath}/license-image?name=${formData.imageBack}"
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
                       ${formData == null || empty formData.imageBack ? 'required' : ''}
                       disabled
                       onchange="previewImage(this)">

                <c:if test="${errs.imageBack != null}">
                    <small style="display:block;margin-top:6px;color:#e53935;font-size:13px;font-weight:600;">
                        ${errs.imageBack}
                    </small>
                </c:if>
            </div>
        </div>

        <h3 style="margin-top:30px;">Ảnh xác minh chính chủ</h3>
        <div class="license-images">
            <div class="image-box">
                <label>Ảnh selfie cầm giấy tờ</label>
                <c:choose>
                    <c:when test="${formData != null && formData.selfieImage != null}">
                        <img src="${pageContext.request.contextPath}/license-image?name=${formData.selfieImage}"
                             class="preview-img">
                    </c:when>
                    <c:otherwise>
                        <div class="empty-img">Chưa có ảnh</div>
                    </c:otherwise>
                </c:choose>


                <input type="file"
                       name="selfieImage"
                       class="file-input"
                       accept="image/*"
                       ${formData == null || empty formData.selfieImage ? 'required' : ''}
                       disabled
                       onchange="previewImage(this)">

                <c:if test="${errs.selfieImage != null}">
                    <small style="display:block;margin-top:6px;color:#e53935;font-size:13px;font-weight:600;">
                        ${errs.selfieImage}
                    </small>
                </c:if>
            </div>

            <div class="image-box">
                <label>CCCD mặt trước</label>
                <c:choose>
                    <c:when test="${formData != null && formData.nationalIdFront != null}">
                        <img src="${pageContext.request.contextPath}/license-image?name=${formData.nationalIdFront}"
                             class="preview-img">
                    </c:when>
                    <c:otherwise>
                        <div class="empty-img">Chưa có ảnh</div>
                    </c:otherwise>
                </c:choose>

                <input type="file"
                       name="nationalIdFront"
                       class="file-input"
                       accept="image/*"
                       ${formData == null || empty formData.nationalIdFront ? 'required' : ''}
                       disabled
                       onchange="previewImage(this)"
                       ">

                <c:if test="${errs.nationalIdFront != null}">
                    <small style="display:block;margin-top:6px;color:#e53935;font-size:13px;font-weight:600;">
                        ${errs.nationalIdFront}
                    </small>
                </c:if>
            </div>

            <div class="image-box">
                <label>CCCD mặt sau</label>
                <c:choose>
                    <c:when test="${formData != null && formData.nationalIdBack != null}">
                        <img src="${pageContext.request.contextPath}/license-image?name=${formData.nationalIdBack}"
                             class="preview-img">
                    </c:when>
                    <c:otherwise>
                        <div class="empty-img">Chưa có ảnh</div>
                    </c:otherwise>
                </c:choose>

                <input type="file"
                       name="nationalIdBack"
                       class="file-input"
                       accept="image/*"
                       ${formData == null || empty formData.nationalIdBack ? 'required' : ''}
                       disabled
                       onchange="previewImage(this)"
                       ">

                <c:if test="${errs.nationalIdBack != null}">
                    <small style="display:block;margin-top:6px;color:#e53935;font-size:13px;font-weight:600;">
                        ${errs.nationalIdBack}
                    </small>
                </c:if>
            </div>
        </div>



        <div class="form-group" style="margin-bottom:16px;">
            <label>Số GPLX</label>
            <input type="text"
                   name="licenseNumber"
                   value="${formData != null ? formData.licenseNumber : ''}"
                   readonly
                   required
                   style="width:100%;${errs.licenseNumber != null ? 'border:1.5px solid #e53935;background:#fff5f5;' : ''}">
            <c:if test="${errs.licenseNumber != null}">
                <small style="display:block;margin-top:6px;color:#e53935;font-size:13px;font-weight:600;">
                    ${errs.licenseNumber}
                </small>
            </c:if>
        </div>

        <div class="form-group" style="margin-bottom:16px;">
            <label>Họ và tên</label>
            <input type="text"
                   name="fullName"
                   value="${formData != null ? formData.fullName : ''}"
                   readonly
                   required
                   style="width:100%;${errs.fullName != null ? 'border:1.5px solid #e53935;background:#fff5f5;' : ''}">
            <c:if test="${errs.fullName != null}">
                <small style="display:block;margin-top:6px;color:#e53935;font-size:13px;font-weight:600;">
                    ${errs.fullName}
                </small>
            </c:if>
        </div>

        <div class="form-group" style="margin-bottom:16px;">
            <label>Ngày sinh</label>
            <input type="date"
                   name="dob"
                   value="${formData != null && formData.dob != null ? formData.dob : ''}"
                   readonly
                   required
                   style="width:100%;${errs.dob != null ? 'border:1.5px solid #e53935;background:#fff5f5;' : ''}">
            <c:if test="${errs.dob != null}">
                <small style="display:block;margin-top:6px;color:#e53935;font-size:13px;font-weight:600;">
                    ${errs.dob}
                </small>
            </c:if>
        </div>

        <div class="form-group" style="margin-bottom:16px;">
            <label>Ngày cấp</label>
            <input type="date"
                   name="issueDate"
                   value="${formData != null && formData.issueDate != null ? formData.issueDate : ''}"
                   readonly
                   required
                   style="width:100%;${errs.issueDate != null ? 'border:1.5px solid #e53935;background:#fff5f5;' : ''}">
            <c:if test="${errs.issueDate != null}">
                <small style="display:block;margin-top:6px;color:#e53935;font-size:13px;font-weight:600;">
                    ${errs.issueDate}
                </small>
            </c:if>
        </div>

        <div class="form-group" style="margin-bottom:16px;">
            <label>Ngày hết hạn</label>
            <input type="date"
                   name="expiryDate"
                   value="${formData != null && formData.expiryDate != null ? formData.expiryDate : ''}"
                   readonly
                   required
                   style="width:100%;${errs.expiryDate != null ? 'border:1.5px solid #e53935;background:#fff5f5;' : ''}">
            <c:if test="${errs.expiryDate != null}">
                <small style="display:block;margin-top:6px;color:#e53935;font-size:13px;font-weight:600;">
                    ${errs.expiryDate}
                </small>
            </c:if>
        </div>

        <c:if test="${not empty saveError}">
            <div style="margin-top:10px;padding:10px 14px;border-radius:8px;background:#fff1f0;color:#d32f2f;border:1px solid #ffcdd2;">
                ${saveError}
            </div>
        </c:if>

        <button type="submit"
                class="save-btn"
                style="${enableLicenseEdit ? 'display:inline-block' : 'display:none'}">
            Lưu
        </button>
    </form>
</div>

<div id="updateModal" class="modal-overlay">
    <div class="modal-box">
        <button class="modal-close" onclick="closeModal()">✕</button>
        <h2>Cập nhật thông tin</h2>

        <form action="${pageContext.request.contextPath}/customer/profile" method="post">
            <input type="hidden" name="action" value="update">

            <div class="form-group">
                <label>Tên tài khoản</label>
                <input type="text" name="fullName" value="${CUSTOMER_PROFILE.fullName}" required>
            </div>

            <div class="form-group">
                <label>Ngày sinh</label>
                <input type="date" name="dob" value="${CUSTOMER_PROFILE.dob}">
            </div>

            <div class="form-group">
                <label>Số điện thoại</label>
                <input type="text" name="phone" value="${CUSTOMER_PROFILE.phone}">
            </div>

            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" value="${CUSTOMER_PROFILE.email}">
            </div>

            <button type="submit" class="modal-submit">Cập nhật</button>
        </form>
    </div>
</div>

<input type="hidden" id="enableLicenseEditValue" value="${enableLicenseEdit}">

<div id="profileAlertData"
     data-msg="${param.msg}"
     data-redirect="${pageContext.request.contextPath}/customer/profile"
     style="display:none;"></div>

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="${pageContext.request.contextPath}/assets/js/profile-license.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/profile-alert.js"></script>