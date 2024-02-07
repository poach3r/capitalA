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
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.io.FileWriter;

public class addFilter {
  private int upvotes;
  private int downvotes;
  private String author;
  private MessageCreateEvent event;
  private User voters[] = new User[50];
  private String word;
  private ScheduledExecutorService executorService;

  public addFilter(MessageCreateEvent message, String arg1) {
    upvotes = 0;
    downvotes = 0;
    event = message;
    author = message.getMessageAuthor().getIdAsString();
    voters = initVoters(voters);
    word = arg1;
    executorService = Executors.newSingleThreadScheduledExecutor();
  }

  public void mainFunc() {
    try {
      new MessageBuilder() // send the message
        .setEmbed(new EmbedBuilder()
          .setTitle("Add " + word + " to the word filter?")
          .setDescription(event.getServer().get().getMemberById(author).get().getMentionTag() + " would like to add \"" + word + "\" to the word filter.")
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
          }).removeAfter(1, TimeUnit.MINUTES);

          executorService.schedule(() -> {
            if(upvotes > downvotes && upvotes + downvotes >= 0) { // if vote has gone through
              message.reply(word + " has successfully been added to the word filter with a vote of " + upvotes + "/" + downvotes);
              try {
                String serverId = event.getServer().get().getIdAsString();
                JSONObject settings = new JSONObject(new String(Files.readAllBytes(main.settings.getSettingsLocation())));
                JSONArray words = new JSONArray();
                if(settings.getJSONObject("filters").has(serverId))
                  settings.getJSONObject("filters").getJSONArray(serverId).put(word);
                else 
                  words.put(word); settings.getJSONObject("filters").put(serverId, words);
                FileWriter f = new FileWriter(main.settings.getSettingsLocation().toFile(), false);
                f.write(settings.toString(2));
                f.close();
              } catch(Exception e) {
                System.out.println("Error while adding " + word + " to " + event.getServer().get().getIdAsString() + "'s filter: " + e);
              }
            }
            else { // if vote has failed
              message.reply("Vote has failed with a vote of " + upvotes + "/" + downvotes);
            }
          }, 1, TimeUnit.MINUTES); 
        });
    } catch(Exception e) { 
      System.out.println("Error while adding filter: " + e); 
    }
  }

  // HACK initialize voters[]
  private User[] initVoters(User voters[]) {
    for(int i = 0; i < voters.length; i++) {
      voters[i] = event.getApi().getYourself();
    }
    return voters;
  }
}


