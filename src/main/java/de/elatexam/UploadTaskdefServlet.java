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
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import de.elatexam.dao.DataStoreTaskFactory;

/**
 * @author Steffen Dienst
 *
 */
public class UploadTaskdefServlet extends HttpServlet {
  public static final Logger log = Logger.getLogger(UploadTaskdefServlet.class.getName());
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      resp.sendRedirect("/");
    } else {
      try {
        ServletFileUpload upload = new ServletFileUpload();

        FileItemIterator iterator = upload.getItemIterator(req);
        while (iterator.hasNext()) {
          FileItemStream item = iterator.next();

          long handle = System.nanoTime();
          DataStoreTaskFactory.getInstance().storeTaskDef(item.openStream(), handle, userService.getCurrentUser());
          }
      } catch (Exception ex) {
        throw new ServletException(ex);
      }
      resp.sendRedirect("/");
    }

  }


}
