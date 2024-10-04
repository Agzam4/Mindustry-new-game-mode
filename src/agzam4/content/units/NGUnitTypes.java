package agzam4.content.units;

import static arc.graphics.g2d.Draw.color;
import agzam4.NewGameMod;
import agzam4.content.bullets.LifeBulletType;
import agzam4.content.bullets.LifeLaserBulletType;
import agzam4.content.effects.NGFx;
import agzam4.util.Annotations.EntityDef;
import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.ai.types.BuilderAI;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.gen.Legsc;
import mindustry.gen.Sounds;
import mindustry.gen.Unitc;
import mindustry.graphics.Drawf;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class NGUnitTypes {

    public static @EntityDef({Unitc.class, Legsc.class}) UnitType disorderer, absorber, dissonance;// . chaotic, anarchy
    
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
    		buildSpeed = 0.75f;
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
            buildSpeed = 1f;
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
                    homingPower = 0.05f;
    			}};
    		}});
    	}};
    	
    	dissonance = new LifeUnitType("dissonance")  {{
    		damageReflectionMultiplier = 2f;
    		essenceCapacity = 105f;

    		aiController = BuilderAI::new;
    		isEnemy = false;

    		legCount = 6;
    		
    		legMoveSpace = 3f;
    		legLength = 9f;
    		legForwardScl = 0.6f;

    		lowAltitude = true;
            mineSpeed = 8f;
    		mineTier = 2;
            buildSpeed = 1.25f;
    		drag = 0.05f;
            speed = 3.55f;
    		rotateSpeed = 19f;
    		accel = 0.11f;
    		itemCapacity = 105;
    		health = 220f;
    		hitSize = 11f;
    		alwaysUnlocked = true;

    		lightWeaponIndex = 0;
    		weapons.add(new Weapon(NewGameMod.prefix + "life-single-weapon"){{
    			reload = 20f;

                shoot.shots = 3;
                shoot.shotDelay = 3f;
                
                inaccuracy = 5f;
                
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
                    homingPower = 0.1f;
    			}};
    		}});
    		
    		weapons.add(new Weapon(NewGameMod.prefix + "life-laser-weapon"){{
                shootSound = Sounds.bolt;
                layerOffset = 0.0001f;
                reload = 15f;
                shootY = 10f;
                recoil = 1f;
                rotate = false;
                mirror = false;
                shootCone = 2f;
                x = 0f;
                y = 4.5f;
                heatColor = Color.valueOf("f9350f");
                cooldownTime = 30f;
                shoot = new ShootAlternate(3.5f);
                
                bullet = new LifeLaserBulletType(){{
                    length = 160f;
                    damage = 22f;
                    pierceDamageFactor = 0.8f;
    				buildingDamageMultiplier = 0.01f;

                    endEffect = new Effect(14f, e -> {
                        color(e.color);
                        Drawf.tri(e.x, e.y, e.fout() * 1.5f, 5f, e.rotation);
                    });

                    shootEffect = new Effect(10, e -> {
                        color(e.color);
                        float w = 1.2f + 7 * e.fout();

                        Drawf.tri(e.x, e.y, w, 30f * e.fout(), e.rotation);
                        color(e.color);

                        for(int i : Mathf.signs){
                            Drawf.tri(e.x, e.y, w * 0.9f, 18f * e.fout(), e.rotation + i * 90f);
                        }

                        Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
                    });
                    lineEffect = NGFx.sporeLaser;
                }};
//    			reload = 60f;
//
//                shoot.shots = 3;
//                shoot.shotDelay = 3f;
//                
//    			x = 0f;//9f;
//    			y = 1f;
//    			top = false;
//    			mirror = false;
//    			ejectEffect = Fx.casing1;
//    			immunities.add(StatusEffects.sporeSlowed);
//    			immunities.add(StatusEffects.sapped);
//
//    			bullet = new LifeBulletType(0.5f, 11) {{
//    				width = 17f;
//    				height = 19f;
//    				lifetime = 60f;
//    				shootEffect = Fx.shootSmall;
//    				smokeEffect = Fx.shootSmallSmoke;
//    				buildingDamageMultiplier = 0.01f;
//    			}};
    		}});
    	}};
    }

    
    
}
