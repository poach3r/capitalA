package main;

import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class settings {
  public static String token = ""; 
  public static String prefix = "$"; // TODO add support for custom prefixes
  public static int kickThreshold = 10; // number of votes needed for a successful kick
  public static int kickTimeLimit = 1; // voting time limit in minutes
  public static int banThreshold = 10;
  public static int banTimeLimit = 1;
  public static int muteThreshold = 10;
  public static int muteTimeLimit = 1;
  public static String huggingFaceKey = "";
  public static String filter[] = new String[50];
  public static final Path settingsLocation = Paths.get("./settings.json");

  public static void mainFunc() {
    filter = main.filter.initFilter(filter);

    if(!settingsLocation.toFile().exists()) {
      System.out.println(settingsLocation + " does not exist");
      System.exit(1);
    }

    System.out.println("Parsing " + settingsLocation);
    try { // parse settings json 
      JSONObject settings = new JSONObject(new String(Files.readAllBytes(settingsLocation)));

      // check for token, if it doesnt exist then quit
      if(settings.has("token")) { token = settings.getString("token"); }
      else {
        System.out.println(settingsLocation + " does not have JSON key 'token'");
        System.exit(1);
      }

      if(settings.has("votes")) {
        if(settings.getJSONObject("votes").has("kicks")) {
          if(settings.getJSONObject("votes").getJSONObject("kicks").has("threshold")) { kickThreshold = settings.getJSONObject("votes").getJSONObject("kicks").getInt("threshold"); }
          if(settings.getJSONObject("votes").getJSONObject("kicks").has("timeLimit")) { kickThreshold = settings.getJSONObject("votes").getJSONObject("kicks").getInt("timeLimit"); }
        }
        if(settings.getJSONObject("votes").has("bans")) {
          if(settings.getJSONObject("votes").getJSONObject("bans").has("threshold")) { kickThreshold = settings.getJSONObject("votes").getJSONObject("bans").getInt("threshold"); }
          if(settings.getJSONObject("votes").getJSONObject("bans").has("timeLimit")) { kickThreshold = settings.getJSONObject("votes").getJSONObject("bans").getInt("timeLimit"); }
        }
        if(settings.getJSONObject("votes").has("mutes")) {
          if(settings.getJSONObject("votes").getJSONObject("mutes").has("threshold")) { kickThreshold = settings.getJSONObject("votes").getJSONObject("mutes").getInt("threshold"); }
          if(settings.getJSONObject("votes").getJSONObject("mutes").has("timeLimit")) { kickThreshold = settings.getJSONObject("votes").getJSONObject("mutes").getInt("timeLimit"); }
        }
      }

      if(settings.has("prefix")) { prefix = settings.getString("prefix"); }
      if(settings.has("huggingFaceKey")) { huggingFaceKey = settings.getString("huggingFaceKey"); }
      
      // load filter
      if(settings.has("filters")) {
        if(settings.getJSONObject("filters").has("default")) {
          for(int i = 0; i < settings.getJSONObject("filters").getJSONArray("overarching").length(); i++) {
            filter[i] = settings.getJSONObject("filters").getJSONArray("overarching").getString(i);
          }
        }
      }

    } catch(Exception e) { System.out.println("Error while reading "  + settingsLocation + " " + e); }
  }
}
