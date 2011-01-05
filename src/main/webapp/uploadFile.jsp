<?xml version="1.0" encoding="UTF-8"?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:directive.page contentType="text/html; charset=UTF-8" language="java" isELIgnored="false" />
<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="javax.jdo.PersistenceManager" />
<jsp:directive.page import="com.google.appengine.api.users.User" />
<jsp:directive.page import="com.google.appengine.api.users.UserService" />
<jsp:directive.page import="com.google.appengine.api.users.UserServiceFactory" />
<jsp:directive.page import="de.elatexam.dao.DataStoreTaskFactory" />
<jsp:directive.page import="de.elatexam.PMF" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
    <title>
      Aufgabendefinition hochladen
    </title>
  </head>
  <body>

    <%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    request.setAttribute("user", user);
    if(userService.isUserLoggedIn()){
      request.setAttribute("taskdefs",DataStoreTaskFactory.getInstance().getTaskDefsOf(user.getNickname()));
    }
    %>
    <c:if test="${user == null}">
      <p>
        Hallo!
        <a href="<%=userService.createLoginURL(request.getRequestURI())%>">
          Bitte melden Sie sich an
        </a>
        , um Aufgabenpools hochzuladen.
      </p>
    </c:if>
    <c:if test="${user != null}">
      <p>
        Hallo,
        <c:out value="${user.nickname}" />
        ! (Sie k&ouml;nnen sich
        <a href="<%=userService.createLogoutURL(request.getRequestURI())%>">
          hier abmelden
        </a>
        .)
      </p>

      Neue Aufgabe hochladen:
      <br/>

      <form action="/upload" method="post" enctype="multipart/form-data">
        <div>
          <label>
            Aufgabendatei:
          </label>
          <input type="file" name="content"/>
        </div>
        <div>
          <input type="submit" value="Hochladen" />
        </div>
      </form>

      <br/>
      <br/>
      Links zu allen von Ihnen hochgeladenen Aufgaben:
      <br/>
      <table>
        <th>
          Id
        </th>
        <th>
          Datum
        </th>
        <c:forEach var="td" items="${taskdefs}">
          <tr>
            <td>
              <a href="/preview?id=<c:out value="${td.id}"/>">
                <c:out value="${td.id}"/>
              </a>
            </td>
            <td>
              <c:out value="${td.creationDate}"/>
            </td>
            <td>
              <a href="/delete?id=<c:out value="${td.id}"/>">
                L&ouml;schen
              </a>
            </td>
          </tr>
        </c:forEach>
      </table>
    </c:if>

    <jsp:include page="footer.jsp" />
  </body>
</html>
