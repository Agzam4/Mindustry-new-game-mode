package agzam4.content.blocks.life;

import agzam4.Work;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;

public class MossPlant extends Block {

	public TextureRegion /* tendril, */ centerRegion, botRegion;
    static Rand rand = new Rand();

    public int lobesMin = 7, lobesMax = 7;
    public float botAngle = 60f, origin = 0.1f;
    public float sclMin = 30f, sclMax = 50f, magMin = 5f, magMax = 15f, timeRange = 40f, spread = 0f;
    
    public Attribute attribute = Attribute.spores;
    public float baseEfficiency = 1f;

    public float growthTime, seedsTime;
//	private float tarnsformationTime = 60*60f; Walls decrease FPS more than this block, IDK why
    
	public MossPlant(String name) {
		super(name);
        breakable = true;
        breakEffect = Fx.breakProp;
        breakSound = Sounds.rockBreak;
        update = true;
        mapColor = Pal.spore;
        targetable = false;
        underBullets = true;
        hasShadow = false;
        drawTeamOverlay = false;
        rebuildable = false;
	}
	
	@Override
	public void loadIcon() {
		super.loadIcon();
		centerRegion = Work.texture(name + "-center");
		botRegion = Work.texture(name + "-dark");
	}
	
	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		if(tile == null) return false;

		if(1f - tile.floor().attributes.get(Attribute.spores)*2f < 0) return false;
		if(1f - tile.floor().attributes.get(Attribute.water)*2f < 0) return false;
		return super.canPlaceOn(tile, team, rotation);
	}
	
	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);
//		if(attribute != null){
//			drawPlaceText(Core.bundle.format("bar.efficiency", Math.round(sumAttribute(attribute, x, y)*100 + 100)), x, y, valid);
//		}
	}
	
	@Override
	public void drawShadow(Tile tile) {
	}
	
	@Override
	protected TextureRegion[] icons() {
		return new TextureRegion[] {fullIcon};
	}
	
	public class MossPlantBuild extends Building {
		
		float growth = 0;
		float seeds = 0;
		byte valid = 1 | 2 | 4 | 8, valids = 4;
		float speedScale = 0, growthScale = 1, seedsScale = 1;
		int updates = 0;
		
		@Override
		public Building create(Block block, Team team) {
			return super.create(block, Team.derelict);
		}
		
		
//		float unactiveTime = 0;
		
		@Override
		public void update() {
			super.update();
//			super.updateTile();
			if(speedScale == 0) {
				calc();
			}
			updates++;
			if(health < maxHealth*growth && updates > 10) {
				health = Mathf.clamp(health + speedScale*maxHealth/growthTime, 0, maxHealth*growth);
				updates = 0;
				return;
			}
			growth = Mathf.clamp(growth + speedScale*growthScale/growthTime);
			if(growth == 1) {
				efficiency = speedScale*seedsScale/seedsTime;
				seeds = Mathf.clamp(seeds + speedScale*seedsScale/seedsTime);
//				unactiveTime+=speedScale;
//				if(unactiveTime > tarnsformationTime) {
//					tile.setBlock(NewGameBlocks.mossPlantWall);
//					return;
//				}
				if(seeds == 1) {
					if(valids == 0 && Vars.world.tileChanges == 0) return;
					seeds = 0;
					spread();
				}
			} else {
				efficiency = 0;
			}
		}
		
		protected void calc() {
			rand.setSeed(tile.pos());
			speedScale = .5f + rand.nextFloat()*.5f;
			growthScale += tile.floor().attributes.get(Attribute.water)*2;
			growthScale += tile.floor().liquidDrop == Liquids.water ? 2.5f : 0f;
			seedsScale += tile.floor().attributes.get(Attribute.spores)*2;
			if(growthScale < 0) growthScale = 0;
			if(seedsScale < 0) seedsScale = 0;
			if(Team.derelict != team) { 
				tile.setBlock(this.block);
			}
		}

		public void spread() {
			valid = 0;
			valids = 0;
			for (int i = 0; i < 4; i++) {
				Tile t = Vars.world.tile(tileX() + Geometry.d4x(i), tileY() + Geometry.d4y(i));
				if(valid(t)) {
					valid |= 1 << i;
					valids++;
				}
			}
			if(valids != 0) {
				int angle = rand.nextInt(valids);
				for (int i = 0; i < 4; i++) {
					if((valid & (1 << i)) == 0) continue;
					if(angle == 0) {
						Tile t = Vars.world.tile(tileX() + Geometry.d4x(i), tileY() + Geometry.d4y(i));
						t.setBlock(block(), team, i + rand.random(1, 3));
						break;
					}
					angle--;
				}
			}			
		}

		@Override
		public boolean canPickup() {
			return false;
		}
		
		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(growth);
			write.f(seeds);
//			write.f(unactiveTime);
		}
		
		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			growth = read.f();
			seeds = read.f();
//			unactiveTime = read.f();
		}
		
		public boolean valid(Tile t) {
			if(t == null) return false;
			if(t.block() != Blocks.air) return false;
			if(!t.floor().placeableOn) return false;
			if(t.floor().isDeep()) return false;
			return true;
		}
		
		@Override
		public void draw() {
	        rand.setSeed(tile.pos());
	        float offset = rand.random(180f);
	        int lobes = rand.random(lobesMin, lobesMax);
	        Draw.z(Layer.blockProp);
        	float scale = .1f + growth*.9f;

            Draw.color(0,0,0,.5f*growth);
            
            Draw.rect(customShadowRegion,
            		tile.drawx(), tile.drawy(), customShadowRegion.width*scale*customShadowRegion.scl(),
            		customShadowRegion.height*scale*customShadowRegion.scl());
            Draw.color();

	        for(int i = 0; i < lobes; i++){
	            float ba =  (i == lobes ? rotdeg() : (i / (float)lobes * 360f + offset + rand.range(spread))), angle = ba + Mathf.sin(Time.time + rand.random(0, timeRange), rand.random(sclMin, sclMax), rand.random(magMin, magMax));
	            TextureRegion r = Angles.angleDist(ba, 225f) <= botAngle ? botRegion : region;
	            float w = r.width * r.scl() * scale, h = r.height * r.scl() * scale;
	            Draw.rect(r,
	                tile.worldx() - Angles.trnsx(angle, origin) + w*0.5f, tile.worldy() - Angles.trnsy(angle, origin),
	                w, h,
	                origin*4f, h/2f,
	                angle
	            );
	        }

	        if(centerRegion.found()) {
	            Draw.rect(centerRegion, tile.worldx(), tile.worldy(), 
	            		scale*centerRegion.width* centerRegion.scl(), scale*centerRegion.height* centerRegion.scl(),
	            		rand.range(360) + Mathf.sin(Time.time*2f + rand.random(0, timeRange), rand.random(sclMin, sclMax), rand.random(magMin, magMax)));
	        }
		}
		
		@Override
		public void killed() {
			Fx.breakProp.at(x, y, 1, Pal.spore);
			super.killed();
		}
		
		
		
	}

	@Override
	public int minimapColor(Tile tile) {
		return Pal.spore.rgba();
	}
	
	
}
