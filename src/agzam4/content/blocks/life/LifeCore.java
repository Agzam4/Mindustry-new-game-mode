package agzam4.content.blocks.life;

import static mindustry.Vars.ui;

import agzam4.Work;
import agzam4.content.NGStat;
import agzam4.content.effects.NGFx;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Scaling;
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
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class LifeCore extends CoreBlock {
	
    public TextureRegion glowRegion, glowLightRegion;
    public float damageReflectionMultiplier = 1;

	// 421
    public float lifeessenceCapacity = 1_000;
	
	public LifeCore(String name) {
		super(name);
	}

	@Override
	public void loadIcon() {
		super.loadIcon();
		glowRegion = Work.texture(name + "-glow");
		glowLightRegion = Work.texture(name + "-glow-light");
	}
	
	@Override
	public void setBars() {
		super.setBars();
		LifeEssenceBuild.setBars(this);
	}

    @Override
    public boolean canReplace(Block other){
    	if(isFirstTier) return super.canReplace(other) || (other instanceof CoreBlock && size >= other.size && other != this);
    	return super.canReplace(other) || (other instanceof LifeCore && size >= other.size && other != this);        
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
        public void drawTeamTop() {
            if(!block.teamRegion.found()) return;
            if(block.teamRegions[team.id] == block.teamRegion) {
            	Draw.color(team.color.lerp(Pal.spore, Mathf.absin(flash, 9f, 1f)));
            } else {
                Draw.alpha(1f-Mathf.absin(flash, 9f, 1f)*essence()/essenceCapacity());
            }
            Draw.rect(block.teamRegions[team.id], x, y);
            Draw.color();

            LifeEssenceStorageBlock.drawEssenceLight(this, glowRegion, glowLightRegion, flash);
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
