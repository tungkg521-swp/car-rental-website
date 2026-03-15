<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Send Notification</title>

        ```
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff.css">
        ```

    </head>

    <body>

        <div class="staff-layout">

            ```
            <%@ include file="sidebar.jsp" %>

            <div class="staff-content">

                <h1 class="dashboard-title">Send Notification</h1>

                <!-- SEND NOTIFICATION FORM -->
                <div class="dashboard-table">

                    <form action="${pageContext.request.contextPath}/SendNotificationController" method="post">

                        <table>
                             <tr>
                                <td><b>Title</b></td>
                                <td>
                                    <input type="text" name="customerId" required style="width:300px;">
                                </td>
                            </tr>
                            <tr>
                                <td><b>Title</b></td>
                                <td>
                                    <input type="text" name="title" required style="width:300px;">
                                </td>
                            </tr>

                            <tr>
                                <td><b>Message</b></td>
                                <td>
                                    <textarea name="message" rows="5" cols="50" required></textarea>
                                </td>
                            </tr>

                            <tr>
                                <td></td>
                                <td>
                                    <button type="submit">Send Notification</button>
                                </td>
                            </tr>
                        </table>

                    </form>

                    <!-- SUCCESS MESSAGE -->
                    <c:if test="${not empty success}">
                        <p style="color:green; margin-top:15px;">
                            ${success}
                        </p>
                    </c:if>

                    <!-- ERROR MESSAGE -->
                    <c:if test="${not empty error}">
                        <p style="color:red; margin-top:15px;">
                            ${error}
                        </p>
                    </c:if>

                </div>

            </div>
            ```

        </div>

    </body>
</html>
