package agzam4.content.blocks;

import agzam4.Work;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Nullable;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.ItemStack;
import mindustry.world.blocks.production.GenericCrafter;

public class CopperSeparator extends GenericCrafter {

	@Nullable TextureRegion rotatorRegion, topRegion;
    public float rotateSpeed = 5f;
	
	public CopperSeparator(String name) {
		super(name);
        craftEffect = Fx.pulverizeMedium;
        outputItem = new ItemStack(Items.copper, 10);
        craftTime = 120f;
        size = 2;
        itemCapacity = 50;
        hasItems = true;
        consumeItem(Items.scrap, 1);
	}

	@Override
	public void loadIcon() {
		super.loadIcon();
		rotatorRegion = Work.texture(name + "-rotator");
		topRegion = Work.texture(name + "-top");
	}

    @Override
    public TextureRegion[] icons(){
//    	super.icons();
        return new TextureRegion[]{region, rotatorRegion, topRegion};
    }

	public class CopperSeparatorBuild extends GenericCrafterBuild {

		public float rot, rotSpeed;

		@Override
		public void updateTile() {
			super.updateTile();
			

			rot += efficiency*rotSpeed;
    		
    		if(efficiency > 0) {
    			if(rotSpeed < 1) rotSpeed += .01f;
    			else rotSpeed = 1;
    			return;
    		}
			if(rotSpeed > 0) rotSpeed -= .001f;
			else rotSpeed = 0;
		}

		@Override
		public void draw() {
	            Draw.rect(region, x, y);
	            Draw.z(Layer.blockCracks);
	            drawCracks();

	            Draw.z(Layer.blockAfterCracks);
//	            if(drawRim){
//	                Draw.color(heatColor);
//	                Draw.alpha(warmup * ts * (1f - s + Mathf.absin(Time.time, 3f, s)));
//	                Draw.blend(Blending.additive);
//	                Draw.rect(rimRegion, x, y);
//	                Draw.blend();
//	                Draw.color();
//	            }

	            Drawf.spinSprite(rotatorRegion, x, y, rot * rotateSpeed);

	            Draw.rect(topRegion, x, y);
		 }
	 }
}
