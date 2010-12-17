<?xml version="1.0" encoding="UTF-8"?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:directive.page contentType="text/html; charset=UTF-8"
	language="java" isELIgnored="false" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="javax.jdo.PersistenceManager" />
<jsp:directive.page import="com.google.appengine.api.users.User" />
<jsp:directive.page import="com.google.appengine.api.users.UserService" />
<jsp:directive.page
	import="com.google.appengine.api.users.UserServiceFactory" />
<jsp:directive.page import="de.elatexam.Greeting" />
<jsp:directive.page import="de.elatexam.PMF" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<title>Aufgabendefinition hochladen</title>
</head>
<body>


<form action="/upload" method="post" enctype="multipart/form-data">
<div><input type="file" name="content"/></div>
<div><input type="submit" value="Upload" /></div>
</form>

</body>
</html>