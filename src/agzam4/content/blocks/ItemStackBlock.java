package agzam4.content.blocks;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class ItemStackBlock extends StorageBlock {

	public ItemStackBlock(String name) {
		super(name);

        hasItems = true;
        update = true;
        destructible = true;
        separateItemCapacity = true;
        group = BlockGroup.transportation;
        flags = EnumSet.of(BlockFlag.storage);
        
        allowResupply = true;
        alwaysReplace = true;
        placeableLiquid = true;
        
        envEnabled = Env.any;
        
        solid = false; // TODO
        targetable = false;
        underBullets = true;
        itemCapacity = 1_000_000;
        
        customShadow = true;
        hasShadow = false;
        
        breakEffect = Fx.blastsmoke;
        floating = true;
        
        sync = true;
	}
	
    @Override
    public boolean outputsItems(){
        return false;
    }

    public static void incinerateEffect(Building self, Building source){
    }
    
    @Override
    public boolean canBreak(Tile tile) {
    	return false;
    }
    
    
    @Override
    public void drawShadow(Tile tile) {
    	return;
    }

    @Override
    public int minimapColor(Tile tile){
        Building build = tile.build;
        if(build == null) return super.minimapColor(tile);
        if(build.items == null) return super.minimapColor(tile);
        Item first = build.items.first();
        if(first == null) return super.minimapColor(tile);
        return first.color.rgba();
    }

	public class ItemStackBuild extends StorageBuild {

		@Override
		public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
			Building b = super.init(tile, team, shouldAdd, rotation);
//			if(b.items != null) b.items.set(Items.scrap, 1);
			return b;
		}
		
		float angle = Mathf.random(360);
		float dAngle = Mathf.random(30, 90);
		
		@Override
		public void draw() {
			if(items == null) return;
			
			Item first = items.first();
			if(first == null) {
//				super.draw();
				return;
			}
			
	        int count = Math.max(items.total(), 100);
	        Draw.color(0f, 0f, 0f, .75f - count/200f);
	        Draw.rect(customShadowRegion, x(), y(), 0);
	        Draw.color();
			
			for (int i = 0; i < Vars.content.items().size; i++) {
				int amount = items.get(i);
				if(amount > 0) {
					Draw.rect(Vars.content.item(i).fullIcon, x(), y(), angle + dAngle*i);
				}
			}
		}
		
		@Override
		public void updateTile() {
//			super.updateTile();
//			if(items == null) return;
//			int total = items.total();
//			maxHealth = health = total;
//			if(total == 0) tile.remove();
		}
		
		boolean ready = false;
		
		@Override
		public void update() {
			super.update();
			if(items == null) return;
			int total = items.total();
			maxHealth = health = total;

			if(total == 0) {
				if(ready) tile.setBlock(Blocks.air);
				return;
			}
			
			if(ready) return;
			
			ready = true;
		}
		
		@Override
		public void itemTaken(Item item) {
//			super.itemTaken(item);
//			if(items == null) {
//				tile.setBlock(Blocks.air);
//				return;
//			}
//			int total = items.total();
//			maxHealth = health = total;
//			if(total == 0) tile.setBlock(Blocks.air);
		}
		
		@Override
		public void drawTeamTop() {
//			super.drawTeamTop();
		}
		
		@Override
		public void drawSelect() {
//			super.drawSelect();
		}
		
		@Override
		public void drawTeam() {
//			super.drawTeam();
		}
		
		@Override
		public void drawCracks() {
//			super.drawCracks();
		}
		
		@Override
		public void damage(float damage) {
//			super.damage(damage);
		}
		
		@Override
		public void damage(Bullet bullet, Team source, float damage) {
//			super.damage(bullet, source, damage);
		}
		
		@Override
		public void damage(float amount, boolean withEffect) {
//			super.damage(amount, withEffect);
		}
		
		@Override
		public void damage(Team source, float damage) {
//			super.damage(source, damage);
		}
		
		@Override
		public void damageContinuous(float amount) {
//			super.damageContinuous(amount);
		}
		
		@Override
		public void damageContinuousPierce(float amount) {
//			super.damageContinuousPierce(amount);
		}
		
		@Override
		public void damagePierce(float amount) {
//			super.damagePierce(amount);
		}
		
		@Override
		public void damagePierce(float amount, boolean withEffect) {
//			super.damagePierce(amount, withEffect);
		}
		
		@Override
		public void overwrote(Seq<Building> previous) {
            for(Building other : previous){
                if(other.items != null && other.items != items){
                    items.add(other.items);
                }
            }
            items.each((i, a) -> items.set(i, Math.min(a, itemCapacity)));
		}
		
		@Override
		public String getDisplayName() {
			if(items == null) return "";
			if(items.first() == null) return "";
			return items.first().localizedName;
		}
		
		@Override
		public TextureRegion getDisplayIcon() {
			if(items == null) return block.uiIcon;
			if(items.first() == null) return block.uiIcon;
			return items.first().fullIcon;
		}
		
    }
}
