package net.mysticrealms.fireworks.scavengerhunt;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ScavengerInventory implements Runnable {
	
	private ScavengerHunt plugin;
	
	public ScavengerInventory(ScavengerHunt scavenger) {
		plugin = scavenger;
	}
	
	@Override
	public void run() {
		if (!plugin.isRunning) {
			return;
		}
		if (plugin.end != 0 && plugin.end < System.currentTimeMillis()) {
			plugin.stopScavengerEvent();
			return;
		}
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			
			if (!p.hasPermission("scavengerhunt.participate")) {
				continue;
			}
			
			if(!plugin.checkLocation(p.getLocation())){
				continue;
			}
			
			Inventory i = p.getInventory();
			boolean hasItems = true;
			for (ItemStack item : plugin.currentItems) {
				if (plugin.count(i, item) < item.getAmount()) {
					hasItems = false;
				}
			}
			Map<EntityType, Integer> usedEntities = plugin.getMap(p.getName());
			for (Map.Entry<EntityType, Integer> entry : plugin.currentMobs.entrySet()) {
				if (entry.getValue() > usedEntities.get(entry.getKey())) {
					hasItems = false;
				}
			}
			if (hasItems) {
				plugin.isRunning = false;
				plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "Congratulations to " + ChatColor.GOLD + p.getDisplayName() + ChatColor.DARK_RED + "!");
				plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "Prize was: ");
				for (ItemStack reward : plugin.rewards) {
					plugin.getServer().broadcastMessage(ChatColor.GOLD + plugin.configToString(reward));
					i.addItem(reward);
				}
				if (plugin.isUsingMoney()) {
					plugin.getServer().broadcastMessage(ChatColor.GOLD + " * " + ScavengerHunt.economy.format(plugin.money));
					ScavengerHunt.economy.depositPlayer(p.getName(), plugin.money);
				}
				return;
			}
		}
	}
}
