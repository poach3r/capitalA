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

  private int upvotes = 0, downvotes = 0;

  public void mainFunc(MessageCreateEvent event, String args[]) {
    String author = event.getMessageAuthor().getIdAsString();
    User voters[] = new User[50]; // WARNING there is a potential vulnerability where if 50+ people have voted the bot will crash
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    initVoters(voters, event);

    System.out.println(author + " wants to add \"" + args[1] + "\" to the word filter");

    try {
      new MessageBuilder() // send the message
        .setEmbed(new EmbedBuilder()
          .setTitle("Add " + args[1] + " to the word filter?")
          .setDescription(event.getServer().get().getMemberById(author).get().getMentionTag() + " would like to add " + args[1] + " to the word filter.")
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
          }).removeAfter(10, TimeUnit.SECONDS);

          executorService.schedule(() -> {
            if(upvotes > downvotes && upvotes + downvotes >= 0) { // if vote has gone through
              message.reply(args[1] + " has successfully been added to the word filter with a vote of " + upvotes + "/" + downvotes);
              try {
                String serverId = event.getServer().get().getIdAsString();
                JSONObject settings = new JSONObject(new String(Files.readAllBytes(main.settings.settingsLocation)));
                JSONArray words = new JSONArray();
                if(settings.getJSONObject("filters").has(serverId)) { 
                  settings.getJSONObject("filters").getJSONArray(serverId).put(args[1]);
                }
                else { words.put(args[1]); settings.getJSONObject("filters").put(serverId, words); }
                FileWriter f = new FileWriter(main.settings.settingsLocation.toFile(), false);
                f.write(settings.toString(2));
                f.close();
              } catch(Exception e) {
                System.out.println("Error while adding word to server filter " + event.getServer().get().getIdAsString() + ": " + e);
              }
            }
            else { // if vote has failed
              message.reply("Vote has failed with a vote of " + upvotes + "/" + downvotes);
            }
          }, 10, TimeUnit.SECONDS); 
        });
    } catch(Exception e) { System.out.println("Error while adding filter: " + e); }
  }

  // HACK initialize voters[]
  private void initVoters(User voters[], MessageCreateEvent event) {
    for(int i = 0; i < voters.length; i++) {
      voters[i] = event.getApi().getYourself();
    }
  }
}


