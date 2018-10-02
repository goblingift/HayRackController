<%-- 
    Document   : dashboard
    Created on : 01.10.2018, 19:04:10
    Author     : andre
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>HayRack dashboard</title>
    </head>
    <body>
        <h3>Welcome ${username} to your dashboard!</h3>
    </body>
</html>
