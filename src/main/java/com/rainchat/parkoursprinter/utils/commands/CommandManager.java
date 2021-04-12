package com.rainchat.parkoursprinter.utils.commands;

import com.rainchat.parkoursprinter.utils.ServerProject;
import com.rainchat.parkoursprinter.utils.ServerVersion;
import com.rainchat.parkoursprinter.utils.commands.AbstractCommand.ReturnType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final HashMap<String, SimpleNestedCommand> commands = new HashMap<>();
    private String msg_noConsole;
    private String msg_noPerms;
    private String msg_noCommand;
    private List<String> msg_syntaxError;
    private boolean allowLooseCommands;

    public CommandManager(JavaPlugin plugin) {
        this.msg_noConsole = ChatColor.RED + "You must be a player to use this command.";
        this.msg_noPerms = ChatColor.RED + "You do not have permission to do that.";
        this.msg_noCommand = ChatColor.GRAY + "The command you entered does not exist or is spelt incorrectly.";
        this.msg_syntaxError = Arrays.asList(ChatColor.RED + "Invalid Syntax!", ChatColor.GRAY + "The valid syntax is: " + ChatColor.GOLD + "%syntax%" + ChatColor.GRAY + ".");
        this.allowLooseCommands = false;
        this.plugin = plugin;
    }

    public void setNoConsoleMessage(String msg_noConsole) {
        this.msg_noConsole = msg_noConsole;
    }

    public void setNoPermsMessage(String msg_noPerms) {
        this.msg_noPerms = msg_noPerms;
    }

    public void setNoCommandMessage(String msg_noCommand) {
        this.msg_noCommand = msg_noCommand;
    }

    public void setSyntaxErrorMessage(List<String> msg_syntaxError) {
        this.msg_syntaxError = msg_syntaxError;
    }

    public Set<String> getCommands() {
        return Collections.unmodifiableSet(this.commands.keySet());
    }

    public List<String> getSubCommands(String command) {
        SimpleNestedCommand nested = command == null ? null : (SimpleNestedCommand) this.commands.get(command.toLowerCase());
        return nested == null ? Collections.emptyList() : new ArrayList<>(nested.children.keySet());
    }

    public Set<AbstractCommand> getAllCommands() {
        HashSet<AbstractCommand> all = new HashSet<>();
        this.commands.values().stream().filter((c) -> {
            return c.parent != null && !all.contains(c.parent);
        }).forEach((c) -> {
            all.add(c.parent);
            c.children.values().stream().filter((s) -> {
                return !all.contains(s);
            }).forEach(all::add);
        });
        return all;
    }

    public CommandManager registerCommandDynamically(String command) {
        registerCommandDynamically(this.plugin, command, this, this);
        return this;
    }

    public SimpleNestedCommand registerCommandDynamically(AbstractCommand abstractCommand) {
        SimpleNestedCommand nested = new SimpleNestedCommand(abstractCommand);
        abstractCommand.getCommands().forEach((cmd) -> {
            registerCommandDynamically(this.plugin, cmd, this, this);
            this.commands.put(cmd.toLowerCase(), nested);
            PluginCommand pcmd = this.plugin.getCommand(cmd);
            if (pcmd != null) {
                pcmd.setExecutor(this);
                pcmd.setTabCompleter(this);
            } else {
                this.plugin.getLogger().warning("Failed to register command: /" + cmd);
            }

        });
        return nested;
    }

    public SimpleNestedCommand addCommand(AbstractCommand abstractCommand) {
        SimpleNestedCommand nested = new SimpleNestedCommand(abstractCommand);
        abstractCommand.getCommands().forEach((cmd) -> {
            this.commands.put(cmd.toLowerCase(), nested);
            PluginCommand pcmd = this.plugin.getCommand(cmd);
            if (pcmd != null) {
                pcmd.setExecutor(this);
                pcmd.setTabCompleter(this);
            } else {
                this.plugin.getLogger().warning("Failed to register command: /" + cmd);
            }

        });
        return nested;
    }


    public CommandManager addCommands(AbstractCommand... abstractCommands) {
        AbstractCommand[] var2 = abstractCommands;
        int var3 = abstractCommands.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            AbstractCommand abstractCommand = var2[var4];
            this.addCommand(abstractCommand);
        }

        return this;
    }

    public CommandManager setExecutor(String command) {
        PluginCommand pcmd = command == null ? null : this.plugin.getCommand(command);
        if (pcmd != null) {
            pcmd.setExecutor(this);
        } else {
            this.plugin.getLogger().warning("Failed to register command: /" + command);
        }

        return this;
    }

    public CommandManager setUseClosestCommand(boolean bool) {
        this.allowLooseCommands = bool;
        return this;
    }

    public boolean onCommand(@NotNull CommandSender commandSender, Command command, @NotNull String label, String[] args) {
        SimpleNestedCommand nested = (SimpleNestedCommand) this.commands.get(command.getName().toLowerCase());
        if (nested != null) {
            if (args.length != 0 && !nested.children.isEmpty()) {
                String subCmd = this.getSubCommand(nested, args);
                if (subCmd != null) {
                    AbstractCommand sub = (AbstractCommand) nested.children.get(subCmd);
                    int i = subCmd.indexOf(32) == -1 ? 1 : 2;
                    String[] newArgs = new String[args.length - i];
                    System.arraycopy(args, i, newArgs, 0, newArgs.length);
                    this.processRequirements(sub, commandSender, newArgs);
                    return true;
                }
            }

            if (nested.parent != null) {
                this.processRequirements(nested.parent, commandSender, args);
                return true;
            }
        }

        commandSender.sendMessage(this.msg_noCommand);
        return true;
    }

    private String getSubCommand(SimpleNestedCommand nested, String[] args) {
        String cmd = args[0].toLowerCase();
        if (nested.children.containsKey(cmd)) {
            return cmd;
        } else {
            String match = null;
            int count;
            if (args.length >= 2 && nested.children.keySet().stream().anyMatch((k) -> {
                return k.indexOf(32) != -1;
            })) {
                for (count = args.length; count > 1; --count) {
                    String cmd2 = String.join(" ", (CharSequence[]) Arrays.copyOf(args, count)).toLowerCase();
                    if (nested.children.containsKey(cmd2)) {
                        return cmd2;
                    }
                }
            }

            if (this.allowLooseCommands) {
                count = 0;

                for (String c : nested.children.keySet()) {
                    if (c.startsWith(cmd)) {
                        match = c;
                        ++count;
                        if (count > 1) {
                            match = null;
                            break;
                        }
                    }
                }
            }

            return match;
        }
    }

    private void processRequirements(AbstractCommand command, CommandSender sender, String[] args) {
        if (!(sender instanceof Player) && command.isNoConsole()) {
            sender.sendMessage(this.msg_noConsole);
        } else if (command.getPermissionNode() != null && !sender.hasPermission(command.getPermissionNode())) {
            sender.sendMessage(this.msg_noPerms);
        } else {
            ReturnType returnType = command.runCommand(sender, args);
            if (returnType == ReturnType.NEEDS_PLAYER) {
                sender.sendMessage(this.msg_noConsole);
            } else if (returnType == ReturnType.SYNTAX_ERROR) {
                Iterator var5 = this.msg_syntaxError.iterator();

                while (var5.hasNext()) {
                    String s = (String) var5.next();
                    sender.sendMessage(s.replace("%syntax%", command.getSyntax()));
                }
            }

        }
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, String[] args) {
        SimpleNestedCommand nested = this.commands.get(command.getName().toLowerCase());
        if (nested != null) {
            if (args.length != 0 && !nested.children.isEmpty()) {
                boolean op = sender.isOp();
                boolean console = !(sender instanceof Player);
                String subCmd;
                if (args.length == 1) {
                    subCmd = args[0].toLowerCase();
                    return nested.children.entrySet().stream().filter((e) -> {
                        return !console || !( e.getValue()).isNoConsole();
                    }).filter((e) -> {
                        return (e.getKey()).startsWith(subCmd);
                    }).filter((e) -> {
                        return op || ( e.getValue()).getPermissionNode() == null || sender.hasPermission(( e.getValue()).getPermissionNode());
                    }).map(Map.Entry::getKey).collect(Collectors.toList());
                } else {
                    subCmd = this.getSubCommand(nested, args);
                    AbstractCommand sub;
                    if (subCmd == null || (sub = nested.children.get(subCmd)) == null || console && sub.isNoConsole() || !op && sub.getPermissionNode() != null && !sender.hasPermission(sub.getPermissionNode())) {
                        return Collections.emptyList();
                    } else {
                        int i = subCmd.indexOf(32) == -1 ? 1 : 2;
                        String[] newArgs = new String[args.length - i];
                        System.arraycopy(args, i, newArgs, 0, newArgs.length);
                        return this.fetchList(sub, newArgs, sender);
                    }
                }
            } else {
                return nested.parent != null ? nested.parent.onTab(sender, args) : null;
            }
        } else {
            return Collections.emptyList();
        }
    }

    private List<String> fetchList(AbstractCommand abstractCommand, String[] args, CommandSender sender) {
        List<String> list = abstractCommand.onTab(sender, args);
        if (args.length != 0) {
            String str = args[args.length - 1];
            if (list != null && str != null && str.length() >= 1) {
                try {
                    list.removeIf((s) -> {
                        return !s.toLowerCase().startsWith(str.toLowerCase());
                    });
                } catch (UnsupportedOperationException ignored) {
                }
            }
        }

        return list;
    }

    public static void registerCommandDynamically(Plugin plugin, String command, CommandExecutor executor, TabCompleter tabManager) {
        try {
            Class<?> classCraftServer = Bukkit.getServer().getClass();
            Field fieldCommandMap = classCraftServer.getDeclaredField("commandMap");
            fieldCommandMap.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) fieldCommandMap.get(Bukkit.getServer());
            Constructor<PluginCommand> constructorPluginCommand = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructorPluginCommand.setAccessible(true);
            PluginCommand commandObject = constructorPluginCommand.newInstance(command, plugin);
            if (ServerProject.isServer(ServerProject.PAPER, ServerProject.TACO) && ServerVersion.isServerVersionBelow(ServerVersion.V1_9)) {
                Class<?> clazz = Class.forName("co.aikar.timings.TimingsManager");
                Method method = clazz.getMethod("getCommandTiming", String.class, Command.class);
                Field field = PluginCommand.class.getField("timings");
                field.set(commandObject, method.invoke(null, plugin.getName().toLowerCase(), commandObject));
            }

            commandObject.setExecutor(executor);
            commandObject.setTabCompleter(tabManager);
            Field fieldKnownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            fieldKnownCommands.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) fieldKnownCommands.get(commandMap);
            knownCommands.put(command, commandObject);
        } catch (ReflectiveOperationException var12) {
            var12.printStackTrace();
        }

    }
}