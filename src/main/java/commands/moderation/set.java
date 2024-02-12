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
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.io.FileWriter;

public class set {
  private int upvotes;
  private int downvotes;
  private MessageCreateEvent event;
  private User voters[] = new User[50];
  private ScheduledExecutorService executorService;
  main.settings serverSettings;
  private String args[];
  private String author;

  public set(MessageCreateEvent message, String a[], main.settings s) {
    upvotes = 0;
    downvotes = 0;
    event = message;
    args = a;
    voters = initVoters(voters);
    executorService = Executors.newSingleThreadScheduledExecutor();
    serverSettings = s;
    author = message.getMessageAuthor().getIdAsString();
  }

  public void mainFunc() {
    switch(args[1]) {
      case "filter": // TODO command to remove words from filter, this cant be done rn since we filter all messages before parsing commands
        switch(args[2]) {
          case "add":
            addWordToFilter();
          break;

          case "remove":
          break;
        }
      break;

      case "prefix":
        changePrefix();
      break;

      default:
        System.out.println(event.getMessageAuthor().getIdAsString() + " attempted to set invalid setting.");
      return;
    }
  }

  // HACK initialize voters[]
  private User[] initVoters(User voters[]) {
    for(int i = 0; i < voters.length; i++) {
      voters[i] = event.getApi().getYourself();
    }
    return voters;
  }

  private void addWordToFilter() {
    try {
      new MessageBuilder() // send the message
        .setEmbed(new EmbedBuilder()
          .setTitle("Add \'" + args[3] + "\' to the word filter?")
          .setDescription(event.getServer().get().getMemberById(author).get().getMentionTag() + " would like to add \"" + args[3] + "\" to the word filter.")
          .setColor(Color.GREEN))
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
          }).removeAfter(serverSettings.getMiscTimeLimit(), TimeUnit.MINUTES);

          executorService.schedule(() -> {
            if(upvotes > downvotes && upvotes + downvotes > serverSettings.getMiscThreshold()) { // if vote has gone through
              message.reply(args[3] + " has successfully been added to the word filter with a vote of " + upvotes + "/" + downvotes);
              try {
                JSONObject settings = new JSONObject(new String(Files.readAllBytes(serverSettings.getSettingsLocation())));

                JSONArray words = new JSONArray();
                words.put(args[3]); 
                settings.getJSONObject(serverSettings.getServerId()).put("filter", words);

                FileWriter f = new FileWriter(serverSettings.getSettingsLocation().toFile(), false);
                f.write(settings.toString(2));
                f.close();
                serverSettings.parseSettings();
              } catch(Exception e) {
                System.out.println("Error while adding " + args[3] + " to " + event.getServer().get().getIdAsString() + "'s filter: " + e);
              }
            }
            else { // if vote has failed
              message.reply("Vote has failed with a vote of " + upvotes + "/" + downvotes);
            }
          }, serverSettings.getMiscTimeLimit(), TimeUnit.MINUTES); 
        });
    } catch(Exception e) { 
      System.out.println("Error while adding filter: " + e); 
    }
  }

  private void changePrefix() {
    try {
      new MessageBuilder() // send the message
        .setEmbed(new EmbedBuilder()
          .setTitle("Change prefix to \"" + args[2] + "\"?")
          .setDescription(event.getServer().get().getMemberById(author).get().getMentionTag() + " would like to change the prefix to \"" + args[2] + "\".")
          .setColor(Color.GREEN))
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
          }).removeAfter(serverSettings.getMiscTimeLimit(), TimeUnit.MINUTES);

          executorService.schedule(() -> {
            if(upvotes > downvotes && upvotes + downvotes > serverSettings.getMiscThreshold()) { // if vote has gone through
              message.reply("The prefix has successfully been changed to " + args[2] + " with a vote of " + upvotes + "/" + downvotes);
              try {
                JSONObject settings = new JSONObject(new String(Files.readAllBytes(serverSettings.getSettingsLocation())));

                settings.getJSONObject(serverSettings.getServerId()).put("prefix", args[2]);

                FileWriter f = new FileWriter(serverSettings.getSettingsLocation().toFile(), false);
                f.write(settings.toString(2));
                f.close();
                serverSettings.parseSettings();
              } catch(Exception e) {
                System.out.println("Error while adding " + args[2] + " to " + event.getServer().get().getIdAsString() + "'s filter: " + e);
              }
            }
            else { // if vote has failed
              message.reply("Vote has failed with a vote of " + upvotes + "/" + downvotes);
            }
          }, serverSettings.getMiscTimeLimit(), TimeUnit.MINUTES); 
        });
    } catch(Exception e) { 
      System.out.println("Error while adding filter: " + e); 
    }
  }
}

