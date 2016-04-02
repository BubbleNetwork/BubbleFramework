package com.thebubblenetwork.api.framework.util.mc.chat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

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

    public static class MessageBuilder extends ComponentBuilder implements Cloneable {
        public MessageBuilder(ComponentBuilder original) {
            super(original);
        }

        public MessageBuilder(String text) {
            super(text);
        }
        @Override
        public MessageBuilder reset() {
            return (MessageBuilder)super.reset();
        }

        @Override
        public MessageBuilder retain(FormatRetention retention) {
            return (MessageBuilder)super.retain(retention);
        }

        @Override
        public MessageBuilder event(HoverEvent hoverEvent) {
            return (MessageBuilder)super.event(hoverEvent);
        }

        @Override
        public MessageBuilder event(ClickEvent clickEvent) {
            return (MessageBuilder)super.event(clickEvent);
        }

        @Override
        public MessageBuilder obfuscated(boolean obfuscated) {
            return (MessageBuilder)super.obfuscated(obfuscated);
        }

        @Override
        public MessageBuilder underlined(boolean underlined) {
            return (MessageBuilder)super.underlined(underlined);
        }

        @Override
        public MessageBuilder strikethrough(boolean strikethrough) {
            return (MessageBuilder)super.strikethrough(strikethrough);
        }

        @Override
        public MessageBuilder italic(boolean italic) {
            return (MessageBuilder)super.italic(italic);
        }

        @Override
        public MessageBuilder bold(boolean bold) {
            return (MessageBuilder)super.bold(bold);
        }

        @Override
        public MessageBuilder color(ChatColor color) {
            return (MessageBuilder)super.color(color);
        }

        @Override
        public MessageBuilder append(String text) {
            return (MessageBuilder)super.append(text);
        }

        @Override
        public MessageBuilder append(String text, FormatRetention retention) {
            return (MessageBuilder)super.append(text, retention);
        }

        @Override
        public MessageBuilder clone(){
            return new MessageBuilder(this);
        }
    }
}
