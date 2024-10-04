package agzam4.content.blocks.life;

import agzam4.NewGameMod;
import agzam4.Work;
import agzam4.content.blocks.NewGameBlocks;
import agzam4.content.effects.NGFx;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Rect;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

public class Cutter extends Block {

	private TextureRegion bladeRegionRight, bladeRegionLeft, bladeRegionBlur, rotatorRegion, topRegion;
    public float rotateSpeed = 10f;
    public float damage = 1000/60f;
	private static Rect rect = new Rect();

	public Cutter() {
		super("cutter");
		size = 2;
        update = true;
        solid = true;
        group = BlockGroup.drills;
        hasLiquids = false;
        ambientSound = Sounds.drill;
        ambientSoundVolume = 0.018f;
        health = 1600;
	}

	@Override
	public void loadIcon() {
		super.loadIcon();
		bladeRegionRight = Work.texture(name + "-blade-r");
		bladeRegionLeft = Work.texture(name + "-blade-l");
		bladeRegionBlur = Work.texture(name + "-blade-blur");
		rotatorRegion = Work.texture(name + "-rotator");
		topRegion = Work.texture(name + "-top");
	}

    @Override
    public void setStats(){
    	super.setStats();
        stats.add(Stat.damage, StatValues.number(damage*60f, StatUnit.perSecond));
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }

	public class BladerBuild extends Building {

        private float totalTime;
        private float efficiency;
        private float[] bladeTranslate = new float[8];

		@Override
        public void updateTile(){
            super.updateTile();
            efficiency = Mathf.approach(efficiency, edelta(), .005f);
            totalTime += rotateSeed();
			updates++;
            for (int i = 0; i < 8; i++) {
            	updateBlade(i, getEdges()[i].x, getEdges()[i].y);
			}
            if(updates > 5) updates = 0;
        }
		
		Tile t;
		int updates = 0;

		private void updateBlade(int i, int x, int y) {
			t = Vars.world.tile(tileX() + x, tileY() + y);
			if(t == null) return;
			if(t.team() == team || ((t.block() == NewGameBlocks.mossPlant || t.block() == NewGameBlocks.glowingMossPlant) && t.build != null && t.build.efficiency == 0)) {
				bladeTranslate[i] = Mathf.approach(bladeTranslate[i], .5f, .01f);
			} else {
				bladeTranslate[i] = Mathf.approach(bladeTranslate[i], 1f, .01f);
				if(t.block() != NewGameBlocks.itemStack && t.build != null) {
					if((t.block() == NewGameBlocks.mossPlant || t.block() == NewGameBlocks.glowingMossPlant)) {
						if(NewGameMod.isMode() && efficiency >= delta()) {
							Fx.breakProp.at(t.worldx(), t.worldy(), 1, Pal.spore);
							t.setBlock(NewGameBlocks.itemStack);
							if(t.build != null) {
								if(t.build.items != null) {
									t.build.items.add(Items.sporePod, t.block() == NewGameBlocks.mossPlant ? 1 : 3);
									return;
								}
							}
						}
					} else {
						t.build.damageContinuous(damage*efficiency);
					}
				}
			}
			if(updates >= 5) {
				if(bladeTranslate[i] > .5 && efficiency > 0) {
					float bx = t.worldx(), by = t.worldy();
					if(i/2 == 0) bx -= Vars.tilesize/2f;
					if(i/2 == 1) by -= Vars.tilesize/2f;
					if(i/2 == 2) bx += Vars.tilesize/2f;
					if(i/2 == 3) by += Vars.tilesize/2f;
			        rect.setSize(Vars.tilesize).setCenter(bx, by);
			        final float bbx = bx, bby = by;
		            Units.nearby(rect, u -> {
//		            	if(u.team == team) return;
		            	if(!u.checkTarget(false, true)) return;
						if (!u.within(bbx, bby, Vars.tilesize + u.hitSize / 2f)) return;

//		                boolean dead = u.dead;

//		                float amount = Damage.calculateDamage(scaled ? Math.max(0, unit.dst(x, y) - unit.type.hitSize/2) : unit.dst(x, y), radius, damage);
		                u.damageContinuousPierce(damage*efficiency*5f);
		                NGFx.fireSpark.at(bbx, bby, i/2*90);
//		                NGFx.fireSpark.at(u, i/2*90);
		            });
				}
			}
		}


		@Override
        public void draw(){
            Draw.rect(block.region, x, y);
            for (int dy = 0; dy < size; dy++) for (int dx = 0; dx < size; dx++) {
            	Drawf.spinSprite(rotatorRegion, x + (dx-.5f)*6, y + (dy-.5f)*6, 
            			totalTime*rotateSpeed*(dx+dy == 1 ? 1 : -1)/2f + (dx+dy == 1 ? 45 : 0));
            }
        	Draw.rect(topRegion, x, y);
            index = 0;
            drawBlade(1,-.5f,1);
            drawBlade(1,.5f,-1);

            drawBlade(.5f,1,1);
            drawBlade(-.5f,1,-1);

            drawBlade(-1,.5f,1);
            drawBlade(-1,-.5f,-1);

            drawBlade(-.5f,-1,1);
            drawBlade(.5f,-1,-1);
        }
		
		int index = 0;
		
		private void drawBlade(float dx, float dy, int anglek) {
			dx *= Vars.tilesize*.9f*(Math.abs(dx) == 1 ? bladeTranslate[index] : 1f);
			dy *= Vars.tilesize*.9f*(Math.abs(dy) == 1 ? bladeTranslate[index] : 1f);
            Draw.z(Layer.blockUnder);
            float blur = Mathf.clamp(rotateSeed()-.5f);
            Draw.alpha(blur);
            Drawf.spinSprite(bladeRegionBlur, x + dx, y + dy, totalTime*rotateSpeed*anglek);
            Draw.alpha(1f - blur);
            Drawf.spinSprite((anglek == 1 ? bladeRegionRight : bladeRegionLeft), x + dx, y + dy, totalTime*rotateSpeed*anglek);
            index++;
		}

		private float rotateSeed() {
			return efficiency;
		}
	}
	
}
