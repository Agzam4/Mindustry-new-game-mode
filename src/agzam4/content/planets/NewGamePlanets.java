package agzam4.content.planets;

import agzam4.content.blocks.NewGameBlocks;
import arc.Core;
import arc.graphics.Color;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.content.TechTree;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Planet;

public class NewGamePlanets {

	public static Planet newSerpulo;

	public static void load() {
		newSerpulo = new Planet("newGameMod.new-serpulo", Planets.sun, 1f, 3) {{
//			techTree = TechTree.node(newSerpulo, null);
			techTree = TechTree.nodeRoot(Core.bundle.get("planet." + this.name + ".name", this.name), Blocks.coreShard, () -> {
				TechTree.node(NewGameBlocks.copperSeparator);
				TechTree.node(NewGameBlocks.unloadPoint);
				
				TechTree.node(NewGameBlocks.pneumaticDetonator,
						() -> TechTree.node(NewGameBlocks.differentialDetonator,
								() -> TechTree.node(NewGameBlocks.blastDetonator)));
			});
			
			generator = new SerpuloPlanetGenerator();
			meshLoader = () -> new HexMesh(this, 6);
			cloudMeshLoader = () -> new MultiMesh(
					new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, new Color().set(Pal.spore).mul(0.9f).a(0.75f), 2, 0.45f, 0.9f, 0.38f),
					new HexSkyMesh(this, 1, 0.6f, 0.16f, 5, Color.white.cpy().lerp(Pal.spore, 0.55f).a(0.75f), 2, 0.45f, 1f, 0.41f)
					);
			launchCapacityMultiplier = 0.5f;
			sectorSeed = 2;
			allowWaves = true;
			allowWaveSimulation = true;
			allowSectorInvasion = true;
			allowLaunchSchematics = true;
			enemyCoreSpawnReplace = true;
			allowLaunchLoadout = true;
			//doesn't play well with configs
			
			prebuildBase = false;
			ruleSetter = r -> {
				r.waveTeam = Team.crux;
				r.placeRangeCheck = false;
				r.showSpawns = false;
			};
			iconColor = Color.valueOf("7d4dff");
			atmosphereColor = Color.valueOf("3c1b8f");
			atmosphereRadIn = 0.02f;
			atmosphereRadOut = 0.3f;
			startSector = 15;
			alwaysUnlocked = true;
			landCloudColor = Pal.spore.cpy().a(0.5f);
			hiddenItems.addAll(Items.erekirItems).removeAll(Items.serpuloItems);
		}};
	}
}
