package agzam4.content.blocks.power;

import agzam4.content.blocks.power.LaserBlock.LaserBuild;
import agzam4.content.blocks.power.LaserMixer.LaserMixerBuild;
import agzam4.content.blocks.power.SingleOutLaser.SingleOutLaserBuild;
import arc.Core;
import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.core.UI;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import mindustry.world.meta.BlockStatus;

public class LaserResizer extends SingleOutLaser {

	public float maxLaserSize = 24 / 4f;
	public float cooldownTime = 60f;
	public float laserInOffset = 14f / 4f;
	
	public LaserResizer(String name) {
		super(name);
		rotate = true;
		enableDrawStatus = true;
        outputsPower = true;
        consumesPower = true;
		consumePowerBuffered(10);
	}
	
	@Override
	public void addLaserBar() {
    	addBar("laser-damage", (Building e) -> new Bar(
				() -> {
					LaserResizerBuild lmb = (LaserResizerBuild) e;
					if(lmb.cooldown > 0) return Core.bundle.format("bar.laser-setuping");
					return Core.bundle.format("bar.laser-damageamount", UI.formatAmount((long)(
							((LaserResizerBuild)e).laserDataOut.power
						)));
				}, 
				() -> ((LaserResizerBuild)e).laserDataOut.color, 
				() -> {
					LaserResizerBuild lmb = (LaserResizerBuild) e;
					if(lmb.cooldown > 0) return 0;
					return lmb.laserDataOut.efficiency();
				}
		));		
	}

	public class LaserResizerBuild extends SingleOutLaserBuild {

		LaserData[] laserDataIn = new LaserData[size];
		float[] lastPowerOutputs = new float[size];
		float[] cooldowns = new float[size];

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
			float power = 0, minHiddenPower = Float.MAX_VALUE;
			float hiddenPower = 0;
			float unfixablePower = 0;
			laserDataOut.color.r = 0;
			laserDataOut.color.g = 0;
			laserDataOut.color.b = 0;
//			laserDataOut.stability = 0;
			for (int i = 0; i < laserDataIn.length; i++) {
				if(laserDataIn[i] == null) continue;
				laserDataOut.color.r = Math.max(laserDataIn[i].color.r, laserDataOut.color.r);
				laserDataOut.color.g = Math.max(laserDataIn[i].color.g, laserDataOut.color.g);
				laserDataOut.color.b = Math.max(laserDataIn[i].color.b, laserDataOut.color.b);
				if(lastPowerOutputs[i] != laserDataOut.hiddenPower) {
					lastPowerOutputs[i] = laserDataOut.hiddenPower;
					cooldowns[i] = cooldownTime;
				}
				
				power += laserDataIn[i].power; // merge power
				hiddenPower += laserDataIn[i].hiddenPower;
				unfixablePower += laserDataIn[i].unfixablePower * (1f-cooldowns[i]/cooldownTime);
				minHiddenPower = Math.min(minHiddenPower, laserDataIn[i].power);
				laserDataOut.length += laserDataIn[i].length*laserDataIn[i].power;
				laserDataOut.size += laserDataIn[i].size*laserDataIn[i].power;
//				laserDataOut.stability += laserDataIn[i].stability*laserDataIn[i].power;
				for (int j = 0; j < laserDataOut.effects.length; j++) {
					laserDataOut.effects[j] += laserDataIn[i].effects[j]*laserDataIn[i].power;
				}
			}
			if(power != 0) {
				laserDataOut.length /= power;
				laserDataOut.size /= power;
//				laserDataOut.stability /= power;
				for (int j = 0; j < laserDataOut.effects.length; j++) {
					laserDataOut.effects[j] /= power;
				}
			}
			powerOutput = hiddenPower;
			laserDataOut.power(cooldown > 1 ? 0 : unfixablePower);
			if(cooldown > 0) laserDataOut.size *= Mathf.absin(2f,1f);
			laserDataOut.hiddenPower = (hasChanges ? 0 : hiddenPower);
			laserDataOut.unfixablePower = unfixablePower;
			
			float scl = laserDataOut.size == 0 ? 0 : maxLaserSize / laserDataOut.owner.block.size;
			laserDataOut.size *= scl;
			laserDataOut.power /= scl*LaserData.sizeInPower;
			
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
		public boolean absorb(LaserBuild caller, int myAngle, int offsetX, int offsetY, LaserData data) {
			return myAngle == rotate180(rotation);
		}
		
		@Override
		public void link(LaserBuild caller, int myAngle, int offsetX, int offsetY, LaserData data) {
			int index = indexOffset(myAngle, offsetX, offsetY);
			if(laserDataIn[index] != null && laserDataIn[index].owner != caller) {
				laserDataIn[index].owner.onReplaceLinkFor(this);
			}
			laserDataIn[index] = data;
			updateLaserData();
		}
		
		@Override
		public void unlink(LaserBuild caller, int myAngle, int offsetX, int offsetY) {
			int index = indexOffset(myAngle, offsetX, offsetY);
			laserDataIn[index] = null;
			updateLaserData();
		}
		
		@Override
		public float offset(int myAngle) {
			return myAngle == rotation ? laserOutOffset : laserInOffset;
		}
	}
}
