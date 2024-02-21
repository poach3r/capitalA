package commands;

import org.javacord.api.event.message.MessageCreateEvent;

public class parseCommands {
  public static void mainFunc(MessageCreateEvent event, String args[], main.settings serverSettings) {
    if(args[0].equals(serverSettings.getPrefix() + "ping")) {
      System.out.println("ping");
      event.getChannel().sendMessage("pong");
    }

    else if(args[0].equals(serverSettings.getPrefix() + "help"))
      commands.help.mainFunc(event);

    else if(args[0].equals(serverSettings.getPrefix() + "boot")) {
      commands.moderation.boot bt = new commands.moderation.boot(event, args, serverSettings);
      bt.mainFunc();
    }

    else if(args[0].equals(serverSettings.getPrefix() + "set")) {
      commands.moderation.set se = new commands.moderation.set(event, args, serverSettings); 
      se.mainFunc();
    }

    else if(args[0].charAt(0) == '$') {
      plugins.method1.onParse(event);
    }
  }
}
