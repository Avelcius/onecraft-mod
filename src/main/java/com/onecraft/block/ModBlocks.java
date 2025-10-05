package com.onecraft.block;

import com.onecraft.OneCraft;
import com.onecraft.item.ModItems;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block SPECIAL_WINDOW = registerBlock("special_window",
            new SpecialWindowBlock(FabricBlockSettings.copyOf(Blocks.GLASS).nonOpaque()));
    public static final Block LOCKED_DOOR = registerBlock("locked_door",
            new LockedDoorBlock(FabricBlockSettings.copyOf(Blocks.OAK_DOOR)));
    public static final Block COMPUTER_BLOCK = registerBlock("computer",
            new ComputerBlock(FabricBlockSettings.copyOf(Blocks.STONE)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(OneCraft.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(OneCraft.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        OneCraft.LOGGER.info("Registering Mod Blocks for " + OneCraft.MOD_ID);
    }
}