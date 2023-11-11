package agzam4.ai;

import agzam4.blocks.NewGameBlocks;
import agzam4.blocks.UnloadPoint.UnloadPointBuild;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.entities.units.AIController;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.type.Item;
import mindustry.world.Tile;

public class MyMinerAI extends AIController {
	
	Building target = null;
	
	public static int cooldownTime = 120;
	
	int cooldown = 0;
	
	// TODO: mine sand

	@Nullable Tile mineTile = null;
	
    @Override
    public void updateMovement(){
    	if(cooldown > 0) cooldown--;
    	if(unit == null) return;
    	
    	if(mineTile != null) {
    		if(unit.stack.amount < unit.itemCapacity()/2 && mineTile.block() == Blocks.air) {
                if(unit.within(mineTile, unit.type.range)) {
            		unit.mineTile(mineTile);
                } else {
                    circle(mineTile, unit.type.range / 1.8f);
                }
    			return;
    		}
    		unit.mineTile(null);
    		mineTile = null;
    	}
    	
    	if(unit.stack.amount > 0) {
            Building unload = findClosestUnload(unit.stack.item);//unit.closestCore();
        	if(unload == null) return;

            if(unit.within(unload, unit.type.range)){
            	if(cooldown > 0) return;
                if(unload.acceptStack(unit.stack.item, unit.stack.amount, unit) > 0) {
                	int amount = Math.min(unload.getMaximumAccepted(unit.stack.item), unit.stack.amount);
                    Call.transferItemTo(unit, unit.stack.item, amount, unit.x, unit.y, unload);
                    cooldown = (int) (cooldownTime / unit.type.mineSpeed);
                }

                unit.clearItem();
                target = null;
            }

            circle(unload, unit.type.range / 1.8f);
            return;
    	}
    	if(target == null) target = findClosestOre();
    	if(target == null) {
    		mineTile = Vars.indexer.findClosestOre(unit, Items.sand);
    		return;
    	}

    	Item take = target.items.first();
    	if(take == null) {
    		target = null;
    		return;
    	}
    	
        if(unit.within(target, unit.type.range)){
        	if(cooldown > 0) return;
        	if(unit.acceptsItem(take)) {
                Call.takeItems(target, take, unit.maxAccepted(take), unit);
                cooldown = (int) (cooldownTime / unit.type.mineSpeed);
        		return;
        	}
        	unit.clearItem();
        }

        circle(target, unit.type.range / 1.8f);
    }
    public Building findClosestUnload(Item item) {
    	int ic = NewGameBlocks.unloadPoint.itemCapacity;
    	Building unload = unit.team.data().getBuildings(NewGameBlocks.unloadPoint).min(b -> (b.config() == item && b.items.total() < ic), b -> {
    		return ((UnloadPointBuild)b).lastFillTime;
    	});
    	if(unload == null) return unit.closestCore();
    	return unload;
    }
    
    public Building findClosestOre() {
    	return Team.derelict.data().getBuildings(NewGameBlocks.itemStack).min(b -> (b.items.first() != null), b -> Mathf.dst2(unit.x()-b.x(), unit.y()-b.y()));
    }
}
