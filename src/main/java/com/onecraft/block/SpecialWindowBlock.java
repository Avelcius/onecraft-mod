package com.onecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.GlassBlock;

// We can now extend a simpler block class like GlassBlock
public class SpecialWindowBlock extends GlassBlock {
    public SpecialWindowBlock(Settings settings) {
        super(settings);
    }
}