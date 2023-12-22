package agzam4.content.blocks.life;

import agzam4.Work;
import agzam4.content.effects.NGFx;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

public class LifeWall extends LifeEssenceStorageBlock {

    public TextureRegion glowRegion, glowLightRegion;
    
	public LifeWall(String name) {
		// Wall;
		super(name);
        solid = true;
        rotate = false;
//        absorbLasers = true;
        absorbLasers = false;
        update = true;
	}
	
	@Override
	public void loadIcon() {
		super.loadIcon();
		glowRegion = Work.texture(name + "-glow");
		glowLightRegion = Work.texture(name + "-glow-light");
	}

	public class LifeRouterBuild extends LifeEssenceStorageBuild {
		
		private float flash;

		float fxAttackCooldown = 0;
		
		@Override
		public void updateTile() {
			if(fxAttackCooldown > 0) fxAttackCooldown--;
			super.updateTile();
		}
		
		@Override
		public boolean absorbLasers() {
			return essence > .0001f;
		}
		
		@Override
		public void draw() {
            Draw.rect(region, x, y, 0);
            
            flash += Time.delta;
            
            drawEssenceLight(this, glowRegion, glowLightRegion, flash);
//            float glow = Mathf.clamp(essence()/essenceCapacity());
//            Drawf.light(this, size*Vars.tilesize, Tmp.c1.set(Color.white), glow);
//            
//            glow = Mathf.clamp(essence()*2/essenceCapacity());
//            float light = Mathf.clamp((essence()-essenceCapacity()/2f)/essenceCapacity(), 0, .5f)*2f*(Mathf.absin(flash, 9f, 1f)/2f+.5f);
//
//            Drawf.additive(glowRegion, Tmp.c1.set(fillColor).mula(glow), x, y, rotdeg());
//            Drawf.additive(glowLightRegion, Tmp.c1.set(Color.white).mula(light), x, y, rotdeg());
//            Drawf.light(this, size*Vars.tilesize*2f, Tmp.c1.set(fillColor), light);
		}
	
		@Override
		public boolean collision(Bullet other) {
			if(other.damage() < 1f) return super.collision(other);
			if(absorbDamage(other.damage())) {
				if(other.owner() != null) {
					if(other.owner() instanceof Healthc) {
						Healthc u = (Healthc) other.owner();
						u.damagePierce(other.damage()/2f);
						if(fxAttackCooldown <= 0) NGFx.sporeBeam.at(u.x(), u.y(), rotation, Pal.spore, this);
						if(fxAttackCooldown <= 0) Fx.pointHit.at(u.x(), u.y(), Pal.spore);
						fxAttackCooldown = 12f;
					}
				}
				if(other.type.continuousDamage() < 0) {
					other.remove();
				}
				return false;
			}
			return super.collision(other);
		}
	}
}
