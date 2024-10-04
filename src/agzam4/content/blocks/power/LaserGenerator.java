package agzam4.content.blocks.power;

import static mindustry.Vars.tilesize;

import agzam4.Work;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.graphics.Pal;

public class LaserGenerator extends SingleOutLaser {

	
//	public boolean[] disabledAngles = new boolean[8];
//	TextureRegion baseRegion = new TextureRegion();
	
//	public float rotateSpeed = 180/60f;//45/60f;
	public float laserPower = 30f;
	public float laserLength = 80f;
	public float laserSize = 8f/4f;
	public float coolingSpeed = 1/25f, heatingSpeed = 3/60f;
	
	public LaserGenerator(String name) {
		super(name);
//		rotate = true;
//		configurable = true;
//        config(Integer.class, (LaserGeneratorBuild tile, Integer i) -> {
//            if(!configurable) return;
//            if(tile.angle == i) return;
//            tile.angle = i;
//        });
	}

	@Override
	public void loadIcon() {
		super.loadIcon();
//		baseRegion = Work.texture(name + "-base");
	}
	
	@Override
	public void init() {
		super.init();
		updateClipRadius(laserSize);
	}
	
//	@Override
//	protected TextureRegion[] icons() {
//		return new TextureRegion[] {baseRegion, region};
//	}
	
	public class LaserGeneratorBuild extends SingleOutLaserBuild {

//		@Nullable LaserData laserDataIn = null;
		
//		int angle = 2; // selected angle
//		float vangle = 90; // rotate angle
		float heat = 0; // heat of laser

		@Override
		public void updateLaserData() {
			laserDataOut.power(laserPower * heat, laserPower * heat, laserPower * heat).size(laserSize * heat).length(laserLength).disperse(dst).chainData(id, 0);
		}
		
		@Override
		public void updateLaser() {
			efficiency = hasPower ? power.status : 1f;
			if(shooting) {
				heat = Mathf.approach(heat, efficiency, heatingSpeed);
			} else {
				heat = Mathf.approach(heat, 0, coolingSpeed);
			}
			updateLaserData();
			
			
//			if(vangle != toDeg(angle)) {
//				if(heat > 0) {
//					heat = Mathf.approach(heat, 0, coolingSpeed);
//				}
//				if(heat == 0) {
//					vangle = Angles.moveToward(vangle, toDeg(angle), rotateSpeed*efficiency);
//				}
//				return;
//			}
//			if(heat == 0 && laserDataIn == null && end == null) {
//				end = searchLink(angle, laserDataOut);
//				ex = parmTile == null ? x : parmTile.x;
//				ey = parmTile == null ? y : parmTile.y;
//				if(end != null) end.link(rotate180(angle), laserDataOut);
//			}
//			if(x == ex && y == ey || end == null) {
//				heat = Mathf.approach(heat, 0, coolingSpeed);
//			} else {
//				heat = Mathf.approach(heat, efficiency, heatingSpeed);
//			}
//			
//			laserDataOut.power(laserPower * heat).size(laserSize * heat).length(laserLength);
//			
//			if(laserDataIn != null && !laserDataIn.owner.isValid()) {
//				laserDataIn = null;
//			}
//			
//			if(end != null) {
//				if(!checkValid()) {
//					end.unlink(rotate180(angle));
//					end = null;
//					laserDataIn = null;
//					heat = 0;
//				}
//				
//				updates++;
//				if(updates > 5) {
//					updates = 0;
//					if(heat > 0 && end != null) {
//						rect.setCentered((ex+x)/2f, (ey+y)/2f, Math.abs(ex-x), Math.abs(ey-y));
//						Units.nearbyEnemies(team, rect, e -> {
//							e.damageContinuous(laserDataOut.power);
//							Fx.hitLaserColor.at(e.x, e.y, Pal.lancerLaser);
//						});
//					}
//				}
//			}
		}
		
		@Override
		public void onRotate(int lastRotate) {
			heat = 0;
			updateLaserData();
		}

//		@Override
//		public void link(int myAngle, int offsetX, int offsetY, LaserData data) {
//			laserDataIn = data;
//		}
//		
//		@Override
//		public void unlink(int myAngle, int offsetX, int offsetY) {
//			laserDataIn = null;
//			end = null;
//		}
		
//		private boolean checkValid() {
//			if(!end.isValid()) return false;
//			return end.absorb(rotate180(angle), laserDataOut);
//		}

		@Override
		public boolean absorb(LaserBuild caller, int myAngle, int offsetX, int offsetY, LaserData data) {
			if(caller.block.size > size) return false;
//			if(caller.block.size%2 != size%2) return false;
			return myAngle == rotation;
		}
		

//		@Override
//		public void draw() {
////			Draw.rect(baseRegion, x, y);
////			Draw.rect(region, x, y, rotdeg()-90);
////			if((ex != x || ey != y) && end != null) {
////				drawLaser(this, end, rotation, laserDataOut);
////			}
////			drawLaser(x, y, ex, ey, laserDataOut);
////			drawLaser(x, y, ex, ey, laserDataOut);
//		}

//        @Override
//        public Object config() {
//            return angle;
//        }

//        @Override
//        public void control(LAccess type, double p1, double p2, double p3, double p4){
//            super.control(type, p1, p2, p3, p4);
//            if(type == LAccess.config){
//            	angle = Mathf.mod((int)p1, 8);
//            }
//        }
        
//        @Override
//        public void readAll(Reads read, byte revision) {
//        	super.readAll(read, revision);
//        	angle = read.b();
//        	vangle = read.f();
//        }
//        
//        @Override
//        public void writeAll(Writes write) {
//        	super.writeAll(write);
//        	write.b(angle);
//        	write.f(vangle);
//        }
        
//        @Override
//        public void drawConfigure() {
//        	drawConfigureAngle(angle);
//        }
//
//		@Override
//        public boolean onConfigureTapped(float x, float y) {
//        	return onConfigureTappedAngle(x, y, a -> angle = a);
//        }
	}
}
