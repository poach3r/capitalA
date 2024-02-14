package commands;

import org.javacord.api.event.message.MessageCreateEvent;

public class help {
  public static void mainFunc(MessageCreateEvent event) {
    event.getChannel().sendMessage("https://github.com/poach3r/capitalA");
  }
}

