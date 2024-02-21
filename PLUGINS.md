# About

Plugins are (mostly) non-static Java programs which are executed either during initialization of the bot, or while parsing commands. Plugins allow you to extend the capabilities of the bot to do almost whatever you want.

# Installation

Simply drag and drop the .java file into your ./plugins/ directory.

# Creation

Plugins contain certain standardized parts, these are as follows

```java
private String author; // the plugin author
private String name; // the plugins name
private String desc; // a short description of what the plugin does
private double version; // the version number of the plugin
private MessageCreateEvent event; OR private DiscordApi api; // this depends on where you want your plugin to be ran
private static int pos; // where the plugin should be ran, 1 = initialization, 2 = command parsing

public CLASSNAME(MessageCreateEvent e OR DiscordApi a) {
  // a constructor which sets the values for all of the prior vars except for pos
}

// various getters
public String getAuthor() {
 return author;
}

public String getName() {
  return name;
}

public String getDesc() {
  return desc;
}

public static int getPos() {
  return pos;
}

public void meat() { // the main function
}
```

## Sample Plugins

```java
import org.javacord.api.DiscordApi;

public class sampleInitPlugin {
  private String author;
  private String name;
  private String desc;
  private static int pos = 1;
  private DiscordApi api;

  public sampleInitPlugin(DiscordApi a) {
    author = "poach3r";
    name = "sample init plugin";
    desc = "A sample plugin that prints \"hello world\" on initialization.";
    api = a;
  }

  public String getAuthor() {
    return author;
  }

  public String getName() {
    return name;
  }

  public String getDesc() {
    return desc;
  }

  public static int getPos() {
    return pos;
  }

  public void meat() {
    System.out.println("hello world!");
  }
}
```

```java
import org.javacord.api.event.message.MessageCreateEvent;

public class sampleParsePlugin {
  private String author;
  private String name;
  private String desc;
  private double version;
  private MessageCreateEvent event;
  private static int pos = 2;

  public sampleParsePlugin(MessageCreateEvent a) {
    author = "poach3r";
    name = "sample parse plugin";
    desc = "A sample plugin that prints \"hello world\" when parsing commands, in this case typing $ followed by anything will run it.";
    event = a;
  }

  public String getAuthor() {
    return author;
  }

  public String getName() {
    return name;
  }

  public String getDesc() {
    return desc;
  }

  public static int getPos() {
    return pos;
  }

  public void meat() {
    System.out.println("hello world!");
  }
}
```
