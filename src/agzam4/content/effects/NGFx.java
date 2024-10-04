package agzam4.content.effects;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.tilesize;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;

public class NGFx {

	public static Effect flyingItem, sporeBeam, sporeShield, sporeLaser, crused, fireSpark, sporesExplode;
	private static Vec2 rv = new Vec2();
	
	public static void load() {
		flyingItem = new Effect(12, e -> {
	        if(!(e.data instanceof FlyingItem)) return;
	        FlyingItem fi = (FlyingItem) e.data;
	        Tmp.v1.set(e.x, e.y).interpolate(Tmp.v2.set(fi.target), e.fin(), Interp.pow3)
	        .add(Tmp.v2.sub(e.x, e.y).nor().rotate90(1).scl(Mathf.randomSeedRange(e.id, 1f) * e.fslope() * 10f));
	        float x = Tmp.v1.x, y = Tmp.v1.y;
	        float size = 1f;

//	        color(Pal.accent);
//	        color(Color.white);
	        
	        Draw.rect(fi.item.fullIcon, x, y, e.fslope() * 3f * size);
	        
//	        Fill.circle(x, y, e.fslope() * 3f * size);

//	        color(e.color);
//	        Fill.circle(x, y, e.fslope() * 1.5f * size);
	    });
		
	    sporeBeam = new Effect(25f, 300f, e -> {
	        if(!(e.data instanceof Position)) return;
	        
	        Position p = (Position) e.data;
//	        Draw.color(e.color, e.fout());
//	        Lines.stroke(1.5f);
//	        Lines.line(e.x, e.y, pos.getX(), pos.getY());
//	        Drawf.light(e.x, e.y, pos.getX(), pos.getY(), 20f, e.color, 0.6f * e.fout());
	        

	        float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
	        Tmp.v1.set(p).sub(e.x, e.y).nor();

	        float normx = Tmp.v1.x, normy = Tmp.v1.y;
	        float range = 6f;
	        int links = Mathf.ceil(dst / range);
	        float spacing = dst / links;

	        Lines.stroke(2f*e.fout());
	        Draw.color(Color.white, e.color, Mathf.clamp(e.fin()*1.5f));
	        Draw.alpha(Mathf.clamp(e.fout()*2f));
	        Lines.line(e.x, e.y, p.getX(), p.getY());
	        Drawf.light(e.x, e.y, p.getX(), p.getY(), 20f, e.color, 0.6f * e.fout());

	        Lines.stroke(.75f);
//	        Lines.beginLine();
//	        Lines.linePoint(e.x, e.y);
	        Fx.rand.setSeed(e.id);
	        for(int i = 0; i < links; i++){
	            float nx, ny;
	            if(i == links - 1){
	                nx = tx;
	                ny = ty;
	            }else{
	                float len = (i + 1) * spacing;
	                Tmp.v1.setToRandomDirection(Fx.rand).scl(range/2f);
	                nx = e.x + normx * len + Tmp.v1.x;
	                ny = e.y + normy * len + Tmp.v1.y;
	            }
	            Fill.rect(nx, ny, 3f, 3f, 45);
//	            Lines.linePoint(nx, ny);

	            if(i == links - 1){
	                nx = tx;
	                ny = ty;
	            }else{
	                float len = (i + 1) * spacing;
	                Tmp.v1.setToRandomDirection(Fx.rand).scl(range*2*e.fout());
	                nx = e.x + normx * len + Tmp.v1.x;
	                ny = e.y + normy * len + Tmp.v1.y;
	            }
	            Fill.rect(nx, ny, 2f*e.fin(), 2f*e.fin(), 45);

	            if(i == links - 1){
	                nx = tx;
	                ny = ty;
	            }else{
	                float len = (i + 1) * spacing;
	                Tmp.v1.setToRandomDirection(Fx.rand).scl(range*2*e.fout());
	                nx = e.x + normx * len + Tmp.v1.x;
	                ny = e.y + normy * len + Tmp.v1.y;
	            }
	            Fill.rect(nx, ny, 2f*e.fin(), 2f*e.fin(), 45);
	        }
//	        Lines.endLine();
	    });

	    sporeLaser = new Effect(25f, e -> {
	    	if(!(e.data instanceof Vec2)) return;
	    	Vec2 v = (Vec2) e.data;

	    	color(e.color);
	    	stroke(e.fout() * 0.9f + 0.6f);

	    	for(int i = 0; i < 7; i++){
	    		Fx.v.trns(e.rotation, Fx.rand.random(8f, v.dst(e.x, e.y) - 8f));
	    		Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
	    	}

	    	e.scaled(14f, b -> {
	    		stroke(b.fout() * 1.5f);
	    		color(e.color);
	    		Lines.line(e.x, e.y, v.x, v.y);
	    	});

	        if(!(e.data instanceof Position)) return;
	        
	        Position p = (Position) e.data;

	        float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
	        Tmp.v1.set(p).sub(e.x, e.y).nor();

	        float normx = Tmp.v1.x, normy = Tmp.v1.y;
	        float range = 6f;
	        int links = Mathf.ceil(dst / range);
	        float spacing = dst / links;

	        float unfin = 1f-e.fin();
	        Lines.stroke(2f*unfin);
	        Drawf.light(e.x, e.y, p.getX(), p.getY(), 20f, e.color, 0.6f * e.fout());

	        Lines.stroke(.75f);
	        Fx.rand.setSeed(e.id);
	        for(int i = 0; i < links; i++){
	            float nx, ny;
	            if(i == links - 1){
	                nx = tx;
	                ny = ty;
	            }else{
	                float len = (i + 1) * spacing;
	                Tmp.v1.setToRandomDirection(Fx.rand).scl(range/2f);
	                nx = e.x + normx * len + Tmp.v1.x;
	                ny = e.y + normy * len + Tmp.v1.y;
	            }
//	            Fill.rect(nx, ny, 3f, 3f, 45);
//	            Lines.linePoint(nx, ny);

	            if(i == links - 1){
	                nx = tx;
	                ny = ty;
	            }else{
	                float len = (i + 1) * spacing;
	                Tmp.v1.setToRandomDirection(Fx.rand).scl(range*2*unfin);
	                nx = e.x + normx * len + Tmp.v1.x;
	                ny = e.y + normy * len + Tmp.v1.y;
	            }
	            Fill.rect(nx, ny, 2f*e.fin(), 2f*e.fin(), 45);

	            if(i == links - 1){
	                nx = tx;
	                ny = ty;
	            }else{
	                float len = (i + 1) * spacing;
	                Tmp.v1.setToRandomDirection(Fx.rand).scl(range*2*unfin);
	                nx = e.x + normx * len + Tmp.v1.x;
	                ny = e.y + normy * len + Tmp.v1.y;
	            }
	            Fill.rect(nx, ny, 2f*e.fin(), 2f*e.fin(), 45);
	        }
	    });
	    
	    sporeShield = new Effect(20, e -> {
	        color(Pal.spore);
	        stroke(2f * e.fout() + 0.5f);
	        Lines.square(e.x, e.y, 1f + (e.fin() * e.rotation * tilesize / 2f - 1f));
	    });

	    Vec2[] crusedVertexs = {
	    		new Vec2(0, 0),
	    		new Vec2(-30, 30),
	    		new Vec2(-60, 50),
	    		new Vec2(-48, 100),
	    		new Vec2(0, 88),
	    		new Vec2(48, 100),
	    		new Vec2(60, 50),
	    		new Vec2(30, 30),
	    		new Vec2(0, 0),
	    		null,
	    		new Vec2(-60, 0),
	    		new Vec2(60, -52),
	    		null,
	    		new Vec2(60, 0),
	    		new Vec2(-60, -52)
	    };
	    
	    crused = new Effect(100f, e -> {
	        Lines.stroke(2f*e.fout());
	        Draw.color(Color.white, e.color, Mathf.clamp(e.fin()*1.5f));
	        Draw.alpha(Mathf.clamp(e.fout()*2f));
	        e.y += 1f;
	        float scl = e.rotation/152f;
//	        stroke(1f); // e.fout()*scl*5f
	        for(int i = 0; i < crusedVertexs.length-1; i++){
	            Vec2 current = crusedVertexs[i];
	            Vec2 next = crusedVertexs[i + 1];
	            if(current == null || next == null) continue;
	            Lines.line(current.x * scl + e.x, current.y * scl + e.y - 24f*scl, next.x * scl + e.x, next.y * scl + e.y - 24f*scl);
		        Drawf.light(current.x * scl + e.x, current.y * scl + e.y - 24f*scl, next.x * scl + e.x, next.y * scl + e.y - 24f*scl, 20f, e.color, 0.6f * e.fout());
	        }
	        
	        Fx.rand.setSeed(e.id);
	        for (int i = 0; i < 3; i++) {
                Tmp.v1.setToRandomDirection(Fx.rand).scl(e.rotation/2f*e.fout());
                Fill.rect(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fout(), e.fout(), 45);
			}
	    });
	    
	    fireSpark = new Effect(14, e -> {
	    	color(Pal.lightFlame, Pal.darkFlame, e.fin());
	    	stroke(e.fout());
	    	Fx.rand.setSeed(e.id);
	    	float ang = 0;
	    	for(int i = 0; i < 3; i++){
	    		ang = e.rotation + Fx.rand.range(15f);
	    		rv.trns(ang, 1f + Fx.rand.random(e.fin() * 15f));
	    		lineAngle(e.x + rv.x, e.y + rv.y, ang, e.fout() * 3);
	    	}
	    });
	    
	    sporesExplode =  new Effect(30, b -> {
	        float intensity = 6.8f;
//	        float baseLifetime = 25f + intensity * 11f;
	        b.lifetime = 50f + intensity * 65f;
	        color(Pal.spore);
	        alpha(0.7f);
	        for(int i = 0; i < 4; i++){
	            Fx.rand.setSeed(b.id*2 + i);
	            float lenScl = Fx.rand.random(0.4f, 1f);
	            int fi = i;
	            b.scaled(b.lifetime * lenScl, e -> {
					randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), 4, e.rotation, (x, y, in, out) -> {
	                    float fout = e.fout(Interp.pow5Out) * Fx.rand.random(0.5f, 1f);
	                    float rad = fout * ((2f + e.rotation) * .1f);

	                    Fill.circle(e.x + x, e.y + y, rad);
	                    Drawf.light(e.x + x, e.y + y, rad * 2.5f, Pal.spore, 0.5f);
	                });
	            });
	        }
//	        b.scaled(baseLifetime, e -> {
//	            Draw.color();
//	            e.scaled(5 + intensity * 2f, i -> {
//	                stroke((3.1f + intensity/5f) * i.fout());
//	                Lines.circle(e.x, e.y, (3f + i.fin() * 14f) * intensity);
//	                Drawf.light(e.x, e.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * e.fout());
//	            });
//
//	            color(Pal.lighterOrange, Pal.reactorPurple, e.fin());
//	            stroke((2f * e.fout()));
//
//	            Draw.z(Layer.effect + 0.001f);
//	            randLenVectors(e.id + 1, e.finpow() + 0.001f, (int)(8 * intensity), 28f * intensity, (x, y, in, out) -> {
//	                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity));
//	                Drawf.light(e.x + x, e.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
//	            });
//	        });
	    });
	}

	
	public static class FlyingItem {
		
		public Item item;
		public Position target;
		
		public FlyingItem(Item item, Position target) {
			this.item = item;
			this.target = target;
		}
	}
}
