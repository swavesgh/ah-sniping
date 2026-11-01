package com.snipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

/**
 * Every Minecraft call most likely to differ between 26.x builds lives here.
 * If the build fails, the error should point at one of these lines.
 */
public class Compat {
    /** Current screen. Fallback: mc.screen */
    public static Screen screen(Minecraft mc) { return mc.gui.screen(); }
    /** Open a screen. Fallback: mc.setScreenAndShow(s) or mc.setScreen(s) */
    public static void open(Minecraft mc, Screen s) { mc.gui.setScreen(s); }
    /** Run a command without the slash. */
    public static void command(Minecraft mc, String cmd) { mc.getConnection().sendCommand(cmd); }
    /** Chat message only you see. */
    public static void say(Minecraft mc, String s) { mc.player.sendSystemMessage(Component.literal(s)); }

    /** Left-click a container slot. Fallback: mc.gameMode.handleInventoryMouseClick(id, slot, 0, ClickType.PICKUP, player) */
    public static void click(Minecraft mc, ChestMenu menu, int slot) {
        mc.gameMode.handleContainerInput(menu.containerId, slot, 0, ContainerInput.PICKUP, mc.player);
    }

    /** Inventory slot 0-35 (0-8 = hotbar). */
    public static ItemStack invItem(Minecraft mc, int i) { return mc.player.getInventory().getItem(i); }

    /** Put inventory slot 0-35 into the main hand. Fallback names: getSelectedSlot()/setSelectedSlot() may be a plain field `selected`. */
    public static void holdSlot(Minecraft mc, int inv) {
        var in = mc.player.getInventory();
        if (inv < 9) { in.setSelectedSlot(inv); return; }
        int sel = in.getSelectedSlot();
        mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, inv, sel, ContainerInput.SWAP, mc.player);
    }
}
