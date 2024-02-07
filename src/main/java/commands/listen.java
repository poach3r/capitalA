package commands;

import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.event.message.MessageCreateEvent;
import java.time.Duration;

public class listen implements MessageCreateListener {

  @Override
  public void onMessageCreate(MessageCreateEvent event) {
    String args[] = event.getMessageContent().split(" "); // split message into individual arguments
    if(event.getMessageAuthor().isBotUser()) return; // filter out bot accounts
    
    if(args[0].equals(main.settings.getPrefix() + "ping")) {
      System.out.println("ping");
      commands.fun.ping.mainFunc(event);
    }

    else if(args[0].equals(main.settings.getPrefix() + "help"))
      commands.help.mainFunc(event);

    else if(args[0].equals(main.settings.getPrefix() + "voteKick")) {
      commands.moderation.voteKick vk = new commands.moderation.voteKick(event, args[1]);
      vk.mainFunc();
    }

    else if(args[0].equals(main.settings.getPrefix() + "voteBan")) {
      commands.moderation.voteBan vb = new commands.moderation.voteBan(event, args[1], args[2]);
      vb.mainFunc();
    }


    else if(args[0].equals(main.settings.getPrefix() + "voteMute")) {
      commands.moderation.voteMute vm = new commands.moderation.voteMute(event, args);
      vm.mainFunc();
    }

    else if(args[0].equals(main.settings.getPrefix() + "addFilter")) {
      commands.moderation.addFilter af = new commands.moderation.addFilter(event, args[1]);
      af.mainFunc();
    }

    else if(args[0].equals(main.settings.getPrefix() + "cat")) {
      commands.fun.cat.mainFunc(event);
    }

    else if(args[0].equals(main.settings.getPrefix() + "llm")) {
      if(main.settings.getHuggingFaceKey() != "")
	commands.fun.llm.mainFunc(event, args);
      else
	event.getMessage().reply("This hoster has not setup a huggingface.co access key.");
    }

    // filter out naughty words
    for(String word : main.filter.server(event)) {
      if(event.getMessageContent().contains(word)) {
	System.out.println("muted user " + event.getMessageAuthor().getIdAsString() + " for saying " + word + " in server " + event.getServer().get().getIdAsString());
	event.getMessage().delete();
	event.getServer().get().timeoutUser(event.getServer().get().getMemberById(event.getMessageAuthor().getId()).get(), Duration.ofDays(1));
	event.getChannel().sendMessage(event.getServer().get().getMemberById(event.getMessageAuthor().getId()).get().getMentionTag() + " has been muted for 1 day for saying a banned word.");
	return;
      }
    }

    for(String word : main.settings.getFilter()) {
      if(event.getMessageContent().contains(word)) {
	System.out.println("muted user " + event.getMessageAuthor().getIdAsString() + " for saying " + word + " in server " + event.getServer().get().getIdAsString());
	event.getMessage().delete();
	event.getServer().get().timeoutUser(event.getServer().get().getMemberById(event.getMessageAuthor().getId()).get(), Duration.ofDays(1));
	event.getChannel().sendMessage(event.getServer().get().getMemberById(event.getMessageAuthor().getId()).get().getMentionTag() + " has been muted for 1 day for saying a banned word.");
	return;
      }
    }
  }
}
