package com.gmail.nossr50.runnables.commands;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.skills.SkillType;

import org.apache.commons.lang.Validate;

public class McrankCommandAsyncTask extends BukkitRunnable {
    private final String playerName;
    private final CommandSender sender;
    private final boolean useChat;

    public McrankCommandAsyncTask(String playerName, CommandSender sender, boolean useChat) {
        Validate.isTrue(useChat, "Attempted to start a rank retrieval with both board and chat off");
        Validate.notNull(sender, "Attempted to start a rank retrieval with no recipient");

        this.playerName = playerName;
        this.sender = sender;
        this.useChat = useChat;
    }

    @Override
    public void run() {
        Map<SkillType, Integer> skills = mcMMO.getDatabaseManager().readRank(playerName);

        new McrankCommandDisplayTask(skills, sender, playerName, useChat).runTaskLater(mcMMO.p, 1);
    }
}

