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

/**
 * Implementation for the google datastore.
 *
 * @author Steffen Dienst
 *
 */
public class TaskDefDaoGDS implements TaskDefDao {

  public List<TaskDefVO> getTaskDefs(String username) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    Query query = pm.newQuery(TaskDefVO.class, "username == \"" + username + "\"");
    return (List<TaskDefVO>) query.execute();
  }
  /* (non-Javadoc)
   * @see de.elatexam.dao.TaskDefDao#getTaskDef(long)
   */
  public TaskDefVO getTaskDef(long id) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    Query query = pm.newQuery(TaskDefVO.class, "id == " + id);
    query.setUnique(true);
    return (TaskDefVO) query.execute(id);
  }

  /* (non-Javadoc)
   * @see de.elatexam.dao.TaskDefDao#storeTaskDef(de.elatexam.model.TaskDefVO)
   */
  public void storeTaskDef(TaskDefVO td) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      pm.makePersistent(td);
    } finally {
      pm.close();
    }
  }

}
