package agzam4.content.planets;

import mindustry.game.Rules;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Sector;

public class NgSerpuloPlanetGenerator extends SerpuloPlanetGenerator {

	
	@Override
	public void addWeather(Sector sector, Rules rules) {
		if(sector.preset != null) {
			rules.weather = sector.preset.generator.map.rules().weather;
			return;
		}
		super.addWeather(sector, rules);
	}
}
