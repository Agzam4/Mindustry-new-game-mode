package agzam4.content.blocks.life;

import static mindustry.Vars.tilesize;

import agzam4.content.blocks.life.LifeMover.LifeMoverBuild;
import agzam4.content.effects.NGFx;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.core.Renderer;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;

public class LifeEssenceStorageBlock extends Block {

	public final static Color bottomColor = Color.valueOf("2c2d38"); //2c2d38
	public final static Color fillColor = Pal.spore;
	public static TextureRegion bridge = null;
	
	public static TextureRegion bridge() {
		if(bridge == null) {
			bridge = Core.atlas.find("newgamemod-life-bridge");
		}
		return bridge;
	}
	
	public float essenceCapacity = 1000;
	
	public LifeEssenceStorageBlock(String name) {
		super(name);
	}
	
	@Override
	public void loadIcon() {
//		if(bridge == null) bridge = ;
		super.loadIcon();
	}
	
	@Override
	public void setBars() {
		super.setBars();
		LifeEssenceBuild.setBars(this);
	}
	
    @Override
    public void setStats(){
    	// TODO
        super.setStats();
    }
    
    public static void drawEssenceLight(LifeEssenceBuild build, TextureRegion glowRegion, TextureRegion glowLightRegion, float flash) {
        float glow = Mathf.clamp(build.essence()/build.essenceCapacity());
        Drawf.light(build, build.block().size*Vars.tilesize, Tmp.c1.set(Color.white), glow);
        
        glow = Mathf.clamp(build.essence()*2f/build.essenceCapacity());
        float light = Mathf.clamp((build.essence()-build.essenceCapacity()/2f)/build.essenceCapacity(), 0, .5f)*2f*(Mathf.absin(flash, 9f, 1f)/2f+.5f);

		Drawf.additive(glowRegion, Tmp.c1.set(fillColor).mula(glow), build.x(), build.y(), build.rotdeg());
        Drawf.additive(glowLightRegion, Tmp.c1.set(Color.white).mula(light), build.x(), build.y(), build.rotdeg());
        Drawf.light(build, build.block().size*Vars.tilesize*2f, Tmp.c1.set(fillColor), light);
	}

	public class LifeEssenceStorageBuild extends Building implements LifeEssenceBuild {

		public float essence;

		@Override
		public float essence() {
			return essence;
		}

		@Override
		public float essenceCapacity() {
			return essenceCapacity;
		}

		@Override
		public LifeEssenceBuild essence(float essence) {
			this.essence = essence;
			return this;
		}
		
		public float fxCooldown = 0;
		
		@Override
		public void updateTile() {
			if(fxCooldown > 0) fxCooldown--;
			super.updateTile();
		}

        
        @Override
        public void write(Writes write){
            super.write(write);
            write.f(essence);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            essence = read.f();
        }
        
        @Override
        public void damage(float amount, boolean withEffect) {
        	if(absorbDamage(amount)) return;
        	super.damage(amount, withEffect);
        }
        @Override
        public void damage(Bullet bullet, Team source, float damage) {
        	if(absorbDamage(damage)) return;
        	super.damage(bullet, source, damage);
        }
        
        @Override
        public void damage(float damage) {
        	if(absorbDamage(damage)) return;
        	super.damage(damage);
        }
        
        @Override
        public void damage(Team source, float damage) {
        	if(absorbDamage(damage)) return;
        	super.damage(source, damage);
        }
        
        @Override
        public void damageContinuous(float amount) {
        	if(absorbDamage(amount)) return;
        	super.damageContinuous(amount);
        }
        
        @Override
        public void damageContinuousPierce(float amount) {
        	if(absorbDamage(amount)) return;
        	super.damageContinuousPierce(amount);
        }
        
        @Override
        public void damagePierce(float amount) {
        	if(absorbDamage(amount)) return;
        	super.damagePierce(amount);
        }
        
        @Override
        public void damagePierce(float amount, boolean withEffect) {
        	if(absorbDamage(amount)) return;
        	super.damagePierce(amount, withEffect);
        }
        
        public boolean absorbDamage(float damage) {
        	if(essence >= damage/2f) {
        		essence -= damage/2f;
        		if(fxCooldown <= 0) NGFx.sporeShield.at(x, y, size, Pal.spore);
        		fxCooldown = NGFx.sporeShield.lifetime;
            	return true;
        	}
        	return false;
		}

	}
	
	public static void bridge(Building b, Tile t, float light) {
		bridge(b.x, b.y, t, light);
	}

	public static void bridge(float x, float y, Tile t, float light) {
        Draw.color(Color.white);
        Draw.alpha(Renderer.bridgeOpacity);

        Lines.stroke(6.5f);

        Tmp.v1.set(x, y).sub(t.worldx(), t.worldy()).setLength(tilesize/2f).scl(-1f);
        
        Lines.line(LifeEssenceStorageBlock.bridge(),
        x + Tmp.v1.x,
        y + Tmp.v1.y,
        t.worldx() - Tmp.v1.x,
        t.worldy() - Tmp.v1.y, false);

        Drawf.light(x + Tmp.v1.x, y + Tmp.v1.y, t.worldx() - Tmp.v1.x, t.worldy() - Tmp.v1.y, 12f, Pal.spore, light);
	}

}
