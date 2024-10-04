package agzam4.content.units;

import agzam4.Work;
import agzam4.content.blocks.life.LifeEssenceBuild;
import agzam4.content.bullets.LifeBulletType;
import agzam4.content.effects.NGFx;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.core.UI;
import mindustry.entities.units.WeaponMount;
import mindustry.game.EventType.Trigger;
import mindustry.game.EventType.UnitChangeEvent;
import mindustry.game.EventType.UnitDamageEvent;
import mindustry.game.EventType.UnitDestroyEvent;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;
import mindustry.ui.Bar;

public class LifeUnitType extends MyUnitType {

	public static ObjectMap<Integer, Essence> essences = new ObjectMap<Integer, Essence>();
	
	public final static Color bottomColor = Color.valueOf("2c2d38"); //2c2d38
	public final static Color fillColor = Pal.spore;

	public int lightWeaponIndex = -1;

//	private static int checkCooldown = 60*60*5; // 5 minutes

	private static ObjectMap<Weapon, TextureRegion> glowRegions = new ObjectMap<Weapon, TextureRegion>();
	private static ObjectMap<Weapon, TextureRegion> glowLightRegions = new ObjectMap<Weapon, TextureRegion>();

	private static Seq<Integer> removeIds = new Seq<Integer>();
    public float damageReflectionMultiplier = 0;

    float fxCooldown = 0;
    
	@Override
	public void init() {
		Events.on(UnitDestroyEvent.class, e -> {
			essences.remove(e.unit.id);
		});
		Events.on(UnitChangeEvent.class, e -> {
//			if(e.unit == null) return;
//			Log.info(e.unit.spawnedByCore);
//			if(e.unit.spawnedByCore) {
//				float es = essence(e.unit);
//				Log.info("Es: @", es);
//				if(es > 0) LifeEssenceBuild.tryAddEssence(e.unit.core(), es);
//			}
			clearOld();
//			checkCooldown = 60*60*5;
		});

		Events.on(UnitDamageEvent.class, e -> {
			if(e.bullet == null) return;
			if(e.unit == null) return;
			if(e.bullet.owner == null) return;
			if(e.bullet.type instanceof LifeBulletType) return;
			if(e.unit.type instanceof LifeUnitType && e.bullet.owner instanceof Healthc) {
				if(e.bullet.owner instanceof LifeUnitType) return;
				LifeUnitType damaged = (LifeUnitType) e.unit.type;
				Healthc enemy = (Healthc) e.bullet.owner;
				
				float es = essence(e.unit);
				float dmg = Math.min(es, e.bullet.damage());
				if(dmg <= 0) return;
				
				essence(e.unit, -dmg);
				if(e.unit.spawnedByCore) {
					LifeEssenceBuild.tryAddEssence(e.unit.core(), dmg);
				}
				if(fxCooldown <= 0) {
					NGFx.sporeBeam.at(e.unit.x(), e.unit.y(), 0, Pal.spore, e.bullet.owner);
					fxCooldown = 12f; // It's bad to has one time for all damages
				}
				enemy.damagePierce(dmg*damaged.damageReflectionMultiplier);
				e.unit.heal(dmg);
				
				return;
			}

		});

		Events.run(Trigger.update, () -> {
			if(fxCooldown > 0) fxCooldown--;
//			if(checkCooldown > 0) {
//				checkCooldown--;
//			} else {
//				clearOld();
//				checkCooldown = 60*60*5;
//			}
		});
		super.init();
	}
	
	private static void clearOld() {
		removeIds.clear();
		essences.each((id,e) -> {
			if(Groups.unit.getByID(id) == null) {
				removeIds.add(id);
			}
		});
		removeIds.each(id -> essences.remove(id));
	}
	
	@Override
	public void loadIcon() {
		for (Weapon w : weapons) {
			glowRegions.put(w, Work.texture(w.name + "-glow"));
			glowLightRegions.put(w, Work.texture(w.name + "-glow-light", w.name + "-glow"));
		}
		super.loadIcon();
	}

	public static boolean essence(Entityc e, float essence) {
		if(e instanceof Unit) return essence((Unit) e, essence);
		return false;
	}
	
	public static boolean essence(Unit unit, float essence) {
		if(unit.type instanceof LifeUnitType) {
			LifeUnitType lt = (LifeUnitType) unit.type;
			Essence f = essences.get(unit.id);
			if(f == null) {
				essences.put(unit.id, new Essence(Mathf.clamp(essence, 0, lt.essenceCapacity)));
				return true;
			}
			if(f.essence >= lt.essenceCapacity && essence > 0) return false;
			f.essence = Mathf.clamp(f.essence+essence, 0, lt.essenceCapacity);
			return true;
		}
		return false;
	}
	
	
	public static float essence(Unit unit) {
		Essence f = essences.get(unit.id);
		if(f == null) {
			essences.put(unit.id, new Essence());
			return 0f;
		}
		return f.essence;
	}
	
	
	public float essenceCapacity = 100;

	
	public LifeUnitType(String name) {
		super(name, ConstructorUnitTypes.legs);
	}

	@Override
	public void drawBody(Unit unit) {
        applyColor(unit);
        Draw.rect(region, unit.x, unit.y, unit.rotation - 90);
        Draw.reset();
	}
	
//	@Override
//	public void update(Unit unit) {
//		super.update(unit);
//		float freeh = unit.maxHealth() - unit.health();
//		if(freeh > 0) {
//			float es = essence(unit);
//			if(es == 0) return;
//			freeh = Math.min(freeh, es);
//			unit.heal(freeh);
//			essence(unit, -freeh);
//			if(unit.spawnedByCore) {
//				LifeEssenceBuild.tryAddEssence(unit.core(), freeh);
//			}
//		}
//		float es = essence(unit);
//		if(es > 0) LifeEssenceBuild.tryAddEssence(unit.core(), es);
//	}
	
	@Override
	public void drawWeapons(Unit unit) {
        float z = Draw.z();
		super.drawWeapons(unit);
		if(lightWeaponIndex != -1) {
			for (WeaponMount mount : unit.mounts) {
				Weapon w = mount.weapon;
//				if(w.name.contains("life"))
		        Draw.z(z + w.layerOffset + 1);
		        
		        float
		        rotation = unit.rotation - 90,
		        realRecoil = Mathf.pow(mount.recoil, w.recoilPow) * w.recoil,
		        weaponRotation  = rotation + (w.rotate ? mount.rotation : w.baseRotation),
		        wx = unit.x + Angles.trnsx(rotation, w.x, w.y) + Angles.trnsx(weaponRotation, 0, -realRecoil),
		        wy = unit.y + Angles.trnsy(rotation, w.x, w.y) + Angles.trnsy(weaponRotation, 0, -realRecoil);
				
				float es = essence(unit);

		        float glow = Mathf.clamp(es/essenceCapacity);
		        Drawf.light(unit, unit.hitSize(), Tmp.c1.set(Color.white), glow);
		        
		        glow = Mathf.clamp(es*2f/essenceCapacity);
		        float light = Mathf.clamp((es-essenceCapacity/2f)/essenceCapacity, 0, .5f)*2f*(Mathf.absin(Time.time, 9f, 1f)/2f+.5f);

				Drawf.additive(glowRegions.get(w), Tmp.c1.set(fillColor).mula(glow), wx, wy, rotation, z + w.layerOffset + 1);
		        Drawf.additive(glowLightRegions.get(w), Tmp.c1.set(Color.white).mula(light), wx, wy, rotation, z + w.layerOffset + 1);
		        Drawf.light(unit, unit.hitSize()*2f, Tmp.c1.set(fillColor), light);
			}
		}
	}
	
	@Override
	public void setBars(Unit unit, Table bars) {
		super.setBars(unit, bars);
		bars.add(new Bar(() -> Core.bundle.format("bar.life-essenceamount", UI.formatAmount((long)essence(unit))), () -> Pal.spore, () -> essence(unit)/essenceCapacity).blink(Color.white));
		bars.row();
	}

	public static class Essence {

		public float essence;

		public Essence() {
			this.essence = 0;
		}
		
		public Essence(float essence) {
			this.essence = essence;
		}
		
	}
	
}
