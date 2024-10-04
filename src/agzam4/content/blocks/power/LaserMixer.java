package agzam4.content.blocks.power;

import arc.Core;
import arc.math.Mathf;
import mindustry.core.UI;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import mindustry.world.meta.BlockStatus;

public class LaserMixer extends SingleOutLaser {

	public float laserInOffset = 0;
	public float cooldownTime = 60f;

	public LaserMixer(String name) {
		super(name);
		rotate = true;
		enableDrawStatus = true;
        outputsPower = true;
        consumesPower = true;
		consumePowerBuffered(10);
	}
	
	@Override
	public void init() {
		super.init();
	}

	@Override
	public void loadIcon() {
		super.loadIcon();
	}
	
	@Override
	public void addLaserBar() {
    	addBar("laser-damage", (Building e) -> new Bar(
				() -> {
					LaserMixerBuild lmb = (LaserMixerBuild) e;
					if(lmb.cooldown > 0) return Core.bundle.format("bar.laser-setuping");
					return Core.bundle.format("bar.laser-damageamount", UI.formatAmount((long)(
							((LaserMixerBuild)e).laserDataOut.power
						)));// + ((LaserMixerBuild)e).laserDataOut.effectsToString();
				}, 
				() -> ((LaserMixerBuild)e).laserDataOut.color, 
				() -> {
					LaserMixerBuild lmb = (LaserMixerBuild) e;
					if(lmb.cooldown > 0) return 0;
					return lmb.laserDataOut.efficiency();
				}
		));		
	}
	
	public class LaserMixerBuild extends SingleOutLaserBuild {

		LaserData[] laserDataIn = new LaserData[4*size];
		float[] lastPowerOutputs = new float[4*size];
		float[] cooldowns = new float[4*size];

		float lastPowerOutput = 0;
		float powerOutput = 0;
		float cooldown = 0;

		float smoothPower = 0;

		private boolean hasChanges = false;
		
		@Override
		public BlockStatus status() {
			if(hasChanges) return BlockStatus.noOutput;
			if(powerOutput <= 0) return BlockStatus.noInput;
			return BlockStatus.active;
		}
		
		@Override
		public void updateLaserData() {
			laserDataOut.power(0).size(0).length(0);
			float ws = 0;
			float hiddenPower = 0;
			float unfixablePower = 0;
			laserDataOut.color.r = 0;
			laserDataOut.color.g = 0;
			laserDataOut.color.b = 0;
			for (int j = 0; j < laserDataOut.effects.length; j++) {
				laserDataOut.effects[j] = 0;
			}
			for (int i = 0; i < laserDataIn.length; i++) {
				if(laserDataIn[i] == null) continue;
				laserDataOut.color.r = Math.max(laserDataIn[i].color.r, laserDataOut.color.r);
				laserDataOut.color.g = Math.max(laserDataIn[i].color.g, laserDataOut.color.g);
				laserDataOut.color.b = Math.max(laserDataIn[i].color.b, laserDataOut.color.b);
				if(lastPowerOutputs[i] != laserDataOut.hiddenPower) {
					lastPowerOutputs[i] = laserDataOut.hiddenPower;
					cooldowns[i] = cooldownTime;
				}
				float w = laserDataIn[i].power / laserDataIn[i].owner.block.size;
				ws += w;
				hiddenPower += laserDataIn[i].hiddenPower / laserDataIn[i].owner.block.size;
				unfixablePower += laserDataIn[i].unfixablePower * (1f-cooldowns[i]/cooldownTime) / laserDataIn[i].owner.block.size;
				laserDataOut.length += laserDataIn[i].length*w;
				laserDataOut.size += laserDataIn[i].size*w;
				for (int j = 0; j < laserDataOut.effects.length; j++) {
					laserDataOut.effects[j] += laserDataIn[i].effects[j]*w;
				}
			}
			if(ws != 0) {
				laserDataOut.length /= ws;
				laserDataOut.size /= ws;
				for (int j = 0; j < laserDataOut.effects.length; j++) {
					laserDataOut.effects[j] /= ws;
				}
			}
			powerOutput = hiddenPower;
			laserDataOut.power(cooldown > 0 ? 1 : unfixablePower);
			if(cooldown > 0) laserDataOut.size *= Mathf.absin(2f,1f);
			laserDataOut.hiddenPower = (hasChanges ? 0 : hiddenPower);
			laserDataOut.unfixablePower = unfixablePower;
			if(!shooting) {
				laserDataOut.power(0,0,0).size(0).length(0);
			}
			laserDataOut.disperse(dst);
		}

		@Override
		public void onRotate(int lastRotate) {
			updateValid();
			updateLaserData();
		}
		
		@Override
		public void draw() {
			super.draw();
		}
		
		@Override
		public void updateTile() {
			super.updateTile();
			hasChanges = false;//Mathf.approach(cooldown, -cooldownTime, edelta());
			if(lastPowerOutput != powerOutput) {
				lastPowerOutput = powerOutput;
				hasChanges = true;
			}
			if(hasChanges) {
				cooldown = cooldownTime;//Mathf.approach(cooldown, 1, cooldown);
			} else {
				cooldown = Mathf.approach(cooldown, 0, delta());
			}
			for (int i = 0; i < cooldowns.length; i++) {
				cooldowns[i] = Mathf.approach(cooldowns[i], 0, delta());
			}
		}
		
		@Override
		public void link(LaserBuild caller, int myAngle, int offsetX, int offsetY, LaserData data) {
			int index = myAngle*size + indexOffset(myAngle, offsetX, offsetY);
			for (int i = 0; i < caller.block.size; i++) {
				if(laserDataIn[index + i] != null && laserDataIn[index + i].owner != caller) {
					laserDataIn[index + i].owner.onReplaceLinkFor(this);
				}
				laserDataIn[index + i] = data;
			}
			updateLaserData();
		}

		@Override
		public void unlink(LaserBuild caller, int myAngle, int offsetX, int offsetY) {
			int index = myAngle*size + indexOffset(myAngle, offsetX, offsetY);
			for (int i = 0; i < caller.block.size; i++) {
				laserDataIn[index+i] = null;
			}
			updateLaserData();
		}
		
		@Override
		public float offset(int myAngle) {
			return myAngle == rotation ? laserOutOffset : laserInOffset;
		}
	}
	
}
