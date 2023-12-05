package agzam4.content.blocks.life;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

import agzam4.Work;
import agzam4.content.effects.NGFx;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.Renderer;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.world.Tile;

public class LifeMover extends LifeEssenceStorageBlock {

    public TextureRegion arrow, glowRegion, glowLightRegion;
    public float reload = 60f;
	private int range = 5;
    
	public LifeMover(String name) {
		super(name);
        update = true;
        solid = true;
//        configurable = true;
//        hasItems = true;
//        hasPower = true;
//        outlineIcon = true;
//        sync = true;
        allowDiagonal = false;
        rotate = true;
	}

    @Override
    public void init(){
        super.init();
        updateClipRadius((range + 0.5f) * tilesize);
    }
    
	@Override
	public void loadIcon() {
		super.loadIcon();
		arrow = Work.texture(name + "-arrow");
		glowRegion = Work.texture(name + "-glow");
		glowLightRegion = Work.texture(name + "-glow-light");
	}

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, (point, other) -> Math.max(Math.abs(point.x - other.x), Math.abs(point.y - other.y)) <= range);
    }
	
	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
//		super.drawPlace(x, y, rotation, valid);

		rotation = Mathf.clamp(rotation, 0, 3);
        Point2 dir = Geometry.d4[rotation];
        
        
        
		int offset = size/2;
		//find first block with power in range
		for(int j = 1 + offset; j <= range + offset; j++){
			Building other = world.build(x + j * dir.x, y + j * dir.y);

			if(other != null && other.isInsulated()){
				break;
			}

			if(other != null && other instanceof LifeEssenceBuild) {// && other.team == team){
				Tile tile = world.tile(x + j * dir.x, y + j * dir.y);
				Drawf.dashLine(Pal.spore, x*tilesize, y*tilesize, tile.worldx(), tile.worldy());
				return;
			}
		}
	}

	public class LifeMoverBuild extends LifeEssenceStorageBuild {

        public int link = -1;
        public float rotation = 90;
        public float reloadCounter = 0f;

        public int lastChange = -2;
        public int lastRotate = -2;

        @Nullable Building target;
        @Nullable Tile shoot;
        
		private float flash;
        
        @Override
        public void draw(){
            Draw.rect(region, x, y, 0);
            Draw.rect(arrow, x, y, rotate ? rotdeg() : 0);
            
            flash += Time.delta;
            
            float glow = Mathf.clamp(essence()/essenceCapacity());
            Drawf.light(this, size*Vars.tilesize, Tmp.c1.set(Color.white), glow);
            
            glow = Mathf.clamp(essence()*2/essenceCapacity());
            float light = Mathf.clamp((essence()-essenceCapacity()/2f)/essenceCapacity(), 0, .5f)*2f*(Mathf.absin(flash, 9f, 1f)/2f+.5f);


        	if(shoot != null) {
        		LifeEssenceStorageBlock.bridge(this, shoot, light);
        	}
            
            Drawf.additive(glowRegion, Tmp.c1.set(fillColor).mula(glow), x, y, rotdeg());
            Drawf.additive(glowLightRegion, Tmp.c1.set(Color.white).mula(light), x, y, rotdeg());
            Drawf.light(this, size*Vars.tilesize*2f, Tmp.c1.set(fillColor), light);

        }
        
        Vec2 fxVec2 = new Vec2();
        
        @Override
        public void updateTile() {
        	super.updateTile();
        	
            if(reloadCounter > 0f) {
                reloadCounter = Mathf.clamp(reloadCounter - edelta() / reload);
            }

            if(lastChange != world.tileChanges || lastRotate != rotation()){
                lastChange = world.tileChanges;
                lastRotate = rotation();
                updateTarget();
            }
            
            if(reloadCounter <= 0.0001f){
            	if(target == null) {
            		updateTarget();
            		return;
            	}

            	if(target instanceof LifeEssenceBuild) {
            		LifeEssenceBuild t = (LifeEssenceBuild) target;
            		float free = t.essenceCapacity() - t.essence();
            		free = Math.min(free, essence());
            		if(free <= 0.0001f) return;
            		t.essence(t.essence() + free);
            		essence(essence() - free);
            		
                	reloadCounter = 1f;
                	if(shoot != null) NGFx.sporeBeam.at(x, y, rotation, Pal.spore, fxVec2.set(shoot.worldx(), shoot.worldy()));
            		return;
            	}
        		updateTarget();
        		return;
            }

        }

		private void updateTarget() {
            target = null;
            shoot = null;
            
			int rotation = (int) (rotdeg()/90);
			rotation = Mathf.clamp(rotation, 0, 3);
            Point2 dir = Geometry.d4[rotation];
            
			int offset = size/2;
			//find first block with power in range
			for(int j = 1 + offset; j <= range + offset; j++){
				Building other = world.build(tile.x + j * dir.x, tile.y + j * dir.y);

				//hit insulated wall
				if(other != null && other.isInsulated()){
					break;
				}

				//power nodes do NOT play nice with beam nodes, do not touch them as that forcefully modifies their links
				if(other != null && other instanceof LifeEssenceBuild) {// && other.team == team){
					target = other;
					shoot = world.tile(tile.x + j * dir.x, tile.y + j * dir.y);
					return;
				}
			}
		}
	}
}
