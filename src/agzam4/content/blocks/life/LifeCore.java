package agzam4.content.blocks.life;

import agzam4.Work;
import agzam4.content.NGStat;
import agzam4.content.effects.NGFx;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Player;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.StatUnit;

public class LifeCore extends CoreBlock {
	
    public TextureRegion glowRegion, glowLightRegion, botRegion, botRegionLight;
    public float damageReflectionMultiplier = 1;
    static Rand rand = new Rand();
    

    public float botAngle = 60f, origin = 0.61f;
    public float sclMin = 30f, sclMax = 50f, magMin = 5f, magMax = 15f, timeRange = 40f, spread = 0f;

	// 421
    public float lifeessenceCapacity = 1_000;
    public float lobes[][] = {
    		{0,0,0,8}
    };
	
	public LifeCore(String name) {
		super(name);
	}

	@Override
	public void loadIcon() {
		super.loadIcon();
		glowRegion = Work.texture(name + "-glow");
		glowLightRegion = Work.texture(name + "-glow-light");
		botRegion = Work.texture(name + "-lobe");
		botRegionLight = Work.texture(name + "-lobe-glow");
	}
	
	@Override
	public void setBars() {
		super.setBars();
		LifeEssenceBuild.setBars(this);
	}

    @Override
    public boolean canReplace(Block other){
    	if(isFirstTier) return super.canReplace(other) || (other instanceof CoreBlock && size >= other.size && other != this);
    	return super.canReplace(other) && (other instanceof LifeCore && size >= other.size && other != this);        
    }
    
    @Override
    public void setStats() {
    	super.setStats();

        stats.add(NGStat.damageReflectionMultiplier, damageReflectionMultiplier*100, StatUnit.percent);
    }
    
    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if(tile == null) return false;
        //in the editor, you can place them anywhere for convenience
        if(Vars.state.isEditor()) return true;

        CoreBuild core = team.core();

        //special floor upon which cores can be placed
        tile.getLinkedTilesAs(this, tempTiles);
        if(!tempTiles.contains(o -> !o.floor().allowCorePlacement || o.block() instanceof CoreBlock)){
            return true;
        }

        //must have all requirements
        if(core == null || (!Vars.state.rules.infiniteResources && !core.items.has(requirements, Vars.state.rules.buildCostMultiplier))) return false;

        if(isFirstTier) return tile.block() instanceof CoreBlock && size >= tile.block().size;
        return tile.block() instanceof LifeCore && size > tile.block().size;
    }
	
    public class LifeCoreBuild extends CoreBuild implements LifeEssenceBuild {

        public float lifeessence;
        
        @Override
        public void draw() {
            flash += Time.delta;
            if(thrusterTime > 0) {
                float frame = thrusterTime;

                Draw.alpha(1f);
                drawThrusters(frame);
                Draw.rect(block.region, x, y);
                Draw.alpha(Interp.pow4In.apply(frame));
                drawThrusters(frame);
                Draw.reset();

                drawTeamTop();
                

//                if(centerRegion.found()){
//                    Draw.rect(centerRegion, tile.worldx(), tile.worldy());
//                }
            } else {
                super.draw();
            }
        }
        float flash;

		public float fxCooldown = 0;
		public float fxAttackCooldown = 0;
		
		@Override
		public void requestSpawn(Player player) {
			super.requestSpawn(player);
		}
		
		@Override
		public void updateTile() {
			super.updateTile();
			if(fxCooldown > 0) fxCooldown--;
			if(fxAttackCooldown > 0) fxAttackCooldown--;
		}
        

        @Override
        public double sense(LAccess sensor) {
        	if(sensor == LAccess.liquidCapacity || sensor == LAccess.powerCapacity) return essenceCapacity();
        	if(sensor == LAccess.totalLiquids || sensor == LAccess.totalPower) return essence();
        	return super.sense(sensor);
        }
		
        @Override
        public void drawTeamTop() {
            if(!block.teamRegion.found()) return;
//            if(block.teamRegions[team.id] == block.teamRegion) {
//            	Draw.color(team.color.lerp(Pal.spore, Mathf.absin(flash, 9f, 1f)));
//            } else {
//                Draw.alpha(1f-Mathf.absin(flash, 9f, 1f)*essence()/essenceCapacity());
//            }
            Draw.rect(block.teamRegions[team.id], x, y);
            Draw.color();

            rand.setSeed(tile.pos());
            float pz = Draw.z();
            for(float[] lobe : lobes){
                float offset = rand.random(180f);
                int count = (int) (lobe.length > 3 ? lobe[3] : 1);
                float cx = x() + lobe[0] - Vars.tilesize/4f;
                float cy = y() + lobe[1];
                for(int i = 0; i < count; i++){
                    float ba = i / (float)count * 360f + offset + rand.range(spread) + lobe[2], angle = ba + Mathf.sin(Time.time + rand.random(0, timeRange), rand.random(sclMin, sclMax), rand.random(magMin, magMax));
                    float w = botRegion.width * botRegion.scl(), h = botRegion.height * botRegion.scl();

                    Draw.z(pz);
                    Draw.color(0,0,0,.5f);
                    Draw.rect(botRegionLight,
                            cx - Angles.trnsx(angle, origin) + w*0.5f, cy - Angles.trnsy(angle, origin),
                            w, h,
                            origin*4f, h/2f,
                            angle
                        );
                    Draw.color();
                    
                    Draw.z(pz+.1f);
                    Draw.rect(botRegion,
                    		cx - Angles.trnsx(angle, origin) + w*0.5f, cy - Angles.trnsy(angle, origin),
                        w, h,
                        origin*4f, h/2f,
                        angle
                    );
                    
                    Draw.z(pz+.2f);
                    Draw.color(Pal.spore);
                    Draw.alpha(Mathf.clamp(essence()/essenceCapacity()));
                    Draw.blend(Blending.additive);
                    Draw.rect(botRegionLight,
                    		cx - Angles.trnsx(angle, origin) + w*0.5f, cy - Angles.trnsy(angle, origin),
                    		w, h,
                            origin*4f, h/2f,
                            angle
                        );
                    Draw.blend();
                    Draw.color();
                }
            }
            
            LifeEssenceStorageBlock.drawEssenceLight(this, this, glowRegion, glowLightRegion, flash);
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
        	if(lifeessence >= damage/2f) {
        		lifeessence -= damage/2f;
        		if(fxCooldown <= 0) NGFx.sporeShield.at(x, y, size, Pal.spore);
        		fxCooldown = NGFx.sporeShield.lifetime;
            	return true;
        	}
        	return false;
		}


		@Override
		public float essence() {
			return lifeessence;
		}


		@Override
		public float essenceCapacity() {
			return lifeessenceCapacity;
		}


		@Override
		public LifeEssenceBuild essence(float essence) {
			lifeessence = essence;
			return this;
		}
        
        @Override
        public void write(Writes write){
            super.write(write);
            write.f(lifeessence);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            lifeessence = read.f();
        }
        
		@Override
		public boolean absorbLasers() {
			return essence() > .0001f;
		}
        
		@Override
		public boolean collision(Bullet other) {
			if(other.damage() < 1f) return super.collision(other);
			if(absorbDamage(other.damage())) {
				if(other.owner() != null) {
					if(other.owner() instanceof Healthc) {
						Healthc u = (Healthc) other.owner();
						u.damagePierce(other.damage()*damageReflectionMultiplier);
						u.maxHealth(u.maxHealth() - other.damage()*damageReflectionMultiplier); // TODO
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
