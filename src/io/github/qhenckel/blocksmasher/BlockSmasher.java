package io.github.qhenckel.blocksmasher;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockSmasher extends JavaPlugin {
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new MyListener(), this);
		this.saveDefaultConfig();
	}

	public final class MyListener implements Listener {
	    
		@EventHandler
	    public void onPiston(BlockPistonExtendEvent event) {
			List<org.bukkit.block.Block> blocklist = event.getBlocks();
			int crushdistance = getConfig().getInt("crushdistance") + 1;
			try{
				for (int blocks = 0; blocks != crushdistance; blocks++){
					if (crusher(blocklist.get(blocks))){
						for (int i = 0; i < blocks; i++)
							if(blacklist(blocklist.get(i))){
								ItemStack is = new ItemStack(typeToDrop(blocklist.get(i)));
								event.getBlock().getWorld().dropItem(blocklist.get(i).getLocation(), is);
								blocklist.get(i).setType(Material.AIR);
							}else{
								event.setCancelled(true);
								event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 1012);
								event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.SMOKE, 2000);
								}
					}
				}
			} catch(IndexOutOfBoundsException e){ return; }
			  catch(NullPointerException e) {return;}
		
	    }
	}
	
	public boolean crusher(Block block){
		List<String> list = (getConfig().getStringList("crushblocks"));
		if (list.contains(block.getType().toString())){
			return true;
		}
		return false;
	}
	
	public boolean blacklist(Block block){
		List<String> bliststring = (getConfig().getStringList("blacklist"));
		if (bliststring.contains(block.getType().toString())){
			return false;
		}
		return true;
	}
	
	public boolean dropactual(){
		return getConfig().getBoolean("dropactual");
	}
	
	public Material typeToDrop(Block b){
		if(dropactual()){
			return b.getType();
		}else{
			for(ItemStack is : b.getDrops()){
				return is.getType();
			}
		}
		return null;
	}
}
