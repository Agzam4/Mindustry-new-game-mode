package agzam4.content.blocks.power;

import static mindustry.Vars.renderer;

import agzam4.Drawm;
import agzam4.Work;
import agzam4.content.blocks.power.LaserBlock.LaserBuild;
import agzam4.content.blocks.power.LaserMixer.LaserMixerBuild;
import agzam4.content.blocks.power.SingleOutLaser.SingleOutLaserBuild;
import arc.Core;
import arc.func.Cons;
import arc.func.Intc;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.Renderer;
import mindustry.entities.Damage;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.Payload;

public class LaserBlock extends Block {
	
	public static int lasersChanges = 0;
	
	public static TextureRegion laserInRegion, laserInEndRegion, laserOutRegion, laserOutEndRegion;
	private static final Rect rect = new Rect();
	protected static Tile parmTile = null;
	
	public LaserBlock(String name) {
		super(name);
        update = true;
        solid = true;
        allowDiagonal = false;
        rotate = false;
        replaceable = true;
        canOverdrive = false;
	}
	
	@Override
	public void init() {
		super.init();
		updateClipRadius(LaserData.maxLaserRange*2f);
	}

	@Override
	public void loadIcon() {
		super.loadIcon();
		laserOutRegion = Core.atlas.find("laser-white");
		laserOutEndRegion = Core.atlas.find("laser-white-end");

		laserInRegion = Core.atlas.find("laser-top");
		laserInEndRegion = Core.atlas.find("laser-top-end");
	}

	public static float toDeg(int angle) {
		return angle*90f;
	}
	public static int fromDeg(float angle) {
		return Mathf.round(angle/90f);
	}

	public static int rotate180(int angle) {
		return (angle+2)%4;
	}

	public class LaserBuild extends Building {
		
		public int lastRotate = Integer.MIN_VALUE;
		private int lastChange = Integer.MAX_VALUE;
		public boolean needUpdateDirections = true;
		// TODO: update lasers in range
		
		@Override
		public void updateTile() {
			super.updateTile();
			if(lastRotate != rotation) {
				onRotate(lastRotate);
				lastRotate = rotation;
				lasersChanges++;
				updateDirections();
				updateLaserData();
//				callUpdateDirections(x, y, LaserData.maxLaserRange);
			}
//			if(needUpdateDirections) {
//				needUpdateDirections = false;
////				lastChange = lasersChanges;
//				updateLaserData();
//				updateDirections();
//			}
		}
		
		public void onRotate(int lastRotate) {
			// for @Override
		}

		public void callUpdateDirections(float x, float y, float range) {
			rect.setCentered(x, y, range, range);
			team.data().buildingTree.intersect(rect, build -> {
				if(build instanceof LaserBuild) {
					((LaserBuild)build).needUpdateDirections = true;
//					Fx.hitLaserBlast.at(build);
				}
			});
		}
		
		@Override
		public void placed() {
			callUpdateDirections(x, y, LaserData.maxLaserRange);
			super.placed();
		}
		
		@Override
		public void dropped() {
			callUpdateDirections(x, y, LaserData.maxLaserRange);
			lasersChanges++;
			super.dropped();
		}
		
		public void updateDirections() {
			// for @Override
		}
		
		public void updateLaserData() {
			// for @Override
		}

		public @Nullable LaserBuild searchLink(int x, int y, int angle, LaserData out) {
			int dx = Geometry.d4(angle).x;
			int dy = Geometry.d4(angle).y;
			int unAngle = (angle+2)%4;
			int len = (int) (out.length/Vars.tilesize);
			for (int i = 1; i < len; i++) {
				Building build = Vars.world.build(x + dx*i, y + dy*i);
				if(build == null) continue;
				if(build instanceof LaserBuild) {
					LaserBuild lb = (LaserBuild) build;
					if(lb.absorb(this, unAngle, 0, 0, out)) {
						parmTile = Vars.world.tile(x + dx*i, y + dy*i);
						return lb;
					} else {
						break;
					}
				}
			}
			parmTile = null;
			return null;
		}

		public float offset(int myAngle) {
			// for @Override
			return 0;
		}

		
		public boolean absorb(LaserBuild caller, int myAngle, int offsetX, int offsetY, LaserData data) {
			// for @Override
			return false;
		}
		
		public void link(LaserBuild caller, int myAngle, int offsetX, int offsetY, LaserData data) {
			// for @Override
		}

		public void unlink(LaserBuild caller, int myAngle, int offsetX, int offsetY) {
			// for @Override
		}
		
		/**
		 * Called then other build target setting same target
		 * @param target
		 */
		public void onReplaceLinkFor(LaserBuild target) {
			// for @Override
		}
		
		public void drawLaser(LaserBuild from, LaserBuild end, int angle, LaserData data) {
			drawLaser(
					from.x + Angles.trnsx(toDeg(angle), from.offset(angle)),
					from.y + Angles.trnsy(toDeg(angle), from.offset(angle)),
					end.x + Angles.trnsx(toDeg(angle), -end.offset(rotate180(angle))),
					end.y + Angles.trnsy(toDeg(angle), -end.offset(rotate180(angle))),
					data);
		}

		public void drawLaser(float sx, float sy, float ex, float ey, LaserData data) {
			if(data.size <= 0) return;
			if(sx == ex && sy == ey) return;
			float z = Draw.z();
			Draw.z(Layer.bullet);
			
			Draw.alpha(data.efficiency());
//			if(data.power > 0) {
//				Draw.color(data.color);
//				Drawm.laser(laserOutRegion, laserOutEndRegion, sx, sy, ex, ey, data.size/Vars.tilesize);
//				Draw.color(0f,0f,0f);
//				Drawm.laser(laserInRegion, laserInEndRegion, sx, sy, ex, ey, data.size/Vars.tilesize);
//			} else {
			if(drawHideLaser()) return;
			else Draw.alpha(Renderer.laserOpacity);

			Draw.color(data.color);
			Drawm.laser(laserOutRegion, laserOutEndRegion, sx, sy, ex, ey, data.size/Vars.tilesize);
			Draw.color(1f,1f,1f);
			Drawm.laser(laserInRegion, laserInEndRegion, sx, sy, ex, ey, data.size/Vars.tilesize);
			Tmp.c1.set(data.color);
			Tmp.c1.saturation(1f);
			if(renderer != null) renderer.lights.line(sx, sy, ex, ey, data.size*7f, Tmp.c1.cpy(), 0.7f); // .3f
			Draw.z(z);
		}
		
		public boolean drawHideLaser() {
			// for @Override
			return false;
		}
		
		public void laserDamage(float x1, float y1, float x2, float y2, float radius, Cons<Unit> cons) {
			rect.setCentered((x1+x2)/2f, (y1+y2)/2f, Math.abs(x1-x2) + radius, Math.abs(y1-y2) + radius);
			Units.nearbyEnemies(team, rect, cons);			
//			Units.nearby(rect, cons);			
		}

        public void drawConfigureAngle(int angle) {
        	float mx = Core.input.mouseWorldX();
        	float my = Core.input.mouseWorldY();
        	for (int a = 0; a < Geometry.d8.length; a++) {
        		float x = this.x + Geometry.d8[a].x*Vars.tilesize;
        		float y = this.y + Geometry.d8[a].y*Vars.tilesize;
        		float cx = this.x + Geometry.d8[a].x*Vars.tilesize - Vars.tilesize/2f;
        		float cy = this.y + Geometry.d8[a].y*Vars.tilesize - Vars.tilesize/2f;
        		Draw.color(0f, 0f, 0f, 0.6f);
        		if(a == angle) {
                	Fill.rect(x, y, Vars.tilesize, Vars.tilesize);
                	Draw.color(Pal.accent);
                	Lines.rect(x - Vars.tilesize/2f, y - Vars.tilesize/2f, Vars.tilesize, Vars.tilesize);
        		} else {
            		if(cx <= mx && mx <= cx+Vars.tilesize
            				&& cy <= my && my <= cy+Vars.tilesize) {
                		Draw.color(Pal.darkerGray);
                		Draw.color(.27f, .27f, .27f);
            		}
                	Fill.rect(x, y, Vars.tilesize, Vars.tilesize);
        		}
            	Draw.color();
            	Draw.rect(Icon.right.getRegion(), x, y, a*45);
			}			
		}
        
		public boolean onConfigureTappedAngle(float x, float y, Intc cons) {
        	for (int a = 0; a < Geometry.d8.length; a++) {
        		float cx = this.x + Geometry.d8[a].x*Vars.tilesize - Vars.tilesize/2f;
        		float cy = this.y + Geometry.d8[a].y*Vars.tilesize - Vars.tilesize/2f;
        		if(cx <= x && x <= cx+Vars.tilesize
        				&& cy <= y && y <= cy+Vars.tilesize) {
        			cons.get(a);
                	return true;
        		}
			}
        	return false;
		}
		
		@Override
		public void onRemoved() {
			callUpdateDirections(x, y, LaserData.maxLaserRange);
			lasersChanges++;
		}
		
        public float tileCenterX() {
			return tileX() + sizeOffset + size/2f;
		}
        
        public float tileCenterY() {
			return tileY() + sizeOffset + size/2f;
		}

		public int farX() {
			if(rotation == 0) return tileX() + sizeOffset + size - 1;
			if(rotation == 2) return tileX() + sizeOffset;
			return tileX();
		}

		public int farY() {
			if(rotation == 1) return tileY() + sizeOffset + size - 1;
			if(rotation == 3) return tileY() + sizeOffset;
			return tileY();
		}

		public float closestX(int x) {
			return Mathf.clamp(x, tileX() + sizeOffset, tileX() + sizeOffset + size - 1);
		}
		
		public float closestY(int y) {
			return Mathf.clamp(y, tileY() + sizeOffset, tileY() + sizeOffset + size - 1);
		}

		public float closestWorldX(float x) {
			return Mathf.clamp(x, (tileX() + sizeOffset)*Vars.tilesize, (tileX() + sizeOffset + size - 1)*Vars.tilesize);
//			return Mathf.clamp(x, this.x - size*Vars.tilesize/2f, this.x + size*Vars.tilesize/2f);
		}
		public float closestWorldY(float y) {
			return Mathf.clamp(y, (tileY() + sizeOffset)*Vars.tilesize, (tileY() + sizeOffset + size - 1)*Vars.tilesize);
		}

		public void link(LaserBuild from, LaserData data) {
			link(from, rotate180(from.rotation), 
					(int)closestX(from.tileX()) - (tileX() + sizeOffset), 
					(int)closestY(from.tileY()) - (tileY() + sizeOffset), 
					data);
		}

		public void unlink(LaserBuild from) {
			unlink(from, rotate180(from.rotation), 
					(int)closestX(from.tileX()) - (tileX() + sizeOffset), 
					(int)closestY(from.tileY()) - (tileY() + sizeOffset));
		}

		public int indexOffset(int angle, int offsetX, int offsetY) {
			return angle == 0 || angle == 2 ? Math.abs(offsetY) : Math.abs(offsetX);
		}
	}
}
