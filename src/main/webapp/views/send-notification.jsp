<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Send Notification</title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/notification.css">

    </head>

    <body>

        <div class="staff-layout">
            <%@ include file="sidebar.jsp" %>

            <div class="staff-content">

                <h1 class="dashboard-title">Send Notification</h1>


                <form class="noti-form"
                      action="${pageContext.request.contextPath}/SendNotificationController"
                      method="post">

                    <input type="hidden" name="action" value="sendNotification">

                    <div class="noti-group">
                        <label>Title</label>
                        <input class="noti-input" type="text" name="title" required>
                    </div>

                    <div class="noti-group">
                        <label>Content</label>
                        <textarea class="noti-textarea" name="content" rows="4" required></textarea>
                    </div>

                    <div class="noti-group">
                        <label>Send To</label><br>

                        <div class="noti-radio-group">
                            <label>
                                <input type="radio" name="sendType" value="all"
                                       onclick="toggleCustomer(false)" checked>
                                All Customers
                            </label>

                            <label>
                                <input type="radio" name="sendType" value="specific"
                                       onclick="toggleCustomer(true)">
                                Specific Customer
                            </label>
                        </div>
                    </div>

                    <div class="noti-group" id="customerSelectBox" style="display:none;">
                        <label>Select Customer</label>

                        <select class="noti-select" name="customerId">
                            <c:forEach items="${customerList}" var="c">
                                <option value="${c.customerId}">
                                    ${c.fullName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <button type="submit" class="noti-btn">
                        Send
                    </button>

                </form>

            </div>

        </div>

        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

        <c:if test="${not empty success}">
            <script>
                                                       Swal.fire({
                                                           icon: 'success',
                                                           title: '${success}',
                                                           showConfirmButton: false,
                                                           timer: 1500
                                                       }).then(() => {
                                                           window.location.href = '${pageContext.request.contextPath}/SendNotificationController';
                                                       });
            </script>
        </c:if>

        <c:if test="${not empty error}">
            <script>
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: '${error}'
                });
            </script>
        </c:if>
        <script>
            function toggleCustomer(show) {

                const box = document.getElementById("customerSelectBox");

                if (show) {
                    box.style.display = "block";
                } else {
                    box.style.display = "none";
                }

            }
        </script>
    </body>
</html>
