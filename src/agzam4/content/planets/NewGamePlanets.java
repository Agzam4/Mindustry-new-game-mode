package agzam4.content.planets;

import agzam4.content.blocks.NewGameBlocks;
import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.content.SectorPresets;
import mindustry.content.SerpuloTechTree;
import mindustry.content.TechTree;
import mindustry.content.Weathers;
import mindustry.core.World;
import mindustry.game.Team;
import mindustry.gen.WeatherState;
import mindustry.game.Objectives.Research;
import mindustry.game.Objectives.SectorComplete;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.GenericMesh;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexMesher;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MatMesh;
import mindustry.graphics.g3d.MeshBuilder;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.graphics.g3d.NoiseMesh;
import mindustry.graphics.g3d.ShaderSphereMesh;
import mindustry.graphics.g3d.PlanetGrid.Ptile;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.maps.planet.AsteroidGenerator;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.ItemStack;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.type.SectorPreset;
import mindustry.type.Weather;
import mindustry.type.Weather.WeatherEntry;
import mindustry.ui.dialogs.PlanetDialog;
import mindustry.world.Block;
import mindustry.world.meta.Env;

import static mindustry.content.SectorPresets.groundZero;
import static mindustry.content.TechTree.*;
import static mindustry.content.Blocks.*;

public class NewGamePlanets {

	public static Planet newSerpulo;
	
	public static SectorPreset 
			start, deliverycore, 
			sporelab, repairbase, explosionc,
			lens,
			
			blank = null;

	public static void load() {
//		SerpuloTechTree
		newSerpulo = new Planet("newGameMod.new-serpulo", Planets.sun, 1f, 3) {{
//			techTree = TechTree.node(newSerpulo, null);

			sectorSeed = 4;
			generator = new NgSerpuloPlanetGenerator();
			meshLoader = () -> new HexMesh(this, 6);
			cloudMeshLoader = () -> new MultiMesh(
					new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, new Color().set(Pal.spore).mul(0.9f).a(0.75f), 2, 0.45f, 0.9f, 0.38f),
					new HexSkyMesh(this, 1, 0.6f, 0.16f, 5, Color.white.cpy().lerp(Pal.spore, 0.55f).a(0.75f), 2, 0.45f, 1f, 0.41f)
					);
			launchCapacityMultiplier = 0.5f;
			allowWaves = true;
			allowWaveSimulation = true;
			allowSectorInvasion = true;
			allowLaunchSchematics = true; // false?
			enemyCoreSpawnReplace = true;
			allowLaunchLoadout = false;
			clearSectorOnLose = true; // Edited
			
			//doesn't play well with configs
			
			prebuildBase = false;
			ruleSetter = r -> {
				r.bannedBlocks.addAll(
						mechanicalDrill, 
						pneumaticDrill, 
						laserDrill, 
						blastDrill, 
//						Blocks.waterExtractor, 
//						Blocks.oilExtractor, 
//						Blocks.cultivator,
						cliffCrusher, 
						plasmaBore, 
						largePlasmaBore, 
						impactDrill, 
						eruptionDrill
				);
				r.hideBannedBlocks = true;
				
				r.waveTeam = Team.crux;
				r.placeRangeCheck = false;
				r.showSpawns = false;
				r.solarMultiplier = 3f;
				if(r.sector != null) {
					if(r.sector.preset == null) r.loadout = ItemStack.list(Items.copper, 200, Items.lead, 100, Items.sand, 500);
				}
			};
			
			iconColor = Color.valueOf("7d4dff");
			atmosphereColor = Color.valueOf("3c1b8f");
			atmosphereRadIn = 0.02f;
			atmosphereRadOut = 0.3f;
			startSector = 4;
			alwaysUnlocked = true;
			landCloudColor = Pal.spore.cpy().a(0.5f);
			hiddenItems.addAll(Items.erekirItems).removeAll(Items.serpuloItems);

			orbitRadius = Planets.serpulo.orbitRadius;
			orbitOffset = Planets.serpulo.orbitOffset + 180;
			orbitTime = Planets.serpulo.orbitTime;
		}};

		start = new SectorPreset("ng-start", newSerpulo, 4) {{
            alwaysUnlocked = true;
            addStartingItems = false;
            captureWave = 7;
            difficulty = 1;
            overrideLaunchDefaults = true;
            noLighting = true;
            startWaveTimeMultiplier = 3f;
		}};

		deliverycore = new SectorPreset("ng-deliverycore", newSerpulo, 1) {{
//            alwaysUnlocked = true; 
            addStartingItems = false;
            captureWave = 20;
            difficulty = 1;
            overrideLaunchDefaults = true;
            noLighting = true;
            startWaveTimeMultiplier = 3f;
		}};
//		PlanetGenerator
//		newSerpulo.sectors.get(1).preset.rules
//		World

		sporelab = new SectorPreset("ng-sporelab", newSerpulo, 83) {{ // 55
          addStartingItems = false;
          captureWave = 20;
          difficulty = 2;
          overrideLaunchDefaults = true;
          noLighting = true;
          startWaveTimeMultiplier = 3f;
		}};

		repairbase = new SectorPreset("ng-repairbase", newSerpulo, 199) {{ // 115
          addStartingItems = false;
          captureWave = 30;
          difficulty = 3;
          overrideLaunchDefaults = true;
          noLighting = true;
          startWaveTimeMultiplier = 3f;                                                                         
		}};

		explosionc = new SectorPreset("ng-explosionepicenter", newSerpulo, 24) {{ 
          addStartingItems = false;
          captureWave = 45;
          difficulty = 4;
          overrideLaunchDefaults = true;
          noLighting = true;
          startWaveTimeMultiplier = 3f;
		}};
		

		lens = new SectorPreset("ng-lens", newSerpulo, 216) {{ 
          addStartingItems = false;
          captureWave = 35;
          difficulty = 4;
          overrideLaunchDefaults = true;
          noLighting = false;
          startWaveTimeMultiplier = 1f;
          rules = r -> {
        	  r.winWave = captureWave;
        	  r.lighting = true;
          };
		}};
		
		Planet thanks = makeAsteroid("newGameMod.ng-thanks", newSerpulo, Blocks.ferricStoneWall, Blocks.beryllicStoneWall, 0.55f, 9, 1.3f, gen -> {
            gen.berylChance = 0.8f;
            gen.iceChance = 0f;
            gen.carbonChance = 0.01f;
            gen.max += 2;
        });
		SectorPreset empty = new SectorPreset("", thanks, 0);
		empty.techNode = node(empty, Seq.with(new Research(empty)), () -> {});
		empty.techNode.parent = empty.techNode;
		empty.alwaysUnlocked = false;
		empty.clearUnlock();
		for (int i = 0; i < thanks.sectors.size; i++) {
			thanks.sectors.get(i).preset = empty;
			thanks.sectors.get(i).generateEnemyBase = false;
		};
		thanks.sectors.get(0).preset = start;
		
//		thanks.sectors.add(new Sector(thanks, new Ptile(0, 6)));
		new SectorPreset("thk-aboba", thanks, 0) {{ 
//          addStartingItems = false;
//          captureWave = 35;
//          difficulty = 4;
//          overrideLaunchDefaults = true;
//          noLighting = false;
//          startWaveTimeMultiplier = 1f;
			alwaysUnlocked = true;
			rules = r -> {
				r.winWave = captureWave;
				r.lighting = true;
			};
		}};
		
		
		NGTechTree.load();
		
//		SectorPresets
//		Vars.state.rules.winWave = 1
//		Vars.content.items().each(i => Team.sharded.core().items.set(i, 400000));
//		Vars.content.planets().get(7).sectors.get(55).displayThreat();
//		Vars.content.planets().get(7).sectors.each(s => s.setName(null));
//		Vars.content.planets().get(7).sectors.each(s => s.preset = Vars.content.planets().get(7).sectors.get(4).preset);
		
//		Vars.content.planets().get(7).sectors.get(4).hasEnemyBase();
//		Vars.content.planets().get(7).sectors.get(4).preset.captureWave;
//		Team.sharded.data().getBuildings(Blocks.worldProcessor).each(e => e.tile.nearby(1, 0).setAir());
//		Vars.world.tiles.eachTile(t => {if(t.block().alwaysReplace ) t.setAir()});
//		Blocks
	}
	
	private static Planet makeAsteroid(String name, Planet parent, Block base, Block tint, float tintThresh, int pieces, float scale, Cons<AsteroidGenerator> cgen){
        return new Planet(name, parent, 1f, 1){{
          updateLighting = false;

			sectorSeed = 4;
			generator = new NgSerpuloPlanetGenerator();
//			meshLoader = () -> new HexMesh(this, 6);
//			cloudMeshLoader = () -> new MultiMesh(
//					new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, new Color().set(Pal.spore).mul(0.9f).a(0.75f), 2, 0.45f, 0.9f, 0.38f),
//					new HexSkyMesh(this, 1, 0.6f, 0.16f, 5, Color.white.cpy().lerp(Pal.spore, 0.55f).a(0.75f), 2, 0.45f, 1f, 0.41f)
//					);
			launchCapacityMultiplier = 0.5f;
			allowWaves = true;
			allowWaveSimulation = true;
			allowSectorInvasion = true;
			allowLaunchSchematics = true; // false?
			enemyCoreSpawnReplace = true;
			allowLaunchLoadout = false;
			clearSectorOnLose = true; // Edited
			
//            camRadius = 0.68f * scale;
//            minZoom = 0.6f;
//            drawOrbit = false;
//            accessible = true;
//            clipRadius = 2f;
            icon = "commandRally";
//			alwaysUnlocked = true;
//            generator = new AsteroidGenerator();
//            cgen.get((AsteroidGenerator)generator);

            meshLoader = () -> {
            	Seq<GenericMesh> meshes = new Seq<>();
//            	new MatMesh(cloudMesh, Mat3D.M00);
//mindustry.graphics.g3d.MatMesh
//            	meshes.add(new HexMesh(this, 6));
//            	return new ShaderSphereMesh(this, Shaders.cryofluid, 1);
//            	MeshBuilder.buildHex(new HexMesher() {
//
//            		@Override
//            		public float getHeight(Vec3 position) {
//            			return position.x;
//            		}
//
//            		@Override
//            		public Color getColor(Vec3 position) {
//            			return Pal.spore;
//            		}
//            	}, 6, false, radius, 0.2f);
            	

                for(int j = 0; j < grid.tiles.length; j++){
                    meshes.add(new MatMesh(
								new NoiseMesh(this, j + 1, 1,
										0.1f /* + Mathf.rand.random(0.039f) * scale */, 2, 0.6f, 0.38f, 20f,
										Color.black.cpy().a(0), Color.black.cpy().a(1), 3, 0.6f, 0.38f, tintThresh),
                        new Mat3D().setToTranslation(Tmp.v31.set(grid.tiles[j].v).setLength(.75f * scale)))
                    );
                }
                
//            	meshes.add(new MyMesh());
            	return new MultiMesh(meshes.toArray(GenericMesh.class));
            };

			ruleSetter = newSerpulo.ruleSetter;
			
			iconColor = Color.valueOf("7d4dff");
			atmosphereColor = Color.valueOf("3c1b8f").a(0);
			atmosphereRadIn = 0;
			atmosphereRadOut = 0;
			startSector = 0;
			alwaysUnlocked = true;
			landCloudColor = Pal.spore.cpy().a(0.5f);
			hiddenItems.addAll(Items.erekirItems).removeAll(Items.serpuloItems);

			drawOrbit = false;
//			orbitRadius = Planets.serpulo.orbitRadius;
//			orbitOffset = Planets.serpulo.orbitOffset + 180;
//			orbitTime = Planets.serpulo.orbitTime;
        }};
    }
}
