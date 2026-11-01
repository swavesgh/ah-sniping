package com.snipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.*;

/** Every item you can snipe: all vanilla items + every custom item the AH scan has ever seen. */
public class Catalog {
    public record Row(String key, String name, String id, boolean custom) {}
    static List<Row> vanilla;

    public static List<Row> all() {
        if (vanilla == null) {
            vanilla = new ArrayList<>();
            for (Item item : BuiltInRegistries.ITEM) {
                if (item == Items.AIR) continue;
                String n = item.getDefaultInstance().getHoverName().getString();
                vanilla.add(new Row(Config.key(n), n, BuiltInRegistries.ITEM.getKey(item).getPath(), false));
            }
        }
        Map<String, Row> m = new LinkedHashMap<>();
        for (Row r : vanilla) m.putIfAbsent(r.key(), r);
        for (var e : Market.d.info.entrySet()) {
            Market.Info i = e.getValue();
            m.put(e.getKey(), new Row(e.getKey(), i.name == null ? e.getKey() : i.name, i.id == null ? "" : i.id, i.custom));
        }
        for (String k : Config.I.entries.keySet()) m.putIfAbsent(k, new Row(k, k, "", true));
        return new ArrayList<>(m.values());
    }
}
