<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>测试wangjp的springmvc</title>
</head>
<body>
<div align="center">
    <form action="wangjp/insert">
        姓名：<input type="text" name="name"/>
        <input type="submit" value="提交"/>
    </form>
    <form action="wangjp/query">
        姓名：<input type="text" name="name"/>
        <input type="submit" value="查询"/>
    </form>
</div>
</body>
</html>