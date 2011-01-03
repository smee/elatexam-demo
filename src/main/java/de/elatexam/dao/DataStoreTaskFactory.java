/*

Copyright (C) 2006 Thorsten Berger
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
/**
 *
 */
package de.elatexam.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

import de.elatexam.dao.impl.TaskDefDaoGDS;
import de.elatexam.dao.impl.TaskHandlingDaoGDS;
import de.elatexam.dao.impl.UserTaskHandlingDaoGDS;
import de.elatexam.model.CorrectorTaskletAnnotationVO;
import de.elatexam.model.StudentTaskletAnnotationVO;
import de.elatexam.model.TaskDefVO;
import de.elatexam.model.TaskletVO;
import de.elatexam.model.TaskletVO.ManualCorrectionsVO;
import de.elatexam.util.Tools;
import de.thorstenberger.taskmodel.CategoryFilter;
import de.thorstenberger.taskmodel.CorrectorAnnotation;
import de.thorstenberger.taskmodel.CorrectorAnnotationImpl;
import de.thorstenberger.taskmodel.ManualCorrection;
import de.thorstenberger.taskmodel.MethodNotSupportedException;
import de.thorstenberger.taskmodel.SessionAwareComplexTasklet;
import de.thorstenberger.taskmodel.StudentAnnotation;
import de.thorstenberger.taskmodel.TaskApiException;
import de.thorstenberger.taskmodel.TaskCategory;
import de.thorstenberger.taskmodel.TaskContants;
import de.thorstenberger.taskmodel.TaskDef;
import de.thorstenberger.taskmodel.TaskFactory;
import de.thorstenberger.taskmodel.TaskFilter;
import de.thorstenberger.taskmodel.TaskFilterException;
import de.thorstenberger.taskmodel.TaskManager.UserAttribute;
import de.thorstenberger.taskmodel.Tasklet;
import de.thorstenberger.taskmodel.Tasklet.Status;
import de.thorstenberger.taskmodel.TaskletCorrection;
import de.thorstenberger.taskmodel.TaskmodelUtil;
import de.thorstenberger.taskmodel.UserInfo;
import de.thorstenberger.taskmodel.complex.ComplexTasklet;
import de.thorstenberger.taskmodel.complex.ComplexTaskletCorrectorImpl;
import de.thorstenberger.taskmodel.complex.TaskDef_Complex;
import de.thorstenberger.taskmodel.complex.TaskDef_ComplexImpl;
import de.thorstenberger.taskmodel.complex.complextaskdef.impl.ComplexTaskDefRootImpl;
import de.thorstenberger.taskmodel.complex.impl.ComplexTaskBuilderImpl;
import de.thorstenberger.taskmodel.complex.impl.ComplexTaskFactoryImpl;
import de.thorstenberger.taskmodel.complex.jaxb.ComplexTaskDef;
import de.thorstenberger.taskmodel.impl.AbstractTaskFactory;
import de.thorstenberger.taskmodel.impl.ManualCorrectionImpl;
import de.thorstenberger.taskmodel.impl.StudentAnnotationImpl;
import de.thorstenberger.taskmodel.impl.TaskletCorrectionImpl;
import de.thorstenberger.taskmodel.impl.UserInfoImpl;

/**
 * This implementation deletes tasklets as soon as they get submitted. It should be used for previewing purposes only!
 *
 * @author Thorsten Berger
 * @author Steffen Dienst
 */
public class DataStoreTaskFactory extends AbstractTaskFactory implements TaskFactory {
  private static final Logger log = Logger.getLogger("TaskLogger");

  private static DataStoreTaskFactory instance;

  private final TaskDefDao taskDefDao;
  private final TaskHandlingDao taskHandlingDao;
  private final UserComplexTaskHandlingDAO complexTaskHandlingDAO;

  private DataStoreTaskFactory() {
    this(new TaskDefDaoGDS(), new TaskHandlingDaoGDS(), new UserTaskHandlingDaoGDS());
  }

  public static DataStoreTaskFactory getInstance() {
    if (instance == null) {
      instance = new DataStoreTaskFactory();
    }
    return instance;
  }
  /**
     *
     */
  public DataStoreTaskFactory(TaskDefDao taskDefDao, TaskHandlingDao taskHandlingDao,
            UserComplexTaskHandlingDAO complexTaskHandlingDAO) {

    this.taskDefDao = taskDefDao;
    this.taskHandlingDao = taskHandlingDao;
    this.complexTaskHandlingDAO = complexTaskHandlingDAO;

  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#addTaskCategory(java.lang.String, java.lang.String)
   */
  public TaskCategory addTaskCategory(final String name, final String description) {
    throw new MethodNotSupportedException();
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#availableTypes()
   */
  public List<String> availableTypes() {
    return Arrays.asList(TaskContants.TYPE_COMPLEX);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#availableUserAttributeKeys()
   */
  public List<UserAttribute> availableUserAttributes() {
    throw new MethodNotSupportedException();
  }

  private List<CorrectorTaskletAnnotationVO> copyCorrectorAnnotations(final List<CorrectorAnnotation> annotations) {
    final List<CorrectorTaskletAnnotationVO> ret = new LinkedList<CorrectorTaskletAnnotationVO>();
    for (final CorrectorAnnotation a : annotations) {
      ret.add(new CorrectorTaskletAnnotationVO(a.getCorrector(), a.getText()));
    }
    return ret;
  }

  private List<ManualCorrectionsVO> copyManualCorrections(final TaskletVO taskletVO, final List<ManualCorrection> manualCorrections) {
    final List<ManualCorrectionsVO> ret = new LinkedList<ManualCorrectionsVO>();
    for (final ManualCorrection mc : manualCorrections) {
      ret.add(taskletVO.new ManualCorrectionsVO(mc.getCorrector(), mc.getPoints()));
    }
    return ret;
  }

  private List<StudentTaskletAnnotationVO> copyStudentAnnotations(final List<StudentAnnotation> annotations) {
    final List<StudentTaskletAnnotationVO> ret = new LinkedList<StudentTaskletAnnotationVO>();
    for (final StudentAnnotation a : annotations) {
      ret.add(new StudentTaskletAnnotationVO(a.getText(), a.getDate(), a.isAcknowledged()));
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#createTasklet(java.lang.String, long)
   */
  public Tasklet createTasklet(final String userId, final long taskId) {
    throw new UnsupportedOperationException();
  }

  public Tasklet createTasklet(final String userId, String sessionId, final long taskId)
      throws TaskApiException {

    TaskletVO taskletVO = taskHandlingDao.getTasklet(taskId, sessionId);
    final TaskDefVO taskDefVO = taskDefDao.getTaskDef(taskId);

    if (taskDefVO == null)
      throw new TaskApiException("TaskDef " + taskId + " does not exist!");

    if (taskletVO != null)
      throw new TaskApiException("Tasklet (" + userId + ", " + taskId + ") does already exist!");

    taskletVO = new TaskletVO();
    taskletVO.setLogin(userId);
    taskletVO.setSessionId(sessionId);
    taskletVO.setTaskDefId(taskId);
    taskletVO.setStatus(Tasklet.Status.INITIALIZED.getValue());
    taskletVO.setAutoCorrectionPoints(null);
    taskletVO.setFlags(new LinkedList<String>());
    taskletVO.setStudentAnnotations(new LinkedList<StudentTaskletAnnotationVO>());
    taskletVO.setCorrectorAnnotations(new LinkedList<CorrectorTaskletAnnotationVO>());
    taskletVO.setManualCorrections(new LinkedList<ManualCorrectionsVO>());

    taskHandlingDao.saveTasklet(taskletVO);

    return instantiateTasklet(taskletVO, taskDefVO);

  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#deleteTaskCategory(long)
   */
  public void deleteTaskCategory(final long id) throws MethodNotSupportedException {
    throw new MethodNotSupportedException();
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#deleteTaskDef(long)
   */
  public void deleteTaskDef(final long id) throws MethodNotSupportedException {
    final TaskDefVO tdvo = taskDefDao.getTaskDef(id);
    if (tdvo != null) {
      tdvo.setVisible(false); // make invisible rather than delete physically
      taskDefDao.storeTaskDef(tdvo);
      MemcacheService cache = Tools.c();

      if (cache.contains(id)) {
        cache.delete(id);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#getCategories()
   */
  public List<TaskCategory> getCategories() {
    throw new MethodNotSupportedException();
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#getCategories(de.thorstenberger.taskmodel.CategoryFilter)
   */
  public List<TaskCategory> getCategories(final CategoryFilter categoryFilter) {
    throw new MethodNotSupportedException();
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#getCategory(long)
   */
  public TaskCategory getCategory(final long id) {
    throw new MethodNotSupportedException();
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#getCorrectors()
   */
  public List<UserInfo> getCorrectors() {
    throw new MethodNotSupportedException();
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#getTaskDef(long)
   */
  public TaskDef getTaskDef(final long taskId) {
      final TaskDefVO taskDefVO = taskDefDao.getTaskDef(taskId);
      if (taskDefVO != null) {
        TaskDef td = deserializeTaskDef(taskDefVO.getXml(), taskId);
        return td;
      } else
        return null;
  }

  public void deleteTaskDef(String username, long taskId) {
    taskDefDao.deleteTaskDef(username, taskId);
  }

  /**
   * @param username
   * @return
   */
  public List<TaskDefVO> getTaskDefsOf(String username) {
    return taskDefDao.getTaskDefs(username);
  }

  /**
   * @param stream
   * @return
   */
  private TaskDef_Complex deserializeTaskDef(byte[] xml, long taskDefId) {
    MemcacheService cache = Tools.c();
    ComplexTaskDef ctd = null;

    if (cache.contains(taskDefId)) {
      ctd = (ComplexTaskDef) cache.get(taskDefId);
    } else {
      log.finer("Trying to load xml");
      try {
        // deserialize the xml
        Unmarshaller unmarshaller;
        unmarshaller = JAXBContext.newInstance(ComplexTaskDef.class).createUnmarshaller();
        ctd = (ComplexTaskDef) unmarshaller.unmarshal(new ByteArrayInputStream(xml));
        cache.put(taskDefId, ctd);
      } catch (final JAXBException e) {
        throw new RuntimeException(e);
      }
    }
    return new TaskDef_ComplexImpl(taskDefId, "", "", null, false, 0l, true, true, new ComplexTaskDefRootImpl(ctd, new ComplexTaskFactoryImpl(new ComplexTaskletCorrectorImpl())));
  }
  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#getTaskDefs()
   */
  public synchronized List<TaskDef> getTaskDefs() {
    throw new MethodNotSupportedException();
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#getTaskDefs(de.thorstenberger.taskmodel.TaskFilter)
   */
  public List<TaskDef> getTaskDefs(final TaskFilter filter)
      throws TaskFilterException {
    throw new MethodNotSupportedException();
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#getTasklet(java.lang.String, long)
   */
  public Tasklet getTasklet(final String sessionId, final long taskId) {

    final TaskletVO taskletVO = taskHandlingDao.getTasklet(taskId, sessionId);
    if (taskletVO == null)
      return null;

    final TaskDefVO taskDefVO = taskDefDao.getTaskDef(taskId);
    if (taskDefVO == null)
      throw new RuntimeException("No corresponding taskDef found: " + taskId);

    return instantiateTasklet(taskletVO, taskDefVO);

  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#getTasklets(long)
   */
  public List<Tasklet> getTasklets(final long taskId) {
    return getTasklets(taskId, null);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#getTasklets(long, de.thorstenberger.taskmodel.Tasklet.Status)
   */
  public List<Tasklet> getTasklets(final long taskId, final Tasklet.Status status) {
    throw new MethodNotSupportedException();
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.impl.AbstractTaskFactory#getUserIdsOfAvailableTasklets(long)
   */
  @Override
  public List<String> getUserIdsOfAvailableTasklets(final long taskId) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.impl.AbstractTaskFactory#getUserIdsOfTaskletsAssignedToCorrector(long,
   * java.lang.String, boolean)
   */
  @Override
  public List<String> getUserIdsOfTaskletsAssignedToCorrector(final long taskId, final String correctorId) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#getUserInfo(java.lang.String)
   */
  public UserInfo getUserInfo(final String login) {

    User user = UserServiceFactory.getUserService().getCurrentUser();

    final UserInfoImpl ret = new UserInfoImpl();
    ret.setLogin(user.getUserId());
    ret.setFirstName("");
    ret.setName(user.getNickname());
    ret.setEMail(user.getEmail());

    return ret;

  }

  private Tasklet instantiateTasklet(final TaskletVO taskletVO, final TaskDefVO taskDefVO) {

    // corrector annotations
    final List<CorrectorAnnotation> cas = new LinkedList<CorrectorAnnotation>();
    final List<CorrectorTaskletAnnotationVO> ctavos = taskletVO.getCorrectorAnnotations();
    if (ctavos != null && ctavos.size() > 0) {
      for (final CorrectorTaskletAnnotationVO ctavo : ctavos) {
        cas.add(new CorrectorAnnotationImpl(ctavo.getCorrector(), ctavo.getText()));
      }
    }
    // student annotations
    final List<StudentAnnotation> studentAnnotations = new ArrayList<StudentAnnotation>();
    for (final StudentTaskletAnnotationVO tavo : taskletVO.getStudentAnnotations()) {
      studentAnnotations.add(new StudentAnnotationImpl(tavo.getText(), tavo.getDate(), tavo.isAcknowledged()));
    }

    // manual corrections
    final List<ManualCorrection> mcs = new LinkedList<ManualCorrection>();
    final List<ManualCorrectionsVO> mcvos = taskletVO.getManualCorrections();
    if (mcvos != null && mcvos.size() > 0) {
      for (final ManualCorrectionsVO mcvo : mcvos) {
        mcs.add(new ManualCorrectionImpl(mcvo.getCorrector(), mcvo.getPoints()));
      }
    }

    final TaskletCorrection correction =
            new TaskletCorrectionImpl(taskletVO.getAutoCorrectionPoints(), cas,
                    taskletVO.getCorrectorLogin(), taskletVO.getCorrectorHistory(), studentAnnotations, mcs);

    final TaskDef_Complex complexTaskDef = (TaskDef_Complex) getTaskDef(taskDefVO.getId());
    ComplexTaskBuilderImpl builder = new ComplexTaskBuilderImpl(new ComplexTaskFactoryImpl(new ComplexTaskletCorrectorImpl()));
    SessionAwareComplexTasklet sact = new SessionAwareComplexTasklet(
                    this,
                    builder,
                    taskletVO.getLogin(),
                    complexTaskDef,
                    complexTaskHandlingDAO.load(taskletVO.getXml(), complexTaskDef.getComplexTaskDefRoot()),
                    TaskmodelUtil.getStatus(taskletVO.getStatus()),
                    taskletVO.getFlags(),
                    correction,
                    new HashMap<String, String>());
    sact.setSessionId(taskletVO.getSessionId());
    return sact;


  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#logPostData(java.lang.String, de.thorstenberger.taskmodel.Tasklet,
   * java.lang.String)
   */
  public void logPostData(final String msg, final Tasklet tasklet, final String ip) {
    final String prefix = tasklet.getUserId() + "@" + ip + ": ";
    log.info(prefix + msg);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#logPostData(java.lang.String, java.lang.Throwable,
   * de.thorstenberger.taskmodel.Tasklet, java.lang.String)
   */
  public void logPostData(final String msg, final Throwable throwable, final Tasklet tasklet, final String ip) {
    final String prefix = tasklet.getUserId() + "@" + ip + ": ";
    log.log(Level.SEVERE, prefix + msg, throwable);
  }

  private boolean objectsDiffer(final Object a, final Object b) {
    if (a == null && b == null)
      return false;
    if (a == null && b != null)
      return true;
    if (b == null && a != null)
      return true;

    return !a.equals(b);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#removeTasklet(java.lang.String, long)
   */
  public void removeTasklet(final String userId, final long taskId)
      throws TaskApiException {
    throw new MethodNotSupportedException();
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#storeTaskCategory(de.thorstenberger.taskmodel.TaskCategory)
   */
  public void storeTaskCategory(final TaskCategory category) {
    throw new MethodNotSupportedException();
  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#storeTaskDef(de.thorstenberger.taskmodel.TaskDef, long)
   */
  synchronized public void storeTaskDef(final TaskDef taskDef, final long handle) throws TaskApiException {
    throw new UnsupportedOperationException();
  }

  public void storeTaskDef(InputStream stream, long handle, User user) {
    TaskDefVO tdvo = taskDefDao.getTaskDef(handle);
    if (tdvo == null) {
      tdvo = new TaskDefVO();
      tdvo.setId(handle);
      tdvo.setVisible(true);
      tdvo.setUsername(user.getNickname());
      tdvo.setVisible(true);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        IOUtils.copy(stream, baos);
        tdvo.setXml(baos.toByteArray());
        taskDefDao.storeTaskDef(tdvo);
      } catch (IOException e) {
        log.log(Level.SEVERE, "Could not copy from stream.", e);
      }
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see de.thorstenberger.taskmodel.TaskFactory#storeTasklet(de.thorstenberger.taskmodel.Tasklet)
   */
  public void storeTasklet(final Tasklet tasklet) throws TaskApiException {

    if (!(tasklet instanceof SessionAwareComplexTasklet))
      throw new IllegalStateException("Should be an instance of SessionAwareComplexTasklet!");

    TaskletVO taskletVO = taskHandlingDao.getTasklet(tasklet.getTaskId(), ((SessionAwareComplexTasklet) tasklet).getSessionId());

    onStoreTasklet(tasklet, taskletVO);

    // remove solved tasklets, we do not support correction right now
    if (tasklet.hasOrPassedStatus(Status.SOLVED)) {
      taskHandlingDao.removeTasklet(taskletVO);
      return;
    }

    if (taskletVO.getTaskDefId() != tasklet.getTaskId()) {
      taskletVO.setTaskDefId(tasklet.getTaskId());
    }

    if (objectsDiffer(taskletVO.getLogin(), tasklet.getUserId())) {
      taskletVO.setLogin(tasklet.getUserId());
    }

    if (objectsDiffer(taskletVO.getStatus(), tasklet.getStatus().getValue())) {
      taskletVO.setStatus(tasklet.getStatus().getValue());
    }

    if (objectsDiffer(taskletVO.getCorrectorLogin(), tasklet.getTaskletCorrection().getCorrector())) {
      taskletVO.setCorrectorLogin(tasklet.getTaskletCorrection().getCorrector());
    }

    if (objectsDiffer(taskletVO.getAutoCorrectionPoints(), tasklet.getTaskletCorrection().getAutoCorrectionPoints())) {
      taskletVO.setAutoCorrectionPoints(tasklet.getTaskletCorrection().getAutoCorrectionPoints());
    }

    if (objectsDiffer(taskletVO.getCorrectorHistory(), tasklet.getTaskletCorrection().getCorrectorHistory())) {
      taskletVO.setCorrectorHistory(tasklet.getTaskletCorrection().getCorrectorHistory());
    }

    if (objectsDiffer(taskletVO.getFlags(), tasklet.getFlags())) {
      taskletVO.setFlags(tasklet.getFlags());
    }

    // student annotations
    if (taskletVO.getStudentAnnotations().size() != tasklet.getTaskletCorrection().getStudentAnnotations().size()) {
      taskletVO.setStudentAnnotations(copyStudentAnnotations(tasklet.getTaskletCorrection().getStudentAnnotations()));
    } else {
      for (int i = 0; i < tasklet.getTaskletCorrection().getStudentAnnotations().size(); i++) {
        final StudentAnnotation a = tasklet.getTaskletCorrection().getStudentAnnotations().get(i);
        final StudentTaskletAnnotationVO tavo = taskletVO.getStudentAnnotations().get(i);
        if (objectsDiffer(a.getText(), tavo.getText()) || objectsDiffer(a.getDate(), tavo.getDate())
                        || objectsDiffer(a.isAcknowledged(), tavo.isAcknowledged())) {
          taskletVO.setStudentAnnotations(copyStudentAnnotations(tasklet.getTaskletCorrection().getStudentAnnotations()));
          break;
        }
      }
    }

    // corrector annotations
    if (taskletVO.getCorrectorAnnotations().size() != tasklet.getTaskletCorrection().getCorrectorAnnotations().size()) {
      taskletVO.setCorrectorAnnotations(copyCorrectorAnnotations(tasklet.getTaskletCorrection().getCorrectorAnnotations()));
    } else {
      for (int i = 0; i < tasklet.getTaskletCorrection().getCorrectorAnnotations().size(); i++) {
        final CorrectorAnnotation a = tasklet.getTaskletCorrection().getCorrectorAnnotations().get(i);
        final CorrectorTaskletAnnotationVO tavo = taskletVO.getCorrectorAnnotations().get(i);
        if (objectsDiffer(a.getText(), tavo.getText()) || objectsDiffer(a.getCorrector(), tavo.getCorrector())) {
          taskletVO.setCorrectorAnnotations(copyCorrectorAnnotations(tasklet.getTaskletCorrection().getCorrectorAnnotations()));
          break;
        }
      }
    }

    // manual corrections
    if (taskletVO.getManualCorrections().size() != tasklet.getTaskletCorrection().getManualCorrections().size()) {
      taskletVO.setManualCorrections(copyManualCorrections(taskletVO, tasklet.getTaskletCorrection().getManualCorrections()));
    } else {
      for (int i = 0; i < tasklet.getTaskletCorrection().getManualCorrections().size(); i++) {
        final ManualCorrection m = tasklet.getTaskletCorrection().getManualCorrections().get(i);
        final ManualCorrectionsVO mcvo = taskletVO.getManualCorrections().get(i);
        if (objectsDiffer(m.getCorrector(), mcvo.getCorrector()) || objectsDiffer(m.getCorrector(), m.getPoints())) {
          taskletVO.setManualCorrections(copyManualCorrections(taskletVO, tasklet.getTaskletCorrection().getManualCorrections()));
          break;
        }
      }
    }

    if (tasklet instanceof ComplexTasklet) {
      final ComplexTasklet ct = (ComplexTasklet) tasklet;
      taskletVO.setXml(complexTaskHandlingDAO.save(ct.getComplexTaskHandlingRoot()));
    }

    taskHandlingDao.saveTasklet(taskletVO);

  }

  /**
   * Hook for subclasses to react on calls to {@link #storeTasklet(Tasklet)}.
   *
   * @param tasklet
   *          tasklet to store
   * @param taskletVO
   *          old value object (unmodified)
   */
  protected void onStoreTasklet(final Tasklet tasklet, final TaskletVO taskletVO) {

  }

}
