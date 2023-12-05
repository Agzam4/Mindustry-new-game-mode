package agzam4.content.blocks.life;

import arc.Core;
import mindustry.core.UI;
import mindustry.gen.Building;
import mindustry.gen.Buildingc;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;

public interface LifeEssenceBuild extends Buildingc {

	public float essence();
	public float essenceCapacity();
	public LifeEssenceBuild essence(float essence);
	
	public static void setBars(Block block) {
		block.addBar("life-essence", (Building e) -> new Bar(
				() -> Core.bundle.format("bar.life-essence", UI.formatAmount((long)((LifeEssenceBuild) e).essenceCapacity())), 
				() -> Pal.spore, 
				() -> ((LifeEssenceBuild)e).essence()/((LifeEssenceBuild)e).essenceCapacity()
		));
	}
}
