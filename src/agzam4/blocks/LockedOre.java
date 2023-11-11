package agzam4.blocks;

import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.EnumSet;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.environment.OverlayFloor;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;

public class LockedOre extends OverlayFloor {
	
	OreBlock defaultOre;
	
	public LockedOre(String name) {
		super(name);
		
//        hasItems = true;
//        update = true;
//        destructible = true;
		itemDrop = Items.sand;
        separateItemCapacity = true;
        group = BlockGroup.transportation;
        flags = EnumSet.of(BlockFlag.storage);
        
//        allowResupply = true;
//        alwaysReplace = true;
        
//        acceptsItems = false; // EDITED
        
//        envEnabled = Env.any;
        
//        solid = false;
//        targetable = false;
//        underBullets = true;
//        itemCapacity = 1_000_000;
        
//        customShadow = true;
//        hasShadow = false;
        
//        breakEffect = Fx.blastsmoke;
//        floating = true;
	}
	
	@Override
	public void drawBase(Tile tile) {
		defaultOre.drawBase(tile);
//		 Draw.rect(defaultOre.variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, defaultOre.variantRegions.length - 1))], tile.worldx(), tile.worldy());
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
        return defaultOre.minimapColor(tile);
    }

	public void detonateOre(Tile tile, int power) {
		if(tile.overlay() == null) return;
		if(defaultOre.itemDrop.hardness > power) return;
		tile.setNet(NewGameBlocks.itemStack);
		tile.setOverlay(Blocks.air);
		Building building = tile.build;
		if(building == null) return;
		if(building.items == null) return;
		building.items.add(defaultOre.itemDrop, Mathf.random(25, 50 + 25*(power-defaultOre.itemDrop.hardness)));
	}
	
	@Override
	public TextureRegion getDisplayIcon(Tile tile) {
		return Items.sand.fullIcon;
	}
	
	@Override
	public String getDisplayName(Tile tile) {
		return super.getDisplayName(tile) + " (" + Items.sand.localizedName + ")";
	}
	
//	public class ItemStackBuild extends StorageBuild {
//
//		@Nullable OreBlock oldOverlay = null;
//		
//		@Override
//		public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
//			Building b = super.init(tile, team, shouldAdd, rotation);
////			if(b.items != null) b.items.set(Items.scrap, 1);
//			return b;
//		}
//		
//		float angle = Mathf.random(360);
//		
//		@Override
//		public void draw() {
//			if(oldOverlay == null) return;
//	        Draw.rect(oldOverlay.variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, oldOverlay.variantRegions.length - 1))], tile.worldx(), tile.worldy(), angle);
//		}
//		
//		@Override
//		public void updateTile() {
//			if(oldOverlay != null) return;
//			if(tile.overlay() instanceof OreBlock) {
//				oldOverlay = (OreBlock) tile.overlay();
//				items.add(oldOverlay.itemDrop, Mathf.random(50, 100));
//				tile.setOverlay(Blocks.air);
//			}
//		}
//		
//		boolean ready = false;
//		
//		@Override
//		public void update() {
//			super.update();
//			if(oldOverlay == null) return;
//			if(items == null) return;
//			int total = items.total();
//			maxHealth = health = total;
//
//			if(total == 0) {
//				if(ready) tile.setBlock(Blocks.air);
//				return;
//			}
//			
//			if(ready) return;
//			
//			ready = true;
//		}
//		
//		@Override
//		public void itemTaken(Item item) {
////			super.itemTaken(item);
////			if(items == null) {
////				tile.setBlock(Blocks.air);
////				return;
////			}
////			int total = items.total();
////			maxHealth = health = total;
////			if(total == 0) tile.setBlock(Blocks.air);
//		}
//		
//		@Override
//		public void drawTeamTop() {
////			super.drawTeamTop();
//		}
//		
//		@Override
//		public void drawSelect() {
////			super.drawSelect();
//		}
//		
//		@Override
//		public void drawTeam() {
////			super.drawTeam();
//		}
//		
//		@Override
//		public void drawCracks() {
////			super.drawCracks();
//		}
//		
//		@Override
//		public void damage(float damage) {
////			super.damage(damage);
//		}
//		
//		@Override
//		public void damage(Bullet bullet, Team source, float damage) {
////			super.damage(bullet, source, damage);
//		}
//		
//		@Override
//		public void damage(float amount, boolean withEffect) {
////			super.damage(amount, withEffect);
//		}
//		
//		@Override
//		public void damage(Team source, float damage) {
////			super.damage(source, damage);
//		}
//		
//		@Override
//		public void damageContinuous(float amount) {
////			super.damageContinuous(amount);
//		}
//		
//		@Override
//		public void damageContinuousPierce(float amount) {
////			super.damageContinuousPierce(amount);
//		}
//		
//		@Override
//		public void damagePierce(float amount) {
////			super.damagePierce(amount);
//		}
//		
//		@Override
//		public void damagePierce(float amount, boolean withEffect) {
////			super.damagePierce(amount, withEffect);
//		}
//		
//		@Override
//		public boolean acceptItem(Building source, Item item) {
//			if(oldOverlay == null) return false;
//			if(item != oldOverlay.itemDrop) return false;
//			return super.acceptItem(source, item);
//		}
//		
//		@Override
//		public void overwrote(Seq<Building> previous) {
//            for(Building other : previous){
//                if(other.items != null && other.items != items){
//                    items.add(other.items);
//                }
//            }
//            items.each((i, a) -> items.set(i, Math.min(a, itemCapacity)));
//		}
//		
//		@Override
//		public String getDisplayName() {
//			if(items == null) return "";
//			if(items.first() == null) return "";
//			return items.first().localizedName;
//		}
//		
//		@Override
//		public TextureRegion getDisplayIcon() {
//			if(items == null) return block.uiIcon;
//			if(items.first() == null) return block.uiIcon;
//			return items.first().fullIcon;
//		}
//		
//    }
}
