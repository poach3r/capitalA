// TODO entire per server configurations

package commands;

import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.event.message.MessageCreateEvent;
import java.time.Duration;

public class listen implements MessageCreateListener {

  @Override
  public void onMessageCreate(MessageCreateEvent event) {
    String args[] = event.getMessageContent().split(" "); // split message into individual arguments
    if(event.getMessageAuthor().isBotUser()) return; // filter out bot accounts
    
    switch(args[0]) { // run command
      case "$ping":
	commands.fun.ping.mainFunc(event);
      break;

      case "$help":
	commands.help.mainFunc(event);
      break;

      case "$voteKick":
	commands.moderation.voteKick vk = new commands.moderation.voteKick();
	vk.mainFunc(event, args);
      break;

      case "$voteBan":
	commands.moderation.voteBan vb = new commands.moderation.voteBan();
	vb.mainFunc(event, args);
      break;


      case "$voteMute":
	commands.moderation.voteMute vm = new commands.moderation.voteMute();
	vm.mainFunc(event, args);
      break;

      case "$addFilter":
	commands.moderation.addFilter af = new commands.moderation.addFilter();
	af.mainFunc(event, args);
      break;

      case "$cat":
	commands.fun.cat.mainFunc(event);
      break;

      case "$llm":
	if(main.settings.huggingFaceKey != "") { commands.fun.llm.mainFunc(event, args); }
	else { event.getMessage().reply("This hoster has not setup a huggingface.co access key."); }
      break;

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

    for(String word : main.settings.filter) {
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
