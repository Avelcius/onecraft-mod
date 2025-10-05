package com.onecraft.util;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum CodeColor {
    RED(Formatting.RED, "Red"),
    GREEN(Formatting.GREEN, "Green"),
    BLUE(Formatting.BLUE, "Blue"),
    YELLOW(Formatting.YELLOW, "Yellow");

    private final Formatting formatting;
    private final String name;

    CodeColor(Formatting formatting, String name) {
        this.formatting = formatting;
        this.name = name;
    }

    public Formatting getFormatting() {
        return this.formatting;
    }

    public Text asText() {
        return Text.literal(this.name).formatted(this.formatting);
    }

    public CodeColor getNext() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public CodeColor getPrevious() {
        return values()[(this.ordinal() - 1 + values().length) % values().length];
    }
}