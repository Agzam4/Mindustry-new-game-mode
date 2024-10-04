package agzam4.content.blocks.life;

import static mindustry.Vars.tilesize;

import agzam4.Work;
import agzam4.content.bullets.SporeSeedBulletType;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.Rand;
import arc.struct.EnumSet;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.bullet.BulletType;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

public class LifeReactor extends PowerGenerator {

    public final int timerFuel = timers++;
    public static Rand rand = new Rand();
    
    public @Nullable ConsumeLiquid booster;
    public float boosterMultiplier = 2f;

    public Color lightColor = Color.valueOf("7f19ea");
    public Color coolColor = new Color(1, 1, 1, 0f);
    public Color hotColor = Color.valueOf("f375ffa3");
    
	public float essenceCapacity = 100;
    /** ticks to consume 1 fuel */
    public float itemDuration = 120;
    /** heating per frame * fullness */
    public float heating = 0.01f;
    /** threshold at which block starts smoking */
    public float smokeThreshold = 0.3f;
    /** heat threshold at which lights start flashing */
    public float flashThreshold = 0.1f;

    /** heat removed per unit of coolant */
    public float coolantPower = 0.1f;

    public Item fuelItem = Items.sporePod;

    public TextureRegion glowRegion, glowLightRegion, heatRegion;
    
    BulletType spore = new SporeSeedBulletType() {{
        tier = 2;
        damage = 1250f;
    }};
	
	public LifeReactor(String name) {
		super(name);
        itemCapacity = 30;
        liquidCapacity = 30;
        hasItems = true;
        hasLiquids = true;
        rebuildable = false;
        flags = EnumSet.of(BlockFlag.reactor, BlockFlag.generator);
        schematicPriority = -5;
        envEnabled = Env.any;

        explosionShake = 6f;
        explosionShakeDuration = 16f;

        explosionRadius = 10;
        explosionDamage = 1250 * 4;

        explodeEffect = Fx.reactorExplosion;
        explodeSound = Sounds.explosionbig;
	}
	
	@Override
	public void setStats() {
		super.setStats();
        if(booster != null){
            stats.remove(Stat.booster);
            stats.add(Stat.booster, StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(), booster.amount, boosterMultiplier , true, l -> l == booster.liquid));
        }
        float maxUsed = 1f * heating * Math.min(1f, 4f) / coolantPower;
        stats.add(Stat.input, StatValues.string(Core.bundle.format("stat.life-essence-input", 
        		Strings.autoFixed(maxUsed*60f, 2) + StatUnit.perSecond.localized())));
	}
	
	@Override
	public void init() {
		super.init();
        if(booster == null){
        	booster = findConsumer(c -> c instanceof ConsumeLiquid);
        }
        if(booster != null){
        	booster.update = false;
        	booster.booster = true;
        	booster.optional = true;
            if(!hasConsumer(booster)) consume(booster);
        }
	}

	@Override
	public void loadIcon() {
		super.loadIcon();
		glowRegion = Work.texture(name + "-glow");
		glowLightRegion = Work.texture(name + "-glow-light");
		heatRegion = Work.texture(name + "-heat");
	}

	@Override
	public void setBars() {
		super.setBars();
		LifeEssenceBuild.setBars(this);
	}
	
    public class LifeReactorBuild extends GeneratorBuild implements LifeEssenceBuild {

        public float essence;
		private float heat;

        @Override
        public void updateTile(){
        	float scale = 1f;
            if(booster != null){
                if(booster.efficiency(this) > 0){
                	booster.update(this);
                	scale = boosterMultiplier;
                }
            }
        	
            int fuel = items.get(fuelItem);
            float fullness = (float)fuel / itemCapacity;
            productionEfficiency = fullness*scale;

            if(fuel > 0 && enabled){
                heat += fullness * heating * Math.min(delta(), 4f) * scale / 10f;

                if(timer(timerFuel, itemDuration / timeScale)){
                    consume();
                }
            }else{
                productionEfficiency = 0f;
            }

            if(heat > 0){
                float maxUsed = Math.min(essence(), heat * scale / coolantPower);
                heat -= maxUsed * coolantPower / 10f;
                essence -= maxUsed;
            }

            if(heat > smokeThreshold){
                float smoke = 1.0f + (heat - smokeThreshold) / (1f - smokeThreshold); //ranges from 1.0 to 2.0
                if(Mathf.chance(smoke / 20.0 * delta())){
                    Fx.reactorsmoke.at(x + Mathf.range(size * tilesize / 2f),
                    y + Mathf.range(size * tilesize / 2f));
                }
            }

            heat = Mathf.clamp(heat);

            if(heat >= 0.999f){
                Events.fire(Trigger.thoriumReactorOverheat);
                kill();
            }
        }
        
		@Override
		public float essence() {
			return essence;
		}

		@Override
		public float essenceCapacity() {
			return essenceCapacity;
		}

		@Override
		public LifeEssenceBuild essence(float essence) {
			this.essence = essence;
			return this;
		}
		
        public float flash, essenceflash;
    	
		@Override
		public void draw() {
            super.draw();
            essenceflash += Time.delta;
            LifeEssenceStorageBlock.drawEssenceLight(this, this, glowRegion, glowLightRegion, essenceflash);

            Draw.color(coolColor, hotColor, heat);
            Fill.rect(x, y, size * tilesize, size * tilesize);

            if(heat > flashThreshold){
                flash += (1f + ((heat - flashThreshold) / (1f - flashThreshold)) * 5.4f) * Time.delta;
                Draw.color(Pal.spore);
                Draw.alpha(Mathf.absin(flash, 9f, 1f));
                Drawf.additive(heatRegion, Draw.getColor(), x, y);
            }

            Draw.reset();
        
		}
		
		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(essence);
		}
		
		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			essence = read.f();
		}
		
		@Override
		public void kill() {
			rand.setSeed(pos());
			for (int i = 0; i < 6 + items.get(fuelItem); i++) {
				spore.createNet(team, x, y, rand.nextInt(360), 0, rand.nextFloat()*.5f + .5f, rand.nextFloat()*.1f + .9f);
			}
			super.kill();
		}
		
    }
}
