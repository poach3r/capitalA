package main;

import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.FileWriter;
import org.json.JSONArray;

public class settings {
  private String serverId;
  private String token; 
  private String prefix;
  private int kickThreshold;
  private int kickTimeLimit;
  private int banThreshold;
  private int banTimeLimit;
  private int muteThreshold;
  private int muteTimeLimit;
  private int miscThreshold;
  private int miscTimeLimit;
  private String huggingFaceKey;
  private String filter[] = new String[50];
  private Path settingsLocation;
  private String joinMessage; 
  private int joinTime;

  public settings(String s) {
    serverId = s;
    token = ""; 
    prefix = "$";
    kickThreshold = 10; 
    kickTimeLimit = 1;
    banThreshold = 10;
    banTimeLimit = 2;
    muteThreshold = 5;
    muteTimeLimit = 1;
    miscThreshold = 5;
    miscTimeLimit = 1;
    huggingFaceKey = "";
    filter = main.filter.initFilter(); 
    settingsLocation = Paths.get("./settings.json");
    joinMessage = "Thanks for joining " + s + "! To prevent raids, you are currently banned from voting in elections, this will be lifted in 3 days.";
    joinTime = 3;
  }

  public void setToken(String t) {
    token = t;
  }

  public void setPrefix(String p) {
    prefix = p;
  }

  public void setKickThreshold(int kt) {
    kickThreshold = kt;
  }

  public void setKickTimeLimit(int ktl) {
    kickTimeLimit = ktl;
  }

  public void setBanThreshold(int bt) {
    banThreshold = bt;
  }

  public void setBanTimeLimit(int btl) {
    banTimeLimit = btl;
  }

  public void setMuteThreshold(int mt) {
    muteThreshold = mt;
  }

  public void setMuteTimeLimit(int mtl) {
    muteTimeLimit = mtl;
  }

  
  public void setMiscThreshold(int mt) {
    miscThreshold = mt;
  }

  public void setMiscTimeLimit(int mtl) {
    miscTimeLimit = mtl;
  }

  public void setHuggingFaceKey(String hfk) {
    huggingFaceKey = hfk;
  }

  public void setFilter(String f[]) {
    filter = f;
  }

  public void setFilter(String f, int i) {
    filter[i] = f;
  }

  public void setSettingsLocation(Path sl) {
    settingsLocation = sl;
  }

  public void setJoinMessage(String j) {
    joinMessage = j;
  }

  public void setJoinTime(int t) {
    joinTime = t;
  }

  public String getServerId() {
    return serverId;
  }

  public String getToken() {
    return token;
  }

  public String getPrefix() {
    return prefix;
  }

  public int getKickThreshold() {
    return kickThreshold;
  }

  public int getKickTimeLimit() {
    return kickTimeLimit;
  }

  public int getBanThreshold() {
    return banThreshold;
  }

  public int getBanTimeLimit() {
    return banTimeLimit;
  }

  public int getMuteThreshold() {
    return muteThreshold;
  }

  public int getMuteTimeLimit() {
    return muteTimeLimit;
  }

    public int getMiscThreshold() {
    return miscThreshold;
  }

  public int getMiscTimeLimit() {
    return miscTimeLimit;
  }

  public String getHuggingFaceKey() {
    return huggingFaceKey;
  }

  public String[] getFilter() {
    return filter;
  }

  public String getFilter(int i) {
    return filter[i]; 
  }

  public Path getSettingsLocation() {
    return settingsLocation;
  }

  public String getJoinMessage() {
    return joinMessage;
  }

  public int getJoinTime() {
    return joinTime;
  }

  public void init() { 
    try {
      JSONObject settingsJS = new JSONObject(new String(Files.readAllBytes(getSettingsLocation())));
      // check for token, if it doesnt exist then quit
      if(settingsJS.has("token"))
        setToken(settingsJS.getString("token"));

      else {
        System.out.println("You have not set a token.");
        System.exit(1);
      }

      if(settingsJS.has("huggingFaceKey"))
        setHuggingFaceKey(settingsJS.getString("huggingFaceKey"));

    } catch(Exception e) {
      System.out.println("Error while initializing settings: " + e);
    }
  }

  public void parseSettings() { 
    if(!getSettingsLocation().toFile().exists()) {
      System.out.println(getSettingsLocation() + " does not exist");
      System.exit(1);
    }

    try { // parse settings json 
      JSONObject settingsJS = new JSONObject(new String(Files.readAllBytes(getSettingsLocation())));

      if(settingsJS.has(getServerId())) { // this fucking sucks, im gonna do it anywaus
        if(settingsJS.getJSONObject(getServerId()).has("votes")) {
          if(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").has("kicks")) {
            if(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("kicks").has("threshold")) {
              setKickThreshold(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("kicks").getInt("threshold"));
              System.out.println("kickThreshold: " + getKickThreshold());
            }

            if(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("kicks").has("timeLimit")) {
              setKickTimeLimit(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("kicks").getInt("timeLimit"));
              System.out.println("kickTimeLimit: " + getKickThreshold());
            }
          }

          if(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").has("bans")) {
            if(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("bans").has("threshold")) {
              setBanThreshold(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("bans").getInt("threshold"));
              System.out.println("banThreshold: " + getBanThreshold());
            }

            if(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("bans").has("timeLimit")) {
              setBanTimeLimit(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("bans").getInt("timeLimit"));
              System.out.println("banTimeLimit: " + getBanTimeLimit());
            }
          }

          if(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").has("mutes")) {
            if(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("mutes").has("threshold")) {
              setMuteThreshold(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("mutes").getInt("threshold"));
              System.out.println("muteThreshold: " + getMuteThreshold());
            }

            if(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("mutes").has("timeLimit")) {
              setMuteTimeLimit(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("mutes").getInt("timeLimit"));
              System.out.println("muteTimeLimit: " + getMuteTimeLimit());
            }
          }

          if(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").has("misc")) {
            if(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("misc").has("threshold")) {
              setMiscThreshold(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("misc").getInt("threshold"));
              System.out.println("miscThreshold: " + getMiscThreshold());
            }

            if(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("misc").has("timeLimit")) {
              setMiscTimeLimit(settingsJS.getJSONObject(getServerId()).getJSONObject("votes").getJSONObject("misc").getInt("timeLimit"));
              System.out.println("miscTimeLimit: " + getMiscTimeLimit());
            }
          }
        }

        // anti raid
        if(settingsJS.getJSONObject(getServerId()).has("antiRaid")) {
          if(settingsJS.getJSONObject(getServerId()).getJSONObject("antiRaid").has("joinMessage")) {
            setJoinMessage(settingsJS.getJSONObject(getServerId()).getJSONObject("antiRaid").getString("joinMessage"));
            System.out.println("joinMessage: " + getJoinMessage());
          }

          if(settingsJS.getJSONObject(getServerId()).getJSONObject("antiRaid").has("joinTime")) {
            setJoinTime(settingsJS.getJSONObject(getServerId()).getJSONObject("antiRaid").getInt("joinTime"));
            System.out.println("joinMessage: " + getJoinTime());
          }
        }

        if(settingsJS.getJSONObject(getServerId()).has("prefix")) { 
          setPrefix(settingsJS.getJSONObject(getServerId()).getString("prefix"));
          System.out.println("prefix: " + getPrefix());
        }
      
        if(settingsJS.getJSONObject(getServerId()).has("filter")) {
          for(int i = 0; i < settingsJS.getJSONObject(getServerId()).getJSONArray("filter").length(); i++)
            setFilter(settingsJS.getJSONObject(getServerId()).getJSONArray("filter").getString(i), i);
        }
      } else { // if server does not have configuration, create it
        System.out.println(getServerId() + " does not have a configuration, creating it using the defaults.");
        JSONObject server = new JSONObject();

        JSONObject votes = new JSONObject();

        JSONObject kicks = new JSONObject();
        kicks.put("timeLimit", getKickTimeLimit());
        kicks.put("threshold", getKickThreshold());
        votes.put("kicks", kicks);

        JSONObject bans = new JSONObject();
        bans.put("timeLimit", getBanTimeLimit());
        bans.put("threshold", getBanThreshold());
        votes.put("bans", bans);

        JSONObject mutes = new JSONObject();
        mutes.put("timeLimit", getMuteTimeLimit());
        mutes.put("threshold", getMuteThreshold());
        votes.put("mutes", mutes);

        JSONObject misc = new JSONObject();
        misc.put("timeLimit", getMiscTimeLimit());
        misc.put("threshold", getMiscThreshold());
        votes.put("misc", misc);

        server.put("votes", votes);

        JSONObject antiRaid = new JSONObject();
        antiRaid.put("joinMessage", getJoinMessage());
        antiRaid.put("joinTime", getJoinTime());
        server.put("antiRaid", antiRaid);

        JSONArray filter = new JSONArray();
        server.put("filter", filter);

        server.put("prefix", getPrefix());

        settingsJS.put(getServerId(), server);
        FileWriter f = new FileWriter(getSettingsLocation().toFile(), false);
        f.write(settingsJS.toString(2));
        f.close();
      }
    } catch(Exception e) { 
      System.out.println("Error while reading "  + getSettingsLocation() + " " + e);
    }
  }
}

