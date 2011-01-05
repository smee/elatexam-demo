/*

Copyright (C) 2010 Steffen Dienst

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package de.elatexam;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.elatexam.dao.DataStoreTaskFactory;
import de.thorstenberger.taskmodel.TaskDef;

/**
 * @author Steffen Dienst
 *
 */
public class PreviewServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    long taskdefHandle = Long.parseLong(req.getParameter("id"));

    TaskDef data = DataStoreTaskFactory.getInstance().getTaskDef(taskdefHandle);
      if (data == null) {
        resp.setContentType("text/plain");
        resp.getWriter().printf("Unknown id.");
      } else {

        resp.sendRedirect("/preview.jsp?id="+taskdefHandle);
      }
  }

}
