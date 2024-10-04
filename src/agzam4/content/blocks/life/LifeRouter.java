package agzam4.content.blocks.life;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

import agzam4.Work;
import agzam4.content.blocks.NewGameBlocks;
import agzam4.content.blocks.life.LifeMover.LifeMoverBuild;
import agzam4.content.effects.NGFx;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
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
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.world.Block;
import mindustry.world.Tile;

public class LifeRouter extends LifeEssenceStorageBlock {

    public TextureRegion glowRegion, glowLightRegion;
    public float reload = 60f;
    public int range = 5;
    
	public LifeRouter(String name) {
		super(name);
        update = true;
        solid = true;
        allowDiagonal = false;
        rotate = false;
        replaceable = true;
	}

	@Override
	public boolean canReplace(Block other) {
		return other == this || other == NewGameBlocks.lifeRouter || super.canReplace(other);
	}
	
    @Override
    public void init(){
        super.init();
        updateClipRadius((range + 0.5f) * tilesize);
    }
    
	@Override
	public void loadIcon() {
		super.loadIcon();
		glowRegion = Work.texture(name + "-glow");
		glowLightRegion = Work.texture(name + "-glow-light");
	}

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, (point, other) -> Math.max(Math.abs(point.x - other.x), Math.abs(point.y - other.y)) <= range);
    }
	
	@Override
	public void drawPlace(int x, int y, int r, boolean valid) {
        for (int rotation = 0; rotation < 4; rotation++) {
            Point2 dir = Geometry.d4[rotation];
			int offset = size/2;
			for(int j = 1 + offset; j <= range + offset; j++){
				Building other = world.build(x + j * dir.x, y + j * dir.y);
				if(other == null) continue;

				if(other.isInsulated()) break;
				if(other instanceof LifeMoverBuild && other.rotdeg()/90 == (rotation+2)%4) break;

				if(other instanceof LifeEssenceBuild) {
					Tile tile = world.tile(x + j * dir.x, y + j * dir.y);
					Drawf.dashLine(Pal.spore, x*tilesize, y*tilesize, tile.worldx(), tile.worldy());
					break;
				}
			}
		}
	}

	public class LifeRouterBuild extends LifeEssenceStorageBuild {

        public int link = -1;
        public float rotation = 90;
        public float reloadCounter = 0f;

        public int lastChange = -2;
        public int lastRotate = -2;

        @Nullable Building[] targets = new Building[4];
        @Nullable Tile[] shoots = new Tile[4];
        int links = 0;
        int targetIndex = 0;
        
		private float flash;
        
        @Override
        public void draw(){
            Draw.rect(region, x, y, 0);

            flash += Time.delta;
            
            float glow = Mathf.clamp(essence()/essenceCapacity());
            
            glow = Mathf.clamp(essence()*2/essenceCapacity());
            float light = Mathf.clamp((essence()-essenceCapacity()/2f)/essenceCapacity(), 0, .5f)*2f*(Mathf.absin(flash, 9f, 1f)/2f+.5f);
            
            for (int i = 0; i < 4; i++) {
            	if(shoots[i] == null) continue;
            	LifeEssenceStorageBlock.bridge(this, shoots[i], light);
			}
            
            Drawf.light(this, size*Vars.tilesize, Tmp.c1.set(Color.white), glow);
            
            Drawf.additive(glowRegion, Tmp.c1.set(fillColor).mula(glow), x, y, rotdeg());
            Drawf.additive(glowLightRegion, Tmp.c1.set(Color.white).mula(light), x, y, rotdeg());
            Drawf.light(this, size*Vars.tilesize*2f, Tmp.c1.set(fillColor), light);
        }
        
        @Override
        public void updateTile() {
        	super.updateTile();
        	
            if(reloadCounter > 0f){
                reloadCounter = Mathf.clamp(reloadCounter - edelta() / reload);
            }

            if(lastChange != world.tileChanges){
                lastChange = world.tileChanges;
                updateTarget();
            }
            
            if(reloadCounter <= 0.0001f && essence() > 0.0004f){
            	if(!fire()) {
            		updateTarget();
            		fire();
            	}
        		return;
            }

        }
        
        Vec2[] fxVec2s = {new Vec2(), new Vec2(), new Vec2(), new Vec2()};
        
		private boolean fire() {
			if(links == 0) return false;
			float maxPerLink = essence/links;
			for (int i = 0; i < 4; i++) {
            	Building target = targets[targetIndex];
            	Tile shoot = shoots[targetIndex];
        		targetIndex = (targetIndex+1)%4;
            	
            	if(target == null) continue;
            	if(shoot == null) continue;

            	if(target instanceof LifeEssenceBuild) {
            		LifeEssenceBuild t = (LifeEssenceBuild) target;
            		float free = t.essenceCapacity() - t.essence();
            		free = Math.min(free, essence());
            		free = Math.min(free, maxPerLink);
            		if(free <= 0.0001f) continue;
            		t.essence(t.essence() + free);
            		essence -= free;
                	reloadCounter = 1f;
                	NGFx.sporeBeam.at(x, y, rotation, Pal.spore, fxVec2s[i].set(shoot.worldx(), shoot.worldy()));
            		return true;
            	}
			}		
			return false;
		}

		private void updateTarget() {
			links = 0;
            targets[0] = null;
            targets[1] = null;
            targets[2] = null;
            targets[3] = null;

            shoots[0] = null;
            shoots[1] = null;
            shoots[2] = null;
            shoots[3] = null;
            
            for (int rotation = 0; rotation < 4; rotation++) {
                Point2 dir = Geometry.d4[rotation];
                
    			int offset = size/2;
    			for(int j = 1 + offset; j <= range + offset; j++){
    				Building other = world.build(tile.x + j * dir.x, tile.y + j * dir.y);
    				if(other == null) continue;

    				if(other.isInsulated()) break;
					if(other instanceof LifeMoverBuild && other.rotdeg()/90 == (rotation+2)%4) break;

    				if(other instanceof LifeEssenceBuild) {
    					targets[rotation] = other;
    					shoots[rotation] = world.tile(tile.x + j * dir.x, tile.y + j * dir.y);
    					links++;
    					break;
    				}
    			}
			}
            
		}
	}
}
