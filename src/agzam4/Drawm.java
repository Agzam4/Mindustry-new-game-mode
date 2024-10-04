package agzam4;

import static mindustry.Vars.renderer;

import agzam4.content.blocks.power.SingleOutLaser.SingleOutLaserBuild;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.core.Renderer;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.power.PowerNode;

public class Drawm {

	public static void colorProgress(float t) {
		Draw.color(t <= .5f ? 1f : (1f-2f*(t-.5f)), t < .5f ? t*2f : 1f, 0);
	}

	public static void laser(TextureRegion line, TextureRegion end, float x, float y, float x2, float y2, float scale){
		laser(line, end, end, x, y, x2, y2, scale);
	}
	
    public static void laser(TextureRegion line, TextureRegion start, TextureRegion end, float x, float y, float x2, float y2, float scale){
        float scl = 8f * scale * Draw.scl, rot = Mathf.angle(x2 - x, y2 - y);
        float vx = Mathf.cosDeg(rot) * scl, vy = Mathf.sinDeg(rot) * scl;

//        Draw.
        Draw.rect(start, x, y, start.width * scale * start.scl(), start.height * scale * start.scl(), rot + 180);
        Draw.rect(end, x2, y2, end.width * scale * end.scl(), end.height * scale * end.scl(), rot);

        Lines.stroke(12f * scale);
        Lines.line(line, x + vx, y + vy, x2 - vx, y2 - vy, false);
        Lines.stroke(1f);

        
        /*
         *  float scl = 8f * scale * Draw.scl, rot = Mathf.angle(x2 - x, y2 - y);
        float vx = Mathf.cosDeg(rot) * scl, vy = Mathf.sinDeg(rot) * scl;
        float endScl = 9f/8f;
//        Draw.
        Draw.alpha(Renderer.laserOpacity);
        

//        Draw.blend(Blending.additive);

//        Draw.color(Draw.getColor().mul(2f));
        
        Draw.rect(start, x, y, start.width * scale * start.scl() * endScl, start.height * scale * start.scl() * endScl, rot + 180);
        Draw.rect(end, x2, y2, end.width * scale * end.scl() * endScl, end.height * scale * end.scl() * endScl, rot);

//        Lines.stroke(12f * scale);
        Lines.stroke(Vars.tilesize * scale);
        Lines.line(x + vx, y + vy, x2 - vx, y2 - vy);
//        Lines.line(line, x + vx, y + vy, x2 - vx, y2 - vy, false);
        Lines.stroke(1f);
        Lines.stroke(Math.max(1, Vars.tilesize * scale - 2));
//        Draw.color(Draw.getColor().mul(2f));
        Draw.color(1f,1f,1f);
        Lines.line(x + vx, y + vy, x2 - vx, y2 - vy);

        scale *= (Math.max(1, Vars.tilesize * scale - 4)) / (Vars.tilesize * scale);
        
        Draw.rect(start, x, y, start.width * scale * start.scl() * endScl, start.height * scale * start.scl() * endScl, rot + 180);
        Draw.rect(end, x2, y2, end.width * scale * end.scl() * endScl, end.height * scale * end.scl() * endScl, rot);

        Lines.stroke(Math.max(.5f, Vars.tilesize * scale - 8));
        Lines.line(x + vx, y + vy, x2 - vx, y2 - vy);
        
        if(renderer == null) return;
        renderer.lights.line(x, y, x2, y2, 50*scale*Draw.getColor().a, Draw.getColor(), .3f*Draw.getColor().a); // .3f
         */
    }

	public static void spinSprite(TextureRegion region, float x, float y, int rotation) {
		if(rotation == 1 || rotation == 2) {
			Draw.rect(region, x, y, region.width * region.scl(), region.height * region.scl(), rotation*90-90);
		} else {
			Draw.rect(region, x, y, -region.width * region.scl(), region.height * region.scl(), rotation*90-90);
		}			
	}

	public static void debug(SingleOutLaserBuild build) {
//		Draw.color(Pal.accent);
//		Fill.rect(build.tileX()*Vars.tilesize, build.tileY()*Vars.tilesize, 4, 4, 45);
//		Draw.color(Pal.remove);
//		Fill.rect(build.closestX(build.tileX()+999)*Vars.tilesize, build.tileY()*Vars.tilesize, 4, 4, 45);
	}
	
}
