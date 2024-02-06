package commands;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import java.awt.Color;

public class help {
  public static void mainFunc(MessageCreateEvent event) {
    try {
      new MessageBuilder() // send the message
        .setEmbed(new EmbedBuilder()
          .setTitle("Help")
          .setDescription("voteKick [user] | Kicks a specific user after a poll\nvoteMute [user] [hours] | Mutes a specific user after a poll\nvoteBan [user] [days] | Bans a specific user after a poll\nhelp | Shows this message")
	  .setColor(Color.GREEN))
	.send(event.getChannel());
    } catch(Exception e) {
    }
  }
}

