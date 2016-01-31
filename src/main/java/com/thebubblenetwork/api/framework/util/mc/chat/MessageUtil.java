package com.thebubblenetwork.api.framework.util.mc.chat;

import com.google.common.collect.Iterables;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 24/12/2015.
 */
public class MessageUtil {

    public static TextComponent createMessage(String message, HoverEvent hoverEvent, ClickEvent clickEvent) {
        TextComponent component = new TextComponent(message);
        component.setHoverEvent(hoverEvent);
        component.setClickEvent(clickEvent);
        return component;
    }

    public BaseComponent[] createMessageArray(TextComponent... textComponents) {
        return textComponents;
    }

    public static class MessageBuilder implements Cloneable {
        private final List<BaseComponent> components = new ArrayList<>();

        private MessageBuilder() {

        }

        public MessageBuilder(String s) {
            components.add(new TextComponent(s));
        }

        private BaseComponent getFirst() {
            return Iterables.getLast(components);
        }

        public MessageBuilder withEvent(HoverEvent event) {
            getFirst().setHoverEvent(event);
            return this;
        }

        public MessageBuilder withEvent(ClickEvent event) {
            getFirst().setClickEvent(event);
            return this;
        }

        public MessageBuilder withColor(ChatColor color) {
            getFirst().setColor(color);
            return this;
        }

        public MessageBuilder append(String s) {
            components.add(new TextComponent(s));
            return this;
        }

        public MessageBuilder withExtra(BaseComponent component, BaseComponent... components) {
            this.components.add(component);
            this.components.addAll(Arrays.asList(components));
            return this;
        }

        public BaseComponent[] build() {
            return components.toArray(new BaseComponent[0]);
        }

        public MessageBuilder clone() {
            MessageBuilder instance = new MessageBuilder();
            instance.components.addAll(components);
            return instance;
        }
    }
}
