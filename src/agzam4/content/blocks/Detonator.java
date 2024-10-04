package agzam4.content.blocks;

import agzam4.NewGameMod;
import agzam4.Work;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class Detonator extends Block {

	public static TextureRegion placeRegion;
	
    public @Nullable TextureRegion glow = null;
    public Color glowColor = Color.white; // fffd81
    
    public Color lightColor = Color.valueOf("7f19ea");
    public Color coolColor = new Color(1, 1, 1, 0f);
    public Color hotColor = Color.valueOf("ff9575a3");

    public int radius = 1;
    public int detonatePower = 1;
    
    public float glowScl = 16f, glowMag = 0.6f;
    
	public Detonator(String name) {
		super(name);
		
        update = true;
        solid = true;
        group = BlockGroup.drills;
        hasLiquids = false;
//        hasItems = true;
        ambientSound = Sounds.drill;
        ambientSoundVolume = 0.018f;
        flags = EnumSet.of(BlockFlag.drill);
        rebuildable = false;

        buildVisibility = BuildVisibility.shown;
        requirements(category, requirements);
	}
	
	@Override
	public void loadIcon() {
		super.loadIcon();
		placeRegion = Work.modTexture("detonator-select");
		glow = Work.texture(name + "-glow");
	}

	@Override
	public void setStats() {
		super.setStats();
        stats.add(Stat.range, radius, StatUnit.blocks);
        stats.add(Stat.damage, 10*radius, StatUnit.perShot);
	}
	
	@Override
	public void drawPlace(int tileX, int tileY, int rotation, boolean valid) {
		super.drawPlace(tileX, tileY, rotation, valid);
		for (int y = -radius; y <= radius; y++) {
			for (int x = -radius; x <=radius; x++) {
				if(Mathf.dst2(x, y) > radius*radius/2 + Mathf.floor(radius/3f)) continue;
				Tile tile = Vars.world.tile(tileX + x, tileY + y);
				if(tile == null) continue;
				if(tile.overlay() instanceof LockedOre) {
					if(!((LockedOre)tile.overlay()).canDetonateOre(tile, detonatePower)) continue;
				} else if(tile.block() == NewGameBlocks.mossPlant || tile.block() == NewGameBlocks.glowingMossPlant) {
				} else {
					continue;
				}
				Draw.color(Pal.remove, glowColor, Mathf.absin(Time.time, 9f, 1f));
//				Draw.alpha(.5f);
//	            Fill.rect(tile.worldx(), tile.worldy(), Vars.tilesize, Vars.tilesize);
//				Draw.color(1f,1f,1f);
				Draw.alpha(1f);
	            if(placeRegion.found()) Draw.rect(placeRegion, tile.worldx(), tile.worldy(), Vars.tilesize, Vars.tilesize);
//		        for(int i = 0; i < 4; i++){
//		            Point2 p = Geometry.d8edge[i];
//		            float offset =  tilesize/-2f;
//		            x*tilesize + offset * p.x,
//		            y*tilesize + offset * p.y, i * 90);
//		        }
			}
		}	
        Draw.reset();
	}
	
	 public class DetonatorBuild extends Building {
		 
		private float flash;
		
		int updates = 0;

		@Override
		public void updateTile() {
			super.updateTile();
			if(updates > 60*5) {
				explosion();
			}
			updates++;
		}
		
		private void explosion() {
			Call.logicExplosion(team, x, y, Vars.tilesize*3*radius, 10*radius, true, true, false);
			tile.remove();
			// TODO: generate array of points
			for (int y = -radius; y <= radius; y++) {
				for (int x = -radius; x <=radius; x++) {
					if(Mathf.dst2(x, y) > radius*radius/2 + Mathf.floor(radius/3f)) continue;
					Tile tile = Vars.world.tile(tileX() + x, tileY() + y);
					if(tile == null) continue;
					if(tile.overlay() instanceof LockedOre) {
						((LockedOre) tile.overlay()).detonateOre(tile, detonatePower);
					} else if(tile.block() == NewGameBlocks.mossPlant || tile.block() == NewGameBlocks.glowingMossPlant) {
						if(NewGameMod.isMode()) {
							Fx.breakProp.at(tile.worldx(), tile.worldy(), 1, Pal.spore);
							tile.setBlock(NewGameBlocks.itemStack);
							if(tile.build != null) {
								if(tile.build.items != null) {
									tile.build.items.add(Items.sporePod, tile.block() == NewGameBlocks.mossPlant ? 1 : 3);
								}
							}
						}
					}
				}
			}			
		}

		@Override
		public void draw() {
//			super.draw();
//			Draw.color(coolColor, hotColor, heat);
//			Fill.rect(x, y, size * tilesize, size * tilesize);
//			Draw.color(liquids.current().color);
//			Draw.alpha(liquids.currentAmount() / liquidCapacity);
//			Draw.rect(topRegion, x, y);
//			if(heat > flashThreshold){
//				flash += (1f + ((heat - flashThreshold) / (1f - flashThreshold)) * 5.4f) * Time.delta;
//			}
            flash += Time.delta*updates/60f;
            float a = Mathf.absin(flash, 9f, 1f);
			if(glow == null) Draw.color(Color.red, Color.white, a);
//			Draw.alpha(0.3f);
			Draw.rect(region, x, y);
			
			
			
            if(a > 0.001f && glow != null) {
                Drawf.additive(glow, Tmp.c1.set(glowColor).mula(a), x, y);
            }
//			Draw.(lightsRegion, x, y);
			
			Draw.reset();
		}
		
		@Override
		public void killed() {
			super.killed();
			explosion();
		}
	 }

}
