package com.rainchat.rainlib.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractCommand {
    private final AbstractCommand.CommandType _cmdType;
    private final boolean _hasArgs;
    private final List<String> _handledCommands = new ArrayList();

    protected AbstractCommand(AbstractCommand.CommandType type, String... command) {
        this._handledCommands.addAll(Arrays.asList(command));
        this._hasArgs = false;
        this._cmdType = type;
    }

    protected AbstractCommand(AbstractCommand.CommandType type, boolean hasArgs, String... command) {
        this._handledCommands.addAll(Arrays.asList(command));
        this._hasArgs = hasArgs;
        this._cmdType = type;
    }

    /**
     * @deprecated
     */
    @Deprecated
    protected AbstractCommand(boolean noConsole, String... command) {
        this._handledCommands.addAll(Arrays.asList(command));
        this._hasArgs = false;
        this._cmdType = noConsole ? AbstractCommand.CommandType.PLAYER_ONLY : AbstractCommand.CommandType.CONSOLE_OK;
    }

    /**
     * @deprecated
     */
    @Deprecated
    protected AbstractCommand(boolean noConsole, boolean hasArgs, String... command) {
        this._handledCommands.addAll(Arrays.asList(command));
        this._hasArgs = hasArgs;
        this._cmdType = noConsole ? AbstractCommand.CommandType.PLAYER_ONLY : AbstractCommand.CommandType.CONSOLE_OK;
    }

    public final List<String> getCommands() {
        return Collections.unmodifiableList(this._handledCommands);
    }

    public final void addSubCommand(String command) {
        this._handledCommands.add(command);
    }

    protected abstract AbstractCommand.ReturnType runCommand(CommandSender var1, String... var2);

    protected abstract List<String> onTab(CommandSender var1, String... var2);

    public abstract String getPermissionNode();

    public abstract String getSyntax();

    public abstract String getDescription();

    public boolean hasArgs() {
        return this._hasArgs;
    }

    public boolean isNoConsole() {
        return this._cmdType == AbstractCommand.CommandType.PLAYER_ONLY;
    }

    public enum CommandType {
        PLAYER_ONLY,
        CONSOLE_OK;

        CommandType() {
        }
    }

    public enum ReturnType {
        SUCCESS,
        NEEDS_PLAYER,
        FAILURE,
        SYNTAX_ERROR;

        ReturnType() {
        }
    }
}
