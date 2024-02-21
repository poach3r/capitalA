package plugins;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.javacord.api.DiscordApi;
import java.lang.reflect.Constructor;
import org.javacord.api.event.message.MessageCreateEvent;

public class method1 {
  public static void onInit(DiscordApi api) {
    File pluginDir = new File("./plugins/");
    File[] pluginFiles = pluginDir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(".java");
      }
    });

    if (pluginFiles != null) {
      for (File file : pluginFiles) {
        try {
          JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
          if (compiler == null)
            throw new IllegalStateException("Java compiler not found. Make sure you're using a JDK.");

          int result = compiler.run(null, null, null, file.getPath());

          if (result != 0)
            throw new RuntimeException("Compilation failed for file: " + file.getName());

          String className = file.getName().replace(".java", "");
          URLClassLoader classLoader = new URLClassLoader(new URL[]{file.getParentFile().toURI().toURL()});
          Class<?> pluginClass = classLoader.loadClass(className);

          if((int) pluginClass.getMethod("getPos").invoke(null) == 1) {
            Constructor<?> constructor = pluginClass.getConstructor(DiscordApi.class);
            Object instance = constructor.newInstance(api);
            System.out.println("-------------------");
            System.out.println("Author: " + (String)pluginClass.getMethod("getAuthor").invoke(instance));
            System.out.println("Name: " + (String)pluginClass.getMethod("getName").invoke(instance));
            System.out.println("Description: " + (String)pluginClass.getMethod("getDesc").invoke(instance));
            pluginClass.getMethod("meat").invoke(instance);
            System.out.println("-------------------");
          }

          classLoader.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void onParse(MessageCreateEvent event) {
    File pluginDir = new File("./plugins/");
    File[] pluginFiles = pluginDir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(".java");
      }
    });

    if (pluginFiles != null) {
      for (File file : pluginFiles) {
        try {
          JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
          if (compiler == null)
            throw new IllegalStateException("Java compiler not found. Make sure you're using a JDK.");

          int result = compiler.run(null, null, null, file.getPath());

          if (result != 0)
            throw new RuntimeException("Compilation failed for file: " + file.getName());

          String className = file.getName().replace(".java", "");
          URLClassLoader classLoader = new URLClassLoader(new URL[]{file.getParentFile().toURI().toURL()});
          Class<?> pluginClass = classLoader.loadClass(className);

          if((int) pluginClass.getMethod("getPos").invoke(null) == 2) {
            Constructor<?> constructor = pluginClass.getConstructor(MessageCreateEvent.class);
            Object instance = constructor.newInstance(event);
            System.out.println("-------------------");
            System.out.println("Author: " + (String)pluginClass.getMethod("getAuthor").invoke(instance));
            System.out.println("Name: " + (String)pluginClass.getMethod("getName").invoke(instance));
            System.out.println("Description: " + (String)pluginClass.getMethod("getDesc").invoke(instance));
            pluginClass.getMethod("meat").invoke(instance);
            System.out.println("-------------------");
          }

          classLoader.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
