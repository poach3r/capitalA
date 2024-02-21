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

    System.out.println("-------------------");
    System.out.println("Loading plugins");
    plugins.method1.onInit(api);

    // add listeners to every server the bot is in
    System.out.println("Adding listeners to servers.");
    System.out.println("-------------------");
    for(Object server : api.getServers().toArray()) {
      addServer(server, api);
    }

    // listen for server joins
    api.addServerJoinListener(event -> {
      event.getServer().getOwner().get().sendMessage(
          "Thanks for adding me to " + event.getServer().getName() + "! For me to work, you will need to do the following:" +
          "\n1. Create a role titled \"Voter\" with basic member permissions" +
          "\n2. Give me administrator permissions.");
      addServer(event.getServer(), api);
    });

    System.out.println("Invite URL: " + api.createBotInvite()); // print invite url
  }

  public static void addServer(Object server, DiscordApi api) {
    System.out.println(server);
    String serverId = server.toString().split(" ")[2].replace(",", " ").trim(); // this sucks

    // get server settings
    main.settings serverSettings = new main.settings(serverId);
    serverSettings.parseSettings();

    api.getServerById(serverId).get().addServerMemberJoinListener(event -> {
      antiRaid.newMember newMember = new antiRaid.newMember(event, serverSettings);
      newMember.mainFunc();
    });

    api.getServerById(serverId).get().addMessageCreateListener(event -> {
      String arguments[] = event.getMessageContent().split(" ");

      // if the sender is a bot then skip all parsing
      if(event.getMessageAuthor().isBotUser())
        return;

      // filter the message
      main.filter.mainFunc(event, arguments, serverSettings);

      // parse command
      commands.parseCommands.mainFunc(event, arguments, serverSettings);
    });
    System.out.println("-------------------");
  }
}
