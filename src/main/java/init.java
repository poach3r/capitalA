import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;

public class init {

  public static void main(String[] args) {
    main.settings defaultSettings = new main.settings("default"); 
    defaultSettings.init();

    DiscordApi api = new DiscordApiBuilder() // create bot object
      .setToken(defaultSettings.getToken())
      .addIntents(Intent.MESSAGE_CONTENT)
      .addIntents(Intent.GUILD_MEMBERS)
      .login()
      .join();

    // add listeners to every server the bot is in
    System.out.println("Adding listeners to servers.");
    for(Object server : api.getServers().toArray()) {
      System.out.println(server);
      String serverId = server.toString().split(" ")[2].replace(",", " ").trim(); // this sucks

      // get server settings
      main.settings serverSettings = new main.settings(serverId);
      serverSettings.parseSettings();

      api.getServerById(serverId).get().addMessageCreateListener(event -> {
        String arguments[] = event.getMessageContent().split(" ");

        // if the sender is a bot then skip all parsing
        if(event.getMessageAuthor().isBotUser())
          return;

        // filter the message
        main.filter.mainFunc(event, arguments, serverSettings);

        commands.parseCommands.mainFunc(event, arguments, serverSettings);
      });
    }

    System.out.println("You can invite the bot by using the following url: " + api.createBotInvite()); // print invite url
  }
}
