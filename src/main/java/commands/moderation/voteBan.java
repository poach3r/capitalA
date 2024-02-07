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
  private int upvotes;
  private int downvotes;
  private MessageCreateEvent event;
  private String bannerId;
  private String bannedId;
  private int banTime;
  private User voters[] = new User[50];
  private ScheduledExecutorService executorService;

  public voteBan(MessageCreateEvent message, String args1, String args2) {
    upvotes = 0;
    downvotes = 0;
    event = message;
    bannerId = message.getMessageAuthor().getIdAsString();
    bannedId = args1.replace("<", "").replace(">", "").replace("@", "");
    banTime = Integer.parseInt(args2);
    voters = initVoters(voters);
    executorService = Executors.newSingleThreadScheduledExecutor();
  }

  public void mainFunc() {
    if(checkUserValidity()) // validate that the bot wont error 
      return; 

    try {
      new MessageBuilder() // send the message
        .setEmbed(new EmbedBuilder()
          .setTitle("Ban user " + event.getServer().get().getMemberById(bannedId).get().getName() + " for " + banTime + " days?")
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
              message.reply(event.getServer().get().getMemberById(bannedId).get().getMentionTag() + " has been banned with a vote of " + upvotes + "/" + downvotes + " for " + banTime + " days.");
              message.getServer().get().timeoutUser(message.getServer().get().getMemberById(bannedId).get(), Duration.ofDays(banTime)); //ban user
            }
            else { // if vote has failed
              message.reply("Ban has failed with a vote of " + upvotes + "/" + downvotes);
            }
          }, main.settings.getBanTimeLimit(), TimeUnit.MINUTES); 
        });
    } catch(Exception e) {
      System.out.println("Error while banning " + bannedId + " in server " + event.getServer().get().getIdAsString() + ": " + e); 
    }
  }

  // true = user cannot be banned
  // false = what do you think
  private boolean checkUserValidity() {
    if(bannerId.equals(bannedId)) { // if user tries to ban themself
      System.out.println(bannerId + " attempted to ban themself");
      event.getChannel().sendMessage("You can't ban yourself.");
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
  private User[] initVoters(User voters[]) {
    for(int i = 0; i < voters.length; i++) {
      voters[i] = event.getApi().getYourself();
    }
    return voters;
  }
}


