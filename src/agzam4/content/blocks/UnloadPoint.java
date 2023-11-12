package agzam4.content.blocks;

import static mindustry.Vars.content;

import agzam4.Work;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.meta.BlockFlag;

public class UnloadPoint extends Block {

    public float staleTimeDuration = 60f * 6f;
    public int unloadItemsPerSecond = 10;

    public @Nullable TextureRegion topRegion = null;
    
	public UnloadPoint(String name) {
        super(name);
        update = solid = true;
        hasItems = true;
        configurable = true;
        saveConfig = true;
        clearOnDoubleTap = true;
        
        flags = EnumSet.of(BlockFlag.unitCargoUnloadPoint);

        unloadable = false;
        squareSprite = false;
        
        config(Item.class, (UnloadPointBuild build, Item item) -> build.item = item);
        configClear((UnloadPointBuild build) -> build.item = null);
	}
	
	@Override
	public void loadIcon() {
		super.loadIcon();
		topRegion = Work.texture(name + "-top");
	}
	
    private static int fillTime;

	public class UnloadPointBuild extends Building {
		
        public Item item;
        public float counter;

        @Override
        public void draw() {
            super.draw();
            if(item != null) {
                Draw.color(item.color);
                Draw.rect(topRegion, x, y);
                Draw.color();
            }
        }

        public int lastFillTime;
        
        @Override
        public void handleStack(Item item, int amount, Teamc source) {
        	super.handleStack(item, amount, source);
        	lastFillTime = fillTime++;
        }
        
        @Override
        public void updateTile() {
            super.updateTile();
            dumpAccumulate();
        }
        
        @Override
        public int acceptStack(Item item, int amount, Teamc source) {
            return Math.min(itemCapacity - items.total(), amount);
        }

        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(UnloadPoint.this, table, content.items(), () -> item, this::configure);
        }

        @Override
        public Object config(){
            return item;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(item == null ? -1 : item.id);
//            write.bool(stale);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            item = Vars.content.item(read.s());
//            stale = read.bool();
        }
    }
	
}
