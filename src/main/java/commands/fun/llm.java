package commands.fun;

import dev.langchain4j.model.huggingface.HuggingFaceChatModel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import java.awt.Color;

public class llm {
  public static void mainFunc(MessageCreateEvent event, String args[]) {
    String fullMsg = "";
    for(int i = 1; i < args.length; i++) { fullMsg = fullMsg + " " + args[i]; }
    System.out.println(fullMsg);
    HuggingFaceChatModel model = HuggingFaceChatModel.withAccessToken(main.settings.huggingFaceKey);
    String answer = model.generate(fullMsg);
    System.out.println(answer);
    new MessageBuilder() // send the message
      .setEmbed(new EmbedBuilder()
        .setTitle("Message generated by huggingface.co")
        .setDescription(answer)
        .setColor(Color.RED))
      .send(event.getChannel());
  }
}
