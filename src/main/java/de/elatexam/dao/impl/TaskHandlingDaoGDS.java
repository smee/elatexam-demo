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

import java.util.Date;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import de.elatexam.PMF;
import de.elatexam.dao.TaskHandlingDao;
import de.elatexam.model.TaskletVO;

/**
 * Implementation for the google datastore.
 *
 * @author Steffen Dienst
 *
 */
public class TaskHandlingDaoGDS implements TaskHandlingDao {

  /*
   * (non-Javadoc)
   *
   * @see de.elatexam.dao.TaskHandlingDao#getTasklet(long, java.lang.String)
   */
  public TaskletVO getTasklet(long taskId, String sessionId) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      Query query = pm.newQuery(TaskletVO.class, String.format("taskDefId == %d && sessionId == '%s'", taskId, sessionId));
      query.setUnique(true);
      return (TaskletVO) query.execute(taskId, sessionId);
    } finally {
      pm.close();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see de.elatexam.dao.TaskHandlingDao#saveTasklet(de.elatexam.model.TaskletVO)
   */
  public void saveTasklet(TaskletVO taskletVO) {
    // is this object really new or is it already attached to the store?
    if (!JDOHelper.isPersistent(taskletVO)) {
      taskletVO.setLastStored(new Date());
      // new object, persist it
      PersistenceManager pm = PMF.get().getPersistenceManager();
      try {
        pm.makePersistent(taskletVO);
      } finally {
        pm.close();
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see de.elatexam.dao.TaskHandlingDao#removeTasklet(de.elatexam.model.TaskletVO)
   */
  public void removeTasklet(long taskId, String sessionId) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      Query query = pm.newQuery(TaskletVO.class, String.format("taskDefId == %d && sessionId == '%s'", taskId, sessionId));
      query.deletePersistentAll();
    } finally {
      pm.close();
    }

  }
}
