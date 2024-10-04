package agzam4.content.bullets;

import agzam4.content.blocks.NewGameBlocks;
import agzam4.content.effects.NGFx;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;

public class SporeSeedBulletType extends BulletType {

	public int tier = 1;

	public SporeSeedBulletType() {
		hittable = true;
		collidesAir = false;
		collidesGround = false;
        weaveMag = 5f/5f;
        weaveScale = 50f/5f;
        lifetime = 60f*3;
        speed = Vars.tilesize*10f/60f;
        trailEffect = Fx.sporeSlowed;
        trailChance = .2f;
        trailColor = Pal.spore;
        hitEffect = chargeEffect = smokeEffect = despawnEffect = Fx.none;
        splashDamage = 100;
        splashDamageRadius = Vars.tilesize;
	}
	
	
	@Override
	public void draw(Bullet b) {
		Draw.color(Pal.spore);
		Fill.circle(b.x, b.y, Vars.tilesize/2f * (.1f + b.fin()*.9f));
		super.draw(b);
	}
	
	@Override
	public void update(Bullet b) {
		if(b.damage() != b.damage) {
			b.remove();
			return;
		}
		super.update(b);
	}
	
	@Override
	public boolean testCollision(Bullet bullet, Building tile) {
		return false;
	}
	
	@Override
	public void removed(Bullet b) {
		super.removed(b);
		if(b.tileOn() == null) return;
		if(b.tileOn().block() == Blocks.air) {
			if(NewGameBlocks.mossPlant.canPlaceOn(b.tileOn(), Team.derelict, 0)) {
				b.tileOn().setBlock(tier == 1 ? NewGameBlocks.mossPlant : NewGameBlocks.glowingMossPlant); // setNet?
				NGFx.sporesExplode.at(b.tileOn(), Vars.tilesize*2f);
			}
		}
	}
	
}
