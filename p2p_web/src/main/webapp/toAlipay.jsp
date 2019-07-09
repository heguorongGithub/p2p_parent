<%--
  Created by IntelliJ IDEA.
  User: guoxin@bjpowernode.com
  Date: 2018/11/12
  Time: 15:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>疯狂加载中...</title>
</head>
<body>
<form method="post" action="${p2p_pay_alipay_url}">
    <input type="hidden" id="out_trade_no" name="out_trade_no" value="${out_trade_no}"/>
    <input type="hidden" id="total_amount" name="total_amount" value="${rechargeMoney}"/>
    <input type="hidden" id="subject" name="subject" value="${subject}"/>
    <input type="hidden" id="body" name="body" value="${body}"/>
</form>
<script>document.forms[0].submit();</script>
</body>
</html>
