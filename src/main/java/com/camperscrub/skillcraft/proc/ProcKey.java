package com.camperscrub.skillcraft.proc;

/**
 * Which counter a roll uses. {@code tier} keeps value tiers apart (for example Bountiful Veins
 * on "common_ore" and "rare_ore"), so cheap blocks never build up the chance for rare ones.
 */
public record ProcKey(String proc, String tier) {

    public static ProcKey of(String proc) {
        return new ProcKey(proc, "default");
    }
}
