package agzam4.content.blocks;

import static mindustry.type.ItemStack.with;

import agzam4.content.blocks.life.Devourer;
import agzam4.content.blocks.life.LifeCore;
import agzam4.content.blocks.life.LifeMover;
import agzam4.content.blocks.life.LifeRouter;
import agzam4.content.blocks.life.LifeWall;
import agzam4.content.units.NGUnitTypes;
import arc.graphics.Color;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

public class NewGameBlocks {

	public static Block itemStack, pneumaticDetonator, differentialDetonator, blastDetonator, copperSeparator, unloadPoint, devourer,
	grain, attractor, 
	
	coreEntropy, coreDissipation, 
	lifeMover, lifeRouter, lifeWall;
	
	public static ObjectMap<Block, LockedOre> ores = new ObjectMap<Block, LockedOre>();
	
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
		grain = new ItemTurret("grain") {{
			requirements(Category.turret, with(Items.sand, 50));
			ammo(Items.sand,  new BasicBulletType(2.5f, .1f){{
				width = 7f;
				height = 9f;
				lifetime = 60f;
				ammoMultiplier = 1;
				knockback = 3f;
			}});
			outlineIcon = false;
//			outputFacing
//			Blocks.swarmer;

			recoils = 1;
			shoot = new ShootBarrel(){{
				barrels = new float[]{
						0, 0, 0,
						0, 0, 10,
						0, 0, 0,
						0, 0, -10,
				};
				shots = 4;
				shotDelay = 5f;
			}};

			recoil = 0.5f;
			shootY = 3f;
			reload = 20f;
			range = 110;
			shootCone = 15f;
			ammoUseEffect = Fx.casing1;
			health = 250;
			inaccuracy = 2f;
			rotateSpeed = 10f;
			coolant = consumeCoolant(0.1f);
			researchCostMultiplier = 0.05f;

			limitRange();
		}};

		attractor = new Attractor("attractor") {{
            requirements(Category.turret, with(Items.silicon, 250, Items.thorium, 80, Items.phaseFabric, 40, Items.titanium, 15));
            scaledHealth = 250;
            range = 180f;
            hasPower = true;
            consumePower(4f);
            size = 2;
            shootLength = 5f;
            bulletDamage = 0f;
            researchCostMultiplier = .1f;
            envEnabled |= Env.space;
		}};

		devourer = new Devourer("devourer") {{
            requirements(Category.turret, with(Items.sporePod, 500, Items.silicon, 250, Items.blastCompound, 150, Items.plastanium, 75));
            scaledHealth = 500;
            size = 3;
            researchCostMultiplier = .1f;
			range = 80f;
			reload = 15f;
		}};

		coreEntropy = new LifeCore("core-entropy") {{
			requirements(Category.effect, with(Items.sand, 3000, Items.sporePod, 2500));
			unitType = NGUnitTypes.disorderer;
			damageReflectionMultiplier = 1f;
			lifeessenceCapacity = 2_200;
            itemCapacity = 4000;
	        health = 1100;
	        isFirstTier = true;
            unitCapModifier = 8;
            size = 3;
            researchCostMultiplier = 0.05f;
		}};
		
		coreDissipation = new LifeCore("core-dissipation") {{
            requirements(Category.effect, with(Items.sand, 3000, Items.sporePod, 3000, Items.blastCompound, 2000));
            unitType = NGUnitTypes.absorber;  // TODO
			damageReflectionMultiplier = 2f;
			lifeessenceCapacity = 7_000;
            itemCapacity = 9000;
            health = 3500;
            thrusterLength = 34/4f;
            size = 4;
            unitCapModifier = 16;
            researchCostMultiplier = 0.07f;
		}};

//		coreLifeLvl3 = new LifeCore("core-lif-lvl3"){{
//			requirements(Category.effect, with(Items.copper, 8000, Items.lead, 8000, Items.silicon, 5000, Items.thorium, 4000));
//			unitType = NGUnitTypes.disorderer; // TODO
//			damageReflectionMultiplier = 4f;
//			lifeessenceCapacity = 12_000;
//          	health = 6000;
//        }};

		// Dissipation
		
		lifeMover = new LifeMover("life-mover"){{
			requirements(Category.effect, with(Items.sporePod, 3, Items.sand, 1, Items.blastCompound, 1));
			essenceCapacity = 250;
		}};
		lifeRouter = new LifeRouter("life-router"){{
			requirements(Category.effect, with(Items.sporePod, 3, Items.sand, 1, Items.blastCompound, 1));
			essenceCapacity = 1000;
		}};
		lifeWall = new LifeWall("life-wall"){{
			requirements(Category.defense, with(Items.sporePod, 7, Items.sand, 5, Items.silicon, 5, Items.plastanium, 5, Items.blastCompound, 5));
			essenceCapacity = 5000;
			size = 2;
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
