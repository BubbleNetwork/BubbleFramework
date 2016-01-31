package com.thebubblenetwork.api.framework.plugin;

/**
 * Created by Jacob on 12/12/2015.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.google.common.collect.ImmutableList;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginAwareness;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class PluginDescriptionFile {
    private static final ThreadLocal<Yaml> YAML = new ThreadLocal() {
        protected Yaml initialValue() {
            return new Yaml(new SafeConstructor() {
                {
                    this.yamlConstructors.put(null, new AbstractConstruct() {
                        public Object construct(final Node node) {
                            return !node.getTag().startsWith("!@") ? SafeConstructor.undefinedConstructor.construct
                                    (node) : new PluginAwareness() {
                                public String toString() {
                                    return node.toString();
                                }
                            };
                        }
                    });
                    PluginAwareness.Flags[] var5;
                    int var4 = (var5 = PluginAwareness.Flags.values()).length;

                    for (int var3 = 0; var3 < var4; ++var3) {
                        final PluginAwareness.Flags flag = var5[var3];
                        this.yamlConstructors.put(new Tag("!@" + flag.name()), new AbstractConstruct() {
                            public PluginAwareness.Flags construct(Node node) {
                                return flag;
                            }
                        });
                    }

                }
            });
        }
    };
    String rawName = null;
    private String name = null;
    private String main = null;
    private String classLoaderOf = null;
    private String version = null;
    private String description = null;
    private List<String> authors = null;
    private String prefix = null;
    private short priority = LoadPriority.NORMAL;

    public PluginDescriptionFile(InputStream stream) throws InvalidDescriptionException {
        this.loadMap(this.asMap(((Yaml) YAML.get()).load(stream)));
    }

    public PluginDescriptionFile(Reader reader) throws InvalidDescriptionException {
        this.loadMap(this.asMap(((Yaml) YAML.get()).load(reader)));
    }

    public PluginDescriptionFile(String pluginName, String pluginVersion, String mainClass) {
        this.name = pluginName.replace(' ', '_');
        this.version = pluginVersion;
        this.main = mainClass;
    }


    public static org.bukkit.plugin.PluginDescriptionFile asMirror(PluginDescriptionFile f) {
        return new org.bukkit.plugin.PluginDescriptionFile(f.getName(), f.getVersion(), f.getMain());
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getMain() {
        return this.main;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getAuthors() {
        return this.authors;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getFullName() {
        return this.name + " v" + this.version;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String getClassLoaderOf() {
        return this.classLoaderOf;
    }

    public void save(Writer writer) {
        ((Yaml) YAML.get()).dump(this.saveMap(), writer);
    }

    private void loadMap(Map<?, ?> map) throws InvalidDescriptionException {
        try {
            this.name = this.rawName = map.get("name").toString();
            if (!this.name.matches("^[A-Za-z0-9 _.-]+$")) {
                throw new InvalidDescriptionException("name \'" + this.name + "\' contains invalid characters.");
            }

            this.name = this.name.replace(' ', '_');
        } catch (NullPointerException var25) {
            throw new InvalidDescriptionException(var25, "name is not defined");
        } catch (ClassCastException var26) {
            throw new InvalidDescriptionException(var26, "name is of wrong type");
        }

        try {
            this.version = map.get("version").toString();
        } catch (NullPointerException var19) {
            throw new InvalidDescriptionException(var19, "version is not defined");
        } catch (ClassCastException var20) {
            throw new InvalidDescriptionException(var20, "version is of wrong type");
        }

        try {
            this.main = map.get("main").toString();
            if (this.main.startsWith("org.bukkit.")) {
                throw new InvalidDescriptionException("main may not be within the org.bukkit namespace");
            }
        } catch (NullPointerException var17) {
            throw new InvalidDescriptionException(var17, "main is not defined");
        } catch (ClassCastException var18) {
            throw new InvalidDescriptionException(var18, "main is of wrong type");
        }

        Iterator var4;

        if (map.get("class-loader-of") != null) {
            this.classLoaderOf = map.get("class-loader-of").toString();
        }

        if (map.get("description") != null) {
            this.description = map.get("description").toString();
        }

        if (map.get("priority") != null) {
            this.priority = Byte.parseByte(map.get("priority").toString());
        }

        Object ex4;
        if (map.get("authors") != null) {
            com.google.common.collect.ImmutableList.Builder ex2 = ImmutableList.builder();
            if (map.get("author") != null) {
                ex2.add(map.get("author").toString());
            }

            try {
                var4 = ((Iterable) map.get("authors")).iterator();

                while (var4.hasNext()) {
                    ex4 = var4.next();
                    ex2.add(ex4.toString());
                }
            } catch (ClassCastException var22) {
                throw new InvalidDescriptionException(var22, "authors are of wrong type");
            } catch (NullPointerException var23) {
                throw new InvalidDescriptionException(var23, "authors are improperly defined");
            }

            this.authors = ex2.build();
        } else if (map.get("author") != null) {
            this.authors = ImmutableList.of(map.get("author").toString());
        } else {
            this.authors = ImmutableList.of();
        }
    }

    private Map<String, Object> saveMap() {
        HashMap map = new HashMap();
        map.put("name", this.name);
        map.put("main", this.main);
        map.put("version", this.version);

        if (this.description != null) {
            map.put("description", this.description);
        }

        if (this.authors.size() == 1) {
            map.put("author", this.authors.get(0));
        } else if (this.authors.size() > 1) {
            map.put("authors", this.authors);
        }

        if (this.classLoaderOf != null) {
            map.put("class-loader-of", this.classLoaderOf);
        }

        if (this.prefix != null) {
            map.put("prefix", this.prefix);
        }

        return map;
    }

    private Map<?, ?> asMap(Object object) throws InvalidDescriptionException {
        if (object instanceof Map) {
            return (Map) object;
        } else {
            throw new InvalidDescriptionException(object + " is not properly structured.");
        }
    }

    public short getPriority() {
        return priority;
    }


    @Deprecated
    public String getRawName() {
        return this.rawName;
    }
}
