package agzam4.content.blocks.life;

import agzam4.Work;
import agzam4.content.blocks.NewGameBlocks;
import agzam4.content.bullets.SporeSeedBulletType;
import agzam4.content.effects.NGFx;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;

public class GlowingMossPlant extends MossPlant {
	
    public TextureRegion glowRegion;
    public float explodeRange = Vars.tilesize*2f;
	public float explodeDamage = 100;
	
    public SporeSeedBulletType spore = new SporeSeedBulletType() {{
//    	ForceProjector
//    	PointDefenseTurret
        lifetime = 60f;
        tier = 2;
    }};
	
	public GlowingMossPlant(String name) {
		super(name);
	}
	
	@Override
	public void loadIcon() {
		super.loadIcon();
		glowRegion = Work.texture(name + "-glow");
	}
	
	public class GlowingMossPlantBuild extends MossPlantBuild {

		
		private long seed;
		public float glowing = 0;
		private float seedsBubble = 0; // For visual

		@Override
		public void update() {
			super.update();
			seedsBubble = Math.max(seedsBubble, seeds);
		}
		
		@Override
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
			if(valids == 0) {
				float spores = 4;
				for (int i = 0; i < 4; i++) {
					Tile t = Vars.world.tile(tileX() + Geometry.d4x(i), tileY() + Geometry.d4y(i));
					if(t == null) {
						spores--;
						continue;
					}
					if(t.block() == NewGameBlocks.mossPlant) {
						spores-=.5f;
					}
					if(t.block() == NewGameBlocks.glowingMossPlant) {
						spores--;
					}
				}
				if(spores >= 1) {
					Damage.damage(team, x, y, explodeRange, explodeDamage);
					NGFx.sporesExplode.at(x, y, explodeRange);
	                Effect.shake(3f, 3f, this);
	                seedsBubble = 0;
				}
				rand.setSeed(seed);
				for (int i = 0; i < spores; i++) {
					spore.create(null, team, x, y, rand.nextInt(350), -1, rand.nextFloat()*.5f + .5f, rand.nextFloat()*.1f + .9f, null);
				}
				glowing = Mathf.clamp(glowing + spores/4f);
				seed = rand.nextLong();
			} else {
				Damage.damage(team, x, y, explodeRange, explodeDamage);
				NGFx.sporesExplode.at(x, y, explodeRange);
                Effect.shake(3f, 3f, this);
                seedsBubble = 0;
				int angle = rand.nextInt(valids);
				for (int i = 0; i < 4; i++) {
					if((valid & (1 << i)) == 0) continue;
					if(angle == 0) {
						Tile t = Vars.world.tile(tileX() + Geometry.d4x(i), tileY() + Geometry.d4y(i));
						t.setBlock(block(), team, i + rand.random(1, 3));
						glowing = Mathf.clamp(glowing + .1f);
						break;
					}
					angle--;
				}
			}
		}
		
		@Override
		protected void calc() {
			super.calc();
			seed = rand.nextLong();
		}
		
		@Override
		public void write(Writes write) {
			super.write(write);
			write.l(seed);
			write.f(glowing);
		}
		
		@Override
		public void readAll(Reads read, byte revision) {
			super.readAll(read, revision);
			seed = read.l();
			glowing = read.f();
		}
		
		@Override
		public void kill() {
			Damage.damage(team, x, y, explodeRange, explodeDamage);
			NGFx.sporesExplode.at(x, y, explodeRange);
			super.kill();
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

            float bz = Draw.z();
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
                Draw.z(bz + .1f);
                Draw.color(Pal.spore);
                Draw.alpha(Mathf.clamp(glowing));
                Draw.blend(Blending.additive);
                r = glowRegion;
//	            w = r.width * r.scl() * scale;
//	            h = r.height * r.scl() * scale;
                float gorigin = origin*32f/52f;
                Draw.rect(r,
    	                tile.worldx() - Angles.trnsx(angle, gorigin) + w*0.5f, tile.worldy() - Angles.trnsy(angle, gorigin),
    	                w, h,
    	                gorigin*4f, h/2f,
    	                angle
                    );
                Draw.blend();
                Draw.color();
                Draw.z(bz);
	        }

	        if(centerRegion.found()) {
	            Draw.rect(centerRegion, tile.worldx(), tile.worldy(), 
	            		scale*centerRegion.width* centerRegion.scl(), scale*centerRegion.height* centerRegion.scl(),
	            		rand.range(360) + Mathf.sin(Time.time*2f + rand.random(0, timeRange), rand.random(sclMin, sclMax), rand.random(magMin, magMax)));
	        }

            Draw.color(Pal.spore);
            Draw.z(bz + .2f);
            Draw.color(Pal.spore);
            Draw.alpha(.75f);
            scale = seedsBubble;
            float angle = 225 + Mathf.sin(Time.time*2f + rand.random(0, timeRange));
            Draw.rect(customShadowRegion, 
            		tile.worldx(), 
            		tile.worldy(), 
            		scale*centerRegion.width* centerRegion.scl(), scale*centerRegion.height* centerRegion.scl());
            scale *= .25f;
            Draw.blend(Blending.additive);
            Draw.color(1f,1f,1f);
            Draw.alpha(.5f);
            Draw.rect(customShadowRegion, 
            		tile.worldx() - Angles.trnsx(angle, 1f*seedsBubble), 
            		tile.worldy() - Angles.trnsx(angle, 1f*seedsBubble), 
            		scale*centerRegion.width* centerRegion.scl(), scale*centerRegion.height* centerRegion.scl());
            Draw.blend();
            Draw.color();
		}

		public float essence() {
			return glowing*4f;
		}

		public void clearEssence() {
			glowing = 0;
		}
	}
}
