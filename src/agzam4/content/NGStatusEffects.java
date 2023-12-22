package agzam4.content;

import mindustry.gen.Unit;
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
