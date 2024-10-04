package agzam4.content;

import agzam4.content.effects.NGFx;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;

public class NGStatusEffects {

	public static StatusEffect disintegration, test;
	
	public static void load() {
		disintegration = new StatusEffect("disintegration") {{
			
			}
			@Override
			public void update(Unit unit, float time) {
				super.update(unit, time);
				unit.maxHealth(unit.health);
			}
			@Override
			public void applied(Unit unit, float time, boolean extend) {
				NGFx.crused.at(unit.x(), unit.y(), unit.hitSize, Pal.spore);
				super.applied(unit, time, extend);
			}
		};
		// StatusEffects
//		test = new StatusEffect("none") {{
//
//			draw(null);
//		}
//		@Override
//		public void update(Unit unit, float time) {
//			super.update(unit, time);
//		}
//		};
	}
}
