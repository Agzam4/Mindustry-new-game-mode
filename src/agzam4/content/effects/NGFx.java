package agzam4.content.effects;

import arc.graphics.g2d.Draw;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.type.Item;

public class NGFx {

	public static Effect flyingItem;
	
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
