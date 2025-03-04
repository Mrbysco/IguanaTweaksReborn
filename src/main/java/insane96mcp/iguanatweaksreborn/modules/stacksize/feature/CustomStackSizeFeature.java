package insane96mcp.iguanatweaksreborn.modules.stacksize.feature;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.modules.stacksize.classutils.CustomStackSize;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Custom Stack Size Feature", description = "Change stack sizes as you please")
public class CustomStackSizeFeature extends Feature {

    private final ForgeConfigSpec.ConfigValue<List<? extends String>> customStackListConfig;
    public List<CustomStackSize> customStackList;

    public CustomStackSizeFeature(Module module) {
        super(Config.builder, module);
        Config.builder.comment(this.getDescription()).push(this.getName());
        customStackListConfig = Config.builder
                .comment("Define custom item stack sizes, one string = one item/tag. Those items are not affected by other changes such as 'Food Stack Reduction'.\nThe format is modid:itemid,stack_size or #modid:tagid,stack_size\nE.g. 'minecraft:stone,16' will make stone stack up to 16.\nE.g. '#forge:stone,16' will make all the stone types stack up to 16.\nValues over 64 or lower than 1 will not work.")
                .defineList("Custom Stack Sizes", Lists.newArrayList(), o -> o instanceof String);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();

        customStackList = parseCustomStackList(customStackListConfig.get());
    }

    @SubscribeEvent
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        processCustomStackSizes();
    }

    private ArrayList<CustomStackSize> parseCustomStackList(List<? extends String> list) {
        ArrayList<CustomStackSize> stackSizes = new ArrayList<>();
        for (String line : list) {
            CustomStackSize customStackSize = CustomStackSize.parseLine(line);
            if (customStackSize != null)
                stackSizes.add(customStackSize);
        }
        return stackSizes;
    }

    private boolean processed = false;

    public void processCustomStackSizes() {
        if (!this.isEnabled())
            return;
        if (customStackList.isEmpty())
            return;
        if (processed)
            return;

        processed = true;

        for (CustomStackSize customStackSize : customStackList) {
            if (customStackSize.tag != null) {
                ITag<Item> tag = ItemTags.getCollection().get(customStackSize.tag);
                if (tag == null)
                    continue;
                tag.getAllElements().forEach(item -> {
                    item.maxStackSize = MathHelper.clamp(customStackSize.stackSize, 1, 64);
                });
            }
            else if (customStackSize.id != null) {
                Item item = ForgeRegistries.ITEMS.getValue(customStackSize.id);
                item.maxStackSize = MathHelper.clamp(customStackSize.stackSize, 1, 64);
            }
        }
    }
}
