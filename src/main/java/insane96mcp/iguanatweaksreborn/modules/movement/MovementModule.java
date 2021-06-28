package insane96mcp.iguanatweaksreborn.modules.movement;

import insane96mcp.iguanatweaksreborn.modules.movement.feature.NoPillaringFeature;
import insane96mcp.iguanatweaksreborn.modules.movement.feature.WeightedArmorFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Movement")
public class MovementModule extends Module {

	public WeightedArmorFeature weightedArmor;
	public NoPillaringFeature noPillaring;

	public MovementModule() {
		super(Config.builder);
		pushConfig(Config.builder);
		weightedArmor = new WeightedArmorFeature(this);
		noPillaring = new NoPillaringFeature(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		weightedArmor.loadConfig();
		noPillaring.loadConfig();
	}

}
