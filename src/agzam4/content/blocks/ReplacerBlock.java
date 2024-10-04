package agzam4.content.blocks;

import mindustry.gen.Building;
import mindustry.world.Block;

public class ReplacerBlock extends Block {

	public Block replace;

	public ReplacerBlock(String name, Block replace) {
		super(name);
		this.replace = replace;
		this.region = replace.region;
        update = true;
	}

    public class ReplacerBuild extends Building {
    	
    	@Override
    	public void updateTile() {
    		super.updateTile();
    		tile.setBlock(replace);
    	}
    	
    }
	
}
