package com.snipe;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class Config {
    /** One snipe target. Key = lowercase item name. */
    public static class Entry { public boolean enabled; public double maxPrice; public boolean each = true; }

    public Map<String, Entry> entries = new LinkedHashMap<>();

    // --- behaviour (all editable in the Settings tab) ---
    public boolean buyEnabled = true;        // false = only scan/track prices
    public boolean resell = false;           // relist bought items on the AH
    public String resellMode = "LOWEST";     // LOWEST | AVERAGE | MEDIAN
    public double undercutPercent = 1;       // list this % under the reference price
    public double minProfitPercent = 5;      // skip resell if profit (after tax) is below this
    public double ahTaxPercent = 0;          // server's AH listing tax / fee
    public int maxPages = 50, delayTicks = 6, refreshTicks = 40, resellWaitTicks = 30;
    public String command = "ah";                    // opens the auction house
    public String sellCommand = "ah sell {price}";   // {price} = total price for the stack in your hand

    // --- GUI parsing (edit in config/snipe.json if your server's AH looks different) ---
    public String priceRegex = "(?i)price\\D*([0-9][0-9,]*\\.?[0-9]*)\\s*([kmbt]?)";
    public String sellerRegex = "(?i)(?:seller|sold by|owner|listed by)\\W+([A-Za-z0-9_]{3,16})";
    public String nextName = "next";
    public String confirmName = "confirm";

    public static Config I = new Config();
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    static Path path() { return FabricLoader.getInstance().getConfigDir().resolve("snipe.json"); }

    public static void load() {
        try { if (Files.exists(path())) I = GSON.fromJson(Files.readString(path()), Config.class); } catch (Exception e) { e.printStackTrace(); }
        if (I == null) I = new Config();
        if (I.entries == null) I.entries = new LinkedHashMap<>();
    }
    public static void save() { try { Files.writeString(path(), GSON.toJson(I)); } catch (Exception e) { e.printStackTrace(); } }

    public static String key(String name) { return name.toLowerCase().trim(); }
    public static Entry entryOrNew(String key) { return I.entries.computeIfAbsent(key, k -> new Entry()); }

    public static double suffix(String s) {
        return switch (s.toLowerCase()) { case "k" -> 1e3; case "m" -> 1e6; case "b" -> 1e9; case "t" -> 1e12; default -> 1; };
    }
    /** Parses 16799111, 16,799,111, 16.8m, 2k. Returns -1 on failure. */
    public static double parseUser(String s) {
        Matcher m = Pattern.compile("([0-9][0-9,]*\\.?[0-9]*)\\s*([kKmMbBtT]?)").matcher(s.trim());
        if (!m.find()) return -1;
        try { return Double.parseDouble(m.group(1).replace(",", "")) * suffix(m.group(2)); } catch (Exception e) { return -1; }
    }
    public static String fmt(double v) {
        if (v >= 1e9) return String.format("%.2fB", v / 1e9);
        if (v >= 1e6) return String.format("%.2fM", v / 1e6);
        if (v >= 1e3) return String.format("%.1fK", v / 1e3);
        return String.format("%.0f", v);
    }
}
