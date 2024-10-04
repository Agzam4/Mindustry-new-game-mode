package agzam4.content.blocks.power;

import agzam4.content.blocks.power.LaserBlock.LaserBuild;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.gen.Bullet;
import mindustry.type.StatusEffect;

public class LaserData {

	public static final Color laserDefaultColor = Color.valueOf("85A9F2");

	public static float maxLaserRange = Vars.tilesize*25, dispersion = .1f;

	public static float sizeInPower = .5f;
			
	public final LaserBuild owner;
	public float power;
	public float hiddenPower;
	public float unfixablePower; // increasing up to infinity in circles
	public float size;
	public float length;
	public Color color = laserDefaultColor.cpy();
//	public float r = laserDefaultColor.r, g = laserDefaultColor.g, b = laserDefaultColor.b;

	public float effects[] = new float[Vars.content.statusEffects().size];
	
	public float efficiency = 1;
//	public float stability = 1;
	
	public LaserData(LaserBuild owner) {
		this.owner = owner;
	}
	
	public int chainSize = 0, sourceId = -1;
	
	public void chainData(int chainSize, int sourceId) {
		this.chainSize = chainSize;
		this.sourceId = sourceId;
	}

	public LaserData copyStats(@Nullable LaserData data) {
		if(data == null) {
			power = 0;
			size = 0;
			length = 0;
			power = 0;
			hiddenPower = 0;
			unfixablePower = 0;
//			stability = 1;
			color.set(laserDefaultColor);
			for (int i = 0; i < effects.length; i++) effects[i] = 0;
			return this;
		}
		power = data.power;
		size = data.size;
		length = data.length;
		power = data.power;
		hiddenPower = data.hiddenPower;
		unfixablePower = data.unfixablePower;
//		stability = data.stability;
		color.set(color);
		for (int i = 0; i < effects.length; i++) effects[i] = data.effects[i];
		return this;
	}

	public LaserData power(float power) {
		this.power = power;
		return this;
	}

	public LaserData power(float power, float hiddenPower, float unfixablePower) {
		this.power = power;
		this.hiddenPower = hiddenPower;
		this.unfixablePower = unfixablePower;
		return this;
	}

	public LaserData size(float size) {
		this.size = size;
		return this;
	}

	public LaserData length(float length) {
		this.length = length;
		return this;
	}
	
	public float efficiency() {
		if(power <= 0) return 0;
		return efficiency;
	}
	
	@Override
	public String toString() {
		return Strings.format("p: @ s: @ l: @", power, size, length);
	}

	public LaserData disperse(int dst) {
//		float disperse = (dst*Vars.tilesize-length)*dispersion;
//		if(disperse < 0) return;
		if(length == maxLaserRange || dst*Vars.tilesize <= length) {
			efficiency = 1;
			return this;
		}
		efficiency = rangeEfficiency(length, dst);
		if(efficiency < 0) {
			efficiency = 0;
			size = 0;
		}
		power *= efficiency;
		return this;
	}

	public static float rangeEfficiency(float length, int dst) {
		if(length == maxLaserRange) return 1;
		if(dst*Vars.tilesize <= length) return 1;
		return 1f - (dst*Vars.tilesize - length)/(maxLaserRange - length);
	}

	public void apply(StatusEffect statusEffect, float power) {
		effects[statusEffect.id] = power;
		float allp = 0;
		for(int i = 0; i < effects.length; i++) {
			allp += effects[i];
		}
		if(allp == 0) return;
		for (int i = 0; i < effects.length; i++) {
			effects[i] /= allp;
		}
	}

	public String effectsToString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < effects.length; i++) {
			if(effects[i] <= 0) continue;
			builder.append(' ');
			builder.append(Mathf.round(effects[i]*100));
			builder.append(Vars.content.statusEffects().get(i).emoji());
		}
		return builder.toString();
	}
}
