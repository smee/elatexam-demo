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
package de.thorstenberger.taskmodel;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

import de.elatexam.dao.DataStoreTaskFactory;

/**
 * Patched class to redirect to google services instead of local map.
 *
 * @author Steffen Dienst
 *
 */
public class TaskModelViewDelegate {

  public static DelegateObject getDelegateObject(final String sessionId, final long taskId) {
      return new TaskModelViewDelegateObject(){

        public TaskDef getTaskDef() throws TaskApiException {
          return DataStoreTaskFactory.getInstance().getTaskDef(taskId);
        }

        public long getTaskId() {
          return 0;
        }

        public String getReturnURL() {
          return "http://www.elatexam.de";
        }

        public String getUserName() {
          User user = UserServiceFactory.getUserService().getCurrentUser();
          if(user !=null)
            return user.getNickname();
          else return "Demouser";
        }

        public Tasklet getTasklet() throws TaskApiException {
          Tasklet tasklet = DataStoreTaskFactory.getInstance().getTasklet(sessionId, taskId);
          if (tasklet != null)
          return tasklet;
          else
          return DataStoreTaskFactory.getInstance().createTasklet(getUserName(), sessionId, taskId);
        }

        public long getRandomSeed() {
          return System.nanoTime();
        }

        public String getLogin() {
          return getUserName();
        }

		  };
	}

  public static void removeSession(String sessionId) {
  }

  /**
   * @param sessionId
   * @param taskDefCacheId
   */
  public static void startPreview(String sessionId, long taskDefHandle) {
    // XXX better use session parameters?
    // MemcacheService mcs = c();
    // Map m = new HashMap();
    // m.put("taskdefcacheid", taskDefCacheId);
    // mcs.put(sessionId, m);
    // log.fine("using session " + sessionId + " and taskDefCacheId " + taskDefCacheId);
  }


}
