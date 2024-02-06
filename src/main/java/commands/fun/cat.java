package commands.fun;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.MessageBuilder;
import java.util.Random;
import java.io.File;

public class cat {
  public static void mainFunc(MessageCreateEvent event) {
    Random r = new Random();
    int rand = r.nextInt(111);
    String randStr;
    if(rand < 10) { randStr = "00" + rand + ".jpg"; }
    else if(rand > 10 && rand < 100) { randStr = "0" + rand + ".jpg"; }
    else { randStr = rand + ".jpg"; }
    System.out.println(randStr);
    try {
      new MessageBuilder()
        .setEmbed(new EmbedBuilder()
          .setTitle("car")
          .setImage(new File("./assets/cats/" + randStr)))
      .send(event.getChannel());
    } catch(Exception e) {
      System.out.println("15 cat pile up: " + e);
    }
  }
}
