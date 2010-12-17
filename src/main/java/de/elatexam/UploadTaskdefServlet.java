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
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import de.thorstenberger.taskmodel.complex.jaxb.ComplexTaskDef;

/**
 * @author Steffen Dienst
 *
 */
public class UploadTaskdefServlet extends HttpServlet {
  private static final Logger log = Logger.getLogger(UploadTaskdefServlet.class.getName());
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      ServletFileUpload upload = new ServletFileUpload();
      resp.setContentType("text/html");

      FileItemIterator iterator = upload.getItemIterator(req);
      while (iterator.hasNext()) {
        FileItemStream item = iterator.next();
        InputStream stream = item.openStream();

        ComplexTaskDef taskdef = deserializeTaskDef(stream);

        MemcacheService mcs = MemcacheServiceFactory.getMemcacheService();
        String handle = generateHandle(item.getName());
        mcs.put(handle, taskdef);

        resp.getWriter().printf("<a href=\"/preview?id=%s\">Link to file</a>", handle);
      }
    } catch (Exception ex) {
      throw new ServletException(ex);
    }

  }

  private ComplexTaskDef deserializeTaskDef(InputStream stream) {
    log.finer("Trying to load xml");
    try {
      // deserialize the xml
      Unmarshaller unmarshaller;
      unmarshaller = JAXBContext.newInstance(ComplexTaskDef.class).createUnmarshaller();
      final ComplexTaskDef obj = (ComplexTaskDef) unmarshaller.unmarshal(stream);
      stream.close();
      return obj;

    } catch (final JAXBException e) {
      throw new RuntimeException(e);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String generateHandle(String name) {
    return md5(name + System.nanoTime());
  }

  private String md5(String input) {
    MessageDigest md5;
    try {
      md5 = MessageDigest.getInstance("MD5");
      md5.reset();
      md5.update(input.getBytes());
      byte[] result = md5.digest();

      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < result.length; i++) {
        hexString.append(Integer.toHexString(0xFF & result[i]));
      }
      return hexString.toString();

    } catch (NoSuchAlgorithmException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
