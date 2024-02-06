package main;

import org.json.JSONObject;
import java.nio.file.Files;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONArray;

public class filter {
  public static String[] server(MessageCreateEvent event) {
    String filter[] = new String[50];
    filter = initFilter(filter);
    String serverId = event.getServer().get().getIdAsString();
    try { 
      JSONObject settings = new JSONObject(new String(Files.readAllBytes(main.settings.getSettingsLocation())));

      if(!settings.getJSONObject("filters").has(serverId)) { return filter; }

      JSONArray temp = settings.getJSONObject("filters").getJSONArray(serverId);

      for(int i = 0; i < temp.length(); i++) {
        filter[i] = temp.getString(i);
      }
    } catch(Exception e) {
      System.out.println("Error while parsing server filter: " + e);
    }
    return filter;
  }

  // HACK initialize filter[]
  public static String[] initFilter(String filter[]) {
    for(int i = 0; i < filter.length; i++) {
      filter[i] = "this is a hack!!!!!!!!!";
    }
    return filter;
  }
}

