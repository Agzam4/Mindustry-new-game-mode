package agzam4;

import agzam4.ai.MyMinerAI;
import agzam4.content.blocks.LockedOre;
import agzam4.content.blocks.NewGameBlocks;
import agzam4.content.effects.NGFx;
import agzam4.content.planets.NewGamePlanets;
import agzam4.struct.Vec1;
import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ai.types.MinerAI;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.core.World;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.game.EventType.BuildingBulletDestroyEvent;
import mindustry.game.EventType.UnitControlEvent;
import mindustry.game.EventType.UnitCreateEvent;
import mindustry.game.EventType.UnitDestroyEvent;
import mindustry.game.EventType.WorldLoadEndEvent;
import mindustry.mod.Mod;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock.ConstructBuild;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Attribute;

public class NewGameMod extends Mod {
	
	
	@Override
	public void init() {
		Events.on(BuildingBulletDestroyEvent.class, e -> {
			if(!isMode()) return;
			if(e.build == null) return;
			if(e.build instanceof ConstructBuild) return;
			
			ItemStack[] stacks = e.build.block.requirements;
			for (int i = 0; i < stacks.length; i++) {
				Item item = stacks[i].item;
				int totalAmount = Mathf.random(stacks[i].amount/2, stacks[i].amount);
				int count = totalAmount/25;
				if(count < 1) count = 1;
				if(count > 7) count = 7;

				for (int j = 0; j < count; j++) {
					int amount = totalAmount/count;
					
					int size = e.build.block.size;
			        int offsetx = -(size - 1) / 2;
			        int offsety = -(size - 1) / 2;

					int x = e.build.tileX() + Mathf.random(0, size-1) + offsetx;
					int y = e.build.tileY() + Mathf.random(0, size-1) + offsety;
					Building building = Vars.world.build(x, y);
					
					if(building != null) {
						if(building.block == NewGameBlocks.itemStack && building.items != null) {
							building.items.add(item, amount);
							continue;
						}
					}
					
					if(!Work.validPlaceItem(NewGameBlocks.itemStack, x, y, 0)) {
						x = e.build.tileX();
						y = e.build.tileY();
						building = Vars.world.build(x, y);
					}
					
					if(building != null) {
						if(building.block == NewGameBlocks.itemStack && building.items != null) {
							building.items.add(item, amount);
							continue;
						}
					}

					if(!Work.validPlaceItem(NewGameBlocks.itemStack, x, y, 0)) continue;
					
					Tile tile = Vars.world.tile(x, y);
					if(tile == null) continue;
					tile.setNet(NewGameBlocks.itemStack);
					building = tile.build;
					if(building != null) {
						if(building.items != null) {
							building.items.add(item, amount);
						}
					}
				}
			}
		});
		
		Events.on(UnitDestroyEvent.class, e -> {
			if(!isMode()) return;
			if(e.unit == null) return;
			float dist = e.unit.hitSize()*2/Mathf.sqrt2 + 5;
			
			ItemStack[] stacks = e.unit.type.getTotalRequirements();
			
			for (int i = -1; i < stacks.length; i++) {
				Item item = i == -1 ? Items.scrap : stacks[i].item;
				int totalAmount = (int) (i == -1 ? Mathf.random(e.unit.type.health/20, e.unit.type.health/10) : (stacks[i].amount/2 + Mathf.random(stacks[i].amount/4, stacks[i].amount/2)));
				int count = totalAmount/25;
				if(count < 1) count = 1;
				if(count > 7) count = 7;
				
				for (int j = 0; j < count; j++) {
					int amount = totalAmount/count;
					float angle = Mathf.random(360);
					float range = Mathf.random(dist);
					int x = World.toTile(e.unit.x() + range*Mathf.cosDeg(angle));
					int y = World.toTile(e.unit.y() + range*Mathf.sinDeg(angle));
					Building building = Vars.world.build(x, y);
					
					if(building != null) {
						if(building.block == NewGameBlocks.itemStack && building.items != null) {
							building.items.add(item, amount);
							Call.setItem(building, item, building.items.get(item));
							continue;
						}
					}
					
					if(!Work.validPlaceItem(NewGameBlocks.itemStack, x, y, 0)) {
						x = e.unit.tileX();
						y = e.unit.tileY();
						building = Vars.world.build(x, y);
					}
					
					if(building != null) {
						if(building.block == NewGameBlocks.itemStack && building.items != null) {
							building.items.add(item, amount);
							Call.setItem(building, item, building.items.get(item));
							continue;
						}
					}

					if(!Work.validPlaceItem(NewGameBlocks.itemStack, x, y, 0)) continue;
					
					Tile tile = Vars.world.tile(x, y);
					if(tile == null) continue;
					tile.setNet(NewGameBlocks.itemStack);
					building = tile.build;
					if(building != null) {
						if(building.items != null) {
							Call.setItem(building, item, amount);
//							building.items.add(item, amount);
						}
					}
				}
			}
		});
		
//		Events.on(WorldLoadBeginEvent.class, e -> {
//			World
////			if(Vars.state.rules.planet != NewGamePlanets.newSerpulo) return;
//			Vars.world.setGenerating(true);
//			Vars.world.tiles.eachTile(t -> {
//				if(t.overlay() instanceof OreBlock) {
//					OreBlock ore = (OreBlock) t.overlay();
//					if(ore.itemDrop != null) {
//						t.setBlock(NewGameBlocks.itemStack);
//						Building building = t.build;
//						if(building != null) {
//							if(building.items != null) {
//								building.items.add(ore.itemDrop, Mathf.random(100, 250));
//							}
//						}
//					}
//				}
//			});
//			Vars.world.setGenerating(false);
//		});

		Events.on(WorldLoadEndEvent.class, e -> {
			if(!isMode()) return;
			Vars.state.rules.bannedBlocks.addAll(
					Blocks.mechanicalDrill, 
					Blocks.pneumaticDrill, 
					Blocks.laserDrill, 
					Blocks.blastDrill, 
//					Blocks.waterExtractor, 
//					Blocks.oilExtractor, 
//					Blocks.cultivator,
					Blocks.cliffCrusher, 
					Blocks.plasmaBore, 
					Blocks.largePlasmaBore, 
					Blocks.impactDrill, 
					Blocks.eruptionDrill
			);
			Vars.state.rules.hideBannedBlocks = true;
			Vars.state.rules.solarMultiplier *= 5f;

			Vec1 dark = new Vec1();
			Vars.world.tiles.eachTile(t -> {
				if(t.block() != Blocks.air) {
					float oil = t.block().attributes.get(Attribute.oil);
					float spores = t.block().attributes.get(Attribute.spores);
					if(oil != 0) dark.add(oil-1f);
					if(spores != 0) dark.add(spores-1f);
					return;
				}
				if(t.overlay() instanceof OreBlock) {
//					OreBlock ore = (OreBlock) t.overlay();
					LockedOre locked = NewGameBlocks.ores.get(t.overlay());
					if(locked == null) return;
					t.setOverlay(locked);
//					if(ore.itemDrop != null) {

//						Building building = t.build;
//						if(building != null) {
//							if(building.items != null) {
//								building.items.add(ore.itemDrop, Mathf.random(50, 100));
//								t.setOverlay(Blocks.air);
//							}
//						}
//					}
				}
			});
			
			Vars.state.rules.defaultTeam.cores().each(core -> {
				Floor sand = (Floor) (dark.x < 0 ? Blocks.sand : Blocks.darksand);
				Point2[] ps = Edges.getEdges(core.block.size);
				for (Point2 p : ps) {
					Tile tile = Vars.world.tile(p.x + core.tileX(), p.y + core.tileY());
					if(tile == null) return;
					tile.setFloorUnder(sand);
				}
			});

//			if(t.block() instanceof CoreBlock) {
//				if(!t.isCenter()) return;
//				return;
//			}
		});

		Events.on(UnitCreateEvent.class, e -> {
			if(!isMode()) return;
			if(e.unit.type == UnitTypes.mono) {
				e.unit.controller(new MyMinerAI());
			}
		});
		
//		Events.on(UnitControlEvent.class, e -> Log.info("@ @", e.player, e.unit));
		
//		Events.on(BlockBuildEndEvent.class, e -> {
//			Log.info("@ @ @", e.breaking, e.unit, e.tile);
//			if(!e.breaking) return;
//			if(e.unit == null) return;
//			if(e.tile.block() != NewGameBlocks.itemStack) return;
//			CoreBuild core = e.unit.closestCore();
//			if(e.tile.build == null || core == null) return;
//			if(e.tile.build.items == null) return;
//
//			e.tile.build.items.each((i, a) -> core.items.add(i, a));
//		});
	}
	
	@Override
	public void loadContent() {
		NGFx.load();
		NewGameBlocks.load();
		NewGamePlanets.load();
		UnitTypes.mono.controller = u -> isMode() ? new MyMinerAI() : new MinerAI();
	}
	
	public static boolean isMode() {
		return Vars.state.rules.planet == NewGamePlanets.newSerpulo;
	}
}
