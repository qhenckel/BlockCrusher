package io.github.qhenckel.blocksmasher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockSmasher extends JavaPlugin implements Listener{
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		this.saveDefaultConfig();
	}
	    
	@EventHandler
    public void onPiston(BlockPistonExtendEvent event) {
		int crushdistance = getConfig().getInt("crushdistance") + 1;
		List<Block> crush = new ArrayList<Block>();		
		Block crusher = null;
		Chest c = null;
		int count = 0;
		
		for(Block b : event.getBlocks()){
			if(isBlacklist(b)){continue;}
			if(isCrusher(b)){crusher = b; continue;}
			crush.add(b);
			if(count > crushdistance){break;}
			count++;
			
		}
		
		if(crusher == null){return;}
		if(dropToChest()){c = getChest(crusher);}
		if(c == null){
			for(Block b : crush){
				ItemStack is = new ItemStack(typeToDrop(b));
				event.getBlock().getWorld().dropItem(b.getLocation(), is);
				b.setType(Material.AIR);
			}
		} else {
			Inventory inv = c.getBlockInventory();
			for(Block b : crush){
				ItemStack is = new ItemStack(typeToDrop(b));
				HashMap<Integer, ItemStack> hm = inv.addItem(is);
				if(!hm.isEmpty()){
					event.getBlock().getWorld().dropItem(b.getLocation(), is);
				}
				b.setType(Material.AIR);
			}
		}
	}
	
	public boolean isCrusher(Block block){
		List<String> list = (getConfig().getStringList("crushblocks"));
		if (list.contains(block.getType().toString())){
			return true;
		}
		return false;
	}
	
	public boolean isBlacklist(Block block){
		List<String> bliststring = (getConfig().getStringList("blacklist"));
		if (bliststring.contains(block.getType().toString())){
			return true;
		}
		return false;
	}
	
	public Chest getChest(Block b){
		for(BlockFace f : BlockFace.values()){
			Block test = b.getRelative(f);
			if(test.getState() instanceof Chest){
				Chest c = (Chest) test.getState();
				return c;
			}
		}
		return null;
	}
	
	public boolean dropactual(){
		return getConfig().getBoolean("dropactual");
	}
	
	public boolean dropToChest(){
		return getConfig().getBoolean("putIntoChest");
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
