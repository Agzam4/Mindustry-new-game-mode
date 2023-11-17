package agzam4.content.blocks;

import agzam4.Work;
import agzam4.content.effects.NGFx;
import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.turrets.ReloadTurret;

public class Devourer extends ReloadTurret {

	public final static Color bottomColor = Color.valueOf("2c2d38"); //2c2d38
	public final static Color fillColor = Pal.spore;

    public final int timerTarget = timers++;
    public float retargetTime = 10f;
    
    public Color color = Pal.spore;
    public Effect beamEffect = NGFx.sporeBeam;
    public Effect hitEffect = Fx.pointHit;
    public Effect shootEffect = Fx.sparkShoot;
    public Effect shieldEffect = NGFx.sporeShield;
    public Sound shootSound = Sounds.lasershoot;
    
    public TextureRegion bottomRegion, baseRegion, glowRegion, glowLightRegion;
    
    public float shootCone = 5f;
    public float bulletDamage = 10f;
    public float shootLength = 6f;
    public float maxAbsorbedHealth = 25e3f;
	private float shake = 3f;

	public Devourer(String name) {
		super(name);
		outlineIcon = false;
	}

	@Override
	public void loadIcon() {
		super.loadIcon();
		bottomRegion = Work.texture(name + "-bottom");
		glowRegion = Work.texture(name + "-glow");
		glowLightRegion = Work.texture(name + "-glow-light");
		baseRegion = Core.atlas.find("block-" + size);
        reload = 30f;
	} 

	@Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }
	
	public class DevourerBuild extends ReloadTurretBuild {
		
		public @Nullable Unit target;
        public float absorbedHealth;
        public float damage;

        @Override
        public void updateTile() {

            if(timer(timerTarget, retargetTime) && target == null) {
            	target = Groups.unit.intersect(x - range, y - range, range*2, range*2).min(
            			b ->b.type().hittable && !b.spawnedByCore,
            			b -> b.dst2(this) + (b.team == team ? range*range*4 : 0)
            	);
            	damage = 2;
            }
            absorbedHealth = Math.min(absorbedHealth, maxAbsorbedHealth);

            if(target == null) return;
            
            if(!target.isAdded()) {
                target = null;
                return;
            }
            
            if(target == null) return;

            if(coolant != null) {
                updateCooling();
            }
            	
            //look at target
            float dest = angleTo(target);
            rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta());
            reloadCounter += edelta();

            //shoot when possible
            if(Angles.within(rotation, dest, shootCone) && reloadCounter >= reload) {
            	if(target.team == team) {
                	Tmp.v1.trns(rotation, shootLength);
                	beamEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color, new Vec2().set(target));
                	shootEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color);
                	hitEffect.at(target.x, target.y, color);
                	shootSound.at(x + Tmp.v1.x, y + Tmp.v1.y, Mathf.random(0.9f, 1.1f));
                    Effect.shake(shake, shake, this);
                	reloadCounter = 0;
                	if(target.health() > damage) {
                		target.damagePierce(damage);
                		if(damage < 65536) {
                    		damage *= 2;
                		}
                	} else {
                		absorbedHealth += target.type.health*.75f;
                		target.kill();
                	}
                	return;
            	}
            	
            	if(target.type.health <= absorbedHealth) {
                	Tmp.v1.trns(rotation, shootLength);
                	beamEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color, new Vec2().set(target));
                	shootEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color);
                	hitEffect.at(target.x, target.y, color);
                	shootSound.at(x + Tmp.v1.x, y + Tmp.v1.y, Mathf.random(0.9f, 1.1f));
                	reloadCounter = 0;
                	
            		absorbedHealth -= target.type.health;
            		target.kill();
            	}
            }
        }

        float flash;
        
        @Override
        public void draw(){
            flash += Time.delta;
            
            Draw.rect(baseRegion, x, y);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);
            
            float glow = Mathf.clamp(absorbedHealth/maxAbsorbedHealth);
            
            Draw.color(bottomColor, fillColor, glow);
            Draw.rect(bottomRegion, x, y, rotation - 90);
            Draw.color();
            Draw.rect(region, x, y, rotation - 90);

            glow = Mathf.clamp(absorbedHealth*2/maxAbsorbedHealth);
            float light = Mathf.clamp((absorbedHealth-maxAbsorbedHealth/2)/maxAbsorbedHealth, 0, .5f)*2f*(Mathf.absin(flash, 9f, 1f)/2f+.5f);

            Drawf.additive(glowRegion, Tmp.c1.set(fillColor).mula(glow), x, y, rotation - 90);
            
//            Drawf.additive(glowRegion,  Tmp.c1.set(fillColor).lerp(Color.white, light).a(glow), x, y, rotation - 90);
            Drawf.additive(glowLightRegion, Tmp.c1.set(Color.white).mula(light), x, y, rotation - 90);
            
           
        }
        
        
        @Override
        public void write(Writes write){
            super.write(write);
            write.f(rotation);
            write.f(absorbedHealth);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            rotation = read.f();
            absorbedHealth = read.f();
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
        
        private boolean absorbDamage(float damage) {
        	if(absorbedHealth >= damage/2f) {
            	absorbedHealth -= damage/2f;
            	shieldEffect.at(x, y, size, color);
            	return true;
        	}
        	return false;
		}
	}
}
