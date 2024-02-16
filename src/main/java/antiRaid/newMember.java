// When a member joins the server, we greet them in dms and then after a specified amount of time give them the voter role

package antiRaid;

import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.javacord.api.entity.user.User;

public class newMember {

  private ScheduledExecutorService executorService;
  private ServerMemberJoinEvent event;
  private main.settings serverSettings;
  private User user;

  public newMember(ServerMemberJoinEvent e, main.settings s) {
    executorService = Executors.newSingleThreadScheduledExecutor();
    event = e;
    serverSettings = s;
    user = e.getUser();
  }

  public User getUser() {
    return user;
  }

  public void mainFunc() {
    user.sendMessage(serverSettings.getJoinMessage());

    executorService.schedule(() -> {
      user.sendMessage("You are now eligible to vote in " + event.getServer().getName() + "!");
      user.addRole(event.getServer().getRolesByName("Voter").get(0));
    }, serverSettings.getJoinTime(), TimeUnit.HOURS); 
  }
}
