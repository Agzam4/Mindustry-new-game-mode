package agzam4.content.units;

import agzam4.NewGameMod;
import agzam4.content.bullets.LifeBulletType;
import agzam4.util.Annotations.EntityDef;
import arc.graphics.Color;
import mindustry.ai.types.BuilderAI;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Legsc;
import mindustry.gen.Sounds;
import mindustry.gen.Unitc;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class NGUnitTypes {

    public static @EntityDef({Unitc.class, Legsc.class}) UnitType disorderer, absorber;// . chaotic, anarchy
    
    public static void load() {
    	disorderer = new LifeUnitType("disorderer") {{
    		damageReflectionMultiplier = 1f;
    		essenceCapacity = 150f;
    		
    		aiController = BuilderAI::new;
    		isEnemy = false;

    		legCount = 3;
    		
    		legMoveSpace = 3f;
            legLength = 9f;
            legForwardScl = 0.6f;
    		
    		lowAltitude = true;
    		mineSpeed = 6.5f;
    		mineTier = 0;
    		buildSpeed = 0.5f;
    		drag = 0.05f;
    		speed = 3f;
    		rotateSpeed = 15f;
    		accel = 0.1f;
    		itemCapacity = 50;
    		health = 150f;
    		hitSize = 8f;
    		alwaysUnlocked = true;

    		lightWeaponIndex = 0;
            weapons.add(new Weapon(NewGameMod.prefix + "life-single-weapon"){{
    			reload = 17f;
    			x = 0;//9f;
    			y = 1f;
    			top = false;
                mirror = false;
    			ejectEffect = Fx.casing1;
                immunities.add(StatusEffects.sporeSlowed);
                immunities.add(StatusEffects.sapped);

                bullet = new LifeBulletType(2.5f, 11) {{
                	width = 7f;
                	height = 9f;
                	lifetime = 60f;
                	shootEffect = Fx.shootSmall;
                	smokeEffect = Fx.shootSmallSmoke;
                	buildingDamageMultiplier = 0.01f;
                }};
            }});
//    		weapons.add(new Weapon("small-basic-weapon"){{
//    			reload = 17f;
//    			x = 2.75f;
//    			y = 1f;
//    			top = false;
//    			ejectEffect = Fx.casing1;
//
//    			bullet = new BasicBulletType(2.5f, 11){{
//    				width = 7f;
//    				height = 9f;
//    				lifetime = 60f;
//    				shootEffect = Fx.shootSmall;
//    				smokeEffect = Fx.shootSmallSmoke;
//    				buildingDamageMultiplier = 0.01f;
//    			}};
//    		}});
    	}};
    	
    	absorber = new LifeUnitType("absorber") {{
    		damageReflectionMultiplier = 2f;
    		essenceCapacity = 170f;

    		aiController = BuilderAI::new;
    		isEnemy = false;

    		legCount = 4;
    		
    		legMoveSpace = 3f;
    		legLength = 9f;
    		legForwardScl = 0.6f;

    		lowAltitude = true;
            mineSpeed = 7f;
    		mineTier = 1;
            buildSpeed = 0.75f;
    		drag = 0.05f;
            speed = 3.3f;
    		rotateSpeed = 17f;
    		accel = 0.1f;
    		itemCapacity = 75;
    		health = 170f;
    		hitSize = 9f;
    		alwaysUnlocked = true;

    		lightWeaponIndex = 0;
    		weapons.add(new Weapon(NewGameMod.prefix + "life-single-weapon"){{
    			reload = 20f;

                shoot.shots = 2;
                shoot.shotDelay = 4f;
                
    			x = 4.5f;//9f;
    			y = 1f;
    			top = false;
//    			mirror = false;
    			ejectEffect = Fx.casing1;
    			immunities.add(StatusEffects.sporeSlowed);
    			immunities.add(StatusEffects.sapped);

    			bullet = new LifeBulletType(2.5f, 11) {{
    				width = 7f;
    				height = 9f;
    				lifetime = 60f;
    				shootEffect = Fx.shootSmall;
    				smokeEffect = Fx.shootSmallSmoke;
    				buildingDamageMultiplier = 0.01f;
    			}};
    		}});
    	}};
    }

    
    
}
