package agzam4.content.blocks.power;

import agzam4.Work;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.util.Nullable;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.type.Item;
import mindustry.type.StatusEffect;
import mindustry.ui.ItemImage;
import mindustry.ui.Styles;
import mindustry.world.consumers.ConsumeItemFilter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class LaserEffects extends SingleOutLaser {

    public ObjectMap<Item, StatusEffect> effectsTypes = new OrderedMap<>();
    public float[] itemDurations = new float[Vars.content.items().size];
    public StatusEffect[] effects = new StatusEffect[Vars.content.items().size];
	public TextureRegion topRegion;
    
	public LaserEffects(String name) {
		super(name);
		hasItems = true;
	}
	
	@Override
	public void loadIcon() {
		super.loadIcon();
		topRegion = Work.texture(name + "-top");
	}
	
	@Override
	public void setStats() {
		super.setStats();
		stats.remove(Stat.input);
		stats.add(Stat.input, table -> {
			table.row();
			for (int i = 0; i < effects.length; i++) {
				if(effects[i] == null) continue;
				StatusEffect effect = effects[i];
				Item item = Vars.content.items().get(i);
				float duration = itemDurations[i];
                table.table(Styles.grayPanel, it -> {
                    it.left().top().defaults().padRight(3).left();
                    it.table(title -> {
                    	title.image(effect.uiIcon).size(32f).padRight(4).right().scaling(Scaling.fit).top();
                    	title.add(effect.localizedName + "\n[lightgray]100%").width(100).style(Styles.outlineLabel).padRight(10).left().top();
                    });
                    it.table(itemDisplay -> {
                    	itemDisplay.add(new ItemImage(item.uiIcon, 0)).right();
                    	itemDisplay.add(item.localizedName + "\n[lightgray]" + Strings.autoFixed((60f / duration), 2) + StatUnit.perSecond.localized())
                    	.padLeft(2).padRight(0).style(Styles.outlineLabel).right().grow().pad(10f).padRight(0);
                    }).right().grow().pad(0).padRight(0).padLeft(50f);
                }).padLeft(0).padTop(5).padBottom(5).growX().margin(10);
                table.row();
			}
		});
	}
	
    public void effect(Item item, float itemDuration, StatusEffect effect){
    	itemDurations[item.id] = itemDuration;
    	effects[item.id] = effect;
    }
    
    public void consume() {
    	consume(new ConsumeItemFilter(i -> effects[i.id] != null));
    }
	
	public class LaserEffectsBuild extends SingleOutLaserBuild {
		
		@Nullable LaserData laserDataIn = null;

		@Override
		public void updateLaserData() {
			laserDataOut.copyStats(shooting ? laserDataIn : null).disperse(dst);
			if(consumed != null) {
				laserDataOut.apply(effects[consumed.id], efficiency);
				laserDataOut.color.lerp(effects[consumed.id].color, efficiency);
				laserDataOut.color.value(1f);
			}
		}
		
		@Nullable Item consumed = null;
		float itemDuration = 0;
		
		@Override
		public void updateTile() {
			super.updateTile();

			if(!hasItems) {				
				return;
			}
			
			if(itemDuration <= 0) {
				consumed = items.first();
				if(consumed != null) {
					itemDuration = itemDurations[consumed.id];
					for (int i = 0; i < 4; i++) {
						effects[consumed.id].effect.at(
								x + Geometry.d8edge[i].x*size*21/8f, 
								y + Geometry.d8edge[i].y*size*21/8f, consumed.color);
					}
//					Fx.vapor.at(x + Mathf.range(3f), y + Mathf.range(3f), consumed.color);
					consume();
				}
			}
			itemDuration = Mathf.approach(itemDuration, 0, edelta()*efficiency);
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
			if(laserDataOut.size > 0) {
				Draw.color(laserDataOut.color);
				Draw.rect(topRegion, x, y);
			}
		}
	}
}
