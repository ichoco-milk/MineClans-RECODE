package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.utils.Paginator;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FactionsHelpCommand {
    private static final String basePath = "factions.help.";
    private static Paginator<HelpCommand> helpCommands;

    static {
        Set<HelpCommand> helpCommands = ConcurrentHashMap.newKeySet();
        helpCommands.add(new HelpCommand("create", "/f create <factionName>"));
        helpCommands.add(new HelpCommand("join", "/f join <factionName>"));
        helpCommands.add(new HelpCommand("leave", "/f leave"));
        helpCommands.add(new HelpCommand("home", "/f home"));
        helpCommands.add(new HelpCommand("sethome", "/f sethome"));
        helpCommands.add(new HelpCommand("claim", "/f claim"));
        helpCommands.add(new HelpCommand("unclaim", "/f unclaim <all>"));
        helpCommands.add(new HelpCommand("claims", "/f claims"));
        helpCommands.add(new HelpCommand("focus", "/f focus"));
        helpCommands.add(new HelpCommand("unfocus", "/f unfocus"));
        helpCommands.add(new HelpCommand("deposit", "/f deposit <amount:all>"));
        helpCommands.add(new HelpCommand("withdraw", "/f withdraw <amount:all>"));
        helpCommands.add(new HelpCommand("carry", "/f carry"));
        helpCommands.add(new HelpCommand("who", "/f who <player:factionName>"));
        helpCommands.add(new HelpCommand("invite", "/f invite <player>"));
        helpCommands.add(new HelpCommand("uninvite", "/f uninvite <player>"));
        helpCommands.add(new HelpCommand("invites", "/f invites"));
        helpCommands.add(new HelpCommand("kick", "/f kick <player>"));
        helpCommands.add(new HelpCommand("announcement", "/f announcement [message here]"));
        helpCommands.add(new HelpCommand("promote", "/f promote <player>"));
        helpCommands.add(new HelpCommand("demote", "/f demote <player>"));
        helpCommands.add(new HelpCommand("open", "/f open"));
        helpCommands.add(new HelpCommand("rename", "/f rename <newName>"));
        helpCommands.add(new HelpCommand("setdiscord", "/f setdiscord <discord>"));
        helpCommands.add(new HelpCommand("disband", "/f disband"));
        helpCommands.add(new HelpCommand("displayname", "/f displayname <name>"));

        helpCommands.add(new HelpCommand("chat", "/f chat"));
        helpCommands.add(new HelpCommand("tl", "/f tl"));
        helpCommands.add(new HelpCommand("friendlyfire", "/f friendlyfire"));
        helpCommands.add(new HelpCommand("chest", "/f chest"));
        helpCommands.add(new HelpCommand("enemy", "/f enemy <faction>"));
        helpCommands.add(new HelpCommand("neutral", "/f neutral <faction>"));
        helpCommands.add(new HelpCommand("ally", "/f ally <faction>"));
        helpCommands.add(new HelpCommand("melody", "/f melody <arguments>"));
        helpCommands.add(new HelpCommand("buff", "/f buff"));
        helpCommands.add(new HelpCommand("events", "/f events"));
        helpCommands.add(new HelpCommand("list", "/f list"));
        helpCommands.add(new HelpCommand("transfer", "/f transfer <player>"));

        FactionsHelpCommand.helpCommands = new Paginator<>(helpCommands, 5);
    }

    public static void onCommand(CommandSender sender, int page) {
        if (page == -1) {
            page = 1;
        }
        Collection<HelpCommand> commands = helpCommands.getPage(page);
        int maxPages = helpCommands.getTotalPages();
        ConfigWrapper messages = MineClans.getInstance().getMessages();
        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(messages.getText(basePath + "header")
                .replace("{page}", String.valueOf(page))
                .replace("{maxPages}", String.valueOf(maxPages)))
                .append("\n");

        for (HelpCommand command : commands) {
            messageBuilder.append(command.getHelpLine()).append("\n");
        }

        String footer = messages.getText(basePath + "footer");

        if (!footer.isEmpty()) {
            messageBuilder
                    .append(footer
                            .replace("{page}", String.valueOf(page))
                            .replace("{maxPages}", String.valueOf(maxPages)))
                    .append("\n");
        }

        sender.sendMessage(messageBuilder.toString().trim());
    }

    private static class HelpCommand {
        private final String name;
        private final String usage;

        public HelpCommand(String name, String usage) {
            this.name = name;
            this.usage = usage;
        }

        public String getName() {
            return name;
        }

        public String getUsage() {
            return usage;
        }

        public String getDescription() {
            ConfigWrapper messages = MineClans.getInstance().getMessages();
            return messages.getText(basePath + "usage." + getName());
        }

        public String getHelpLine() {
            ConfigWrapper messages = MineClans.getInstance().getMessages();
            String lineFormat = messages.getText(basePath + "line");
            String lineText = lineFormat.replace("{command}", getUsage()).replace("{description}", getDescription());
            return lineText;
        }
    }
}
