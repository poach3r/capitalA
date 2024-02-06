// Returns "Pong"

package commands.fun;

import org.javacord.api.event.message.MessageCreateEvent;

public class ping {
  public static void mainFunc(MessageCreateEvent event) {
    event.getMessage().reply("Pong!");
  }
}
