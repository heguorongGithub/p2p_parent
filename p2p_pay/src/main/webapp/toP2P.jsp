<%--
  Created by IntelliJ IDEA.
  User: guoxin@bjpowernode.com
  Date: 2018/11/12
  Time: 16:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>疯狂加载中...</title>
</head>
<body>
<form method="post" action="${pay_p2p_return_url}">
    <input type="hidden" name="signVerified" value="${signVerified}"/>
    <c:forEach items="${params}" var="paramMap">
        <input type="hidden" name="${paramMap.key}" value="${paramMap.value}"/>
    </c:forEach>
</form>
<script>document.forms[0].submit();</script>
</body>
</html>
