package agzam4.content.effects;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.Vars.tilesize;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.graphics.Pal;
import mindustry.type.Item;

public class NGFx {

	public static Effect flyingItem, sporeBeam, sporeShield;
	
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

	        float unfin = 1f-e.fin();
	        Lines.stroke(2f*unfin);
	        Draw.color(Color.white, e.color, Mathf.clamp(e.fin()*1.5f));
	        Draw.alpha(Mathf.clamp(unfin*2f));
	        Lines.line(e.x, e.y, p.getX(), p.getY());

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
//	        Lines.endLine();
	    });
	    
	    sporeShield = new Effect(20, e -> {
	        color(Pal.spore);
	        stroke(2f * e.fout() + 0.5f);
	        Lines.square(e.x, e.y, 1f + (e.fin() * e.rotation * tilesize / 2f - 1f));
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
