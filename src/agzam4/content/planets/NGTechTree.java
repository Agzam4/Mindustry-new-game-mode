package agzam4.content.planets;

import static mindustry.content.Blocks.*;
import static mindustry.content.TechTree.*;
import static mindustry.content.UnitTypes.*;

import agzam4.content.blocks.NewGameBlocks;
import static agzam4.content.planets.NewGamePlanets.*;

import arc.Core;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.TechTree;
import mindustry.content.UnitTypes;
import mindustry.game.Objectives.OnSector;
import mindustry.game.Objectives.Research;
import mindustry.game.Objectives.SectorComplete;

public class NGTechTree {

	public static void load() {
		newSerpulo.techTree = TechTree.nodeRoot(Core.bundle.get("planet." + newSerpulo.name + ".name", newSerpulo.name), coreShard, () -> {
			node(NewGameBlocks.copperSeparator, Seq.with(new OnSector(deliverycore)), () -> {});
			node(NewGameBlocks.unloadPoint, Seq.with(new OnSector(deliverycore)), () -> {
				node(NewGameBlocks.attractor);
			});
			node(NewGameBlocks.pneumaticDetonator,
					() -> node(NewGameBlocks.differentialDetonator,
							() -> node(NewGameBlocks.blastDetonator)));
			// life branch
			node(NewGameBlocks.cutter, Seq.with(new OnSector(sporelab)),
					() -> node(NewGameBlocks.mossPlant, Seq.with(new SectorComplete(sporelab)),
							() -> node(NewGameBlocks.coreEntropy, () -> {
								node(NewGameBlocks.devourer, Seq.with(new OnSector(repairbase)),() -> {
									node(NewGameBlocks.lifeMover, () -> {
										node(NewGameBlocks.lifeRouter);
										node(NewGameBlocks.lifeWall);
									});
									node(NewGameBlocks.lifeReactor, Seq.with(new OnSector(explosionc)), () -> {
										node(NewGameBlocks.glowingMossPlant, Seq.with(new SectorComplete(explosionc)), () -> {});
									});
								});
								node(NewGameBlocks.coreDissipation, Seq.with(new SectorComplete(repairbase)), () -> {
									node(NewGameBlocks.coreDiscord, Seq.with(new SectorComplete(explosionc)), () -> {
									});
								});
							})));
    		// laser branch
			node(NewGameBlocks.coreAnode, Seq.with(new SectorComplete(deliverycore)), () -> {
				node(NewGameBlocks.coreCathode, Seq.with(new OnSector(lens)), () -> {
					node(NewGameBlocks.coreIon, Seq.with(new SectorComplete(lens)), () -> {});
				});
				node(NewGameBlocks.emitter, Seq.with(new OnSector(lens)), () -> {
					node(NewGameBlocks.largeEmitter, Seq.with(new SectorComplete(lens)), () -> {});
					node(NewGameBlocks.laserMixer, Seq.with(new OnSector(lens)), () -> {
						node(NewGameBlocks.largeLaserMixer, Seq.with(new SectorComplete(lens)), () -> {});
					});
					node(NewGameBlocks.laserPainter, Seq.with(new OnSector(lens)), () -> {
						node(NewGameBlocks.largeLaserPainter, Seq.with(new SectorComplete(lens)), () -> {});
					});
					node(NewGameBlocks.laserResizer, Seq.with(new SectorComplete(lens)), () -> {
						node(NewGameBlocks.laserEffect, Seq.with(new SectorComplete(lens)), () -> {});
					});
				});
			});
			
            node(start, Seq.with(new Research(duo), new Research(scatter), new Research(Items.sand)), () -> {
            	node(deliverycore, Seq.with(new SectorComplete(start), new Research(mono), new Research(airFactory), new Research(Items.silicon), new Research(graphitePress)), () -> {
            		// life branch
            		node(sporelab, Seq.with(new SectorComplete(deliverycore), new Research(NewGameBlocks.unloadPoint), new Research(NewGameBlocks.differentialDetonator)), () -> {
            			node(repairbase, Seq.with(new SectorComplete(sporelab), new Research(NewGameBlocks.coreEntropy), new Research(UnitTypes.fortress)), () -> {
            				node(explosionc, Seq.with(new SectorComplete(repairbase), new Research(NewGameBlocks.coreDissipation), new Research(Items.thorium)), () -> {
                            	
                            });
                        });
                    });
            		// laser branch
            		node(lens, Seq.with(new SectorComplete(deliverycore), new Research(NewGameBlocks.unloadPoint), new Research(NewGameBlocks.blastDetonator), new Research(NewGameBlocks.coreAnode)), () -> {});
                });
            });
		});
		
//		Vars.state.rules.weather.get(0).always
//		Vars.content.planets().get(7).techTree.each(c => {c.content.clearUnlock();});
	}
}
