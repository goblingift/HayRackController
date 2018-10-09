<%-- 
    Document   : timetable
    Created on : 08.10.2018, 07:14:12
    Author     : andre
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<link href="webjars/bootstrap/4.1.3/css/bootstrap.min.css"
      rel="stylesheet">
<script src="webjars/bootstrap/4.1.3/js/bootstrap.min.js"></script>
<script src="webjars/jquery/3.3.1/jquery.min.js"></script>

<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
        <meta name="description" content="">
        <meta name="author" content="">

        <title>Timetable</title>

        <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
        <link href="${contextPath}/resources/css/common.css" rel="stylesheet">

        <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
        <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>

    <body>

        <div class="container">

            <h3>Scheduled lunch times</h3>
            <!--Table-->
            <table id="scheduled-timings" class="table">
                <!--Table head-->
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Open at</th>
                        <th>Close at</th>
                        <th>Comment</th>
                        <th>Defined at</th>
                        <th>Defined from</th>
                    </tr>
                </thead>
                <!-- Existing scheduled timings -->
                <tbody>
                    <c:if test="${not empty scheduledMovements}">

                        <c:forEach items="${scheduledMovements}" var="actItem" varStatus="actStatus">
                            <tr>
                                <th scope="row">${actStatus.count}</th>
                                <td>${actItem.openAt}</td>
                                <td>${actItem.closeAt}</td>
                                <td>${actItem.comment}</td>
                                <td>${actItem.createdAt}</td>
                                <td>${actItem.createdBy}</td>
                                <td><a class="btn btn-default" href="/timetable/delete/${actItem.id}">Delete</a></td>
                            </tr>
                        </c:forEach>
                    </c:if>
                    <!-- Inputs for new scheduled timings -->

                    <form:form action="/timetable/add" modelAttribute="newMovement">
                        <tr>
                            <th scope="row">ADD NEW</th>
                            <td><form:input path="openAt"/></td>
                            <td><form:input path="closeAt"/></td>
                            <td><form:input path="comment"/></td>
                            <td></td>
                            <td></td>
                            <td><input type="submit" value="submit"/>ADD</td>
                        </tr>
                    </form:form>
                </tbody>
                <!--Table body-->
            </table>
            <!--Table-->


        </div>
    </body>
</html>
