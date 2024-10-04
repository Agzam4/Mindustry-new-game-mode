package agzam4.content.blocks;

import static mindustry.type.ItemStack.with;

import agzam4.content.blocks.life.Cutter;
import agzam4.content.blocks.life.Devourer;
import agzam4.content.blocks.life.GlowingMossPlant;
import agzam4.content.blocks.life.LifeCore;
import agzam4.content.blocks.life.LifeMover;
import agzam4.content.blocks.life.LifeReactor;
import agzam4.content.blocks.life.LifeRouter;
import agzam4.content.blocks.life.LifeWall;
import agzam4.content.blocks.life.MossPlant;
import agzam4.content.blocks.power.LaserEffects;
import agzam4.content.blocks.power.LaserGenerator;
import agzam4.content.blocks.power.LaserMixer;
import agzam4.content.blocks.power.LaserPainter;
import agzam4.content.blocks.power.LaserResizer;
import agzam4.content.blocks.power.PowerCore;
import agzam4.content.units.NGUnitTypes;
import arc.graphics.Color;
import arc.struct.EnumSet;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

public class NewGameBlocks {

	public static Block itemStack, pneumaticDetonator, differentialDetonator, blastDetonator, copperSeparator, unloadPoint, devourer,
	grain, attractor, 
	
	mossPlant, glowingMossPlant, mossPlantWall, cutter, lifeReactor,
	coreEntropy, coreDissipation, coreDiscord, 
	lifeMover, lifeRouter, lifeWall, largeLifeRouter,
	
	coreAnode, coreCathode, coreIon, // Emitted, electrode Cathode Anode
	
	emitter, largeEmitter, 
	laserMixer, largeLaserMixer, 
	laserPainter, largeLaserPainter,
	laserResizer, laserEffect,
	
	placeholder; 
	
	public static ObjectMap<Block, LockedOre> ores = new ObjectMap<Block, LockedOre>();
	
	public static void load() {

		Seq<OreBlock> defaultOres = new Seq<>();
		
		Vars.content.blocks().each(b -> {
			if(b instanceof OreBlock) {
				defaultOres.add((OreBlock) b);
			}
		});
		
		defaultOres.each(ore -> {
			ores.put(ore, new LockedOre(ore.name) {{
			        localizedName = ore.localizedName;
			        defaultOre = ore;
			        variants = 3;
			        useColor = true;
			        mapColor = ore.mapColor;
			}});
		});
		
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
			requirements(Category.production, with(Items.blastCompound, 20));
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
            requirements(Category.distribution, with(Items.silicon, 250, Items.thorium, 80, Items.phaseFabric, 10, Items.titanium, 5));
            scaledHealth = 250;
            range = 180f;
            hasPower = true;
            consumePower(2f);
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
			range = 80f;
			reload = 15f;
            researchCost = with(Items.sporePod, 250, Items.silicon, 50, Items.blastCompound, 30, Items.plastanium, 10);
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
            lobes = new float[][] {
            	{0,0,0,8},
            	{10,0,90,3},
            	{-10,0,270,3},
            	{0,10,0,3},
            	{0,-10,180,3}
            };
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
            lobes = new float[][] {
            	{0,0,0,8},
            	{12,4,90,3},{12,-4,90,3},
            	{-12,4,270,3},{-12,-4,270,3},
            	{4,12,0,3},{-4,12,0,3},
            	{4,-12,180,3},{-4,-12,180,3}
            };
		}};

		coreDiscord = new LifeCore("core-discord"){{
            requirements(Category.effect, with(Items.sand, 3000, Items.sporePod, 3500, Items.blastCompound, 2500, Items.plastanium, 1500));
            unitType = NGUnitTypes.dissonance;  // TODO
			damageReflectionMultiplier = 3f;
			lifeessenceCapacity = 18_000;
            itemCapacity = 13000;
            health = 6000;
            thrusterLength = 40/4f;
            size = 5;
            unitCapModifier = 24;
            researchCostMultiplier = 0.11f;
            lobes = new float[][] {
            	{0,0,0,8},
            	{0,18,0,5},{18,0,90,5},{0,-18,180,5},{-18,0,270,5},
            	{12,12,45,3},{12,-12,135,3},{-12,-12,225,3},{-12,12,315,3}
            };
        }};

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
		largeLifeRouter = new LifeRouter("large-life-router"){{
			requirements(Category.effect, with(Items.sporePod, 27, Items.sand, 9, Items.blastCompound, 9));
			essenceCapacity = 10000;
			range = 7;
			size = 3;
			buildVisibility = BuildVisibility.hidden;
		}};
		
		mossPlant = new MossPlant("moss-plant") {{
			requirements(Category.production, with(Items.sporePod, 1));
	        health = 100;
	        growthTime = 5f*60f;
	        seedsTime = 1f*60f;
            researchCost = with(Items.sporePod, 5000);
		}};
		glowingMossPlant = new GlowingMossPlant("glowing-moss-plant") {{
			requirements(Category.production, with(Items.sporePod, 10));
	        health = 250;
	        growthTime = 1f*60f;
	        seedsTime = 5f*60f;
	        researchCostMultiplier = 1000;
		}};
		mossPlantWall = new Wall("moss-plant-wall") {{
			requirements(Category.defense, with(Items.sporePod, 100));
            health = 250;
            envDisabled |= Env.scorching;
            absorbLasers = true;
            buildCostMultiplier = 100;
            buildVisibility = BuildVisibility.sandboxOnly;
		}};
		cutter = new Cutter() {{
			requirements(Category.production, with(Items.copper, 125, Items.lead, 75, Items.silicon, 20, Items.graphite, 10));
            consumePower(50 / 60f);
            flags = EnumSet.of(BlockFlag.factory);
		}};
		
		lifeReactor = new LifeReactor("life-reactor") {{
            requirements(Category.power, with(Items.sporePod, 500, Items.lead, 300, Items.silicon, 200, Items.metaglass, 15));
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.24f;
            size = 4;
            health = 750;
            itemDuration = 220f;
            powerProduction = 35f;
            heating = 0.02f;

            consumeItem(Items.sporePod);
            booster = consumeLiquid(Liquids.water, .2f);
		}};

		coreAnode = new PowerCore("core-anode") {{
            requirements(Category.effect, with(Items.copper, 800, Items.lead, 1000));
            powerProduction = 4f;
            health = 1100-275;
            shieldHealth = 275*1.5f;
            radius = 50f;
            alwaysUnlocked = true;
            isFirstTier = true;
            unitType = UnitTypes.alpha;
            itemCapacity = 4000;
            size = 3;
            unitCapModifier = 8;
		}};

		coreCathode = new PowerCore("core-cathode") {{
            requirements(Category.effect, with(Items.copper, 1000, Items.lead, 5000, Items.silicon, 2000));
            powerProduction = 7.25f;
            health = 3500-875;
            shieldHealth = 875*1.5f;
            radius = 80f;
            unitType = UnitTypes.beta;
            itemCapacity = 9000;
            size = 4;
            thrusterLength = 34/4f;
            unitCapModifier = 16;
            researchCostMultiplier = 0.07f;
		}};
		
		coreIon = new PowerCore("core-ion") {{
            requirements(Category.effect, with(Items.copper, 6000, Items.lead, 8000, Items.silicon, 5000, Items.surgeAlloy, 500));

            powerProduction = 11f;
            health = 6000-1500;
            shieldHealth = 1500*1.5f;
            radius = 130f;
            
            unitType = UnitTypes.gamma;
            itemCapacity = 13000;
            size = 5;
            thrusterLength = 40/4f;
            unitCapModifier = 24;
            researchCostMultiplier = 0.11f;
		}};
		
		emitter = new LaserGenerator("micro-emitter") {{
			requirements(Category.turret, with(Items.copper, 50, Items.lead, 50, Items.silicon, 5));
			laserOutOffset = 12f / 4f;
			consumePower(1.5f);
		}};
		
		laserMixer = new LaserMixer("laser-mixer") {{
			requirements(Category.turret, with(Items.copper, 50, Items.lead, 50, Items.metaglass, 5));
//			consumePower(1.5f);
			laserOutOffset = 12f / 4f;
			laserInOffset = 4f;
		}};
		
		laserPainter = new LaserPainter("laser-painter") {{
			requirements(Category.turret, with(Items.copper, 50, Items.lead, 10, Items.metaglass, 10, Items.graphite, 5));
			laserOutOffset = 12f / 4f;
		}};


		largeEmitter = new LaserGenerator("emitter") {{
			requirements(Category.turret, with(Items.lead, 100, Items.titanium, 70, Items.silicon, 60, Items.plastanium, 5));
			laserOutOffset = -2f / 4f;
			laserSize = 16f/4f;
			size = 2;
			laserPower = 40;
			consumePower(1);
		}};

		new LaserGenerator("large-emitter") {{
			requirements(Category.turret, with(Items.lead, 25));
			buildVisibility = BuildVisibility.hidden;
			laserOutOffset = 12f / 4f;
			laserSize = 3*Vars.tilesize;
			size = 3;
			consumePower(4f);
		}};
		
		laserResizer = new LaserResizer("laser-resizer") {{
			requirements(Category.turret, with(Items.copper, 100, Items.lead, 80, Items.metaglass, 20, Items.graphite, 15));
			size = 2;
			laserOutOffset = 0f;
			laserInOffset = 14f / 4f;
			maxLaserSize = 24f/4f;
		}};
		
		laserEffect = new LaserEffects("laser-effect") {{
			requirements(Category.turret, with(Items.copper, 200, Items.lead, 200, Items.silicon, 80, Items.phaseFabric, 15));
			size = 2;
			effect(Items.coal, 60f, StatusEffects.burning);
			effect(Items.titanium, 100f, StatusEffects.freezing);
			effect(Items.pyratite, 90f, StatusEffects.melting);
			effect(Items.sporePod, 20f, StatusEffects.sapped);
			effect(Items.plastanium, 120f, StatusEffects.electrified);
			consume();
			
			consumePower(100 / 60f);
		}};
		
		largeLaserMixer = new LaserMixer("large-laser-mixer") {{
			requirements(Category.turret, with(Items.copper, 200, Items.lead, 200, Items.metaglass, 80, Items.plastanium, 15));
			size = 2;
			laserInOffset = 4;
		}};
		
		largeLaserPainter = new LaserPainter("large-laser-painter") {{
			requirements(Category.turret, with(Items.copper, 200, Items.lead, 200, Items.metaglass, 10, Items.graphite, 25, Items.plastanium, 10));
			size = 2;
			laserOutOffset = 12f / 4f;
		}};
	}
}
