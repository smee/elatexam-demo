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

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import de.elatexam.PMF;
import de.elatexam.dao.TaskDefDao;
import de.elatexam.model.TaskDefVO;
import de.elatexam.util.Tools;

/**
 * Implementation for the google datastore.
 *
 * @author Steffen Dienst
 *
 */
public class TaskDefDaoGDS implements TaskDefDao {

  public void deleteTaskDef(String username, long id) {
    Tools.c().delete(getKey(id));

    System.out.println("deleting taskdef");
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
    Query query = pm.newQuery(TaskDefVO.class, String.format("username == '%s' && id == %d", username, id));
    query.setUnique(true);
      TaskDefVO td = (TaskDefVO) query.execute();
      if(td != null) {
        td.setVisible(false);
      }
    } finally {
      pm.close();
    }
  }

  public List<TaskDefVO> getTaskDefs(String username) {
    System.out.println("loading taskdefs");

    PersistenceManager pm = PMF.get().getPersistenceManager();
    Query query = pm.newQuery(TaskDefVO.class, String.format("username == '%s' && visible == true", username));
    return (List<TaskDefVO>) query.execute();
  }
  /* (non-Javadoc)
   * @see de.elatexam.dao.TaskDefDao#getTaskDef(long)
   */
  public TaskDefVO getTaskDef(long id) {
    TaskDefVO fromCache = (TaskDefVO) Tools.c().get(getKey(id));
    if (fromCache != null)
      return fromCache;

    System.out.println("loading taskdef");

    PersistenceManager pm = PMF.get().getPersistenceManager();
    Query query = pm.newQuery(TaskDefVO.class, String.format("id == %d && visible == true", id));
    query.setUnique(true);
    TaskDefVO res = (TaskDefVO) query.execute(id);

    // cache it
    if (res != null) {
      Tools.c().put(getKey(id), res);
    }

    return res;
  }

  private String getKey(long id) {
    return "taskdefvo:" + id;
  }

  /* (non-Javadoc)
   * @see de.elatexam.dao.TaskDefDao#storeTaskDef(de.elatexam.model.TaskDefVO)
   */
  public void storeTaskDef(TaskDefVO td) {
    Tools.c().put(getKey(td.getId()), td);

    System.out.println("storing taskdef");
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      pm.makePersistent(td);
    } finally {
      pm.close();
    }
  }

}
