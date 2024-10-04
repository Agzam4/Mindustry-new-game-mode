package agzam4.content.bullets;

import agzam4.content.effects.NGFx;
import agzam4.content.units.LifeUnitType;
import arc.Events;
import arc.graphics.Color;
import arc.util.Tmp;
import mindustry.entities.bullet.RailBulletType;
import mindustry.game.EventType.UnitBulletDestroyEvent;
import mindustry.game.EventType.UnitDamageEvent;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;

public class LifeLaserBulletType extends RailBulletType {

    static final UnitDamageEvent bulletDamageEvent = new UnitDamageEvent();
    
    public LifeLaserBulletType() {
    	super();
    	lightColor = lightningColor = trailColor = hitColor = Color.valueOf("af79f7");
    }
    
	@Override
	public void hitEntity(Bullet b, Hitboxc entity, float health) {
		boolean wasDead = false;
		
		if(entity instanceof Healthc){
			Healthc h = (Healthc) entity;
			wasDead = h.dead();
			if(pierceArmor){
				h.damagePierce(b.damage);
			}else{
				h.damage(b.damage);
			}
		}

		if(entity instanceof Unit){
			Unit unit = (Unit) entity;
			Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f);
			if(impact) Tmp.v3.setAngle(b.rotation() + (knockback < 0 ? 180f : 0f));
			unit.impulse(Tmp.v3);
			unit.apply(status, statusDuration);

			Events.fire(bulletDamageEvent.set(unit, b));
		}

		if(!wasDead && entity instanceof Unit) {
			Unit unit = (Unit) entity;
			if(unit.dead) {
				if(LifeUnitType.essence(b.owner, unit.type.health)) {
					NGFx.sporeBeam.at(unit.x(), unit.y(), 0, Pal.spore, b.owner);
				}
				Events.fire(new UnitBulletDestroyEvent(unit, b));
			}
		}

		handlePierce(b, health, entity.x(), entity.y());
	}
}
