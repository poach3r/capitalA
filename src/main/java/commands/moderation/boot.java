// A monolithic class for moderation commands

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
import org.javacord.api.event.interaction.ButtonClickEvent;
import java.util.ArrayList;

public class boot {
  private int upvotes;
  private int downvotes;
  private MessageCreateEvent event;
  private String author;
  private String victim;
  private ArrayList<User> voters;
  private ScheduledExecutorService executorService;
  private String args[] = new String[50];
  main.settings serverSettings;

  public boot(MessageCreateEvent message, String arguments[], main.settings s) {
    upvotes = 0;
    downvotes = 0;
    event = message;
    author = message.getMessageAuthor().getIdAsString();
    victim = arguments[2].replace("<", "").replace(">", "").replace("@", "");
    voters = new ArrayList<User>();
    executorService = Executors.newSingleThreadScheduledExecutor();
    serverSettings = s;
    args = arguments;
  }

  public void mainFunc() {
    if(!userIsValid())  // validate that the bot wont error  
      return;

    switch(args[1]) {
      case "kick":
        kick();
      break;

      case "ban":
        ban();
      break;

      case "mute":
        mute();
      break;

      default:
        System.out.println(event.getMessageAuthor().getIdAsString() + " attempted to do an invalid boot.");
      return;
    }
  }

  private void kick() {
    try {
      new MessageBuilder() // send the message
        .setEmbed(new EmbedBuilder()
          .setTitle("Kick user " + event.getServer().get().getMemberById(victim).get().getName() + "?")
          .setDescription(event.getServer().get().getMemberById(author).get().getMentionTag() + " has called a votekick on " + event.getServer().get().getMemberById(victim).get().getMentionTag())
          .setImage(event.getServer().get().getMemberById(victim).get().getAvatar())
          .setColor(Color.RED))
        .addComponents(
            ActionRow.of(
              Button.success("upvote", "Yes"),
              Button.danger("downvote", "No")))
        .send(event.getChannel())
        .thenAcceptAsync(message -> {
          message.addButtonClickListener( e -> { // get votes
            if(canVote(e) && !alreadyVoted(e))
              getVote(e, e.getButtonInteraction().getCustomId());
          }).removeAfter(serverSettings.getKickTimeLimit(), TimeUnit.MINUTES);

          executorService.schedule(() -> {
            if(upvotes > downvotes && upvotes + downvotes > serverSettings.getKickThreshold()) { // if vote has gone through
              message.reply(event.getServer().get().getMemberById(victim).get().getMentionTag() + " has been kicked with a vote of " + upvotes + "/" + downvotes + "."); // if the message gets 50%> upvotes out of main.settings.kickThreshold>= total votes then the user specified in args[1] is kicked
              message.getServer().get().kickUser(message.getServer().get().getMemberById(victim).get()); //kick user
            }
            else { // if vote has failed
              message.reply("Kick has failed with a vote of " + upvotes + "/" + downvotes);
            }
          }, serverSettings.getKickTimeLimit(), TimeUnit.MINUTES);
        });
    } catch(Exception e) { 
      System.out.println("Error while kicking " + victim + " in server " + event.getServer().get().getIdAsString() + ": " + e); 
    }
  }

  private void ban() {
    try {
      new MessageBuilder() // send the message
        .setEmbed(new EmbedBuilder()
          .setTitle("Ban user " + event.getServer().get().getMemberById(victim).get().getName() + " for " + Integer.parseInt(args[3]) + " days?")
          .setDescription(event.getServer().get().getMemberById(author).get().getMentionTag() + " has called a vote-ban on " + event.getServer().get().getMemberById(victim).get().getMentionTag())
          .setImage(event.getServer().get().getMemberById(victim).get().getAvatar()) 
          .setColor(Color.RED))
        .addComponents(
            ActionRow.of(
              Button.success("upvote", "Yes"),
              Button.danger("downvote", "No")))
        .send(event.getChannel())
        .thenAcceptAsync(message -> {
          message.addButtonClickListener( e -> { // get votes
            if(canVote(e) && !alreadyVoted(e))
              getVote(e, e.getButtonInteraction().getCustomId());
          }).removeAfter(serverSettings.getBanTimeLimit(), TimeUnit.MINUTES);

          executorService.schedule(() -> {
            if(upvotes > downvotes && upvotes + downvotes > serverSettings.getBanThreshold()) { // if vote has gone through
              message.reply(event.getServer().get().getMemberById(victim).get().getMentionTag() + " has been banned with a vote of " + upvotes + "/" + downvotes + " for " + Integer.parseInt(args[3]) + " days.");
              message.getServer().get().timeoutUser(message.getServer().get().getMemberById(victim).get(), Duration.ofDays(Integer.parseInt(args[3]))); //ban user
            }
            else { // if vote has failed
              message.reply("Ban has failed with a vote of " + upvotes + "/" + downvotes);
            }
          }, serverSettings.getBanTimeLimit(), TimeUnit.MINUTES); 
        });
    } catch(Exception e) {
      System.out.println("Error while banning " + victim + " in server " + event.getServer().get().getIdAsString() + ": " + e); 
    }
  }

  public void mute() {
    try {
      new MessageBuilder() // send the message
        .setEmbed(new EmbedBuilder()
          .setTitle("Mute user " + event.getServer().get().getMemberById(victim).get().getName() + " for " + Integer.parseInt(args[3]) + " hours?")
          .setDescription(event.getServer().get().getMemberById(author).get().getMentionTag() + " has called a vote-mute on " + event.getServer().get().getMemberById(victim).get().getMentionTag())
          .setImage(event.getServer().get().getMemberById(victim).get().getAvatar())
          .setColor(Color.RED))
        .addComponents(
            ActionRow.of(
              Button.success("upvote", "Yes"),
              Button.danger("downvote", "No")))
        .send(event.getChannel())
        .thenAcceptAsync(message -> {
          message.addButtonClickListener( e -> { // get votes
            String type = e.getButtonInteraction().getCustomId();
            if(canVote(e) && !alreadyVoted(e))
              getVote(e, e.getButtonInteraction().getCustomId());
          }).removeAfter(serverSettings.getMuteTimeLimit(), TimeUnit.MINUTES); 

          executorService.schedule(() -> {
            if(upvotes > downvotes && upvotes + downvotes > serverSettings.getMuteThreshold()) { // if vote has gone through
              message.reply(event.getServer().get().getMemberById(victim).get().getMentionTag() + " has been muted with a vote of " + upvotes + "/" + downvotes + " for " + Integer.parseInt(args[3]) +  " days.");
              message.getServer().get().timeoutUser(message.getServer().get().getMemberById(victim).get(), Duration.ofHours(Integer.parseInt(args[3]))); //mute user
            }
            else { // if vote has failed
              message.reply("Mute has failed with a vote of " + upvotes + "/" + downvotes);
            }
          }, serverSettings.getMuteTimeLimit(), TimeUnit.MINUTES);
        });
    } catch(Exception e) { 
      System.out.println("Error while banning " + victim + " in server " + event.getServer().get().getIdAsString() + ": " + e); 
    }
  }

  private boolean userIsValid() {
    if(!event.getServer().get()
        .getMemberById(
          event.getMessageAuthor()
          .getId())
        .get()
        .getRoles(
          event.getServer().get()
        )
        .contains(
          event.getServer().get().getRolesByName("Voter").get(0)
        )
    ) {
      System.out.println(author + " does not have permission to call votes.");
      event.getChannel().sendMessage("You do not have permission to call votes.");
      return false;
    }

    if(author.equals(victim)) { // if user tries to kick themself
      System.out.println(author + " attempted to boot themself");
      event.getChannel().sendMessage("You can't boot yourself.");
      return false;
    } 

    if(!event.getServer().get().getMemberById(victim).isPresent()) { // if member doesnt exist
      System.out.println(author + " attempted to boot nonexistant member " + victim);
      event.getChannel().sendMessage(victim + " is not a present member.");
      return false;
    }

    if(!event.getServer().get().canYouManage()) { // if bot cant kick
      System.out.println("I don't have boot perms");
      event.getChannel().sendMessage("I do not have permission to boot members.");
      return false; 
    }

    if(event.getServer().get().getMemberById(victim).get().isYourself()) { // if user tries to kick bot
      System.out.println("smh");
      event.getChannel().sendMessage("smh");
      return false;
    }
    return true;
  }

  // check if user has already voted
  private boolean alreadyVoted(ButtonClickEvent e) {
    for(User voter : voters) {
      if(voter.equals(e.getButtonInteraction().getUser())) {
        e.getInteraction().createImmediateResponder()
        .setContent("You have already voted!")
        .setFlags(MessageFlag.EPHEMERAL)
        .respond();
        return true;
      }
    } 
    return false;
  }

  // check if user has the right to vote
  private boolean canVote(ButtonClickEvent e) {
    if(!e.getButtonInteraction()
        .getUser()
        .getRoles(
        e.getButtonInteraction()
        .getServer().get())
        .contains(
          event.getServer().get().
          getRolesByName("Voter").get(0))
    ) {
      e.getInteraction().createImmediateResponder()
        .setContent("You don't have permission to vote.")
        .setFlags(MessageFlag.EPHEMERAL)
        .respond();
      return false;
    }
    
    else if(e.getButtonInteraction().getUser().getIdAsString().equals(author)) {
      e.getInteraction().createImmediateResponder()
        .setContent("You cannot vote on your own poll.")
        .setFlags(MessageFlag.EPHEMERAL)
        .respond();
      return false;
    }

    return true;
  }


  // parse vote
  private void getVote(ButtonClickEvent e, String type) {
    switch(type) {
      case "upvote":
        voters.set(upvotes + downvotes, e.getButtonInteraction().getUser()); 
        upvotes += 1;
        e.getInteraction().createImmediateResponder()
          .setContent("You have successfully upvoted")
          .setFlags(MessageFlag.EPHEMERAL)
          .respond();
        break;

        case "downvote":
          voters.set(upvotes + downvotes, e.getButtonInteraction().getUser());
          downvotes += 1;
          e.getInteraction().createImmediateResponder()
            .setContent("You have successfully downvoted")
            .setFlags(MessageFlag.EPHEMERAL)
            .respond();
        break;
    }
  }
}
