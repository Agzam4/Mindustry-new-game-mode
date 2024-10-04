package agzam4.content.blocks.power;

import static mindustry.Vars.tilesize;
import agzam4.Drawm;
import agzam4.Work;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Eachable;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.UI;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.meta.BlockStatus;

public class SingleOutLaser extends LaserBlock {

    public final int timerUpdate = timers++;
//	private static Rect rect = new Rect();
    
	public float laserOutOffset;

	public TextureRegion baseRegion;
	
    public @Nullable SingleOutLaserBuild lastBuild; // for auto link

	public int maxRange = (int) (LaserData.maxLaserRange/Vars.tilesize);
	
	public SingleOutLaser(String name) {
		super(name);
		rotate = true;
		
		configurable = true;
//        config(Point2.class, (SingleOutLaserBuild tile, Point2 i) -> {
//        	Building b = Vars.world.build(i.x + tile.tileX(), i.y + tile.tileY());
//        	if(b == null) tile.target = null;
//        	if(b instanceof LaserBuild) {
//        		tile.target = (LaserBuild) b;
//        	} else {
//        		tile.target = null;
//        	}
//        	tile.updateTarget();
//        });
        config(Integer.class, (SingleOutLaserBuild tile, Integer i) -> {
        	if(i == null || i <= 0) {
        		tile.target = null;
            	tile.updateTarget();
            	return;
        	}
        	Building b = Vars.world.build(
        			tile.farX() + Geometry.d4[tile.rotation].x*i,
        			tile.farY() + Geometry.d4[tile.rotation].y*i
        	);
        	if(b == null) tile.target = null;
        	if(b instanceof LaserBuild) {
        		tile.target = (LaserBuild) b;
            	tile.awaitingConfigure = 0;
        	} else {
        		tile.target = null;
        	}
        	tile.updateTarget();
        });
	}
	
	@Override
	public void loadIcon() {
		super.loadIcon();
		baseRegion = Work.texture(name + "-base");
	}

	@Override
	protected TextureRegion[] icons() {
		return new TextureRegion[] {baseRegion, region};
	}
	
	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(baseRegion, plan.drawx(), plan.drawy());
		if(plan.rotation == 1 || plan.rotation == 2) {
			Draw.rect(region, plan.drawx(), plan.drawy(), region.width * region.scl(), region.height * region.scl(), plan.rotation*90-90);
		} else {
			Draw.rect(region, plan.drawx(), plan.drawy(), -region.width * region.scl(), region.height * region.scl(), plan.rotation*90-90);
		}
	}
	
	@Override
	public void setBars() {
		super.setBars();
		addLaserBar();
	}

	public void addLaserBar() {
    	addBar("laser-damage", (Building e) -> new Bar(
				() -> Core.bundle.format("bar.laser-damageamount", UI.formatAmount((long)(
						((SingleOutLaserBuild)e).laserDataOut.power
					))), 
				() -> ((SingleOutLaserBuild)e).laserDataOut.color, 
				() -> ((SingleOutLaserBuild)e).laserDataOut.efficiency()
		));		
	}

	public SingleOutLaserBuild findLink(SingleOutLaserBuild solb) {
        if(lastBuild != null && linkValid(solb, lastBuild) && lastBuild != solb && lastBuild.target == null){
            return lastBuild;
        }
        return null;
    }

//    public boolean linkValid(SingleOutLaserBuild from, LaserBuild other){
//        return linkValid(from, other, true);
//    }
    public boolean linkValid(SingleOutLaserBuild from, LaserBuild other) {
    	return linkValid(from, other, true);
    }

    public boolean absorbValid(SingleOutLaserBuild from, LaserBuild other) {
    	if(from == null || other == null) return false;
    	return other.absorb(from, rotate180(from.rotation), 0, 0, from.laserDataOut());
    }
    	
    public boolean linkValid(SingleOutLaserBuild from, LaserBuild other, boolean hardCheck) {
    	if(from == other) return false;
    	if(from == null || other == null) return false;
    	if(hardCheck && !absorbValid(from, other)) return false;
    	if(from.team != other.team) return false;
    	if(hardCheck && !positionsValid(from, other)) return false;
		return true;
    	
//        if(other == null || from == null || !positionsValid(tile.x, tile.y, other.x, other.y)) return false;
        
//        return ((other.block() == tile.block() && tile.block() == this) || (!(tile.block() instanceof SingleOutLaser) && other.block() == this))
//            && (other.team() == tile.team() || tile.block() != this)
//            && (!checkDouble || ((SingleOutLaserBuild)other.build).link != tile.pos());
    }

    public boolean positionsValid(SingleOutLaserBuild from, LaserBuild other) {
    	if(Geometry.d4x[from.rotation] != 0) {
    		if(Geometry.d4x[from.rotation] != Mathf.clamp(other.tileX() - from.tileX(), -1, 1)) return false;
    		return Math.abs(from.tileCenterY() - other.tileCenterY()) <= Math.abs(from.block.size - other.block.size)/2f;
    	}
		if(Geometry.d4y[from.rotation] != Mathf.clamp(other.tileY() - from.tileY(), -1, 1)) return false;
		return Math.abs(from.tileCenterX() - other.tileCenterX()) <= Math.abs(from.block.size - other.block.size)/2f;
    }
    
	public class SingleOutLaserBuild extends LaserBuild {

		LaserData laserDataOut = new LaserData(this);
		@Nullable LaserBuild target, lastTarget; // end of laser
		float laserEndX, laserEndY;
		short targetX, targetY;
		int dst = 0;
		protected boolean shooting = false;
		
		private int awaitingConfigure = 0;
		
        @Override
        public void pickedUp(){
        	target = null;
            updateTarget();
        }

		@Override
        public void onReplaceLinkFor(LaserBuild target) {
        	resetConfigure();
        }
        
        public void updateTarget() {
        	// to do: link offset 
			if(target == null) {
				laserEndX = laserOutX();
				laserEndY = laserOutY();
				dst = 0;
				if(lastTarget != null) {
					lastTarget.unlink(this);
				}
			} else {
				target.link(this, laserDataOut);
				laserEndX = target.closestWorldX(x) - Angles.trnsx(rotdeg(), target.offset(rotate180(rotation)));
				laserEndY = target.closestWorldY(y) - Angles.trnsy(rotdeg(), target.offset(rotate180(rotation)));
				dst = linkDst(target);
			}
			lastTarget = target;
		}

		public LaserData laserDataOut() {
        	updateLaserData();
			return laserDataOut;
		}

		@Override
        public void playerPlaced(Object config){
            super.playerPlaced(config);
            if(config != null && config instanceof Integer) {
            	awaitingConfigure = (Integer)config;
            	if(awaitingConfigure > 0) return;
            }
            SingleOutLaserBuild link = findLink(this);
            if(linkValid(this, link) && this.target != link && !proximity.contains(link)) {
                link.configure(linkDst(link));
            }
            lastBuild = this;
        }
		
		public int getAwaitingConfigure() {
			return awaitingConfigure;
		}
		
		public int linkDst(@Nullable LaserBuild lb) {
			if(lb == null) return 0;
			return (int) (Math.abs(Geometry.d4x[rotation]*(closestX(lb.tileX()) - lb.closestX(tileX())))
				 + Math.abs(Geometry.d4y[rotation]*(closestY(lb.tileY()) - lb.closestY(tileY()))));
		}

        @Override
        public void drawSelect(){
            if(linkValid(this, target)){
                drawInput(target);
            }
//            incoming.each(pos -> drawInput(world.tile(pos)));
            Draw.reset();
        }
        
        private void drawInput(LaserBuild other){
        	if(other == null) return;
        	if(other != target) return;
            if(!linkValid(this, other)) return;

            float tx = closestWorldX(x() + size*Vars.tilesize*Geometry.d4x[rotation]),
            	  ty = closestWorldY(y() + size*Vars.tilesize*Geometry.d4y[rotation]);
            float ox = other.closestWorldX(x()), oy = other.closestWorldY(y());
            Tmp.v2.trns(Mathf.angle(ox-tx, oy-ty), 2f);
            float alpha = Math.abs(100-(Time.time * 2f) % 100f) / 100f;
            float ax = Mathf.lerp(ox, tx, alpha);
            float ay = Mathf.lerp(oy, ty, alpha);
//
//            LaserBuild otherLink = linked ? other : this;

            //draw "background"
            Draw.color(Pal.gray);
            Lines.stroke(2.5f);
            Lines.square(ox, oy, 2f, 45f);
            Lines.square(tx, ty, 2f, 45f);
            Lines.square(ax, ay, 2f*alpha, 45f);
            Lines.stroke(2.5f);
            Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);

            //draw foreground colors
            Draw.color(Pal.place);
            Lines.stroke(1f);
            Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);

            Lines.square(ox, oy, 2f, 45f);
            Lines.square(tx, ty, 2f, 45f);
            Lines.square(ax, ay, 2f*alpha, 45f);
            Draw.mixcol(Draw.getColor(), 1f);
            Draw.color();
//            Draw.rect("bridge-conveyor-arrow", ax, ay, rotdeg());
            Draw.mixcol();
            
            updateLaserData();
        	target.block.drawPlaceText(Math.round(laserDataOut.efficiency*100) + "%", other.tileX(), other.tileY(), true);
        }
        
//        @Override
//        public boolean drawHideLaser() {
//			rect.setCentered((laserOutX()+targetX)/2f, (laserOutY()+targetY)/2f, 
//					Math.abs(laserOutX()-targetX), Math.abs(laserOutY()-targetY));
//        	return rect.contains(Core.input.mouseWorld());
//        }

        @Override
        public void drawConfigure(){
        	boolean less = false;
    		int dx = Geometry.d4[rotation].x;
    		int dy = Geometry.d4[rotation].y;
			updateLaserData();
    		Tile last = null;
    		Building lastBuild = this;
    		
            for(int i = 1; i <= maxRange; i++) {
            	Tile otherTile = tile.nearby(dx * i, dy * i);
        		if(otherTile == null) break;
            	if(otherTile.build == lastBuild) continue;
            	if(otherTile.build != null) {
            		if(otherTile.build instanceof LaserBuild) {
            			if(last != null) {
            				boolean linked = last.build == target;
                    		Drawf.select(last.build.x(), last.build.y(),
            						last.block().size * tilesize / 2f + 2f + (linked ? 0f : Mathf.absin(Time.time, 4f, 1f)), linked ? Pal.place : Pal.breakInvalid);
            				last.block().drawPlaceText(Math.round(LaserData.rangeEfficiency(laserDataOut.length, i-1)*100) + "%", last.build.tileX(), last.build.tileY(), true);
            				last = null;
            			}
            			if(linkValid(this, (LaserBuild) otherTile.build)) {
            				last = otherTile;
            				lastBuild = otherTile.build;
            				continue;
            			}
            		}
            	}
        		float wx = this.x + dx*i*Vars.tilesize;
        		float wy = this.y + dy*i*Vars.tilesize;
                Draw.color(Pal.gray);
        		Fill.rect(wx, wy, Math.max(3, Math.abs(dx*Vars.tilesize)), Math.max(3, Math.abs(dy*Vars.tilesize)));
            	float eff = LaserData.rangeEfficiency(laserDataOut.length, i);
            	Drawm.colorProgress(eff);
            	if(eff < 1 && !less) {
            		less = true;
            	}
        		Fill.rect(wx, wy, 
        				Math.max(3, Math.abs(dx*Vars.tilesize)) - 2, 
        				Math.max(3, Math.abs(dy*Vars.tilesize)) - 2);
            	if(less || i == 1 || last != null) {
            		Fill.rect(wx, wy, 
            				Math.max(3, Math.abs(dx*Vars.tilesize)) - 2, 
            				Math.max(3, Math.abs(dy*Vars.tilesize)) - 2);
            	} else {
            		Fill.rect(wx-dx*2f, wy-dy*2f, 
            				Math.max(3, Math.abs(dx*Vars.tilesize)) - 2 + Math.abs(dx*2f), 
            				Math.max(3, Math.abs(dy*Vars.tilesize)) - 2 + Math.abs(dy*2f));
            	}
            	
            	if(last != null) {
        			boolean linked = last.build == target;
            		Drawf.select(last.build.x(), last.build.y(),
            				last.block().size * tilesize / 2f + 2f + (linked ? 0f : Mathf.absin(Time.time, 4f, 1f)), linked ? Pal.place : Pal.breakInvalid);
            		last.block().drawPlaceText(Math.round(LaserData.rangeEfficiency(laserDataOut.length, i-1)*100) + "%", last.build.tileX(), last.build.tileY(), true);
    				last = null;
            	}
            }
            Drawf.select(x, y, tile.block().size * tilesize / 2f + 2f, Pal.accent);
        }

        @Override
        public boolean onConfigureBuildTapped(Building other) {
        	if(!(other instanceof LaserBuild)) return false;
        	// TODO: laser generator auto link?
            if(linkValid(this, (LaserBuild) other)){
                if(target == other){
                	resetConfigure();
                }else{
                    configure(linkDst((LaserBuild) other));
                }
                return false;
            }
            return true;
        }
        
		
		@Override
		public void updateTile() {
			super.updateTile();
			if(awaitingConfigure > 0) {
				configure(awaitingConfigure);
			}
			updateValid();
			updateLaser();
			updateDamage();
		}
		
		@Override
		public void update() {
			super.update();
		}
		
		public void updateValid() {
			shooting = true;
            if(!linkValid(this, target, false) || !target.isValid()){
            	target = null;
            	updateTarget();
            	shooting = false;
            }
            if(!absorbValid(this, target) || !positionsValid(this, target)) {
            	shooting = false;
            }			
		}

		public boolean shooting() {
			return shooting;
		}
		
		public void updateLaser() {
			updateLaserData();
		}

		int reload = 0;
		public void updateDamage() {
			if(isPayload()) return;
			if(timer(timerUpdate, 5f)) {
				if(laserDataOut.size > 0) {
					laserDamage(laserOutX(),laserOutY(),laserEndX,laserEndY, laserDataOut.size, e -> {
						for (int i = 0; i < laserDataOut.effects.length; i++) {
							if(Mathf.randomBoolean(laserDataOut.effects[i])) {
								e.apply(Vars.content.statusEffects().get(i), 60f*laserDataOut.size*laserDataOut.effects[i]);
							}
						}
						e.damageContinuousPierce(laserDataOut.power*5f/60f);
						Fx.hitLaserColor.at(e.x, e.y, Pal.lancerLaser);
					});
				}
			}			
		}
		
		@Override
		public boolean absorb(LaserBuild caller, int myAngle, int offsetX, int offsetY, LaserData data) {
			if(caller.block.size > size) return false;
//			if(caller.block.size%2 != size%2) return false;
			return myAngle != rotation;
		}

		@Override
		public void updateDirections() {
//			LaserBuild lastTarget = target;
//			int lastx = targetX;
//			int lasty = targetY;
//			target = searchLink(tileX(), tileY(), rotation, laserDataOut);
//			targetX = parmTile == null ? (short)tileX() : parmTile.x;
//			targetY = parmTile == null ? (short)tileY() : parmTile.y;
//			if(parmTile != null && target != null) {
//				laserEndX = parmTile.worldx() - Angles.trnsx(rotdeg(), target.offset(rotate180(rotation)));
//				laserEndY = parmTile.worldy() - Angles.trnsy(rotdeg(), target.offset(rotate180(rotation)));
//			} else {
//				laserEndX = laserOutX();
//				laserEndY = laserOutY();
//			}
//			if(lastTarget == null && target != null && parmTile != null) {
//				target.link(rotate180(rotation), parmTile.x-target.tileX(), parmTile.y-target.tileY(), laserDataOut);
//			}
//			if(lastTarget != null && target == null) {
//				lastTarget.unlink(rotate180(rotation), lastx-lastTarget.tileX(), lasty-lastTarget.tileY());
//			}
		}
		
		@Override
		public void control(LAccess type, double p1, double p2, double p3, double p4) {
			if(type == LAccess.config) {
				rotation(Mathf.mod((int)p1, 4));
			}
		}

		@Override
		public double sense(LAccess access) {
			if(access == LAccess.shooting) return shooting ? 1 : 0;
			return super.sense(access);
		}
		
		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			awaitingConfigure = read.i();
		}
		
		@Override
		public void write(Writes write) {
			super.write(write);
			write.i(awaitingConfigure > 0 ? awaitingConfigure : linkDst(target));
		}
		
		@Override
		public Object config() {
			return awaitingConfigure > 0 ? awaitingConfigure : linkDst(target);
		}

		@Override
		public float offset(int myAngle) {
			return laserOutOffset;
		}

		public float laserOutX() {
			return closestWorldX(x + size*Vars.tilesize*Geometry.d4x[rotation]) + Angles.trnsx(rotdeg(), offset(rotation));
		}
		
		public float laserOutY() {
			return closestWorldY(y + size*Vars.tilesize*Geometry.d4y[rotation]) + Angles.trnsy(rotdeg(), offset(rotation));
		}
		
		@Override
		public BlockStatus status() {
			if(target == null) return BlockStatus.noOutput;
			return super.status();
		}
		
		@Override
		public void draw() {
			super.draw();
			if(Vars.state.isEditor()) { // o-o-o-o-oops
				if(awaitingConfigure > 0) {
					configure(awaitingConfigure);
				}
				laserDataOut.power = 1;
				laserDataOut.size = 1;
				laserDataOut.efficiency = 1;
			}
			Draw.rect(baseRegion, x, y);
			Drawm.spinSprite(region, x, y, rotation);
			if(isPayload()) return;
			
			if(rotation == lastRotate) drawLaser(
					laserOutX(), laserOutY(),
					laserEndX, laserEndY, laserDataOut);
			
			Drawm.debug(this);
		}
		
		@Override
		public void onRemoved() {
			resetConfigure();
			super.onRemoved();
		}

		public void resetConfigure() {
			configure(0);
		}
		
		public @Nullable LaserBuild target() {
			return target;
		}
		
	}
}
