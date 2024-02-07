// if the message gets 50%> upvotes out of main.settings.kickThreshold>= total votes then the user specified in args[1] is kicked

package commands.moderation;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.user.User;

public class voteKick {
  private int upvotes;
  private int downvotes;
  private MessageCreateEvent event;
  private String kickerId;
  private String kickedId;
  private User voters[] = new User[50];
  private ScheduledExecutorService executorService;

  public voteKick(MessageCreateEvent message, String args1) {
    upvotes = 0;
    downvotes = 0;
    event = message;
    kickerId = message.getMessageAuthor().getIdAsString();
    kickedId = args1.replace("<", "").replace(">", "").replace("@", "");
    voters = initVoters(voters);
    executorService = Executors.newSingleThreadScheduledExecutor();
  }

  public void mainFunc() {
    if(checkUserValidity())  // validate that the bot wont error  
      return;

    try {
      new MessageBuilder() // send the message
        .setEmbed(new EmbedBuilder()
          .setTitle("Kick user " + event.getServer().get().getMemberById(kickedId).get().getName() + "?")
          .setDescription(event.getServer().get().getMemberById(kickerId).get().getMentionTag() + " has called a votekick on " + event.getServer().get().getMemberById(kickedId).get().getMentionTag())
          .setImage(event.getServer().get().getMemberById(kickedId).get().getAvatar())
          .setColor(Color.RED))
        .addComponents(
            ActionRow.of(
              Button.success("upvote", "Yes"),
              Button.danger("downvote", "No")))
        .send(event.getChannel())
        .thenAcceptAsync(message -> {
          message.addButtonClickListener( e -> { // get votes
            String type = e.getButtonInteraction().getCustomId();
            for(User voter : voters) {
              if(voter.equals(e.getButtonInteraction().getUser())) {
                e.getInteraction().createImmediateResponder()
                  .setContent("You have already voted!")
                  .setFlags(MessageFlag.EPHEMERAL)
                  .respond();
                return;
              }
            }

            switch(type) {
              case "upvote":
                voters[upvotes+downvotes] = e.getButtonInteraction().getUser();
                upvotes += 1;
                e.getInteraction().createImmediateResponder()
                  .setContent("You have successfully upvoted")
                  .setFlags(MessageFlag.EPHEMERAL)
                  .respond();
              break;

              case "downvote":
                voters[upvotes+downvotes] = e.getButtonInteraction().getUser();
                downvotes += 1;
                e.getInteraction().createImmediateResponder()
                  .setContent("You have successfully downvoted")
                  .setFlags(MessageFlag.EPHEMERAL)
                  .respond();
              break;
            }
          }).removeAfter(main.settings.getKickTimeLimit(), TimeUnit.MINUTES);

          executorService.schedule(() -> {
            if(upvotes > downvotes && upvotes + downvotes >= main.settings.getKickThreshold()) { // if vote has gone through
              message.reply(event.getServer().get().getMemberById(kickedId).get().getMentionTag() + " has been kicked with a vote of " + upvotes + "/" + downvotes + "."); // if the message gets 50%> upvotes out of main.settings.kickThreshold>= total votes then the user specified in args[1] is kicked
              message.getServer().get().kickUser(message.getServer().get().getMemberById(kickedId).get()); //kick user
            }
            else { // if vote has failed
              message.reply("Kick has failed with a vote of " + upvotes + "/" + downvotes);
            }
          }, main.settings.getKickTimeLimit(), TimeUnit.MINUTES);
        });
    } catch(Exception e) { 
      System.out.println("Error while kicking " + kickedId + " in server " + event.getServer().get().getIdAsString() + ": " + e); 
    }
  }

  // true = user cannot be kicked
  // false = what do you think
  private boolean checkUserValidity() {
    if(kickerId.equals(kickedId)) { // if user tries to kick themself
      System.out.println(kickerId + " attempted to kick themself");
      event.getChannel().sendMessage("You can't kick yourself.");
      return true;
    } 

    if(!event.getServer().get().getMemberById(kickedId).isPresent()) { // if member doesnt exist
      System.out.println(kickerId + " attempted to kick nonexistant member " + kickedId);
      event.getChannel().sendMessage(kickedId + " is not a present member.");
      return true;
    }

    if(!event.getServer().get().canYouKickUsers()) { // if bot cant kick
      System.out.println("I don't have kick perms");
      event.getChannel().sendMessage("I do not have permission to kick members.");
      return true; 
    }

    if(event.getServer().get().getMemberById(kickedId).get().isYourself()) { // if user tries to kick bot
      System.out.println("smh");
      event.getChannel().sendMessage("smh");
      return true;
    }
    return false;
  }

  // HACK initialize voters[]
  private User[] initVoters(User voters[]) {
    for(int i = 0; i < voters.length; i++) {
      voters[i] = event.getApi().getYourself();
    }
    return voters;
  }
}


