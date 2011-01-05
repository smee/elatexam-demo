<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:directive.page import="de.elatexam.dao.DataStoreTaskFactory" />
<%
long id = Long.parseLong(request.getParameter("id"));
request.setAttribute("taskdef",DataStoreTaskFactory.getInstance().getTaskDef(id));
%>
<html>
  <head>
    <!-- HTTP 1.1 -->
    <meta http-equiv="Cache-Control" content="no-store"/>
    <!-- HTTP 1.0 -->
    <meta http-equiv="Pragma" content="no-cache"/>
    <!-- Prevents caching at the Proxy Server -->

    <meta http-equiv="Expires" content="0"/>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <title>
    </title>

    <link rel="stylesheet" type="text/css" media="all" href="format.css" />

  </head>
  <body>
    <div id="page">
      <table 
          cellspacing="0"
          cellpadding="0"
          border="0"
          bgcolor="#ffffff"
          height="100%"
          style="height:100%"
          align="center">


        <tr>
          <td width=35 bgcolor="#cccccc" background="pics/lshadow.jpg">
            &nbsp;
          </td>
          <td width=1 bgcolor=#000000 !bgcolor="#035E8D">
            <img src="pics/pixel.gif" width=1 height=1>
            <br>
          </td>
          <!-- workaround fuer IE #&%$* -->
          <td 
              width=628
              valign=top
              style="background-image:url(pics/header_blank.jpg); background-repeat:repeat-x;"
              background="pics/header_blank.jpg">

            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td height="110">
                  &nbsp;
                </td>
              </tr>

              <tr>
                <td>
                  &nbsp;
                </td>
              </tr>
            </table>
            <table border=0 width=100% cellpadding=10>
              <tr>
                <td>

                  <!-- Ende Header -->

                  <div id="header" class="clearfix">
                  </div>

                  <div id="content" class="clearfix">
                    <div id="main">
                      <table cellpadding="15" width="100%">
                        <tr>
                          <td>

                            <table width="100%">
                              <tr>
                                <th>
                                  <p class="header">
                                    <c:out value="${taskdef.complexTaskDefRoot.title}"/>
                                  </p>
                                </th>
                              </tr>
                            </table>
                            <br/>
                            <br/>

                            <c:out value="${taskdef.complexTaskDefRoot.description}"/>
                            <br>
                            <br>
                            <fieldset class="tasks">
                              <legend>
                                Informationen zur Bearbeitung
                              </legend>

                              <table border="0" cellspacing="2" cellpadding="2">
                                <tr>
                                  <td>
                                    Maximale Bearbeitungszeit:
                                  </td>
                                  <td>

                                    <c:out 
                                        value="${taskdef.complexTaskDefRoot.timeInMinutesWithoutKindnessExtensionTime}"/>

                                    min
                                  </td>

                                  <td>
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                  </td>
                                </tr>
                                <tr>

                                  <td>
                                    Bereits durchgef&uuml;hrte L&ouml;sungsversuche:
                                  </td>
                                  <td>
                                    0
                                  </td>
                                  <td>
                                    &nbsp;
                                  </td>
                                </tr>

                                <tr>
                                  <td>
                                    Anzahl maximaler L&ouml;sungsversuche:
                                  </td>
                                  <td>
                                    <c:out value="${taskdef.complexTaskDefRoot.tries}"/>
                                  </td>
                                  <td>
                                    &nbsp;
                                  </td>
                                </tr>
                              </table>

                              <br>
                            </fieldset>
                            <br>

                            <fieldset class="tasks">
                              <legend>
                                Informationen zur Korrektur
                              </legend>



                              Korrekturstand des letzten Versuchs: nicht verf&uuml;gbar
                              <br>
                            </fieldset>
                            <br>

                            <fieldset class="tasks">
                              <legend>
                                Start
                              </legend>
                              <table border="0" cellspacing="2" cellpadding="2" width="100%">
                                <tr>

                                  <td valign="top" width="50%">
                                    <fieldset class="tasks">
                                      <legend>
                                        Neuer L&ouml;sungsversuch
                                      </legend>
                                      <form method="get" action="/execute.do">
                                        <input type="submit" value="Starten">
                                        <input type="hidden" name="action" value="ComplexTaskExecute">
                                        <input type="hidden" name="id" value="<c:out value="${taskdef.id}"/>">
                                        <input type="hidden" name="todo" value="new">
                                        <input type="hidden" name="try" value="1">

                                      </form>
                                      <c:out value="${taskdef.complexTaskDefRoot.startText}"/>
                                    </fieldset>
                                  </td>
                                  <td valign="top" width="50%">
                                    <fieldset class="tasks">
                                      <legend>
                                        L&ouml;sungsversuch fortsetzen
                                      </legend>
                                      <form method="get" action="/execute.do">
                                        <input type="submit" value="Fortsetzen" disabled="true">
                                        <input type="hidden" name="action" value="ComplexTaskExecute">
                                        <input type="hidden" name="id" value="???">
                                        <input type="hidden" name="todo" value="continue">
                                        <input type="hidden" name="page" value="1">
                                      </form>

                                    </fieldset>
                                  </td>
                                </tr>
                              </table>

                            </fieldset>
                            <br>
                            <br>

                            <fieldset class="tasks">
                              <legend>
                                Allgemeine Hinweise
                              </legend>
                              <p>
                                <b>
                                  Die Bearbeitung
                                </b>
                                :
                                <br>

                                <ul>
                                  <li>
                                  Mit der Schaltfl&auml;che &quot;Starten&quot; beginnen Sie Ihren L&ouml;sungsversuch.
                                </li>

                                <li>
                                Die Bearbeitung der Aufgaben erfolgt seitenweise.
                              </li>
                              <li>
                              Ihre Eingaben werden
                              <b>
                                erst dann wirksam
                              </b>
                              , wenn Sie die Schaltfl&auml;che &quot;Speichern&quot; bet&auml;tigt haben, die Sie
                              jeweils unten auf einer Seite finden. Was Sie nicht speichern, geht nach Verlassen einer
                              Seite unwiederbringlich verloren! Sollten Sie einmal das Speichern vor dem Verlassen einer
                              Seite vergessen, erscheint ein entsprechender Warnhinweis. Klicken Sie auf "Abbrechen", um
                              auf der aktuellen Seite zu bleiben und Ihre &Auml;nderungen abspeichern zu k&ouml;nnen.
                            </li>
                            <li>
                            Wenn Sie sich durch die Klausur bewegen, benutzen Sie bitte nur die Navigationsleiste links
                            oben. Hier k&ouml;nnen Sie auch sehen, welche Seiten Sie bereits teilweise oder
                            vollst&auml;ndig bearbeitet haben und welche noch nicht.
                            <b>
                              Bitte vermeiden Sie unbedingt, die &quot;Gehe zur&uuml;ck&quot;- und &quot;Gehe
                              vor&quot;-Schaltfl&auml;chen Ihres Browsers zu bet&auml;tigen!
                            </b>
                            Das System kann diese Operationen nicht nachvollziehen, weil sie nur auf dem lokalen PC, der
                            gerade vor Ihnen steht, ablaufen.
                          </li>

                          <li>
                          Im Rahmen der Bearbeitungszeit k&ouml;nnen Sie auch wieder zu dieser Seite wechseln
                          ("Hauptseite"), sich aus- und wieder einloggen und diesen L&ouml;sungsversuch jederzeit
                          fortsetzen. Allerdings l&auml;uft die Bearbeitungszeit auch dann weiter.
                        </li>
                        <li>
                        Die Bearbeitung endet wenn Sie auf &quot;Abgeben&quot; klicken oder die Bearbeitungszeit
                        abl&auml;uft. Alle bis dahin
                        <b>
                          gespeicherten
                        </b>
                        Seiten flie&szlig;en in die Bewertung ein.
                      </li>

                    </ul>
                    <p>
                      <br>

                      <b>
                        Die Bewertung
                      </b>
                      :
                      <br>
                      <br>
                      Bewertet werden alle bis zum Bearbeitungsende abgespeicherten Seiten.
                      <ul>
                        <li>
                        Die maximal erreichbare Punktzahl ist bei jeder Aufgabenstellung angegeben.
                        <li>
                        Die minimale Punktzahl pro Aufgabe sind 0 Punkte.
                        <li>

                        Nicht bearbeitete Aufgaben werden mit 0 Punkten bewertet.
                      </ul>
                      <br/>
                    </fieldset>


                  </td>

                </tr>
              </table>
              <br/>
              <br/>
            </div>


          </div>

          <div id="footer" class="clearfix">
          </div>
          <jsp:include page="footer.jsp" />
        </body>
      </html>

