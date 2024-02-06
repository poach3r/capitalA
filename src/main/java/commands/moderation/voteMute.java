// if the message gets 50%> upvotes out of main.settings.muteThreshold>= total votes then the user specified in args[1] is muted for the amount of hours in args[2]

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
import java.time.Duration;

public class voteMute {

  private int upvotes = 0, downvotes = 0;

  public void mainFunc(MessageCreateEvent event, String args[]) {
    String muterId = event.getMessageAuthor().getIdAsString();
    String mutedId = args[1].replace("<", "").replace(">", "").replace("@", "");
    User voters[] = new User[50]; // WARNING there is a potential vulnerability where if 50+ people have voted the bot will crash
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    initVoters(voters, event);

    System.out.println(muterId + " is attempting to mute " + mutedId);

    if(checkUserValidity(event, args, muterId, mutedId)) { return; } // validate that the bot wont error 

    try {
      new MessageBuilder() // send the message
        .setEmbed(new EmbedBuilder()
          .setTitle("Mute user " + event.getServer().get().getMemberById(mutedId).get().getName() + " for " + args[2] + " hours?")
          .setDescription(event.getServer().get().getMemberById(muterId).get().getMentionTag() + " has called a vote-mute on " + event.getServer().get().getMemberById(mutedId).get().getMentionTag())
          .setImage(event.getServer().get().getMemberById(mutedId).get().getAvatar())
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
                System.out.println("new upvote, current total is " + upvotes);
                e.getInteraction().createImmediateResponder()
                  .setContent("You have successfully upvoted")
                  .setFlags(MessageFlag.EPHEMERAL)
                  .respond();
              break;

              case "downvote":
                voters[upvotes+downvotes] = e.getButtonInteraction().getUser();
                downvotes += 1;
                System.out.println("new downvote, current total is " + downvotes);
                e.getInteraction().createImmediateResponder()
                  .setContent("You have successfully downvoted")
                  .setFlags(MessageFlag.EPHEMERAL)
                  .respond();
              break;
            }
          }).removeAfter(main.settings.getMuteTimeLimit(), TimeUnit.MINUTES); 

          executorService.schedule(() -> {
            if(upvotes > downvotes && upvotes + downvotes >= main.settings.getMuteThreshold()) { // if vote has gone through
              System.out.println("mute called by " + muterId + " has resulted in the mute of " + mutedId + " with a result of " + upvotes + "/" + downvotes);
              message.reply(event.getServer().get().getMemberById(mutedId).get().getMentionTag() + " has been muted with a vote of " + upvotes + "/" + downvotes + ".");
              message.getServer().get().timeoutUser(message.getServer().get().getMemberById(mutedId).get(), Duration.ofHours(Integer.parseInt(args[1]))); //mute user
            }
            else { // if vote has failed
              System.out.println("mute called by " + muterId + " has failed to mute " + mutedId + " with a result of " + upvotes + "/" + downvotes);
              message.reply("Mute has failed with a vote of " + upvotes + "/" + downvotes);
            }
          }, main.settings.getMuteTimeLimit(), TimeUnit.MINUTES);
        });
    } catch(Exception e) { 
      System.out.println("Error while muting: " + e); 
    }
  }

  // true = user cannot be muted
  // false = what do you think
  private boolean checkUserValidity(MessageCreateEvent event, String args[], String muterId, String mutedId) {
    if(muterId.equals(mutedId)) { // if user tries to mute themself
      System.out.println(muterId + " attempted to mute themself");
      event.getChannel().sendMessage("You can't mute yourself.");
      return true;
    } 

    if(!event.getServer().get().getMemberById(mutedId).isPresent()) { // if member doesnt exist
      System.out.println(muterId + " attempted to mute nonexistant member " + mutedId);
      event.getChannel().sendMessage(mutedId + " is not a present member.");
      return true;
    }

    if(!event.getServer().get().canYouTimeoutUsers()) { // if bot cant mute
      System.out.println("I don't have mute perms");
      event.getChannel().sendMessage("I do not have permission to mute members.");
      return true; 
    }

    if(event.getServer().get().getMemberById(mutedId).get().isYourself()) { // if user tries to mute bot
      System.out.println("smh");
      event.getChannel().sendMessage("smh");
      return true;
    }

    return false;
  }

  // HACK initialize voters[]
  private void initVoters(User voters[], MessageCreateEvent event) {
    for(int i = 0; i < voters.length; i++) {
      voters[i] = event.getApi().getYourself();
    }
  }
}


