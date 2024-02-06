// if the message gets 50%> upvotes out of main.settings.bannThreshold>= total votes then the user specified in args[1] is banned for the amount of time specified in args[2]

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

public class voteBan {

  private int upvotes = 0, downvotes = 0;

  public void mainFunc(MessageCreateEvent event, String args[]) {
    String bannerId = event.getMessageAuthor().getIdAsString();
    String bannedId = args[1].replace("<", "").replace(">", "").replace("@", "");
    User voters[] = new User[50]; // WARNING there is a potential vulnerability where if 50+ people have voted the bot will crash
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    initVoters(voters, event);

    System.out.println(bannerId + " is attempting to ban " + bannedId);

    if(checkUserValidity(event, args, bannerId, bannedId)) { return; } // validate that the bot wont error 

    try {
      new MessageBuilder() // send the message
        .setEmbed(new EmbedBuilder()
          .setTitle("Ban user " + event.getServer().get().getMemberById(bannedId).get().getName() + " for " + args[2] + " days?")
          .setDescription(event.getServer().get().getMemberById(bannerId).get().getMentionTag() + " has called a vote-ban on " + event.getServer().get().getMemberById(bannedId).get().getMentionTag())
          .setImage(event.getServer().get().getMemberById(bannedId).get().getAvatar()) 
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
          }).removeAfter(main.settings.getBanTimeLimit(), TimeUnit.MINUTES);

          executorService.schedule(() -> {
            if(upvotes > downvotes && upvotes + downvotes >= main.settings.getBanThreshold()) { // if vote has gone through
              System.out.println("bann called by " + bannerId + " has resulted in the ban of " + bannedId + " with a result of " + upvotes + "/" + downvotes);
              message.reply(event.getServer().get().getMemberById(bannedId).get().getMentionTag() + " has been banned with a vote of " + upvotes + "/" + downvotes + ".");
              message.getServer().get().timeoutUser(message.getServer().get().getMemberById(bannedId).get(), Duration.ofDays(Integer.parseInt(args[2]))); //ban user
            }
            else { // if vote has failed
              System.out.println("bann called by " + bannerId + " has failed to ban " + bannedId + " with a result of " + upvotes + "/" + downvotes);
              message.reply("Kick has failed with a vote of " + upvotes + "/" + downvotes);
            }
          }, main.settings.getBanTimeLimit(), TimeUnit.MINUTES); 
        });
    } catch(Exception e) {
      System.out.println("Error while banning: " + e);
    }
  }

  // true = user cannot be banned
  // false = what do you think
  private boolean checkUserValidity(MessageCreateEvent event, String args[], String bannerId, String bannedId) {
    if(bannerId.equals(bannedId)) { // if user tries to ban themself
      System.out.println(bannerId + " attempted to ban themself");
      event.getChannel().sendMessage("You can't bann yourself.");
      return true;
    } 

    if(!event.getServer().get().getMemberById(bannedId).isPresent()) { // if member doesnt exist
      System.out.println(bannerId + " attempted to ban nonexistant member " + bannedId);
      event.getChannel().sendMessage(bannedId + " is not a present member.");
      return true;
    }

    if(!event.getServer().get().canYouKickUsers()) { // if bot cant bann
      System.out.println("I don't have bann perms");
      event.getChannel().sendMessage("I do not have permission to bann members.");
      return true; 
    }

    if(event.getServer().get().getMemberById(bannedId).get().isYourself()) { // if user tries to ban bot
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


