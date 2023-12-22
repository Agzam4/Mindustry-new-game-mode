package agzam4.content.units;

import static mindustry.Vars.iconMed;
import static mindustry.Vars.net;
import static mindustry.Vars.state;

import arc.Core;
import arc.func.Prov;
import arc.graphics.Color;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import mindustry.ai.types.LogicAI;
import mindustry.content.Blocks;
import mindustry.entities.abilities.Ability;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.ui.Bar;

public class MyUnitType extends UnitType {
	
	public enum ConstructorUnitTypes {

		flying(UnitEntity::create),
		mech(MechUnit::create),
		legs(LegsUnit::create),
		naval(UnitWaterMove::create),
		payload(PayloadUnit::create),
		missile(TimedKillUnit::create),
		tank(TankUnit::create),
		hover(ElevationMoveUnit::create),
		tether(BuildingTetherPayloadUnit::create),
		crawl(CrawlUnit::create),
		
		
		placeholder(UnitEntity::create);

		Prov<Unit> prov;
		
		ConstructorUnitTypes(Prov<Unit> prov) {
			this.prov = prov;
		}
	}

	public MyUnitType(String name, ConstructorUnitTypes type) {
		super(name);
		constructor = type.prov;
	}
	
	public void display(Unit unit, Table table){
        table.table(t -> {
            t.left();
            t.add(new Image(uiIcon)).size(iconMed).scaling(Scaling.fit);
            t.labelWrap(unit.isPlayer() ? unit.getPlayer().coloredName() + "\n[lightgray]" + localizedName : localizedName).left().width(190f).padLeft(5);
        }).growX().left();
        table.row();

        table.table(bars -> {
            bars.defaults().growX().height(20f).pad(4);
            
            setBars(unit, bars);
            //TODO overlay shields
            bars.add(new Bar("stat.health", Pal.health, unit::healthf).blink(Color.white));
            bars.row();

            if(state.rules.unitAmmo){
                bars.add(new Bar(ammoType.icon() + " " + Core.bundle.get("stat.ammo"), ammoType.barColor(), () -> unit.ammo / ammoCapacity));
                bars.row();
            }

            for(Ability ability : unit.abilities){
                ability.displayBars(unit, bars);
            }

            if(payloadCapacity > 0 && unit instanceof Payloadc){
            	Payloadc payload = (Payloadc) unit;
                bars.add(new Bar("stat.payloadcapacity", Pal.items, () -> payload.payloadUsed() / unit.type().payloadCapacity));
                bars.row();

                float[] count = new float[]{-1};
                bars.table().update(t -> {
                    if(count[0] != payload.payloadUsed()){
                        payload.contentInfo(t, 8 * 2, 270);
                        count[0] = payload.payloadUsed();
                    }
                }).growX().left().height(0f).pad(0f);
            }
        }).growX();

        if(unit.controller() instanceof LogicAI){
        	LogicAI ai = (LogicAI) unit.controller();
            table.row();
            table.add(Blocks.microProcessor.emoji() + " " + Core.bundle.get("units.processorcontrol")).growX().wrap().left();
            if(ai.controller != null && (Core.settings.getBool("mouseposition") || Core.settings.getBool("position"))){
                table.row();
                table.add("[lightgray](" + ai.controller.tileX() + ", " + ai.controller.tileY() + ")").growX().wrap().left();
            }
            table.row();
            table.label(() -> Iconc.settings + " " + (long)unit.flag + "").color(Color.lightGray).growX().wrap().left();
            if(net.active() && ai.controller != null && ai.controller.lastAccessed != null){
                table.row();
                table.add(Core.bundle.format("lastaccessed", ai.controller.lastAccessed)).growX().wrap().left();
            }
        }else if(net.active() && unit.lastCommanded != null){
            table.row();
            table.add(Core.bundle.format("lastcommanded", unit.lastCommanded)).growX().wrap().left();
        }

        table.row();
    }

	public void setBars(Unit unit, Table bars) {
		// for @Override
	}

//    private Prov<Unit> unitType(String type){
//        return switch(type){
//            case "flying" -> UnitEntity::create;
//            case "mech" -> MechUnit::create;
//            case "legs" -> LegsUnit::create;
//            case "naval" -> UnitWaterMove::create;
//            case "payload" -> PayloadUnit::create;
//            case "missile" -> TimedKillUnit::create;
//            case "tank" -> TankUnit::create;
//            case "hover" -> ElevationMoveUnit::create;
//            case "tether" -> BuildingTetherPayloadUnit::create;
//            case "crawl" -> CrawlUnit::create;
//            default -> UnitEntity::create;//throw new RuntimeException("Invalid unit type: '" + type + "'. Must be 'flying/mech/legs/naval/payload/missile/tether/crawl'.");
//        };
//    }
}
