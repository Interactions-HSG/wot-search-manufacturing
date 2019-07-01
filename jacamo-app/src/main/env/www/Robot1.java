package www;

import cartago.Artifact;
import cartago.LINK;
import cartago.OPERATION;
import www.infra.Notification;

public class Robot1 extends Artifact {

  @OPERATION
  void init() {
  }
  
  @LINK
  void onNotification(Notification notification) {
    signal("event", notification.getMessage());
  }
}