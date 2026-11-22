package com.camperscrub.skillcraft.xp;

public record LinearTailSettings(int maxLevel, long base, double slope, double tailSize, double tailDoubling) {

    public static final LinearTailSettings DEFAULTS = new LinearTailSettings(999, 2000, 17, 55, 111);

    // A "compact constructor": runs before the fields are set, so it can reject bad values.
    public LinearTailSettings {
        if (maxLevel < 1) throw new IllegalArgumentException("maxLevel must be at least 1, got " + maxLevel);
        if (base < 1) throw new IllegalArgumentException("base must be at least 1, got " + base);
        if (slope < 0) throw new IllegalArgumentException("slope cannot be negative, got " + slope);
        if (tailSize < 0) throw new IllegalArgumentException("tailSize cannot be negative, got " + tailSize);
        if (tailDoubling <= 0) throw new IllegalArgumentException("tailDoubling must be positive, got " + tailDoubling);
    }
}
