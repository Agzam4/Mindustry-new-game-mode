package agzam4.content.blocks.power;

import static mindustry.Vars.renderer;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

import agzam4.content.blocks.life.LifeCore;
import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.util.Strings;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.defense.ForceProjector.ForceBuild;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class PowerCore extends CoreBlock {

	public float powerProduction;
    public float shieldHealth = 1f;
    public float cooldownNormal = 1.75f;
    public float cooldownBrokenBase = 0.35f;
    public float radius = 101.7f;
    public int sides = 6;
    public Effect absorbEffect = Fx.absorb;
    
    protected static PowerCoreBuild paramEntity;
    protected static Effect paramEffect;
    protected static final Cons<Bullet> shieldConsumer = bullet -> {
        if(bullet.team != paramEntity.team && bullet.type.absorbable && Intersector.isInRegularPolygon(((PowerCore)(paramEntity.block)).sides, paramEntity.x, paramEntity.y, paramEntity.realRadius(), 0, bullet.x, bullet.y)){
            bullet.absorb();
            paramEffect.at(bullet);
            paramEntity.hit = 1f;
            paramEntity.buildup += bullet.damage;
        }
    };
	
	public PowerCore(String name) {
		super(name);
		hasPower = true;
        outputsPower = true;
        consumesPower = false;
        canOverdrive = true;
	}

    @Override
    public boolean canReplace(Block other){
    	if(isFirstTier) return super.canReplace(other) || (other instanceof CoreBlock && size >= other.size && other != this);
    	return super.canReplace(other) || (other instanceof LifeCore && size >= other.size && other != this);        
    }
	
	@Override
	public void setStats() {
        if(powerProduction > 0) stats.add(Stat.basePowerGeneration, powerProduction * 60.0f, StatUnit.powerSecond);
        stats.add(Stat.shieldHealth, shieldHealth, StatUnit.none);
        stats.add(Stat.cooldownTime, (int) (shieldHealth / cooldownBrokenBase / 60f), StatUnit.seconds);
	}
	
	@Override
	public void setBars() {
		super.setBars();
//        if(consPower != null) {
//            boolean buffered = consPower.buffered;
//            float capacity = consPower.capacity;
//            removeBar("power");
//            addBar("power", entity -> new Bar(
//                () -> buffered ? Core.bundle.format("bar.poweramount", Float.isNaN(entity.power.status * capacity) ? "<ERROR>" : UI.formatAmount((int)(entity.power.status * capacity))) :
//                Core.bundle.get("bar.power"),
//                () -> Pal.powerBar,
//                () -> Mathf.zero(consPower.requestedPower(entity)) && entity.power.graph.getPowerProduced() + entity.power.graph.getBatteryStored() > 0f ? 1f : entity.power.status)
//            );
//        }
        if(hasPower && outputsPower && powerProduction > 0) {
            addBar("power-out", (PowerCoreBuild entity) -> new Bar(() ->
            Core.bundle.format("bar.poweroutput",
            Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)),
            () -> Pal.powerBar,
            () -> entity.getPowerProduction()/powerProduction));
        }
        addBar("shield", (PowerCoreBuild entity) -> new Bar("stat.shieldhealth", 
        		Pal.accent, 
        		() -> entity.broken ? 0 : 1f - entity.buildup / shieldHealth)
        		.blink(Color.white));
	}

    public class PowerCoreBuild extends CoreBuild {

        public boolean broken = true;
        public float buildup, radscl, hit, warmup;
    	
        @Override
        public void updateTile() {
        	super.updateTile();

            radscl = Mathf.lerpDelta(radscl, broken ? 0f : warmup, 0.05f);

            if(Mathf.chanceDelta(buildup / shieldHealth * 0.1f)){
                Fx.reactorsmoke.at(x + Mathf.range(tilesize / 2f), y + Mathf.range(tilesize / 2f));
            }

            warmup = Mathf.lerpDelta(warmup, efficiency, 0.1f);

            if(buildup > 0){
                float scale = !broken ? cooldownNormal : cooldownBrokenBase;
                buildup -= delta() * scale;
            }

            if(broken && buildup <= 0){
                broken = false;
            }

            if(buildup >= shieldHealth && !broken){
                broken = true;
                buildup = shieldHealth;
                Fx.shieldBreak.at(x, y, realRadius(), team.color);
                if(team != state.rules.defaultTeam){
                    Events.fire(Trigger.forceProjectorBreak);
                }
            }

            if(hit > 0f){
                hit -= 1f / 5f * Time.delta;
            }

            deflectBullets();
        }
        
        private void deflectBullets() {
    		float realRadius = realRadius();
            if(realRadius > 0 && !broken){
                paramEntity = this;
                paramEffect = absorbEffect;
                Groups.bullet.intersect(x - realRadius, y - realRadius, realRadius * 2f, realRadius * 2f, shieldConsumer);
            }
		}

    
        
        public float realRadius(){
            return radius * radscl;
        }
        
		@Override
    	public float getPowerProduction() {
    		return powerProduction;
    	}
		
		@Override
		public void draw() {
			super.draw();
			drawShield();
		}
		
		public void drawShield(){
            if(!broken){
                float radius = realRadius();

                if(radius > 0.001f){
                    Draw.color(team.color, Color.white, Mathf.clamp(hit));

                    if(renderer.animateShields){
                        Draw.z(Layer.shields + 0.001f * hit);
                        Fill.poly(x, y, sides, radius, 0);
                    }else{
                        Draw.z(Layer.shields);
                        Lines.stroke(1.5f);
                        Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
                        Fill.poly(x, y, sides, radius, 0);
                        Draw.alpha(1f);
                        Lines.poly(x, y, sides, radius, 0);
                        Draw.reset();
                    }
                }
            }

            Draw.reset();
        }
    }
}
