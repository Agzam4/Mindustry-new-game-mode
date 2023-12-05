package agzam4.content.blocks;

import static mindustry.Vars.content;

import agzam4.StacksIndexer;
import agzam4.Work;
import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.LightRenderer;
import mindustry.type.Item;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.defense.turrets.ReloadTurret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class Attractor extends ReloadTurret {

    public final int timerTarget = timers++;
    public float retargetTime = 5f;

    public TextureRegion topRegion, baseRegion;

    public Color color = Color.white;
    public Effect beamEffect = Fx.pointBeam;
    public Effect hitEffect = Fx.pointHit;
    public Effect shootEffect = Fx.sparkShoot;

    public Sound shootSound = Sounds.lasershoot;

    public float shootCone = 5f;
    public float bulletDamage = 10f;
    public float shootLength = 3f;
    
	public Attractor(String name) {
		super(name);
        rotateSpeed = 5f;
        reload = 30f;

        coolantMultiplier = 2f;
        
        itemCapacity = 10;
        hasItems = true;
        
        configurable = true;
        saveConfig = true;
        clearOnDoubleTap = true;
        
        unloadable = false;
//        squareSprite = false;
        config(Item.class, (AttractorBuild build, Item item) -> build.selected = item);
        configClear((AttractorBuild build) -> build.selected = null);
	}

	@Override
	public void loadIcon() {
		super.loadIcon();
		topRegion = Work.texture(name + "-top");
		baseRegion = Core.atlas.find("block-" + size);
	} 
	
	@Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.reload, 60f / reload, StatUnit.perSecond);
    }

    public class AttractorBuild extends ReloadTurretBuild {
    	
        public @Nullable Building target;
        public @Nullable Item selected;

        @Override
        public void updateTile() {

            if(timer(timerTarget, retargetTime)) {
            	if(selected != null)
            	target = StacksIndexer.findClosestOreInRange2(this, selected, range*range);
//                target = Groups.bullet.intersect(x - range, y - range, range*2, range*2).min(b -> b.team != team && b.type().hittable, b -> b.dst2(this));
            }

            dumpAccumulate();
            
            if(selected == null) return;
            if(itemCapacity - items.get(selected) <= 0) return;
            
            //pooled bullets
            if(target != null && !target.isAdded()) {
                target = null;
            }

            if(coolant != null) {
                updateCooling();
            }
            
            if(target == null) return;
            if(target.items == null) {
            	target = null;
                return;
            }
            	
            //look at target
            float dest = angleTo(target);
            rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta());
            reloadCounter += edelta();

            //shoot when possible
            if(Angles.within(rotation, dest, shootCone) && reloadCounter >= reload) {
            	int amount = target.items.get(selected);
            	int take = Math.min(amount, itemCapacity - items.get(selected));
            	Call.setItem(target, selected, amount-take); 
            	Call.setItem(this, selected, items.get(selected)+take);

            	Tmp.v1.trns(rotation, shootLength);

            	beamEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color, new Vec2().set(target));
            	shootEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color);
            	hitEffect.at(target.x, target.y, color);
            	shootSound.at(x + Tmp.v1.x, y + Tmp.v1.y, Mathf.random(0.9f, 1.1f));
            	reloadCounter = 0;
            }
        }

        @Override
        public boolean shouldConsume(){
            return super.shouldConsume() && target != null;
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);
            Draw.rect(region, x, y, rotation - 90);
            if(selected != null) {
                Draw.color(selected.color);
                Draw.rect(topRegion, x, y, rotation - 90);
                Draw.color();
            }
//            LightRenderer
        }

        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(Attractor.this, table, content.items(), () -> selected, this::configure);
        }

        @Override
        public Object config(){
            return selected;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(rotation);
            write.s(selected == null ? -1 : selected.id);
//            write.bool(stale);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            rotation = read.f();
            selected = Vars.content.item(read.s());
//            stale = read.bool();
        }
    }
}
