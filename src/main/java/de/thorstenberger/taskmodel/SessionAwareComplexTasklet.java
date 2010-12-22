package de.thorstenberger.taskmodel;

import java.util.List;
import java.util.Map;

import de.thorstenberger.taskmodel.complex.ComplexTaskBuilder;
import de.thorstenberger.taskmodel.complex.ComplexTaskletImpl;
import de.thorstenberger.taskmodel.complex.TaskDef_Complex;
import de.thorstenberger.taskmodel.complex.complextaskhandling.ComplexTaskHandlingRoot;

public class SessionAwareComplexTasklet extends ComplexTaskletImpl {

  private String sessionId;

  public SessionAwareComplexTasklet(TaskFactory taskFactory, ComplexTaskBuilder complexTaskBuilder, String userId,
      TaskDef_Complex complexTaskDef, ComplexTaskHandlingRoot complexTaskHandlingRoot, Status status, List<String> flags,
      TaskletCorrection taskletCorrection, Map<String, String> properties) {
    super(taskFactory, complexTaskBuilder, userId, complexTaskDef, complexTaskHandlingRoot, status, flags, taskletCorrection, properties);
  }

  public String getSessionId() {
    return this.sessionId;
  }

  public void setSessionId(String id) {
    this.sessionId = id;
  }
}
