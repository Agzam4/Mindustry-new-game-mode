package agzam4;

import agzam4.content.blocks.NewGameBlocks;
import agzam4.content.blocks.UnloadPoint.UnloadPointBuild;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Posc;
import mindustry.gen.Unit;
import mindustry.type.Item;

public class StacksIndexer {

	public static void init() {}
	
    public static @Nullable Building findClosestUnload(Unit unit, Item item) {
    	int ic = NewGameBlocks.unloadPoint.itemCapacity;
    	Building unload = unit.team.data().getBuildings(NewGameBlocks.unloadPoint).min(b -> (b.config() == item && b.items.total() < ic), b -> {
    		return ((UnloadPointBuild)b).lastFillTime;
    	});
    	if(unload == null) return unit.closestCore();
    	return unload;
    }

    public static @Nullable Building findClosestOre(Posc pos) {
    	return Team.derelict.data().getBuildings(NewGameBlocks.itemStack).min(b -> (b.items.first() != null), b -> Mathf.dst2(pos.x()-b.x(), pos.y()-b.y()));
    }

    public static @Nullable Building findClosestOreInRange2(Posc pos, Item type, float range2) {
    	return Team.derelict.data().getBuildings(NewGameBlocks.itemStack).min(b -> 
    	b.items != null &&
    	(b.items.first() != null) && 
    	b.items.has(type) && 
    	Mathf.dst2(pos.x()-b.x(), pos.y()-b.y()) <= range2
    	, b -> Mathf.dst2(pos.x()-b.x(), pos.y()-b.y()));
//    	Seq<Building> seq = Team.derelict.data().getBuildings(NewGameBlocks.itemStack);
//        Building result = null;
//        float min = Float.MAX_VALUE;
//        for(int i = 0; i < seq.size; i++){
//            Building b = seq.items[i];
//            if(b.items.first() == null) continue;
//            float val = Mathf.dst2(pos.x()-b.x(), pos.y()-b.y());
//            if(val > range2) continue;
//            if(val <= min){
//                result = b;
//                min = val;
//            }
//        }
//        return result;
    }
}
