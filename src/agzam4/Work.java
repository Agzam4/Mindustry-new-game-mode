package agzam4;

import static mindustry.Vars.world;

import arc.Core;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Tmp;
import mindustry.content.Liquids;
import mindustry.world.Block;
import mindustry.world.Tile;

public class Work {

	public static boolean validPlaceItem(Block type, int x, int y, int rotation){
//        if((type.solid || type.solidifes) && Units.anyEntities(x * tilesize + type.offset - type.size*tilesize/2f, y * tilesize + type.offset - type.size*tilesize/2f, type.size * tilesize, type.size*tilesize)){
//            return false;
//        }

        Tile tile = world.tile(x, y);

        if(tile == null) return false;
        if(tile.block() == type) return false;
        
        if(!type.requiresWater && !contactsShallows(tile.x, tile.y, type) && !type.placeableLiquid){
            return false;
        }


        int offsetx = -(type.size - 1) / 2;
        int offsety = -(type.size - 1) / 2;

        for(int dx = 0; dx < type.size; dx++){
            for(int dy = 0; dy < type.size; dy++){
                int wx = dx + offsetx + tile.x, wy = dy + offsety + tile.y;
                Tile check = world.tile(wx, wy);
                if(
                check == null || //nothing there
                (check.floor().isDeep() && !type.floating && !type.requiresWater && !type.placeableLiquid) || //deep water
                (type == check.block() && check.build != null && rotation == check.build.rotation && type.rotate) || //same block, same rotation
                !check.floor().placeableOn || //solid wall
                (!check.block().alwaysReplace) || //replacing a block that should be replaced (e.g. payload placement)
                    !((type.canReplace(check.block())) && //can replace type
                    type.bounds(tile.x, tile.y, Tmp.r1).grow(0.01f).contains(check.block().bounds(check.centerX(), check.centerY(), Tmp.r2))) || //no replacement
                (type.requiresWater && check.floor().liquidDrop != Liquids.water) //requires water but none found
                ) return false;
            }
        }
        return true;
    }

    public static boolean contactsShallows(int x, int y, Block block){
        if(block.isMultiblock()){
            for(Point2 point : block.getInsideEdges()){
                Tile tile = world.tile(x + point.x, y + point.y);
                if(tile != null && !tile.floor().isDeep()) return true;
            }

            for(Point2 point : block.getEdges()){
                Tile tile = world.tile(x + point.x, y + point.y);
                if(tile != null && !tile.floor().isDeep()) return true;
            }
        }else{
            for(Point2 point : Geometry.d4){
                Tile tile = world.tile(x + point.x, y + point.y);
                if(tile != null && !tile.floor().isDeep()) return true;
            }
            Tile tile = world.tile(x, y);
            return tile != null && !tile.floor().isDeep();
        }
        return false;
    } 

    public static TextureRegion texture(String name) {
    	return Core.atlas.find(name);
	}

    public static TextureRegion texture(String name, String def) {
    	return Core.atlas.find(name, def);
	}

    public static TextureRegion modTexture(String name) {
		return Core.atlas.find(/* NewGameMod.mod.name +*/NewGameMod.prefix + name);
	}
   
}
