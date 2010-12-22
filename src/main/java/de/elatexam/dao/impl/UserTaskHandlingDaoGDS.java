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
package de.elatexam.dao.impl;

import java.io.ByteArrayInputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;

import de.elatexam.dao.UserComplexTaskHandlingDAO;
import de.thorstenberger.taskmodel.complex.ComplexTaskFactory;
import de.thorstenberger.taskmodel.complex.ComplexTaskletCorrectorImpl;
import de.thorstenberger.taskmodel.complex.complextaskdef.ComplexTaskDefRoot;
import de.thorstenberger.taskmodel.complex.complextaskhandling.ComplexTaskHandlingDAO;
import de.thorstenberger.taskmodel.complex.complextaskhandling.ComplexTaskHandlingRoot;
import de.thorstenberger.taskmodel.complex.complextaskhandling.impl.ComplexTaskHandlingDAOImpl;
import de.thorstenberger.taskmodel.complex.complextaskhandling.impl.ComplexTaskHandlingRootImpl;
import de.thorstenberger.taskmodel.complex.impl.ComplexTaskFactoryImpl;
import de.thorstenberger.taskmodel.complex.jaxb.ObjectFactory;

/**
 * Implementation for the google datastore.
 *
 * @author Steffen Dienst
 *
 */
public class UserTaskHandlingDaoGDS implements UserComplexTaskHandlingDAO {

  private ComplexTaskHandlingDAO dao;

  /* (non-Javadoc)
   * @see de.elatexam.dao.UserComplexTaskHandlingDAO#load(java.lang.String, java.lang.String, de.thorstenberger.taskmodel.complex.complextaskdef.ComplexTaskDefRoot)
   */
  public ComplexTaskHandlingRoot load(byte[] xml, ComplexTaskDefRoot r) {
    if(xml == null)
      return new ComplexTaskHandlingRootImpl( r, getCTF(), new ObjectFactory().createComplexTaskHandling() );
    else
      return getDao().getComplexTaskHandlingRoot(new ByteArrayInputStream(xml), r);
  }
  /**
   * @return
   */
  ComplexTaskHandlingDAO getDao() {
    if (dao == null) {
      dao = new ComplexTaskHandlingDAOImpl(getCTF());
    }
    return dao;
  }

  private ComplexTaskFactory getCTF() {
    return new ComplexTaskFactoryImpl(new ComplexTaskletCorrectorImpl());
  }
  /* (non-Javadoc)
   * @see de.elatexam.dao.UserComplexTaskHandlingDAO#save(java.lang.String, java.lang.String, de.thorstenberger.taskmodel.complex.complextaskhandling.ComplexTaskHandlingRoot)
   */
  public byte[] save(ComplexTaskHandlingRoot complexTaskHandlingRoot) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    getDao().save(complexTaskHandlingRoot, baos);
    return baos.toByteArray();
  }

}
