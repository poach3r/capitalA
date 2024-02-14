package main;

import org.javacord.api.event.message.MessageCreateEvent;
import java.time.Duration;

public class filter {
  public static void mainFunc(MessageCreateEvent event, String args[], settings serverSettings) {
    for(String word : serverSettings.getFilter()) {
      if(event.getMessageContent().contains(word)) {
	System.out.println("muted user " + event.getMessageAuthor().getIdAsString() + " for saying " + word + " in server " + event.getServer().get().getIdAsString());
	event.getMessage().delete();
	event.getServer().get().timeoutUser(event.getServer().get().getMemberById(event.getMessageAuthor().getId()).get(), Duration.ofDays(1));
	event.getChannel().sendMessage(event.getServer().get().getMemberById(event.getMessageAuthor().getId()).get().getMentionTag() + " has been muted for 1 day for saying a banned word.");
      }
    }
  }

  // HACK initialize filter[]
  public static String[] initFilter() {
    String f[] = new String[50];
    for(int i = 0; i < f.length; i++) {
      f[i] = "this is a hack!!!!!!!!!";
    }
    return f;
  }
}

