package agzam4.gen;

import agzam4.content.blocks.power.LaserData;
import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.math.Mathf;

public class GenLaser {

	static int tilesize = 8*1;
	private static int scale = 1;

	public static void main(String[] args) {
//		LightRenderer
		Color out = LaserData.laserDefaultColor.cpy();
		out.hue(0);

		int blurPasses = 4;
		float originalIntensity = 4f;
		float bloomIntensity = 2f;
		
		Pixmap laserPixmap = new Pixmap(30, tilesize*3);

		for (int y = 0; y < tilesize; y++) {
			laserPixmap.set(0, tilesize+y, out);
		}
//		for (int y = 0; y < tilesize/2; y++) {
//			laserPixmap.set(0, tilesize+y+tilesize/4, Color.white);
//		}
		
		float closer = 1.3846153846f;
		float close = 0.3162162162f;
		float center = 0.2270270270f; 

		for (int i = 0; i < blurPasses; i++) {
			Color[] gaussian = new Color[laserPixmap.height];
			for (int y = 0; y < gaussian.length; y++) {
				gaussian[y] = new Color();

				Color top = colorAt(laserPixmap, 0, y - closer);
				Color bottom = colorAt(laserPixmap, 0, y + closer);
				Color result = colorAt(laserPixmap, 0, y);
				
				top.mul(close);
				bottom.mul(close);
				result.mul(center);
//				top.a *= close;
//				bottom.a *= close;
				
//				result.a *= center;
				
				result.add(top);
				result.add(bottom);
//				result.a += top.a + bottom.a;
				result.a = 1;
				
				gaussian[y] = result;
//				colorAt(laserPixmap, 0, 0);
				
			}
			for (int y = 0; y < gaussian.length; y++) {
				laserPixmap.set(0, y, gaussian[y]);
			}
		}
		

		for (int type = 0; type < 2; type++) {
			Pixmap layer = Pixmaps.scale(laserPixmap, laserPixmap.width, laserPixmap.height*scale , true);
			for (int y = 0; y < layer.height; y++) {
				Color o = new Color(layer.get(0, y));
				o.mul(originalIntensity);
				Color b = new Color(layer.get(0, y));
				b.mul(bloomIntensity);
				
				o.mul(1f - b.r, 1f - b.g, 1f - b.b, 1f);
				Color c = new Color();
				if(type == 0) {
					c.add(b);
					c.a = c.r;
					c.r(1).g(1).b(1);
				}
				if(type == 1) {
					c.add(o);
					c.add(b);
					c.a = c.r;
					c.r(1).g(1).b(1);
				}
				layer.set(0, y, c);
			}

			for (int y = 0; y < layer.height; y++) {
				for (int x = 1; x < layer.width; x++) {
					layer.setRaw(x, y, layer.getRaw(0, y));
				}
			}
			System.out.println("Generated: " + (type == 0 ? "in" : "out"));
			Fi.get("assets/sprites/effects/laser-" + (type == 0 ? "in" : "out") + ".png").writePng(layer);
		}
		
		
		
		
//		mindustry.core.Renderer
	}

	private static Color colorAt(Pixmap p, float x, float y) {
		Color f = new Color(p.get(Mathf.floor(x), Mathf.floor(y)));
		Color t = new Color(p.get(Mathf.ceil(x), Mathf.ceil(y)));
		float dst = 0;
		if(x%1 != 0 && y%1 != 0) dst = Mathf.sqrt2;
		else if(x%1 != 0 || y%1 != 0) dst = 1;
		else dst = 0;
		float a = dst == 0 ? 0 : (x%1 + y%1)/dst;
		f.r = Mathf.lerp(f.r, t.r, a);
		f.g = Mathf.lerp(f.g, t.g, a);
		f.b = Mathf.lerp(f.b, t.b, a);
		f.a = Mathf.lerp(f.a, t.a, a);
		return f;
	}
}
