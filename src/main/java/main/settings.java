// TODO entire per server configurations

package main;

import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class settings {
  private static String token; 
  private static String prefix; 
  private static int kickThreshold;
  private static int kickTimeLimit;
  private static int banThreshold;
  private static int banTimeLimit;
  private static int muteThreshold;
  private static int muteTimeLimit;
  private static String huggingFaceKey;
  private static String filter[] = new String[50];
  private static Path settingsLocation; 

  public settings() {
    String token = ""; 
    String prefix = "$";
    kickThreshold = 10; 
    kickTimeLimit = 1;
    banThreshold = 10;
    banTimeLimit = 1;
    muteThreshold = 10;
    muteTimeLimit = 1;
    huggingFaceKey = "";
    filter = main.filter.initFilter(filter); 
    settingsLocation = Paths.get("./settings.json");
  }

  public static void setToken(String t) {
    token = t;
  }

  public static void setPrefix(String p) {
    prefix = p;
  }

  public static void setKickThreshold(int kt) {
    kickThreshold = kt;
  }

  public static void setKickTimeLimit(int ktl) {
    kickTimeLimit = ktl;
  }

  public static void setBanThreshold(int bt) {
    banThreshold = bt;
  }

  public static void setBanTimeLimit(int btl) {
    banTimeLimit = btl;
  }

  public static void setMuteThreshold(int mt) {
    muteThreshold = mt;
  }

  public static void setMuteTimeLimit(int mtl) {
    muteTimeLimit = mtl;
  }

  public static void setHuggingFaceKey(String hfk) {
    huggingFaceKey = hfk;
  }

  public static void setFilter(String f[]) {
    filter = f;
  }

  public static void setFilter(String f, int i) {
    filter[i] = f;
  }

  public static void setSettingsLocation(Path sl) {
    settingsLocation = sl;
  }

  public static String getToken() {
    return token;
  }

  public static String getPrefix() {
    return prefix;
  }

  public static int getKickThreshold() {
    return kickThreshold;
  }

  public static int getKickTimeLimit() {
    return kickTimeLimit;
  }

  public static int getBanThreshold() {
    return banThreshold;
  }

  public static int getBanTimeLimit() {
    return banTimeLimit;
  }

  public static int getMuteThreshold() {
    return muteThreshold;
  }

  public static int getMuteTimeLimit() {
    return muteTimeLimit;
  }

  public static String getHuggingFaceKey() {
    return huggingFaceKey;
  }

  public static String[] getFilter() {
    return filter;
  }

  public static String getFilter(int i) {
    return filter[i]; 
  }

  public static Path getSettingsLocation() {
    return settingsLocation;
  }
}

class parseSettings {
  public static void mainFunc() {
    if(!settings.getSettingsLocation().toFile().exists()) {
      System.out.println(settings.getSettingsLocation() + " does not exist");
      System.exit(1);
    }

    System.out.println("Parsing " + settings.getSettingsLocation());
    try { // parse settings json 
      JSONObject settingsJS = new JSONObject(new String(Files.readAllBytes(settings.getSettingsLocation())));

      // check for token, if it doesnt exist then quit
      if(settingsJS.has("token"))
        settings.setToken(settingsJS.getString("token"));
      else {
        System.out.println(settings.getSettingsLocation() + " does not have JSON key 'token'");
        System.exit(1);
      }

      if(settingsJS.has("votes")) { // spahgetti ahh code
        if(settingsJS.getJSONObject("votes").has("kicks")) {
          if(settingsJS.getJSONObject("votes").getJSONObject("kicks").has("threshold"))
            settings.setKickThreshold(settingsJS.getJSONObject("votes").getJSONObject("kicks").getInt("threshold"));
          if(settingsJS.getJSONObject("votes").getJSONObject("kicks").has("timeLimit")) 
            settings.setKickTimeLimit(settingsJS.getJSONObject("votes").getJSONObject("kicks").getInt("timeLimit"));
        }

        if(settingsJS.getJSONObject("votes").has("bans")) {
          if(settingsJS.getJSONObject("votes").getJSONObject("bans").has("threshold"))
            settings.setBanThreshold(settingsJS.getJSONObject("votes").getJSONObject("bans").getInt("threshold"));
          if(settingsJS.getJSONObject("votes").getJSONObject("bans").has("timeLimit"))
            settings.setBanTimeLimit(settingsJS.getJSONObject("votes").getJSONObject("bans").getInt("timeLimit"));
        }

        if(settingsJS.getJSONObject("votes").has("mutes")) {
          if(settingsJS.getJSONObject("votes").getJSONObject("mutes").has("threshold"))
            settings.setMuteThreshold(settingsJS.getJSONObject("votes").getJSONObject("mutes").getInt("threshold"));
          if(settingsJS.getJSONObject("votes").getJSONObject("mutes").has("timeLimit")) 
            settings.setMuteTimeLimit(settingsJS.getJSONObject("votes").getJSONObject("mutes").getInt("timeLimit"));
        }
      }

      if(settingsJS.has("prefix"))
        settings.setPrefix(settingsJS.getString("prefix"));

      if(settingsJS.has("huggingFaceKey"))
        settings.setHuggingFaceKey(settingsJS.getString("huggingFaceKey"));
      
      // load filter
      if(settingsJS.has("filters")) {
        if(settingsJS.getJSONObject("filters").has("default")) {
          for(int i = 0; i < settingsJS.getJSONObject("filters").getJSONArray("default").length(); i++) {
            settings.setFilter(settingsJS.getJSONObject("filters").getJSONArray("default").getString(i), i);
          }
        }
      }
    } catch(Exception e) { System.out.println("Error while reading "  + settings.getSettingsLocation() + " " + e); }
  }
}
