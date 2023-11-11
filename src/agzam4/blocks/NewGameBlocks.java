package agzam4.blocks;

import static mindustry.type.ItemStack.with;

import java.util.HashMap;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.environment.OreBlock;

public class NewGameBlocks {

	public static Block itemStack, pneumaticDetonator, differentialDetonator, blastDetonator, copperSeparator, unloadPoint;
	public static HashMap<Block, LockedOre> ores = new HashMap<Block, LockedOre>();
	
	public static void load() {
		itemStack = new ItemStackBlock("item-stack");
		pneumaticDetonator = new Detonator("pneumatic-detonator") {{
			requirements(Category.production, with(Items.sand, 50));
			glowColor = Color.valueOf("f4b37a");
			radius = 1;
			detonatePower = 2;
			buildCostMultiplier = 2f;
            researchCost = with(Items.sand, 10);
		}};
		differentialDetonator = new Detonator("differential-detonator") {{
			requirements(Category.production, with(Items.pyratite, 20));
			glowColor = Color.valueOf("ffad66");
			radius = 2;
			detonatePower = 3;
			buildCostMultiplier = 2.5f;
            researchCost = with(Items.pyratite, 30);
		}};
		blastDetonator = new Detonator("blast-detonator") {{
			requirements(Category.production, with(Items.blastCompound, 50));
			glowColor = Color.valueOf("ff7f66");
			radius = 3;
			detonatePower = 4;
            researchCost = with(Items.blastCompound, 100);
		}};
		copperSeparator = new CopperSeparator("copper-separator") {{
            requirements(Category.crafting, with(Items.scrap, 75, Items.lead, 30));
            researchCost = with(Items.scrap, 100);
		}};
		unloadPoint = new UnloadPoint("unload-point") {{
            requirements(Category.distribution, with(Items.copper, 50, Items.silicon, 10));
            size = 2;
            itemCapacity = 100;
            researchCost = with(Items.copper, 100, Items.silicon, 10);
		}};
		
		Seq<OreBlock> defaultOres = new Seq<>();
		
		Vars.content.blocks().each(b -> {
			if(b instanceof OreBlock) {
				defaultOres.add((OreBlock) b);
			}
		});
		
		defaultOres.each(ore -> {
			ores.put(ore, new LockedOre(ore.name + "-locked") {{
			        localizedName = ore.localizedName;
			        defaultOre = ore;
			        variants = 3;
			        useColor = true;
			        mapColor = ore.mapColor;
			}});
		});
	}
}
