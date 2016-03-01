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
public class BubbleAddonLoader extends URLClassLoader {
    public static AddonDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        Validate.notNull(file, "File cannot be null");
        JarFile jar = null;
        InputStream stream = null;

        AddonDescriptionFile var6;
        try {
            jar = new JarFile(file);
            JarEntry ex = jar.getJarEntry("bubbleplugin.yml");
            if (ex == null) {
                throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain bubbleplugin" + ".yml"));
            }

            stream = jar.getInputStream(ex);
            var6 = new AddonDescriptionFile(stream);
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

    private BubbleAddon plugin;
    private AddonDescriptionFile file;
    private File jar;

    public BubbleAddonLoader(File jar, AddonDescriptionFile file) throws MalformedURLException, InvalidDescriptionException, InvalidPluginException {
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
        Class<? extends BubbleAddon> subclass;
        try {
            subclass = ex.asSubclass(BubbleAddon.class);
        } catch (Exception e) {
            throw new InvalidPluginException("main class `" + file.getMain() + "\' does not extend BubblePlugin", e);
        }
        try {
            plugin = (BubbleAddon) subclass.newInstance();
        } catch (Exception e) {
            throw new InvalidPluginException("Error loading main class`" + file.getMain(), e);
        }
    }

    public File getJar() {
        return jar;
    }

    public AddonDescriptionFile getFile() {
        return file;
    }

    public BubbleAddon getPlugin() {
        return plugin;
    }
}
