package main;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;

public class init {
  public static void main(String[] args) {
    main.settings.mainFunc(); // parses settings json

    DiscordApi api = new DiscordApiBuilder() // create bot object
      .setToken(main.settings.token)
      .addIntents(Intent.MESSAGE_CONTENT)
      .addIntents(Intent.GUILD_MEMBERS)
      .login()
      .join();
    
    api.addListener(new commands.listen()); // listen for commands

    System.out.println("You can invite the bot by using the following url: " + api.createBotInvite()); // print invite url
  }
}