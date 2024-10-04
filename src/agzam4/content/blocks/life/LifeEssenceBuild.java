package agzam4.content.blocks.life;

import arc.Core;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.core.UI;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;

public interface LifeEssenceBuild /* extends Buildingc */ {

	public float essence();
	public float essenceCapacity();
	public LifeEssenceBuild essence(float essence);
	
	public static void setBars(Block block) {
		block.addBar("life-essence", (Building e) -> new Bar(
				() -> Core.bundle.format("bar.life-essenceamount", 
						e instanceof LifeEssenceBuild ? UI.formatAmount((long)((LifeEssenceBuild) e).essence()) : -1), 
				() -> Pal.spore, 
				() -> e instanceof LifeEssenceBuild ? ((LifeEssenceBuild)e).essence()/((LifeEssenceBuild)e).essenceCapacity() : 1
		));
	}

	public static boolean addEssence(LifeEssenceBuild leb, float essence) {
		if(leb.essence() >= leb.essenceCapacity()) return false;
		leb.essence(Mathf.clamp(leb.essence() + essence, 0, leb.essenceCapacity()));
		return true;
	}

	public static boolean tryAddEssence(@Nullable Building b, float essence) {
		if(b == null) return false;
		if(b instanceof LifeEssenceBuild) return addEssence((LifeEssenceBuild) b, essence);
		return false;
	}
}
