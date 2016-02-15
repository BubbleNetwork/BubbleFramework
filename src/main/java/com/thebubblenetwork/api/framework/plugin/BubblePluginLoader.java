package com.thebubblenetwork.api.framework.plugin;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Jacob on 12/12/2015.
 */
public class BubblePluginLoader extends URLClassLoader {
    private BubblePlugin plugin;
    private PluginDescriptionFile file;
    private File jar;

    public BubblePluginLoader(File jar, PluginDescriptionFile file) throws MalformedURLException,
            InvalidDescriptionException, InvalidPluginException {
        super(new URL[]{jar.toURI().toURL()}, BubbleNetwork.class.getClassLoader());
        this.jar = jar;
        Class ex;
        this.file = file;
        try {
            ex = loadClass(file.getMain());
        } catch (Exception e) {
            throw new InvalidPluginException("Cannot find main class via ex`" + file.getMain() + "\'", e);
        }
        if (ex == null) {
            throw new InvalidPluginException("Cannot find main class no ex `" + file.getMain() + "\'");
        }
        Class<? extends BubblePlugin> subclass;
        try {
            subclass = ex.asSubclass(BubblePlugin.class);
        } catch (Exception e) {
            throw new InvalidPluginException("main class `" + file.getMain() + "\' does not extend BubblePlugin", e);
        }
        try {
            plugin = (BubblePlugin) subclass.newInstance();
        } catch (Exception e) {
            throw new InvalidPluginException("Error loading main class`" + file.getMain(), e);
        }
    }

    public static PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        Validate.notNull(file, "File cannot be null");
        JarFile jar = null;
        InputStream stream = null;

        PluginDescriptionFile var6;
        try {
            jar = new JarFile(file);
            JarEntry ex = jar.getJarEntry("bubbleplugin.yml");
            if (ex == null) {
                throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain bubbleplugin" +
                        ".yml"));
            }

            stream = jar.getInputStream(ex);
            var6 = new PluginDescriptionFile(stream);
        } catch (IOException var16) {
            throw new InvalidDescriptionException(var16);
        } catch (YAMLException var17) {
            throw new InvalidDescriptionException(var17);
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException var15) {
                    ;
                }
            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException var14) {
                    ;
                }
            }

        }

        return var6;
    }

    public File getJar(){
        return jar;
    }

    public PluginDescriptionFile getFile() {
        return file;
    }

    public BubblePlugin getPlugin() {
        return plugin;
    }
}
