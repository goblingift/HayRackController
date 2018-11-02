<%-- 
    Document   : dashboard
    Created on : 01.10.2018, 19:04:10
    Author     : andre
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<link href="webjars/bootstrap/4.1.3/css/bootstrap.min.css"
      rel="stylesheet">
<script src="webjars/bootstrap/4.1.3/js/bootstrap.min.js"></script>
<script src="webjars/jquery/3.3.1/jquery.min.js"></script>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>HayRack dashboard</title>
    </head>
    <body>
        <div>
            <p>
                <h3>Welcome ${username} to your dashboard!</h3>  
            </p>
            <p>
                ${build_artifact}, version ${build_version} built at ${build_time}
            </p>
        </div>

        <div>
            <a class="btn btn-default" href="/dashboard/shutters-up">Shutters up</a>
        </div>

        <div>
            <a class="btn btn-default" href="/dashboard/shutters-down">Shutters down</a>
        </div>

        <br>
        <br>


        <form:form action="/dashboard/shutters-move-custom" method="POST" modelAttribute="shutterMovement">
            <table>
                <tr>
                    <td><form:label path="duration">Duration (Milliseconds)</form:label></td>
                    <td><form:input path="duration" type="number"/></td>
                </tr>
                <tr>
                    <form:radiobutton path = "direction" value = "UP" label = "Up" />
                    <form:radiobutton path = "direction" value = "DOWN" label = "Down" />
                </tr>
                <tr>
                    <td><input type="submit" value="Submit"/></td>
                </tr>
            </table>
        </form:form>

        <div>
            <a class="btn btn-default" href="/timetable">Display timetable</a>
        </div>

    </body>
</html>
