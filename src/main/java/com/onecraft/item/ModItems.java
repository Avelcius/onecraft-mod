package com.onecraft.item;

import com.onecraft.OneCraft;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item REMOTE_CONTROL = registerItem("remote_control", new RemoteControlItem(new FabricItemSettings()));

    private static void addItemsToItemGroup(FabricItemGroupEntries entries) {
        entries.add(REMOTE_CONTROL);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(OneCraft.MOD_ID, name), item);
    }

    public static void registerModItems() {
        OneCraft.LOGGER.info("Registering Mod Items for " + OneCraft.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(ModItems::addItemsToItemGroup);
    }
}