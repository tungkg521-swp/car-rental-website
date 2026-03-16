<head>
    <title>My Profile</title>
  <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/notification.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/profile.css">

</head>


<body>
    <jsp:include page="includes/header.jsp"/>

    <div class="profile-wrapper">

        <jsp:include page="profile-sidebar.jsp"/>





        <div class="profile-content">

            <jsp:include page="profile-info.jsp"/>

        </div>




    </div>
            <script src="${pageContext.request.contextPath}/assets/js/profile.js"></script>
</body>