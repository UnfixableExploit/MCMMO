package com.gmail.nossr50.runnables.skills;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class AbilityDisableTask extends BukkitRunnable {
    private McMMOPlayer mcMMOPlayer;
    private AbilityType ability;

    public AbilityDisableTask(McMMOPlayer mcMMOPlayer, AbilityType ability) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.ability = ability;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void run() {
        if (!mcMMOPlayer.getAbilityMode(ability)) {
            return;
        }

        Player player = mcMMOPlayer.getPlayer();

        switch (ability) {
            case SUPER_BREAKER:
            case GIGA_DRILL_BREAKER:
                SkillUtils.handleAbilitySpeedDecrease(player);
                // Fallthrough

            case BERSERK:
                if (Config.getInstance().getRefreshChunksEnabled()) {
                	
                	// Uodate the MMO Chunk
                	Bukkit.getScheduler().runTaskAsynchronously(mcMMO.p, new Runnable() {
                	    @Override
                	    public void run() {
                	    	// Using the bukkit api in async is very bad idea this is just for testing!
                	    	Player p = mcMMOPlayer.getPlayer();
                	    	p.sendMessage("Start of runnable");
                        	int x = p.getLocation().getChunk().getX();
                        	int z = p.getLocation().getChunk().getZ();
                        	p.sendMessage("About to refresh the chunk");
                        	p.getWorld().refreshChunk(x, z);
                        	p.sendMessage("Updated the Chunk or Chunks");
                	    }
                	});
                }
                // Fallthrough

            default:
                break;
        }

        EventUtils.callAbilityDeactivateEvent(player, ability);

        mcMMOPlayer.setAbilityMode(ability, false);
        mcMMOPlayer.setAbilityInformed(ability, false);

        ParticleEffectUtils.playAbilityDisabledEffect(player);

        if (mcMMOPlayer.useChatNotifications()) {
            player.sendMessage(ability.getAbilityOff());
        }

        SkillUtils.sendSkillMessage(player, ability.getAbilityPlayerOff(player));
        new AbilityCooldownTask(mcMMOPlayer, ability).runTaskLaterAsynchronously(mcMMO.p, PerksUtils.handleCooldownPerks(player, ability.getCooldown()) * Misc.TICK_CONVERSION_FACTOR);
    }
}
