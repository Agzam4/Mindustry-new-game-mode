package agzam4.content.blocks.power;

import agzam4.Drawm;
import agzam4.Work;
import agzam4.ui.HuePicker;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Icon;
import mindustry.logic.LAccess;
import mindustry.ui.Styles;

public class LaserPainter extends SingleOutLaser {

	public TextureRegion topRegion;

	public LaserPainter(String name) {
		super(name);
        config(Long.class, (LaserPainterBuild tile, Long color) -> {
        	if(color == null) return;
        	tile.color.set(color.intValue());
//        	tile.color.
        	tile.fixColor();
        });
	}
	
	@Override
	public void loadIcon() {
		super.loadIcon();
		topRegion = Work.texture(name + "-top");
	}

	public class LaserPainterBuild extends SingleOutLaserBuild {
		
		public Color color = LaserData.laserDefaultColor.cpy().r(1);
		@Nullable LaserData laserDataIn = null;

		@Override
		public void updateLaserData() {
			laserDataOut.copyStats(shooting ? laserDataIn : null).disperse(dst);
			laserDataOut.color.set(color);
		}

		public void fixColor() {
        	int[] hsv = Color.RGBtoHSV(color);
        	setHue(hsv[0]);			
		}
		public void setHue(int hue) {
        	Color.HSVtoRGB(hue, 45, 94, color);			
		}

		@Override
		public void link(LaserBuild caller, int myAngle, int offsetX, int offsetY, LaserData data) {
			if(laserDataIn != null && laserDataIn.owner != caller) {
				laserDataIn.owner.onReplaceLinkFor(this);
			}
			laserDataIn = data;
			updateLaserData();
		}
		

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.color){
            	setHue(Mathf.mod((int)p1, 360));
            }
            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.color) return color.toDoubleBits();
            return super.sense(sensor);
        }
        
		@Override
		public void writeAll(Writes write) {
			super.writeAll(write);
            write.i(color.rgba());
		}
		
		@Override
		public void readAll(Reads read, byte revision) {
			super.readAll(read, revision);
			color.set(read.i());
		}
		
		@Override
		public void unlink(LaserBuild caller, int myAngle, int offsetX, int offsetY) {
			laserDataIn = null;
			updateLaserData();
		}
		
		@Override
		public boolean absorb(LaserBuild caller, int myAngle, int offsetX, int offsetY, LaserData data) {
			if(caller.block.size != size) return false;
			return myAngle == rotate180(rotation);
		}
		
		@Override
		public void draw() {
			super.draw();
			Draw.color(color);
			Drawm.spinSprite(topRegion, x, y, rotation);
//			HeatConductorBuild
//			HeatConductorBuild;
		}

        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.pencil, Styles.cleari, () -> {
                HuePicker.instance.show(Tmp.c1.set(color), false, res -> configure((long)res.rgba())); // TODO: custom picker
                deselect();
            }).size(40f);
        }
	}
}
