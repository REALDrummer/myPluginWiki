package REALDrummer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/** This is the main class of the plugin
 * 
 * @author REALDrummer */
@SuppressWarnings("deprecation")
public class myPluginWiki extends JavaPlugin {
    public static Plugin mPW;
    public static Server server;
    public static ConsoleCommandSender console;
    public static final ChatColor COLOR = ChatColor.DARK_PURPLE;
    private static String[] parameters;
    public static final String[] MINECRAFT_COLORS = { "black", "red", "green", "brown", "blue", "purple", "cyan", "light gray", "gray", "pink", "lime", "yellow",
            "light blue", "magenta", "orange", "white" }, COLOR_COLOR_CODE_CHARS = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" },
            FORMATTING_COLOR_CODE_CHARS = { "k", "l", "m", "n", "o", "r" }, COLOR_CODE_CHARS = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e",
                    "f", "k", "l", "m", "n", "o", "r" };
    // String[item I.D.][special data][all the names that could be applied to that item]
    // the special data works like this:
    /* [?][0] is the name for the overall item, e.g. "logs" for any type of log, which all have the I.D. 17; the other indexes are [data+1], e.g. birch logs (I.D. = 17 & data
     * = 2) are found at [17][3] */
    /* in the third dimension of the array, [0] is the plural, [1] is the singular, and the others are just a list of other possible names for the item */
    public static final String[][][] ITEM_IDS = {
            { { "air", "some air" } },
            { { "stone", "some stone", "rock", "smooth stone" } },
            { { "grass", "some grass", "grass blocks" } },
            { { "dirt", "some dirt", "filth" }, { "grassless dirt", "some grassless dirt", "dirt without grass", "dirt with no grass", "filth without grass" },
                    { "podzol", "some podzol", "forest floor", "pine needles", "acid dirt", "acid soil", "dirty grass", "dead leaves", "fallen leaves", "pine droppings" } },
            { { "cobblestone", "some cobblestone", "cobblies" } },
            { { "wooden planks", "some wooden planks", "planks" }, { "oak planks", "some oak planks", "oak wooden planks" },
                    { "spruce planks", "some spruce planks", "spruce wooden planks", "pine planks", "pine wooden planks" },
                    { "birch planks", "some birch planks", "birch wooden planks" }, { "jungle planks", "some jungle planks", "jungle wooden planks" },
                    { "acacia planks", "some acacia planks", "acacia wooden planks" },
                    { "dark oak planks", "some dark oak planks", "dark oak wooden planks", "big large dark big huge old ancient oak wooden planks" } },
            { { "saplings", "a sapling" }, { "oak saplings", "an oak sapling", "oak tree saplings" },
                    { "spruce saplings", "a spruce sapling", "spruce tree saplings", "pine tree saplings" }, { "birch saplings", "a birch sapling", "birch tree saplings" },
                    { "jungle saplings", "a jungle sapling", "jungle tree saplings" }, { "acacia saplings", "an acacia sapling", "acacia tree saplings" },
                    { "dark oak saplings", "a dark oak sapling", "some dark big large huge old ancient oak tree saplings" } },
            { { "bedrock", "some bedrock" } },
            { { "water", "some water" } },
            { { "stationary water", "some stationary water", "immobile water", "nonspreadable water" } },
            { { "lava", "some lava" } },
            { { "stationary lava", "some stationary lava", "immobile lava", "nonspreadable lava" } },
            { { "sand", "some sand" }, { "red sand", "some red sand", "new mesa sand" } },
            { { "gravel", "some gravel", "pebbles" } },
            { { "gold ore", "some gold ore", "golden ore" } },
            { { "iron ore", "some iron ore" } },
            { { "coal ore", "some coal ore" } },
            { { "logs", "a log", "wood" }, { "oak logs", "an oak log", "oak wood" }, { "spruce logs", "a spruce log", "spruce wood", "pine logs", "pine wood" },
                    { "birch logs", "a birch log", "birch wood" }, { "jungle logs", "a jungle log", "jungle wood" } },
            { { "leaves", "some leaves", "leaves blocks", "leafs blocks" }, { "oak leaves", "some oak leaves", "oak leaves blocks", "oak leafs blocks" },
                    { "spruce leaves", "some spruce leaves", "spruce leaves blocks", "spruce leafs blocks", "pine needles", "pine leaves blocks", "pine leafs blocks" },
                    { "birch leaves", "some birch leaves", "birch leaves blocks", "birch leafs blocks" },
                    { "jungle leaves", "some jungle leaves", "jungle leaves blocks", "jungle leafs blocks" },
                    { "acacia leaves", "some acacia leaves", "acacia leaves blocks", "acacia leafs blocks" },
                    { "dark oak leaves", "some dark oak leaves", "dark big large huge old ancient oak leafs leaves blocks" } },
            { { "sponges", "a sponge", "loofas" } },
            { { "glass", "some glass", "glass blocks", "glass cubes" } },
            { { "lapis lazuli ore", "some lapis lazuli ore" } },
            { { "lapis lazuli blocks", "a lapis lazuli block", "blocks of lapis lazuli" } },
            { { "dispensers", "a dispenser", "shooters" } },
            {
                    { "sandstone", "some sandstone", "sandbricks" },
                    { "regular sandstone", "some regular sandstone", "regular sandbricks", "normal sandstone", "normal sandbricks", "natural sandstone", "natural sandbricks" },
                    { "chiseled sandstone", "some chiseled sandstone", "chiseled sandbricks", "ancient sandstone", "ancient sandbricks", "pyramid sandstone",
                            "pyramid sandbricks", "hieroglyphics sandstone", "hieroglyphics sandbricks" },
                    { "smooth sandstone", "some smooth sandstone", "smooth sandbricks", "clean sandstone", "clean sandbricks" } },
            { { "note blocks", "a note block", "music blocks", "sound blocks", "speakers" } },
            { { "beds", "a bed" } },
            { { "powered rails", "a powered rail", "powered redstone rails", "powered redstone tracks", "powered redstone railroad tracks" } },
            { { "detector rails", "a detector rail", "detection rails", "sensor rails", "sensory rails", "pressure rails", "pressure plate rails", "detector railroad tracks",
                    "detection railroad tracks", "sensor railroad tracks", "sensory railroad tracks", "pressure railroad tracks", "pressure plate railroad tracks" } },
            { { "sticky pistons", "a sticky piston", "slime pistons", "slimy pistons" } },
            { { "cobwebs", "a cobweb", "spider webs", "webs" } },
            { { "tall grass and dead shrubs", "some tall grass or a dead shrub", "tall grass and dead shrubbery", "weeds" },
                    { "dead grass", "some dead grass", "dead grass bushes", "dead grass shrubs", "dead grass shrubbery" }, { "tall grass", "high grass" },
                    { "ferns", "a fern", "small plants" } },
            { { "dead shrubs", "a dead shrub", "dead bushes", "dead shrubbery", "dried plants" } },
            { { "pistons", "a piston" } },
            { { "piston arms", "a piston arm", "piston extensions", "piston pusher" } },
            { { "wool", "some wool", "wool blocks" }, { "white wool", "some white wool", "white wool blocks" }, { "orange wool", "some orange wool", "orange wool blocks" },
                    { "magenta wool", "some magenta wool", "magenta wool blocks" }, { "light blue wool", "some light blue wool", "light blue wool blocks" },
                    { "yellow wool", "some yellow wool", "yellow wool blocks" },
                    { "lime green wool", "some lime green wool", "lime green wool blocks", "green wool blocks", "light green wool blocks" },
                    { "pink wool", "some pink wool", "pink wool blocks" }, { "gray wool", "some gray wool", "dark gray wool blocks" },
                    { "light gray wool", "some light gray wool", "light gray wool blocks", "off white wool blocks" }, { "cyan wool", "some cyan wool", "cyan wool blocks" },
                    { "purple wool", "some purple wool", "purple wool blocks" }, { "blue wool", "some blue wool", "dark blue wool blocks" },
                    { "brown wool", "some brown wool", "brown wool blocks" },
                    { "cactus green wool", "some cactus green wool", "cactus green wool blocks", "dark green wool blocks" },
                    { "red wool", "some red wool", "red wool blocks" }, { "black wool", "some black wool", "black wool blocks" } },
            { { "blocks moved by pistons", "a block moved by a piston" } },
            { { "dandelions", "a dandelion", "yellow flowers" } },
            { { "flowers (except dandelions)", "a flower (not a dandelion)", "flowers that's aren't dandelions", "non-dandelion flowers", "flowers" },
                    { "poppies", "a poppy", "new roses" }, { "blue orchids", "a blue orchid", "blue flowers", "bluebells" },
                    { "alliums", "an allium", "allium flowers", "allia flowers", "purple flowers" }, { "red tulips", "a red tulip", "red flowers" },
                    { "orange tulips", "an orange tulip", "orange flowers" }, { "white tulips", "a white tulip", "white flowers" },
                    { "pink tulips", "a pink tulip", "pink flowers" }, { "oxeye daisies", "an oxeye daisy", "wild daisies", "wildflowers" } },
            { { "brown mushrooms", "a brown mushroom", "small brown mushrooms", "little brown mushrooms" } },
            { { "red mushrooms", "a red mushroom", "small red mushrooms", "little red mushrooms" } },
            { { "gold blocks", "a gold block", "blocks of gold" } },
            { { "iron blocks", "an iron block", "blocks of iron" } },
            {
                    { "all non-wood double slab blocks", "a non-wood double slab block", "all non-wood double slabs", "all non-wood stacked slabs" },
                    { "stone double slab blocks", "a stone double slab block", "stone double slabs", "stone stacked slabs" },
                    { "sandstone double slab blocks", "a sandstone double slab block", "sandstone double slabs", "sandstone stacked slabs", "sandbrick double slab blocks",
                            "sandbrick double slabs", "sandbrick stacked slabs" }, { "wooden double slab blocks [obsolete]", "a wooden double slab block [obsolete]" },
                    { "cobblestone double slab blocks", "a cobblestone double slab block", "cobblestone double slabs", "cobblestone stacked slabs" },
                    { "brick double slab blocks", "a brick double slab block", "clay brick stacked slabs", "clay brick double slab blocks", "clay brick double slabs" },
                    { "stone brick double slab blocks", "a stone brick double slab block", "stone brick double slabs", "stone brick stacked slabs" },
                    { "Nether brick double slab blocks", "a Nether brick double slab block", "Nether brick double slabs", "Nether brick stacked slabs" },
                    { "Nether Quartz double slab blocks", "a Nether Quartz double slab block", "Nether Quartz double slabs", "Nether Quartz stacked slabs" } },
            { { "all non-wood slabs", "a non-wood slab", "all non-wood half blocks" }, { "stone slabs", "a stone slab", "stone half blocks" },
                    { "sandstone slabs", "a sandstone slab", "sandstone half blocks", "sandbrick slabs", "sandbrick half blocks" },
                    { "wooden slabs [obsolete]", "a wooden slab [obsolete]" }, { "cobblestone slabs", "a cobblestone slab", "cobblestone half blocks" },
                    { "brick slabs", "a brick slab", "clay brick slabs", "clay brick half blocks" }, { "stone brick slabs", "a stone brick slab", "stone brick half blocks" },
                    { "Nether brick slabs", "a Nether brick slab", "Nether brick half blocks" },
                    { "Nether Quartz slabs", "a Nether Quartz slab", "Nether Quartz half blocks" } },
            { { "bricks", "some bricks", "bricks blocks" } },
            { { "T.N.T.", "some T.N.T.", "TNT", "dynamite", "trinitrotoluene" } },
            { { "bookcases", "a bookcase", "bookshelves", "bookshelfs" } },
            { { "mossy stone", "some mossy stone", "mossy cobblestone", "ancient cobblestone", "old cobblestone" } },
            { { "obsidian", "some obsidian", "volcanic glass" } },
            { { "torches", "a torch", "fire sticks" } },
            { { "fire", "a fire", "flames" } },
            { { "monster spawners", "a monster spawner", "spawners" } },
            { { "oak stairs", "some oak stairs", "oak wood stairs", "wooden stairs", "oak wood steps", "wooden steps" } },
            { { "chests", "a chest" } },
            { { "redstone wire", "a piece of redstone wire", "wire" } },
            { { "diamond ore", "some diamond ore" } },
            { { "diamond blocks", "a diamond block", "blocks of diamonds" } },
            { { "crafting tables", "a crafting table", "crafting workbench" } },
            { { "wheat", "some wheat", "crops" } },
            { { "farmland blocks", "some farmland", "plowed tilled land soil blocks" } },
            { { "furnaces", "a furnace", "ovens", "ranges" } },
            { { "burning furnaces", "a burning furnace", "active burning ovens ranges furnaces" } },
            { { "sign posts", "a sign post", "ground sign posts" } },
            { { "wooden doors", "a wooden door", "doors" } },
            { { "ladders", "a ladder", "wooden rope ladders" } },
            { { "rails", "a rail", "normal regular unpowered iron rails railroad tracks" } },
            { { "cobblestone stairs", "some cobblestone stairs", "cobblestone steps" } },
            { { "wall signs", "a wall sign", "posted wall signs" } },
            { { "levers", "a lever", "switches" } },
            { { "stone pressure plates", "a stone pressure plate", "stone foot switches plates" } },
            { { "iron doors", "an iron door" } },
            { { "wooden pressure plates", "a wooden pressure plate", "wooden foot switches plates" } },
            { { "redstone ore", "some redstone ore" } },
            { { "glowing redstone ore", "some glowing redstone ore", "luminescent redstone ore" } },
            { { "inactive redstone torches", "an inactive redstone torch" } },
            { { "redstone torches", "a redstone torch" } },
            { { "stone buttons", "a stone button", "buttons" } },
            { { "snow on the ground", "some snow on the ground", "snow", "snow on the ground", "fallen snow", "fresh snow", "ground snow" } },
            { { "ice", "some ice", "blocks of ice", "ice blocks" } },
            { { "snow blocks", "a snow block", "blocks of snow" } },
            { { "cacti", "a cactus", "cactuses", "saguaros", "saguaro cacti", "saguaro cactuses" } },
            { { "clay blocks", "a clay block", "blocks of clay" } },
            { { "sugar cane", "some sugar cane", "sugar canes", "sugarcanes" } },
            { { "jukeboxes", "a jukebox", "disc player", "music box", "slotted block", ".mp3 player" } },
            { { "wooden fences", "a wooden fence post", "wooden fence posts", "wooden railings" } },
            { { "pumpkins", "a pumpkin", "unlit Jack-o'-Lanterns", "dark Jack-o'-Lanterns" } },
            { { "Netherrack", "some Netherrack", "Nether rack", "Nether dirt" } },
            { { "Soul Sand", "some Soul Sand", "Nether sand", "quicksand", "quick sand" } },
            { { "glowstone", "some glowstone", "glowstone blocks", "blocks of glowstone" } },
            { { "Nether portal blocks", "a Nether portal block", "Nether portal swirly blocks" } },
            { { "Jack-o'-Lanterns", "a Jack-o'-Lantern", "lit Jack-o'-Lanterns", "lit Jack o Lanterns", "lit JackoLanterns" } },
            { { "cakes", "a cake", "cake blocks" } },
            { { "repeaters", "a repeater", "diodes", "delayers", "redstone repeaters", "redstone diodes", "redstone delayers" } },
            { { "active repeaters", "an active repeater", "active diodes", "active delayers", "active redstone repeaters", "active redstone diodes",
                    "active redstone delayers" } },
            {
                    { "stained glass", "some stained glass", "colored glass", "church glass", "tinted glass" },
                    { "white stained glass", "some white stained glass", "white colored glass", "white church glass", "white tinted glass" },
                    { "orange stained glass", "some orange stained glass", "orange colored glass", "orange church glass", "orange tinted glass" },
                    { "magenta stained glass", "some magenta stained glass", "magenta colored glass", "magenta church glass", "magenta tinted glass" },
                    { "light blue stained glass", "some light blue stained glass", "light blue colored glass", "light blue church glass", "light blue tinted glass" },
                    { "yellow stained glass", "some yellow stained glass", "yellow colored glass", "yellow church glass", "yellow tinted glass" },
                    { "lime green stained glass", "some lime green stained glass", "lime green colored glass", "lime green church glass", "lime green tinted glass" },
                    { "pink stained glass", "some pink stained glass", "pink colored glass", "pink church glass", "pink tinted glass" },
                    { "gray stained glass", "some gray stained glass", "gray colored glass", "gray church glass", "gray tinted glass" },
                    { "light gray stained glass", "some light gray stained glass", "light gray colored glass", "light gray church glass", "light gray tinted glass" },
                    { "cyan stained glass", "some cyan stained glass", "cyan colored glass", "cyan church glass", "cyan tinted glass" },
                    { "purple stained glass", "some purple stained glass", "purple colored glass", "purple church glass", "purple tinted glass" },
                    { "blue stained glass", "some blue stained glass", "blue colored glass", "blue church glass", "blue tinted glass" },
                    { "brown stained glass", "some brown stained glass", "brown colored glass", "brown church glass", "brown tinted glass" },
                    { "cactus green stained glass", "some cactus green stained glass", "cactus green colored glass", "cactus green church glass", "cactus green tinted glass" },
                    { "red stained glass", "some red stained glass", "red colored glass", "red church glass", "red tinted glass" },
                    { "black stained glass", "some black stained glass", "black colored glass", "black church glass", "black tinted glass" } },
            { { "trapdoors", "a trapdoor", "ground doors" } },
            { { "monster egg blocks", "a monster egg block", "monster eggs", "silverfish spawners" },
                    { "monster egg stone blocks", "a monster egg stone block", "monster egg stone", "silverfish stone", "silverfish smooth stone" },
                    { "monster egg cobblestone blocks", "a monster egg cobblestone block", "silverfish cobblestone" },
                    { "monster egg stone brick blocks", "a monster egg stone brick block", "monster egg stone bricks", "silverfish stone bricks" } },
            { { "stone bricks", "some stone brick", "cobblestone bricks", "cobble bricks" }, { "stone bricks", "some stone brick", "cobblestone bricks", "cobble bricks" },
                    { "mossy stone bricks", "some mossy stone brick", "mossy cobblestone bricks", "mossy cobble bricks" },
                    { "cracked stone bricks", "some cracked stone brick", "cracked cobblestone bricks", "cracked cobble bricks" },
                    { "chiseled stone bricks", "some chiseled stone brick", "chiseled cobblestone bricks", "chiseled cobble bricks", "circle blocks" } },
            { { "giant brown mushroom blocks", "a giant brown mushroom block", "huge brown mushrooms", "big brown mushrooms", "giant brown 'shroom blocks",
                    "huge brown 'shrooms", "big brown shrooms", "giant brown shroom blocks", "huge brown shrooms", "big brown shrooms" } },
            { { "giant red mushroom blocks", "a giant red mushroom block", "huge red mushrooms", "big red mushrooms", "giant red 'shroom blocks", "huge red 'shrooms",
                    "big red shrooms", "giant red shroom blocks", "huge red shrooms", "big red shrooms" } },
            { { "iron bars", "some iron bars", "wrought iron bars" } },
            { { "glass panes", "a glass pane", "windows", "window panes" } },
            { { "melon blocks", "a melon", "full melons", "watermelon blocks", "whole melons", "whole watermelons" } },
            { { "pumpkin stems", "a pumpkin stem", "pumpkin stalks", "pumpkin vines" } },
            { { "melon stems", "a melon stem", "melon stalks", "melon vines", "watermelon stems", "watermelon stalks", "watermelon vines" } },
            { { "vines", "some vines", "jungle vines", "swamp vines" } },
            { { "fence gates", "a fence gate", "wooden gates", "wood gates" } },
            { { "brick stairs", "some brick stairs", "brick steps", "clay brick stairs", "clay brick steps" } },
            { { "stone brick stairs", "some stone brick stairs", "stone brick steps", "stone stairs", "stone steps" } },
            { { "mycelium", "some mycelium", "mushroom grass", "shroom grass", "mushroom biome grass" } },
            { { "lily pads", "a lily pad", "lilies", "pond lilies", "lilypads", "water lily", "water lilies" } },
            { { "Nether brick blocks", "some Nether brick", "Nether fortress bricks blocks", "Nether dungeon bricks blocks" } },
            { { "Nether brick fences", "a Nether brick fence post", "Nether fortress fences", "Nether dungeon fences" } },
            { { "Nether brick stairs", "some Nether brick stairs", "Nether fortress stairs", "Nether dungeon stairs", "Nether brick steps", "Nether fortress steps",
                    "Nether dungeon steps" } },
            { { "Nether warts", "some Nether warts", "Nether mushrooms", "Nether 'shrooms", "Nether fungi" } },
            { { "enchantment tables", "an enchantment table" } },
            { { "brewing stands", "a brewing stand" } },
            { { "cauldrons", "a cauldron" } },
            { { "End portal blocks", "an End portal block" } },
            { { "End portal frame blocks", "an End portal frame block" } },
            { { "End stone", "some End stone", "End blocks" } },
            { { "dragon eggs", "a dragon egg", "Enderdragon eggs" } },
            { { "redstone lamps", "a redstone lamp", "glowstone lamps" } },
            { { "active redstone lamps", "an active redstone lamp", "active glowstone lamps" } },
            {
                    { "wooden double slab blocks", "a wooden double slab block", "wooden double slabs", "wooden plank double slab blocks", "wooden plank double slabs",
                            "wood double slab blocks", "wood double slabs", "wood plank double slab blocks", "wood plank double slabs" },
                    { "oak double slab blocks", "an oak double slab block", "oak double slabs", "oak plank double slab blocks", "oak plank double slabs",
                            "oak wood double slab blocks", "oak wood double slabs", "oak wood plank double slab blocks", "oak wood plank double slabs" },
                    { "spruce double slab blocks", "a spruce double slab block", "spruce double slabs", "spruce plank double slab blocks", "spruce plank double slabs",
                            "spruce wood double slab blocks", "spruce wood double slabs", "spruce wood plank double slab blocks", "spruce wood plank double slabs",
                            "pine double slab blocks", "pine double slabs", "pine plank double slab blocks", "pine plank double slabs", "pine wood double slab blocks",
                            "pine wood double slabs", "pine wood plank double slab blocks", "pine wood plank double slabs" },
                    { "birch double slab blocks", "a birch double slab block", "birch double slabs", "birch plank double slab blocks", "birch plank double slabs",
                            "birch wood double slab blocks", "birch wood double slabs", "birch wood plank double slab blocks", "birch wood plank double slabs" },
                    { "acacia double slab blocks", "a acacia double slab block", "acacia double slabs", "acacia plank double slab blocks", "acacia plank double slabs",
                            "acacia wood double slab blocks", "acacia wood double slabs", "acacia wood plank double slab blocks", "acacia wood plank double slabs" },
                    { "dark oak double slab blocks", "a dark oak double slab block", "dark big large huge old ancient oak double slabs",
                            "dark big large huge old ancient oak plank double slab blocks", "dark big large huge old ancient oak plank double slabs",
                            "dark big large huge old ancient oak wood double slab blocks", "dark big large huge old ancient oak wood double slabs",
                            "dark big large huge old ancient oak wood plank double slab blocks", "dark big large huge old ancient oak wood plank double slabs" } },
            {
                    { "wooden slabs", "a wooden slab", "wooden half blocks", "wood slabs", "wood half blocks", "wooden plank slabs", "wooden plank half blocks",
                            "wood plank slabs", "wood plank half blocks" },
                    { "oak slabs", "an oak slab", "oak half blocks", "oak wood slabs", "oak wood half blocks", "oak plank slabs", "oak plank half blocks",
                            "oak wood plank slabs", "oak wood plank half blocks" },
                    { "spruce slabs", "a spruce slab", "spruce half blocks", "spruce wood slabs", "spruce wood half blocks", "spruce plank slabs", "spruce plank half blocks",
                            "spruce wood plank slabs", "spruce wood plank half blocks", "pine slabs", "pine half blocks", "pine wood slabs", "pine wood half blocks",
                            "pine plank slabs", "pine plank half blocks", "pine wood plank slabs", "pine wood plank half blocks" },
                    { "birch slabs", "a birch slab", "birch half blocks", "birch wood slabs", "birch wood half blocks", "birch plank slabs", "birch plank half blocks",
                            "birch wood plank slabs", "birch wood plank half blocks" },
                    { "acacia slabs", "a acacia slab", "acacia half blocks", "acacia wood slabs", "acacia wood half blocks", "acacia plank slabs", "acacia plank half blocks",
                            "acacia wood plank slabs", "acacia wood plank half blocks" },
                    { "dark oak slabs", "a dark oak slab", "dark big large huge old ancient oak half blocks", "dark big large huge old ancient oak wood slabs",
                            "dark big large huge old ancient oak wood half blocks", "dark big large huge old ancient oak plank slabs",
                            "dark big large huge old ancient oak plank half blocks", "dark big large huge old ancient oak wood plank slabs",
                            "dark big large huge old ancient oak wood plank half blocks" } },
            { { "cocoa bean plants", "a cocoa bean plant", "cocoa bean pods", "cocoa beans" } },
            { { "sandstone stairs", "some sandstone stairs", "sandstone steps" } },
            { { "emerald ore", "some emerald ore" } },
            { { "Ender Chests", "an Ender Chest", "Enderchests" } },
            { { "tripwire hooks", "a tripwire hook", "tripwire mechanisms", "trip wire hooks", "trip wire mechanisms" } },
            { { "tripwire", "some tripwire", "trip wire" } },
            { { "emerald blocks", "an emerald block", "blocks of emerald", "blocks of emeralds" } },
            { { "spruce stairs", "some spruce stairs", "spruce wood stairs", "spruce steps", "spruce wood steps" } },
            { { "birch stairs", "some birch stairs", "birch wood stairs", "birch steps", "birch wood steps" } },
            { { "jungle stairs", "some jungle stairs", "jungle wood stairs", "jungle steps", "jungle wood steps" } },
            { { "command blocks", "a command block" } },
            { { "beacons", "a beacon" } },
            {
                    { "cobblestone walls", "a cobblestone wall", "cobblestone fences" },
                    { "mossy cobblestone walls", "a mossy cobblestone wall", "mossy cobblestone fences", "mossy stone walls", "mossy stone fences", "old cobblestone walls",
                            "old cobblestone fences", "old stone walls", "old stone fences", "ancient cobblestone walls", "ancient cobblestone fences", "ancient stone walls",
                            "ancient stone fences" } },
            { { "flower pots", "a flower pot", "pots", "clay pots" } },
            { { "carrots", "some carrots" } },
            { { "potatoes", "some potatoes", "potatos" } },
            { { "wooden buttons", "a wooden button", "wood buttons" } },
            { { "monster heads", "a monster head", "heads" }, { "skeleton skulls", "a skeleton skull", "skeleton heads", "skele heads", "skele skulls" },
                    { "Wither skeleton skulls", "a Wither skeleton skull", "Wither skeleleton heads", "Wither skele skulls", "Wither skele heads" },
                    { "zombie heads", "a zombie head" }, { "Steve heads", "a Steve head", "Minecraft Steve heads", "guy heads", "man heads", "person heads", "human heads" },
                    { "creeper heads", "a creeper head" } },
            { { "anvils", "an anvil" } },
            { { "trapped chests", "a trapped chest" } },
            { { "light weighted pressure plates", "a light weighted pressure plate", "light weight pressure plates", "lightweight pressure plates", "golden pressure plates",
                    "gold pressure plates" } },
            { { "heavy weighted pressure plates", "a heavy weighted pressure plate", "heavy weight pressure plates", "heavyweight pressure plates", "iron pressure plates",
                    "silver pressure plates" } },
            { { "redstone comparators", "a redstone comparator", "redstone comparers" } },
            { { "active redstone comparators", "an active redstone comparator", "active redstone comparers" } },
            { { "daylight sensors", "a daylight sensor", "night sensors", "nighttime sensors", "day night sensors", "day/night sensors", "daytime nighttime sensors",
                    "daytime/nighttime sensors", "solar panels" } },
            { { "redstone blocks", "a redstone block", "blocks of redstone dust" } },
            { { "Nether Quartz ore", "some Nether Quartz ore", "raw Nether Quartz", "crude Nether Quartz", "unrefined Nether Quartz" } },
            { { "hoppers", "a hopper", "funnels" } },
            {
                    { "Nether Quartz blocks", "a Nether Quartz block", "a Nether Quartz block", "blocks of Nether Quartz" },
                    { "chiseled Nether Quartz blocks", "a chiseled Nether Quartz block", "chiseled blocks of Nether Quartz", "fancy Nether Quartz blocks",
                            "fancy blocks of Nether Quartz", "fancy block of Nether Quartz" },
                    { "pillar Nether Quartz blocks", "a pillar Nether Quartz block", "pillar blocks of Nether Quartz" } },
            { { "Nether Quartz stairs", "some Nether Quartz stairs", "Nether Quartz steps" } },
            { { "activator rails", "an activator rail", "T.N.T. activator rails", "TNT activator rails", "striker rails", "T.N.T. starter rails", "TNT starter rails" } },
            { { "droppers", "a dropper", "lazy dispensers", "new dispensers" } },
            {
                    { "stained clay", "some stained clay", "colored clay", "coloured clay", "dyed clay" },
                    { "white stained clay", "some white stained clay", "white colored clay", "white coloured clay", "white dyed clay" },
                    { "orange stained clay", "some orange stained clay", "orange colored clay", "orange coloured clay", "orange dyed clay" },
                    { "magenta stained clay", "some magenta stained clay", "magenta colored clay", "magenta coloured clay", "magenta dyed clay" },
                    { "light blue stained clay", "some light blue stained clay", "light blue colored clay", "light blue coloured clay", "light blue dyed clay" },
                    { "yellow stained clay", "some yellow stained clay", "yellow colored clay", "yellow coloured clay", "yellow dyed clay" },
                    { "lime green stained clay", "some lime green stained clay", "lime green colored clay", "lime green coloured clay", "lime green dyed clay" },
                    { "pink stained clay", "some pink stained clay", "pink coloured clay", "pink dyed clay" },
                    { "gray stained clay", "some gray stained clay", "gray coloured clay", "gray dyed clay" },
                    { "light gray stained clay", "some light gray stained clay", "light gray colored clay", "light gray coloured clay", "light gray dyed clay" },
                    { "cyan stained clay", "some cyan stained clay", "cyan colored clay", "cyan coloured clay", "cyan dyed clay" },
                    { "purple stained clay", "some purple stained clay", "purple colored clay", "purple coloured clay", "purple dyed clay" },
                    { "blue stained clay", "some blue stained clay", "blue colored clay", "blue coloured clay", "blue dyed clay" },
                    { "brown stained clay", "some brown stained clay", "brown colored clay", "brown coloured clay", "brown dyed clay" },
                    { "cactus green stained clay", "some cactus green stained clay", "dark cactus green stained clay", "cactus green colored clay",
                            "cactus green coloured clay", "cactus green dyed clay" },
                    { "red stained clay", "some red stained clay", "red colored clay", "red coloured clay", "red dyed clay" },
                    { "black stained clay", "some black stained clay", "black colored clay", "black coloured clay", "black dyed clay" } },
            {
                    { "stained glass panes", "some stained glass panes", "colored glass panes", "coloured glass panes", "dyed glass panes", "church glass panes",
                            "tainted glass panes" },
                    { "white stained glass panes", "some white stained glass panes", "white colored glass panes", "white coloured glass panes", "white dyed glass panes",
                            "white church glass panes", "white tainted glass panes" },
                    { "orange stained glass panes", "some orange stained glass panes", "orange colored glass panes", "orange coloured glass panes", "orange dyed glass panes",
                            "orange church glass panes", "orange tainted glass panes" },
                    { "magenta stained glass panes", "some magenta stained glass panes", "magenta colored glass panes", "magenta coloured glass panes",
                            "magenta dyed glass panes", "magenta church glass panes", "magenta tainted glass panes" },
                    { "light blue stained glass panes", "some light blue stained glass panes", "light blue colored glass panes", "light blue coloured glass panes",
                            "light blue dyed glass panes", "light blue church glass panes", "light blue tainted glass panes" },
                    { "yellow stained glass panes", "some yellow stained glass panes", "yellow colored glass panes", "yellow coloured glass panes", "yellow dyed glass panes",
                            "yellow church glass panes", "yellow tainted glass panes" },
                    { "lime stained glass panes", "some lime stained glass panes", "lime colored glass panes", "lime coloured glass panes", "lime dyed glass panes",
                            "lime church glass panes", "lime tainted glass panes" },
                    { "pink stained glass panes", "some pink stained glass panes", "pink colored glass panes", "pink coloured glass panes", "pink dyed glass panes",
                            "pink church glass panes", "pink tainted glass panes" },
                    { "gray stained glass panes", "some gray stained glass panes", "gray colored glass panes", "gray coloured glass panes", "gray dyed glass panes",
                            "gray church glass panes", "gray tainted glass panes" },
                    { "light gray stained glass panes", "some light gray stained glass panes", "light gray colored glass panes", "light gray coloured glass panes",
                            "light gray dyed glass panes", "light gray church glass panes", "light gray tainted glass panes" },
                    { "cyan stained glass panes", "some cyan stained glass panes", "cyan colored glass panes", "cyan coloured glass panes", "cyan dyed glass panes",
                            "cyan church glass panes", "cyan tainted glass panes" },
                    { "purple stained glass panes", "some purple stained glass panes", "purple colored glass panes", "purple coloured glass panes", "purple dyed glass panes",
                            "purple church glass panes", "purple tainted glass panes" },
                    { "blue stained glass panes", "some blue stained glass panes", "blue colored glass panes", "blue coloured glass panes", "blue dyed glass panes",
                            "blue church glass panes", "blue tainted glass panes" },
                    { "brown stained glass panes", "some brown stained glass panes", "brown colored glass panes", "brown coloured glass panes", "brown dyed glass panes",
                            "brown church glass panes", "brown tainted glass panes" },
                    { "cactus green stained glass panes", "some cactus green stained glass panes", "cactus green colored glass panes", "cactus green coloured glass panes",
                            "cactus green dyed glass panes", "cactus green church glass panes", "cactus green tainted glass panes" },
                    { "red stained glass panes", "some red stained glass panes", "red colored glass panes", "red coloured glass panes", "red dyed glass panes",
                            "red church glass panes", "red tainted glass panes" },
                    { "black stained glass panes", "some black stained glass panes", "black colored glass panes", "black coloured glass panes", "black dyed glass panes",
                            "black church glass panes", "black tainted glass panes" } },
            {
                    { "acacia or dark oak leaves", "some acacia or dark oak leaves", "acacia or dark big large huge old ancient oak leaves blocks",
                            "acacia or dark big large huge old ancient oak leafs blocks" },
                    { "acacia leaves", "some acacia leaves", "acacia leaves blocks", "acacia leafs blocks" },
                    { "dark oak leaves", "some dark oak leaves", "dark big large huge old ancient oak leaves blocks", "dark big large huge old ancient oak leafs blocks" } },
            { { "acacia or dark oak logs", "an acacia or dark oak log", "acacia dark big large huge old ancient oak logs wood" },
                    { "acacia logs", "an acacia log", "acacia wood" },
                    { "dark oak logs", "a dark oak log", "dark big large huge old ancient oak logs", "dark big large huge old ancient oak wood" } },
            { { "acacia stairs", "some acacia stairs", "acacia wood stairs", "acacia wood steps" } },
            { { "dark oak stairs", "some dark oak stairs", "dark big large huge old ancient oak wood stairs", "dark big large huge old ancient oak wood steps" } },
            { { "hay bales", "a hay bale", "hay blocks", "wheat block", "horse cow sheep food blocks", "bread blocks" } },
            { { "carpet", "some carpet", "wool carpeting" }, { "white carpet", "some white carpet", "white wool carpeting" },
                    { "orange carpet", "some orange carpet", "orange wool carpeting" }, { "magenta carpet", "some magenta carpet", "magenta wool carpeting" },
                    { "light blue carpet", "some light blue carpet", "light blue wool carpeting" }, { "yellow carpet", "some yellow carpet", "yellow wool carpeting" },
                    { "lime green carpet", "some lime green carpet", "lime green wool carpeting" }, { "pink carpet", "some pink carpet", "pink wool carpeting" },
                    { "dark gray carpet", "some dark gray carpet", "dark gray wool carpeting" },
                    { "light gray carpet", "some light gray carpet", "light gray wool carpeting" }, { "cyan carpet", "some cyan carpet", "cyan wool carpeting" },
                    { "purple carpet", "some purple carpet", "purple wool carpeting" }, { "blue carpet", "some blue carpet", "blue wool carpeting" },
                    { "brown carpet", "some brown carpet", "brown wool carpeting" },
                    { "cactus green carpet", "some cactus green carpet", "cactus green wool carpeting", "dark cactus green carpeting" },
                    { "red carpet", "some red carpet", "red wool carpeting" }, { "black carpet", "some black carpet", "black wool carpeting" } },
            { { "hardened clay", "some hardened clay", "cooked clay" } },
            { { "coal blocks", "a block of coal", "blocks of coal" } },
            { { "packed ice", "some packed ice", "glaciers ice", "glacial ice", "arctic ice", "hard ice", "opaque ice" } },
            { { "tall flowers", "a tall flower", "double block height flowers", "extra large tall flowers", "two 2 block tall flowers" },
                    { "sunflowers", "a sunflower", "big yellow flowers", "big extra large dandelions" }, { "lilacs", "a lilac", "big pink flowers" },
                    { "very tall grass", "some very tall grass", "super extra tall grass", "high grass" },
                    { "large ferns", "a large fern", "some extra tall large double block height ferns" },
                    { "rose bushes", "a rose bush", "large roses", "roses trees", "roses", "big large tall double height red flowers" }, { "peonies", "a peony", "peonys" } },
            // block I.D.s --> item I.D.s
            { { "iron shovels", "an iron shovel", "iron spades" } },
            { { "iron pickaxes", "an iron pickaxe", "iron picks" } },
            { { "iron axes", "an iron axe", "iron hatchets", "iron tree axe" } },
            { { "flint and steel", "some flint and steel" } },
            { { "apples", "an apple", "red apples" } },
            { { "bows", "a bow", "bows and arrows", "longbows", "long bows", "shortbows", "short bows" } },
            { { "arrows", "an arrow" } },
            { { "coal", "a lump of coal" } },
            { { "diamonds", "a diamond" } },
            { { "iron", "an iron ingot", "iron ingots" } },
            { { "gold", "a gold ingot", "gold ingots", "gold bars" } },
            { { "iron swords", "an iron sword" } },
            { { "wooden swords", "a woode sword", "wood swords" } },
            { { "wooden shovels", "a wooden shovel", "wood shovels", "wooden spades", "wood spades" } },
            { { "wooden pickaxes", "a wooden pickaxe", "wooden picks", "wood pickaxes", "wood picks" } },
            { { "wooden axes", "a wooden axe", "wooden hatchets", "wooden tree axes", "wood axes", "wood hatchets", "wood tree axes" } },
            { { "stone swords", "a stone sword", "cobblestone swords", "cobble swords" } },
            { { "stone shovels", "a stone shovel", "cobblestone shovels", "cobble shovels", "stone spades", "cobblestone spades", "cobble spades" } },
            { { "stone pickaxes", "a stone pickaxe", "stone picks", "cobblestone pickaxes", "cobble pickaxes", "cobblestone picks", "cobble picks" } },
            { { "stone axes", "a stone axe", "stone hatchets", "stone tree axes", "cobblestone axes", "cobble axes", "cobblestone hatchets", "cobble hatchets",
                    "cobblestone tree axes", "cobble tree axes" } },
            { { "diamond swords", "a diamond sword" } },
            { { "diamond shovels", "a diamond shovel", "diamond spades" } },
            { { "diamond pickaxes", "a diamond pickaxe", "diamond picks" } },
            { { "diamond axes", "a diamond axe", "diamond hatchets", "diamond tree axes" } },
            { { "sticks", "a stick", "twigs" } },
            { { "bowls", "a bowl", "wooden bowls", "wood bowls", "soup bowls" } },
            { { "mushroom stew", "a bowl of mushroom stew", "mushroom soup", "mooshroom milk", "mooshroom cow milk" } },
            { { "golden swords", "a golden sword", "gold swords" } },
            { { "golden shovels", "a golden shovel", "gold shovels", "golden spades", "gold spades" } },
            { { "golden pickaxes", "a golden pickaxe", "golden picks", "gold pickaxes", "gold picks" } },
            { { "golden axes", "a golden axe", "golden hatchets", "golden tree axes", "gold axes", "gold hatchets", "gold tree axes" } },
            { { "string", "some string" } },
            { { "feathers", "a feather" } },
            { { "gunpowder", "some gunpowder", "sulfur", "sulphur" } },
            { { "wooden hoes", "a wooden hoe", "wood hoes" } },
            { { "stone hoes", "a stone hoe", "cobblestone hoes", "cobble hoes" } },
            { { "iron hoes", "an iron hoe" } },
            { { "diamond hoes", "a diamond hoe" } },
            { { "golden hoes", "a golden hoe", "gold hoes" } },
            { { "seeds", "some seeds", "seed packets" } },
            { { "wheat", "some wheat", "crops" } },
            { { "bread", "a loaf of bread", "bread loaves" } },
            { { "leather caps", "a leather cap", "leather helmets", "leather helms" } },
            { { "leather tunics", "a leather tunic", "leather shirts", "leather chestplates" } },
            { { "leather pants", "a pair of leather pants", "leather leggings", "leather chaps" } },
            { { "leather boots", "a pair of leather boots", "leather shoes" } },
            { { "chainmail helmets", "a chainmail helmet", "chainmail caps", "chainmail helms", "chain helmets", "chain caps", "chain helms" } },
            { { "chainmail chestplates", "a chainmail chestplate", "chainmail tunics", "chainmail shirts", "chain chestplates", "chain tunics", "chain shirts" } },
            { { "chainmail leggings", "some chainmail leggings", "chainmail pants", "chain leggings", "chain pants" } },
            { { "chainmail boots", "a pair of chainmail boots", "chainmail shoes", "chain boots", "chain shoes" } },
            { { "iron helmets", "an iron helmet", "iron caps", "iron helms" } },
            { { "iron chestplates", "an iron chestplate", "iron tunics", "iron shirts" } },
            { { "iron leggings", "some iron leggings", "iron pants" } },
            { { "iron boots", "a pair of iron boots", "iron shoes" } },
            { { "diamond helmets", "a diamond helmet", "diamond caps", "diamond helms" } },
            { { "diamond chestplates", "a diamond chestplate", "diamond tunics", "diamond shirts" } },
            { { "diamond leggings", "some diamond leggings", "diamond pants" } },
            { { "diamond boots", "a pair of diamond boots", "diamond shoes" } },
            { { "golden helmets", "a golden helmet", "golden caps", "golden helms", "gold helmets", "gold caps", "gold helms" } },
            { { "golden chestplates", "a golden chestplate", "gold chestplates", "golden tunics", "gold tunics", "golden shirts", "gold shirts" } },
            { { "golden leggings", "some golden leggings", "gold leggings", "golden pants", "gold pants" } },
            { { "golden boots", "a pair of golden boots", "gold boots", "golden shoes", "gold shoes" } },
            { { "flint", "a piece of flint", "arrowheads" } },
            { { "raw porkchops", "a raw porkchop", "uncooked porkchops" } },
            { { "cooked porkchops", "a cooked porkchop", "porkchops" } },
            { { "paintings", "a painting", "artwork" } },
            {
                    { "golden apples", "a golden apple", "gold apples" },
                    { "enchanted golden apples", "an enchanted golden apple", "enchanted gold apples", "magic golden apples", "magic gold apples", "shiny golden apples",
                            "shiny gold apples", "shining golden apples", "shiny golden apples" } },
            { { "signs", "a sign", "sign posts", "posted signs", "wall signs" } },
            { { "wooden doors", "a wooden door", "wood doors" } },
            { { "buckets", "a bucket", "pails" } },
            { { "buckets of water", "a bucket of water", "water buckets" } },
            { { "buckets of lava", "a bucket of lava", "lava buckets" } },
            { { "minecarts", "a minecart", "mine carts", "minecars", "mine cars", "rail cars" } },
            { { "saddles", "a saddle", "pig saddles" } },
            { { "iron doors", "an iron door", "metal doors" } },
            { { "redstone dust", "some redstone dust", "redstone powder", "redstone" } },
            { { "snowballs", "a snow ball" } },
            { { "boats", "a boat", "wooden boats", "wood boats", "rafts", "wood rafts", "wooden rafts" } },
            { { "leather", "some leather", "cow hides", "cow skin", "cowskin" } },
            { { "milk", "some milk", "leche", "bucket of milk", "buckets of milk", "pail of milk", "pails of milk" } },
            { { "bricks", "a brick", "clay bricks" } },
            { { "clay", "a piece of clay" } },
            { { "sugarcane", "some sugarcane", "sugarcanes", "sugar canes" } },
            { { "papers", "a piece of paper" } },
            { { "books", "a book" } },
            { { "slimeballs", "a slimeball" } },
            { { "storage minecarts", "a storage minecart", "storage minecars", "storage mine cars", "storage rail cars", "chest minecarts", "chest minecars",
                    "chest mine cars", "chest rail cars", "minecarts with chests", "minecars with chests", "mine cars with chests", "rail cars with chests" } },
            { { "powered minecarts", "a powered minecart", "powered minecars", "powered mine cars", "powered rail cars", "furnace minecarts", "furnace minecars",
                    "furnace mine cars", "furnace rail cars", "minecarts with furnaces", "minecars with furnaces", "mine cars with furnaces", "rail cars with furnaces" } },
            { { "eggs", "an egg", "chicken eggs" } },
            { { "compasses", "a compass" } },
            { { "fishing rods", "a fishing rod", "fishing poles" } },
            { { "clocks", "a clock", "watches", "pocketwatches", "pocket watches" } },
            { { "glowstone dust", "some glowstone dust" } },
            { { "all raw fish", "a raw fish" },
                    { "raw cod", "a raw cod", "uncooked cod", "uncooked plain regular normal fish", "raw plain regular normal fish", "raw uncooked green fish" },
                    { "raw salmon", "a raw salmon", "uncooked salmon", "raw uncooked red fish" },
                    { "clownfish", "a clownfish", "raw clownfish", "anemonefish", "Nemo fish", "raw uncooked orange fish" },
                    { "pufferfish", "a pufferfish", "poisonfish", "water breathing fish", "balloonfish", "raw uncooked yellow fish" } },
            { { "all cooked fish", "a cooked fish" }, { "cooked cod", "a cooked cod", "cooked plain regular normal fish" },
                    { "cooked salmon", "a cooked salmon", "cooked red fish" } },
            {
                    { "dyes", "some dye", "wool dyes" },
                    { "ink sacks", "an ink sack", "squid ink sacks", "squid ink sackks", "squid ink pods", "black wool dyes" },
                    { "red dye", "some red dye", "rose red wool dyes" },
                    { "cactus green dye", "some cactus green dye", "cactus green dyes", "cactus green wool dyes" },
                    { "cocoa beans", "some cocoa beans", "chocolate beans", "brown dyes", "brown wool dyes" },
                    { "lapis lazuli", "a piece of lapis lazuli", "lapis lazuli dyes", "lapis lazuli wool dyes", "lapis dyes", "lapis wool dyes", "blue dyes",
                            "blue wool dyes", "dark blue dyes", "dark blue wool dyes" },
                    { "purple dye", "some purple dye", "purple dyes", "purple wool dyes" },
                    { "cyan dye", "some cyan dye", "cyan dyes", "cyan wool dyes" },
                    { "light gray dye", "some light gray dye", "light gray dyes", "light gray wool dyes", "light grey dyes", "light grey wool dyes" },
                    { "gray dye", "some gray dye", "dark gray dyes", "dark gray wool dyes", "dark grey dyes", "dark grey wool dyes" },
                    { "pink dye", "some pink dye", "pink dyes", "pink wool dyes", "light red dyes", "light red wool dyes" },
                    { "bright green dye", "some bright green dye", "bright green dyes", "lime dyes", "lime wool dyes", "lime green dyes", "lime green wool dyes",
                            "green dyes", "green wool dyes" },
                    { "yellow dye", "some yellow dye", "yellow dyes", "yellow wool dyes", "yellow flower dyes", "yellow flower wool dyes", "dandelion yellow dyes",
                            "dandelion yellow wool dyes", "yellow dandelion dyes", "yellow dandelion wool dyes" },
                    { "light blue dye", "light blue dyes", "light blue wool dyes" }, { "magenta dye", "some magenta dye", "magenta dyes", "magenta wool dyes" },
                    { "orange dye", "orange dyes", "orange wool dyes" },
                    { "bone meal", "some bone meal", "bone meals", "bonemeals", "white dyes", "white wool dyes", "wool bleaches" } },
            { { "bones", "a bone" } },
            { { "sugar", "some sugar", "processed sugar", "powdered sugar", "raw sugar", "baker's sugar" } },
            { { "cakes", "a cake", "birthday cakes" } },
            { { "beds", "a bed" } },
            { { "repeaters", "a repeater", "diodes", "delayers", "redstone repeaters", "redstone diodes", "redstone delayers" } },
            { { "cookies", "a cookie", "chocolate chip cookies", "oatmeal raisin cookies" } },
            { { "maps", "a map", "atlases", "charts" } },
            { { "shears", "some shears", "clippers" } },
            { { "melon slices", "a melon slice", "slices of melon" } },
            { { "pumpkin seeds", "some pumpkin seeds" } },
            { { "melon seeds", "some melon seeds" } },
            { { "raw beef", "a hunk of raw beef", "uncooked beef", "uncooked steak" } },
            { { "steak", "a steak", "beef", "cooked beef" } },
            { { "raw chickens", "a raw chicken", "uncooked chickens" } },
            { { "cooked chickens", "a cooked chicken", "chickens" } },
            { { "rotten flesh", "some rotten flesh", "rotted flesh", "flesh", "zombie flesh", "zombie meat" } },
            { { "Ender Pearls", "an Ender Pearl", "Enderpearls", "Enderman Pearls" } },
            { { "Blaze rods", "a Blaze rod", "glowsticks", "glow sticks" } },
            { { "Ghast tears", "a Ghast tear", "tears" } },
            { { "gold nuggets", "a gold nugget", "golden nuggets", "gold pieces", "pieces of gold" } },
            { { "Nether warts", "some Nether warts", "Nether mushrooms", "Nether 'shrooms", "Nether fungi" } },
            {
                    { "potions", "a potion" },
                    { "water bottles", "a water bottle", "canteens" },
                    { "Awkward Potions", "an Awkward Potion", "useless potions", "lame potions" },
                    { "Thick Potions", "a Thick Potion", "pots potions of thickness" },
                    { "Mundane Potions", "a Mundane Potion", "pots potions of mundaneness" },
                    { "Potions of Regeneration", "a Potion of Regeneration", "pots potions of regeneration" },
                    { "Potions of Swiftness", "a Potion of Swiftness", "pots potions of swiftness sprinting running fast" },
                    { "Potions of Fire Resistance", "a Potion of Fire Resistance",
                            "pots potions of no burning fire resistance lava swimming fireproof inflammable inflammability" },
                    { "Potions of Poison", "a Potion of Poison", "pots potions of poison toxic toxins" },
                    { "Potions of Healing", "a Potion of Healing", "pots potions of instant healing instaheal instahealth" },
                    { "Potions of Night Vision", "a Potion of Night Vision", "pots potions of nightvision" },
                    null,
                    { "Potions of Weakness", "a Potion of Weakness", "pots potions of weakness fraility frailness fragility fragile easily hurt" },
                    { "Potions of Strength", "a Potion of Strength", "pots potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman" },
                    { "Potions of Slowness", "a Potion of Slowness", "pots potions of slowness sloth laziness slow down slowing" },
                    null,
                    { "Potions of Harming", "a Potion of Harming", "pots potions of instaharming instahurting instapain instahealth loss" },
                    { "Potions of Water Breathing", "a Potion of Water Breathing", "pots potions of water breathing mermaid's breath" },
                    { "Potions of Invisibility", "a Potion of Invisibility", "pots potions of invisibility invisible no see clear hiding hidden pranking" },
                    { "Potions of Regeneration II", "a Potion of Regeneration II", "pots potions of regeneration II 2 two" },
                    { "Potions of Swiftness II", "a Potion of Swiftness II", "pots potions of swiftness sprinting running fast II 2 two" },
                    null,
                    { "Potions of Poison II", "a Potion of Poison II", "pots potions of poison toxic toxins II 2 two" },
                    { "Potions of Healing II", "a Potion of Healing II", "pots potions of instant healing instaheal instahealth II 2 two" },
                    { "Potions of Strength II", "a Potion of Strength II",
                            "pots potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman II 2 two" },
                    null,
                    null,
                    { "Potions of Harming II", "a Potion of Harming II", "pots potions of instaharming instahurting instapain instahealth loss II 2 two" },
                    { "Potions of Regeneration Extended", "a Potion of Regeneration Extended", "pots potions of regeneration Extended" },
                    { "Potions of Swiftness Extended", "a Potion of Swiftness Extended", "pots potions of swiftness sprinting running fast Extended" },
                    { "Potions of Fire Resistance Extended", "a Potion of Fire Resistance Extended",
                            "pots potions of no burning fire resistance lava swimming fireproof inflammable inflammability Extended" },
                    { "Potions of Poison Extended", "a Potion of Poison Extended", "pots potions of poison toxic toxins Extended" },
                    null,
                    { "Potions of Night Vision Extended", "a Potion of Night Vision Extended", "pots potions of nightvision Extended" },
                    null,
                    { "Potions of Weakness Extended", "a Potion of Weakness Extended", "pots potions of weakness fraility frailness fragility fragile easily hurt Extended" },
                    { "Potions of Strength Extended", "a Potion of Strength Extended",
                            "pots potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman Extended" },
                    { "Potions of Slowness Extended", "a Potion of Slowness Extended", "pots potions of slowness sloth laziness slow down slowing Extended" },
                    null,
                    null,
                    { "Potions of Water Breathing Extended", "a Potion of Water Breathing Extended", "pots potions of water breathing mermaid's breath Extended" },
                    { "Potions of Invisibility Extended", "a Potion of Invisibility Extended",
                            "pots potions of invisibility invisible no see clear hiding hidden pranking Extended" },
                    { "Potions of Regeneration II Extended", "a Potion of Regeneration II Extended", "pots potions of regeneration II 2 two Extended" },
                    { "Potions of Swiftness II Extended", "a Potion of Swiftness II Extended", "pots potions of swiftness sprinting running fast II 2 two Extended" },
                    null,
                    { "Potions of Poison II Extended", "a Potion of Poison II Extended", "pots potions of poison toxic toxins II 2 two Extended" },
                    { "Potions of Strength II Extended", "a Potion of Strength II Extended",
                            "pots potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman II 2 two Extended" },
                    { "Splash Potions of Regeneration", "a Splash Potion of Regeneration", "pots splash thrown potions of regeneration" },
                    { "Splash Potions of Swiftness", "a Splash Potion of Swiftness", "pots splash thrown potions of swiftness sprinting running fast" },
                    { "Splash Potions of Fire Resistance", "a Splash Potion of Fire Resistance",
                            "pots splash thrown potions of no burning fire resistance lava swimming fireproof inflammable inflammability" },
                    { "Splash Potions of Poison", "a Splash Potion of Poison", "pots splash thrown potions of poison toxic toxins" },
                    { "Splash Potions of Healing", "a Splash Potion of Healing", "pots splash thrown potions of instant healing instaheal instahealth" },
                    { "Splash Potions of Night Vision", "a Splash Potion of Night Vision", "pots splash thrown potions of nightvision" },
                    null,
                    { "Splash Potions of Weakness", "a Splash Potion of Weakness", "pots splash thrown potions of weakness fraility frailness fragility fragile easily hurt" },
                    { "Splash Potions of Strength", "a Splash Potion of Strength",
                            "pots splash thrown potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman" },
                    { "Splash Potions of Slowness", "a Splash Potion of Slowness", "pots splash thrown potions of slowness sloth laziness slow down slowing" },
                    null,
                    { "Splash Potions of Harming", "a Splash Potion of Harming", "pots splash thrown potions of instaharming instahurting instapain instahealth loss" },
                    { "Splash Potions of Water Breathing", "a Splash Potion of Water Breathing", "pots splash thrown potions of water breathing mermaid's breath" },
                    { "Splash Potions of Invisibility", "a Splash Potion of Invisibility",
                            "pots splash thrown potions of invisibility invisible no see clear hiding hidden pranking" },
                    { "Splash Potions of Regeneration II", "a Splash Potion of Regeneration II", "pots splash thrown potions of regeneration II 2 two" },
                    { "Splash Potions of Swiftness II", "a Splash Potion of Swiftness II", "pots splash thrown potions of swiftness sprinting running fast II 2 two" },
                    null,
                    { "Splash Potions of Poison II", "a Splash Potion of Poison II", "pots splash thrown potions of poison toxic toxins II 2 two" },
                    { "Splash Potions of Healing II", "a Splash Potion of Healing II", "pots splash thrown potions of instant healing instaheal instahealth II 2 two" },
                    { "Splash Potions of Strength II", "a Splash Potion of Strength II",
                            "pots splash thrown potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman II 2 two" },
                    null,
                    null,
                    { "Splash Potions of Harming II", "a Splash Potion of Harming II",
                            "pots splash thrown potions of instaharming instahurting instapain instahealth loss II 2 two" },
                    { "Splash Potions of Regeneration Extended", "a Splash Potion of Regeneration Extended", "pots splash thrown potions of regeneration Extended" },
                    { "Splash Potions of Swiftness Extended", "a Splash Potion of Swiftness Extended",
                            "pots splash thrown potions of swiftness sprinting running fast Extended" },
                    { "Splash Potions of Fire Resistance Extended", "a Splash Potion of Fire Resistance Extended",
                            "pots splash thrown potions of no burning fire resistance lava swimming fireproof inflammable inflammability Extended" },
                    { "Splash Potions of Poison Extended", "a Splash Potion of Poison Extended", "pots splash thrown potions of poison toxic toxins Extended" },
                    null,
                    { "Splash Potions of Night Vision Extended", "a Splash Potion of Night Vision Extended", "pots splash thrown potions of nightvision Extended" },
                    null,
                    { "Splash Potions of Weakness Extended", "a Splash Potion of Weakness Extended",
                            "pots splash thrown potions of weakness fraility frailness fragility fragile easily hurt Extended" },
                    { "Splash Potions of Strength Extended", "a Splash Potion of Strength Extended",
                            "pots splash thrown potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman Extended" },
                    { "Splash Potions of Slowness Extended", "a Splash Potion of Slowness Extended",
                            "pots splash thrown potions of slowness sloth laziness slow down slowing Extended" },
                    null,
                    null,
                    { "Splash Potions of Water Breathing Extended", "a Splash Potion of Water Breathing Extended",
                            "pots splash thrown potions of water breathing mermaid's breath Extended" },
                    { "Splash Potions of Invisibility Extended", "a Splash Potion of Invisibility Extended",
                            "pots splash thrown potions of invisibility invisible no see clear hiding hidden pranking Extended" },
                    { "Splash Potions of Regeneration II Extended", "a Splash Potion of Regeneration II Extended",
                            "pots splash thrown potions of regeneration II 2 two Extended" },
                    { "Splash Potions of Swiftness II Extended", "a Splash Potion of Swiftness II Extended",
                            "pots splash thrown potions of swiftness sprinting running fast II 2 two Extended" },
                    null,
                    { "Splash Potions of Poison II Extended", "a Splash Potion of Poison II Extended", "pots splash thrown potions of poison toxic toxins II 2 two Extended" },
                    { "Splash Potions of Strength II Extended", "a Splash Potion of Strength II Extended",
                            "pots splash thrown potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman II 2 two Extended" } },
            { { "glass bottles", "a glass bottle" } },
            { { "spider eyes", "a spider eye" } },
            { { "fermented spider eyes", "a fermented spider eye" } },
            { { "Blaze powder", "some Blaze powder" } },
            { { "Magma Cream", "some Magma Cream" } },
            { { "brewing stands", "a brewing stand" } },
            { { "cauldrons", "a cauldron", "kettles" } },
            { { "Eyes of Ender", "an Eye of Ender", "Endereyes" } },
            { { "glistening melon", "a glistening melon", "glistening melon slices", "gold melon slices", "golden melon slices", "shining melon slices" } },
            { { "spawn eggs", "a spawn egg", "spawner spawning eggs" }, { "creeper spawn eggs", "a creeper spawn egg", "creeper spawner spawning eggs" },
                    { "skeleton spawn eggs", "a skeleton spawn egg", "skeleton spawner spawning eggs" },
                    { "spider spawn eggs", "a spider spawn egg", "spider spawner spawning eggs" }, null,
                    { "zombie spawn eggs", "a zombie spawn egg", "human regular old zombies spawner spawning eggs" },
                    { "slime spawn eggs", "a slime spawn egg", "slime spawner spawning eggs" }, { "Ghast spawn eggs", "a Ghast spawn egg", "Ghast spawner spawning eggs" },
                    { "zombie pigman spawn eggs", "a zombie pigman spawn egg", "zombie zombified pigman pigmen spawner spawning eggs" },
                    { "Enderman spawn eggs", "an Enderman spawn egg", "Enderman Endermen spawner spawning eggs" },
                    { "cave spider spawn eggs", "a cave spider spawn egg", "cave spiders spawner spawning eggs" },
                    { "silverfish spawn eggs", "a silverfish spawn egg", "silverfish spawner spawning eggs" },
                    { "Blaze spawn eggs", "a Blaze spawn egg", "Blazes spawner spawning eggs" },
                    { "Magma Cube spawn eggs", "a Magma Cube spawn egg", "Magma Cubes spawner spawning eggs" }, null, null,
                    { "bat spawn eggs", "a bat spawn egg", "bats spawner spawning eggs" }, { "witch spawn eggs", "a witch spawn egg", "witches spawner spawning eggs" },
                    { "pig spawn eggs", "a pig spawn egg", "pigs piggies spawner spawning eggs" },
                    { "sheep spawn eggs", "a sheep spawn egg", "sheeps spawner spawning eggs" },
                    { "cow spawn eggs", "a cow spawn egg", "cows calfs calves spawner spawning eggs" },
                    { "chicken spawn eggs", "a chicken spawn egg", "chickens chicks spawner spawning eggs" },
                    { "squid spawn eggs", "a squid spawn egg", "squids spawner spawning eggs" },
                    { "wolf spawn eggs", "a wolf spawn egg", "wolfs wolves dogs spawner spawning eggs" },
                    { "mooshroom spawn eggs", "a mooshroom spawn egg", "mooshrooms cows calfs calves spawner spawning eggs" }, null,
                    { "ocelot spawn eggs", "an ocelot spawn egg", "ocelots cats kitties kittens spawner spawning eggs" },
                    { "villager spawn eggs", "a villager spawn egg", "villagers N.P.C. Testificates spawner spawning eggs" } },
            { { "Bottles o' Enchanting", "a Bottle o' Enchanting", "xp bottles", "exp bottles", "level botties", "experience bottles" } },
            { { "fire charges", "a fire charge", "fireballs", "cannonballs", "Ghast cannonballs", "Blaze cannonballs", "Ghast fireballs", "Blaze fireballs" } },
            { { "books and quills", "a book and quill", "book and quill" } },
            { { "written-in books", "a written-in book", "novels", "texts" } },
            { { "emeralds", "an emerald" } },
            { { "item frames", "an item frame", "frames" } },
            { { "flower pots", "a flower pot", "pots", "potted flowers plants" } },
            { { "carrots", "a carrot" } },
            { { "potatoes", "a potato", "raw potatoes" } },
            { { "baked potatoes", "a baked potato", "cooked potatoes", "mashed potatoes" } },
            { { "poisonous potatoes", "a poisonous potato", "poison potatoes", "bad potatoes" } },
            { { "maps", "a map", "charts", "atlases" } },
            { { "golden carrots", "a golden carrot", "gold carrots", "glistening carrots", "shiny carrots" } },
            { { "monster heads", "a monster head", "heads" }, { "skeleton skulls", "a skeleton skull", "skeleton heads", "skele heads", "skele skulls" },
                    { "Wither skeleton skulls", "a Wither skeleton skull", "Wither skeleleton heads", "Wither skele skulls", "Wither skele heads" },
                    { "zombie heads", "a zombie head" }, { "Steve heads", "a Steve head", "Minecraft Steve heads", "guy heads", "man heads", "person heads", "human heads" },
                    { "creeper heads", "a creeper head" } },
            { { "carrots on sticks", "a carrot on a stick", "carrots on fishing rods", "carrots on fishing poles", "pig controller" } },
            { { "Nether Stars", "a Nether star" } },
            { { "pumpkin pies", "a pumpkin pie" } },
            { { "fireworks", "a firework", "firework rockets" } },
            { { "firework stars", "a firework star", "firework color effect balls" } },
            { { "enchanted books", "an enchanted book", "magic spellbooks" } },
            { { "redstone comparators", "a redstone comparator", "redstone comparers" } },
            { { "Nether bricks", "a Nether brick", "individual single singular Nether bricks" } },
            { { "Nether Quartz", "some Nether Quartz", "Nether gems" } },
            { { "T.N.T. minecarts", "a T.N.T. minecart", "TNT trinitrotoluene minecarts" } },
            { { "hopper minecarts", "a hopper minecart", "vacuum pickup minecarts" } },
            { { "sets of iron horse armor", "a set of iron horse armor" } },
            { { "sets of gold horse armor", "a set of gold horse armor", "sets of golden horse armor" } },
            { { "sets of diamond horse armor", "a set of diamond horse armor" } },
            { { "leads", "a lead", "mob animal leads leashes" } },
            { { "name tags", "a name tag", "dog tags", "name plates", "nameplates" } },
            { { "command block minecarts", "a command block minecart", "command block mine carts", "command block minecars", "command block mine cars",
                    "command block rail cars" } }, { { "\"13\" music discs", "a \"13\" music disc", "\"13\" disks", "\"13\" records", "\"13\" CDs" } },
            { { "\"cat\" music discs", "\"cat\" disks", "\"cat\" records", "\"cat\" CDs" } },
            { { "\"blocks\" music discs", "a \"blocks\" music disc", "\"blocks\" disks", "\"blocks\" records", "\"blocks\" CDs" } },
            { { "\"chirp\" music discs", "\"chirp\" disks", "\"chirp\" records", "\"chirp\" CDs" } },
            { { "\"far\" music discs", "a \"chirp\" music disc", "\"far\" disks", "\"far\" records", "\"far\" CDs" } },
            { { "\"mall\" music discs", "a \"mall\" music disc", "\"mall\" disks", "\"mall\" records", "\"mall\" CDs" } },
            { { "\"mellohi\" music discs", "a \"mellohi\" music disc", "\"mellohi\" disks", "\"mellohi\" records", "\"mellohi\" CDs" } },
            { { "\"stal\" music discs", "a \"stal\" music disc", "\"stal\" disks", "\"stal\" records", "\"stal\" CDs" } },
            { { "\"strad\" music discs", "a \"strad\" music disc", "\"strad\" disks", "\"strad\" records", "\"strad\" CDs" } },
            { { "\"ward\" music discs", "a \"ward\" music disc", "\"ward\" disks", "\"ward\" records", "\"ward\" CDs" } },
            { { "\"11\" music discs", "an \"11\" music disc", "\"11\" disks", "\"11\" records", "\"11\" CDs" } },
            { { "\"wait\" music discs", "\"wait\" disks", "\"wait\" records", "\"wait\" CDs" } } },
            ENTITY_IDS = {
                    null,
                    { { "dropped items", "a dropped item", "miniblocks", "miniitems", "floating dropped thrown chucked tossed stuff things miniblocks miniitems" } },
                    { { "experience orbs", "an experience orb", "experience balls" } },
                    { { "lead knots", "a lead knot", "a leash knots", "a tie knots", "a rope knots" } },
                    { { "paintings", "a painting", "wall framed paintings" } },
                    { { "flying arrows", "a flying arrow", "shot fired arrows" } },
                    { { "thrown snowballs", "a thrown snowball", "chucked tossed thrown snowballs balls of snow" } },
                    { { "Ghast fireballs", "a Ghast fireball", "Ghast fired shot fireballs cannonballs explosive exploding fire charges" } },
                    { { "Blaze fireballs", "a Blaze fireball", "Blaze fired shot fireballs cannonballs explosive exploding fire charges" } },
                    { { "thrown Ender Pearls", "a thrown Ender Pearl", "Enderman Endermen thrown chucked fired Ender Pearls Enderpearls" } },
                    { { "thrown Eyes of Ender", "a thrown Eye of Ender", "Enderman Endermen thrown chucked fired Eyes of Ender Endereyes" } },
                    { { "thrown splash potions", "a thrown splash potion", "thrown chucked fired used potions pots" } },
                    { { "thrown Bottles o' Enchanting", "a thrown Bottle o' Enchanting", "xp bottle",
                            "thrown chucked fired used Bottles glasses o' of Enchanting experience levels" } },
                    { { "item frames", "an item frame" } },
                    { { "Wither skull projectiles", "a Wither skull projectile", "Wither bosses boss's skulls projectiles fired heads explosives exploding" } },
                    { { "T.N.T.", "some T.N.T.", "primed T.N.T.", "lit T.N.T.", "activated T.N.T.", "primed TNT", "lit TNT", "activated TNT", "primed trinitrotoluene",
                            "lit trinitrotoluene", "activated trinitrotoluene" } },
                    { { "falling blocks", "a falling block", "falling gravel", "falling sand", "falling anvils", "falling dragon eggs" } },
                    { { "fireworks", "a firework", "firework rockets" } },
                    { { "boats", "a boat", "dinghies", "dinghys", "ships" } },
                    { { "minecarts", "a minecart" } },
                    { { "storage minecarts", "a storage minecart", "minecrafts with storage", "minecarts with chests" } },
                    { { "powered minecarts", "a powered minecart", "gas powered minecarts", "coal powered minecarts", "furnace minecarts" } },
                    { { "T.N.T. minecarts", "a T.N.T. minecart", "explosive minecarts", "TNT minecarts", "trinitrotoluene minecarts" } },
                    { { "hopper minecarts", "a hopper minecart", "minecarts with hoppers", "vacuum minecarts" } },
                    { { "spawner minecarts", "a spawner minecart", "minecarts with spawners", "minecarts with monster spawners" } },
                    { { "generic mobs", "a generic mob", "mobs" } },
                    { { "generic monsters", "a generic monster", "monsters" } },
                    {
                            { "creepers", "a creeper", "exploding green penis monsters", "explosive green penis monsters" },
                            { "non-charged creepers", "a non-charged creeper", "unpowered regular normal average run-of-the-mill exploding explosive green penis monsters" },
                            { "charged creepers", "a charged creeper", "lightninged struck exploding green penis monsters",
                                    "lightninged struck explosive green penis monsters" } },
                    { { "skeletons", "a skeleton", "skeles", "skeleton archers" }, { "skeletons", "a skeleton", "skeles", "skeleton archers" },
                            { "Wither skeletons", "a Wither skeleton", "Wither skeles", "Wither skeletons", "swordsman skeletons", "swordsman skeles" } },
                    { { "spiders", "a spider", "giant spiders" } },
                    { { "giants", "a giant", "giant zombies" } },
                    { { "zombies", "a zombie" } },
                    { { "slimes", "a slime", "slime cubes", "living slime" } },
                    { { "Ghasts", "a Ghast", "giant floating jellyfish", "giant flying jellyfish", "giant floating squids", "giant flying squids" } },
                    { { "zombie pigmen", "a zombie pigman", "zombie pigman", "pigman zombies" } },
                    { { "Endermen", "Enderman" } },
                    { { "cave spiders", "a cave spider", "small spiders", "poisonous spiders", "venomous spiders" } },
                    { { "silverfish", "a silverfish", "bugs", "stronghold silverfish", "stronghold bugs" } },
                    { { "Blazes", "a Blaze", "Nether dungeon guards", "Nether stronghold guards", "Nether dungeon monsters", "Nether stronghold monsters" } },
                    { { "Magma Cubes", "a Magma Cube", "lava cubes", "Nether slimes", "living magma", "living lava" } },
                    { { "The Ender Dragon", "the big black scary dragon" } },
                    { { "The Wither", "The Wither", "The Wither boss" } },
                    { { "bats", "a bat" } },
                    { { "witches", "a witch", "wicked witches" } },
                    { { "pigs", "a pig", "piggies", "piglets" } },
                    { { "sheep", "a sheep", "lambs" }, { "white sheep", "a white sheep", "white lambs" }, { "orange sheep", "an orange sheep", "orange lambs" },
                            { "magenta sheep", "a magenta sheep", "magenta lambs" }, { "light blue sheep", "a light blue sheep", "light blue lambs" },
                            { "yellow sheep", "a yellow sheep", "yellow lambs" }, { "lime green sheep", "a lime green sheep", "lime green lambs" },
                            { "pink sheep", "a pink sheep", "pink lambs" },
                            { "gray sheep", "a gray sheep", "dark gray sheep", "dark grey sheep", "dark gray lambs", "dark grey lambs" },
                            { "light gray sheep", "a light gray sheep", "light gray lambs" }, { "cyan sheep", "a cyan sheep", "cyan lambs" },
                            { "purple sheep", "a purple sheep", "purple lambs" }, { "blue sheep", "a blue sheep", "dark blue sheep", "dark blue lambs" },
                            { "brown sheep", "a brown sheep", "brown lambs" },
                            { "cactus green sheep", "a cactus green sheep", "dark green sheep", "cactus green lambs", "dark green lambs" },
                            { "red sheep", "a red sheep", "red lambs" }, { "black sheep", "a black sheep", "black lambs", "nun sheep" } },
                    { { "cows", "a cow" } },
                    { { "chickens", "a chicken", "chicks" } },
                    { { "squid", "a squid", "squids", "octopi", "octopuses" } },
                    { { "wolves", "a wolf", "dogs", "wolfs", "hounds" } },
                    { { "mooshrooms", "a mooshroom", "mooshroom cows" } },
                    { { "snow golems", "a snow golem", "living snowmen" } },
                    { { "ocelots", "an ocelot", "jungle cats" } },
                    { { "iron golems", "an iron golem", "N.P.C. village guards", "NPC village guards", "Testificates village guards" } },
                    {
                            { "villagers", "a villager", "N.P.C.s", "NPCs", "Testificates" },
                            { "farmer villagers", "a farmer villager", "farmers villagers", "farmers N.P.C.s", "farmers NPCs", "farmers Testificates" },
                            { "librarian villagers", "a librarian villager", "librarians villagers", "librarians N.P.C.s", "librarians NPCs", "librarians Testificates" },
                            { "priest villagers", "a priest villager", "Minecraft priests villagers", "Minecraft priests N.P.C.s", "Minecraft priests NPCs",
                                    "Minecraft priests Testificates" },
                            { "butcher villagers", "a butcher villager", "butchers villagers", "butcher N.P.C.s", "butcher NPCs", "butcher testificates" },
                            { "blacksmith villagers", "a blacksmith villager", "blacksmiths villagers", "blacksmiths N.P.C.s", "blacksmiths NPCs", "blacksmiths Testificates" },
                            { "zombie villagers", "a zombie villager", "zombies villagers", "zombies N.P.C.s", "zombies NPCs", "zombies Testificates", "zombified villagers",
                                    "zombified N.P.C.s", "zombified NPCs", "zombified Testificates" } },
                    { { "Ender crystals", "an Ender crystal", "Ender Dragon shield generators" } } };
    public static final String[][] ENCHANTMENT_IDS = {
            { "Protection", "Environmental Protection", "normal Protection", "regular Protection", "general Protection" },
            { "Fire Protection", "lava protection", "burn protection", "magma protection" },
            { "Feather Falling", "falling protection", "landing protection", "soft shoes", "shoes", "boots" },
            { "Blast Protection", "explosion protection", "Creeper protection", "T.N.T. TNT protection" },
            { "Projectile Protection", "arrow protection", "skeleton protection" },
            { "Respiration", "extra breathingunderwater", "SCUBA gear", "increase lung capacity", "air bubble" },
            { "Aqua Affinity", "underwater worker", "underwater miner", "underwater digger" },
            { "Thorns", "reflect damage", "spiky armor", "chestplates" },
            { "Sharpness", "extra sword damage" },
            { "Smite", "undead killer", "zombie killer", "skeleton killer", "zombie pigmen killer", "zombie pigman killer", "extra sword damage to undead",
                    "extra sword damage to zombies", "extra sword damage to skeletons", "extra sword damage to skeles", "extra sword damage to zombie pigman",
                    "extra sword damage to zombie pigmen" },
            { "Bane of Arthropods", "cave spider killer", "extra sword damage to cave spiders" },
            { "Knockback", "sword throwback" },
            { "Fire Aspect", "sword flame", "fire sword" },
            { "Looting", "extra drops", "more rare drops", "more items", "extra items", "extra loots" },
            { "Efficiency", "efficeincy", "haste", "hasty mining", "hasty digging", "fast mining", "fast digging", "quickly digging", "quickly mining" },
            { "Silk Touch", "ice getter", "grass getter", "soft toucher", "soft miner", "soft digger", "careful miner", "careful digger" },
            { "Unbreaking", "longer life living", "more durability", "extra durability", "extra item health", "more item health", "longer lasting items" },
            { "Fortune", "more mining drops", "extra ores", "extra valuables", "extra diamonds", "extra gems", "more ores", "more valuables", "more diamonds", "more gems",
                    "riches" },
            { "Power", "extra bow damage", "stopping power", "stronger arrows", "extra arrow damage", "more bow damage", "more arrow damage", "sharpness arrows",
                    "sharper arrows" }, { "Punch", "knockback bows", "bow throwback" },
            { "Flame", "fire bow", "flame bow", "flaming arrows", "fire arrows", "incendiary rounds" },
            { "Infinity", "infinite ammo", "endless ammo", "infinite ammunitions", "endless ammunitions", "one arrow infinite arrows", "one arrow endless infinite arrows" } };
    /* the gap arrays are here to compensate for the gaps in I.D.s; for example, from the block I.D.s to the item I.D.s (starting at item #159 in this list since 159 is the
     * last block I.D.), there is a 98-number gap. There are two numbers in each item in this list: the item I.D. of the last item before the gap and the item I.D. of the
     * first item after the gap. The point of this is to avoid what I had originally, which was just an insanely long list of null values in ITEM_IDS itself */
    public static final short[] MUST_BE_ATTACHED_BOTTOM_ONLY_IDS = { 6, 26, 27, 28, 31, 32, 37, 38, 39, 40, 55, 59, 63, 64, 66, 70, 71, 72, 78, 81, 83, 93, 94, 104, 105, 111,
            115, 132, 140, 147, 148, 149, 150, 157 }, MUST_BE_ATTACHED_CAN_BE_SIDEWAYS_IDS = { 50, 65, 68, 69, 75, 76, 77, 96, 106, 127, 131 },
            CAN_BE_BROKEN_BY_LIQUIDS_IDS = { 0, 27, 28, 30, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 66, 69, 75, 76, 78, 93, 94, 104, 105, 106, 115, 127, 131, 132, 140, 141,
                    142, 144, 149, 150, 157 }, LOCKABLE_PORTAL_IDS = { 64, 71, 96, 107 }, LOCKABLE_SWITCH_IDS = { 28, 69, 70, 72, 77, 143, 147, 148 },
            LOCKABLE_CONTAINER_IDS = { 23, 54, -58, 61, 62, 84, -116, 117, 130, 137, 138, -145, 146, 154, 158 }, NON_SOLID_BLOCK_IDS = { 0, 6, 27, 28, 30, 31, 32, 37, 38, 39,
                    40, 50, 51, 55, 59, 63, 64, 65, 66, 68, 69, 70, 71, 72, 75, 76, 77, 78, 83, 90, 93, 94, 104, 105, 115, 119, 131, 132, 140, 141, 142, 142, 144, 147, 148,
                    149, 150, 157, 171, 175 }, SOLID_PARTIAL_HEIGHT_BLOCK_IDS = { 44, 93, 94, 96, 111, 126, 149, 150, 151 }, FENCE_HEIGHT_BLOCK_IDS = { 85, 107, 139 };
    public static final short[][] ITEM_GAPS = { { 164, 170 }, { 175, 256 }, { 408, 417 }, { 422, 2256 } }, ENTITY_GAPS = { { 2, 8 }, { 22, 41 }, { 66, 90 }, { 99, 120 },
            { 120, 200 } }, POTION_DATA_GAPS = { { 0, 16 }, { 16, 32 }, { 32, 64 }, { 64, 8193 }, { 8206, 8225 }, { 8229, 8233 }, { 8236, 8257 }, { 8270, 8289 },
            { 8292, 8297 }, { 8297, 16385 }, { 16398, 16417 }, { 16421, 16425 }, { 16428, 16449 }, { 16462, 16481 }, { 16484, 16489 } }, SPAWN_EGG_DATA_GAPS = { { -1, 50 },
            { 66, 90 }, { 98, 120 } }, ENCHANTMENT_GAPS = { { 7, 16 }, { 21, 32 }, { 35, 48 } }, DAMAGEABLE_ITEM_IDS = { { 256, 265 }, { 257, 265 }, { 258, 265 }, { 259 },
            { 261 }, { 267, 265 }, { 268, 5 }, { 269, 5 }, { 270, 5 }, { 271, 5 }, { 272, 4 }, { 273, 4 }, { 274, 4 }, { 275, 4 }, { 276, 264 }, { 277, 264 }, { 278, 264 },
            { 279, 264 }, { 283, 266 }, { 284, 266 }, { 285, 266 }, { 286, 266 }, { 290, 5 }, { 291, 4 }, { 292, 265 }, { 293, 264 }, { 294, 266 }, { 298, 334 },
            { 299, 334 }, { 300, 334 }, { 301, 334 }, { 302, 265 }, { 303, 265 }, { 304, 265 }, { 305, 265 }, { 306, 265 }, { 307, 265 }, { 308, 265 }, { 309, 265 },
            { 310, 264 }, { 311, 264 }, { 312, 264 }, { 313, 264 }, { 314, 266 }, { 315, 266 }, { 316, 266 }, { 317, 266 }, { 346 }, { 359 }, { 398 } };
    private static boolean auto_update = true;
    public static ArrayList<String> debuggers = new ArrayList<String>();

    /* TODO: finish information parts: recipes, and potion recipes (/potion); /potion with no parameters will tell you basics (splash potions need gunpowder, redstone extends
     * time, etc.) */
    /* TODO: make a method and command that will list all the items which can be crafted using a given item or items, prioritizing the list by putting items that need the
     * smallest amount of additional materials first */

    // DONE: added getEnchantment() methods
    // DONE: added romanNumeralToInteger() and integerToRomanNumeral() methods (Utils)
    // DONE: improved getResponse()
    // DONE: added combine() (Utils)
    // DONE: fixed getOtherHalfOfLargeChest()
    // DONE: added arrayListToList() and listToArrayList() methods
    // DONE: added getEntityName(String)
    // DONE: added new 1.7 items
    // DONE: made /id ignore more nothing words

    // plugin enable/disable and the command operator
    @Override
    public void onEnable() {
        mPW = this;
        server = getServer();
        console = server.getConsoleSender();
        loadTheConfig(console);
        if (auto_update)
            checkForUpdates(console);
        // done enabling
        String[] enable_messages =
                { "I'm like the Minecraft-opedia!", "I have info galore! I even have info about my info! Info is coming out of my .classes!",
                        "The Minecraft Library of Congress has got nothing on me." };
        myPluginUtils.tellOps(COLOR + enable_messages[(int) (Math.random() * enable_messages.length)], true);
    }

    @Override
    public void onDisable() {
        saveTheConfig(console, true);
        // done disabling
        String[] disable_messages =
                { "I hope you returned all your files, because we're closing the library for the day!", "This information kiosk is now closed.",
                        "All right. All done with work. Time to go home and read the Minecraft dictionary!" };
        myPluginUtils.tellOps(COLOR + disable_messages[(int) (Math.random() * disable_messages.length)], true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String command, String[] my_parameters) {
        parameters = my_parameters;
        if (command.equalsIgnoreCase("ids") || command.equalsIgnoreCase("id")) {
            id(sender);
            return true;
        } else if (command.equalsIgnoreCase("recipe") || command.equalsIgnoreCase("craft")) {
            if (parameters.length == 0)
                sender.sendMessage(ChatColor.RED + "You forgot to tell me what item you want the recipe for!");
            else
                getRecipe(sender);
            return true;
        } else if ((command.equalsIgnoreCase("myPluginWiki") || command.equalsIgnoreCase("mPW")) && parameters.length >= 1 && parameters[0].toLowerCase().startsWith("update")) {
            if (sender instanceof Player && !sender.isOp())
                if (command.equalsIgnoreCase("myPluginWiki"))
                    sender.sendMessage(ChatColor.RED + "Sorry, but you can't use " + COLOR + "/myPluginWiki update" + ChatColor.RED + ".");
                else
                    sender.sendMessage(ChatColor.RED + "Sorry, but you can't use " + COLOR + "/mPW update" + ChatColor.RED + ".");
            else if (parameters.length == 1)
                checkForUpdates(sender);
            else if (parameters[1].equalsIgnoreCase("on"))
                if (auto_update)
                    sender.sendMessage(ChatColor.RED + "I'm already checking for myPluginWiki updates.");
                else {
                    auto_update = true;
                    sender.sendMessage(COLOR + "All right. I would be happy to check for updates for you.");
                }
            else if (parameters[1].equalsIgnoreCase("off"))
                if (!auto_update)
                    sender.sendMessage(ChatColor.RED
                            + "I'm already not checking for myPluginWiki updates...but I'm not sure why you did that to begin with. Don't you want to expand your server's wealth of knowledge?");
                else {
                    auto_update = false;
                    sender.sendMessage(COLOR + "Fine. I won't check for updates any more, but I would advise that you let me. You might miss out on new knowledge.");
                }
            else
                return false;
            return true;
        } else if ((command.equalsIgnoreCase("mPW") || command.equalsIgnoreCase("myPluginWiki")) && parameters.length == 1 && parameters[0].equalsIgnoreCase("save")) {
            if (!(sender instanceof Player) || sender.hasPermission("mypluginwiki.admin"))
                saveTheConfig(sender, true);
            else if (command.equalsIgnoreCase("myPluginWiki"))
                sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + ChatColor.GREEN + "/myPluginWiki save" + ChatColor.RED + ".");
            else
                sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + ChatColor.GREEN + "/mPW save" + ChatColor.RED + ".");
            return true;
        } else if ((command.equalsIgnoreCase("mPW") || command.equalsIgnoreCase("myPluginWiki")) && parameters.length == 1 && parameters[0].equalsIgnoreCase("load")) {
            if (!(sender instanceof Player) || sender.hasPermission("mypluginwiki.admin"))
                loadTheConfig(sender);
            else if (command.equalsIgnoreCase("myPluginWiki"))
                sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + ChatColor.GREEN + "/myPluginWiki load" + ChatColor.RED + ".");
            else
                sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + ChatColor.GREEN + "/mPW load" + ChatColor.RED + ".");
            return true;
        }
        return false;
    }

    // working methods
    /** This method will tell whether or not a certain block will break if water or lava flows to it. No data value is required as input for this method because items with the
     * same I.D. consistently have this property in common.
     * 
     * @param id
     *            is the I.D. of the block that needs to be checked for the "can be broken by liquids" property.
     * @return <b>true</b> if the block given by the I.D. will break if water or lava flows to it, <b>false</b> if the block will hold back water or lava, or <b>null</b> if
     *         the I.D. given does not apply to a block at all.
     * @see {@link #canBeBrokenByLiquids(String) canBeBrokenByLiquids(String)} and {@link #canBeBrokenByLiquids(Block) canBeBrokenByLiquids(Block)} */
    public static Boolean canBeBrokenByLiquids(int id) {
        // return null if the I.D. doesn't belong to anything or is an item I.D. instead of a block I.D.
        if (getItemName(id, -1, false, false, true) == null || id >= 256)
            return null;
        for (int can_be_broken_by_liquids_ID : CAN_BE_BROKEN_BY_LIQUIDS_IDS)
            if (id == can_be_broken_by_liquids_ID)
                return true;
        return false;
    }

    /** This method returns a two-item Integer array or <b>null</b>. <tt>[0]</tt> is the I.D. of the item given by <tt>item_name</tt>. <tt>[1]</tt> is the data value of the
     * item given, e.g. 2 for birch wood (because all logs have the I.D. 17, but a data value of 2 refers to birch wood specifically). If <tt><b>item_name</tt></b> specifies a
     * general item name such as "logs", the data value returned will be -1.
     * 
     * @param item_name
     *            is the name of the item or block type that was specified split into separate words.
     * @param item_ID
     *            is <b>true</b> if this method should only return item I.D.s and not block type I.D.s, <b>false</b> if this method should only return block type I.D.s and not
     *            item I.D.s, or <b>null</b> if it should return either item I.D.s or block type I.D.s, in which case it will proritize item I.D.s over block type I.D.s.
     * @return the I.D. and numerical data value for the item given by name in <tt><b>item_name</tt></b> in a two-item Integer array or <tt><b>null</b></tt> if <b>1)</b> the
     *         item specified does not exist or <b>2)</b> the object specified is an item, not a block type, and it was specified in <tt><b>item_ID</b></tt> that this method
     *         should only return block types or vice versa.
     * @NOTE This method returns both the I.D. and the data value of an item based on the item's name because it encourages the programmer to only use this method once as
     *       necessary, not once to get the I.D. and again to get the data. It is a long, somewhat complex method and it must search through hundreds and hundreds of Strings
     *       in the <tt>ITEM_IDS</tt> array to find a match. This method should only be called when necessary and results returned by this method should be stored in a
     *       variable if needed more than once; do not simply call this method a second time.
     * @see {@link #getItemIdAndData(String, Boolean) getItemIdAndData(String, Boolean)}, {@link #getItemIdAndDataString(String[], Boolean) getItemIdAndDataString(String[],
     *      Boolean)}, and {@link #getItemIdAndDataString(String, Boolean) getItemIdAndDataString(String, Boolean)} */
    public static Integer[] getItemIdAndData(String[] item_name, Boolean item_ID) {
        Integer result_id = null, result_data = null, result_i = null;
        int start_id = 0;
        // if item_ID is true, we only want item I.D.s, so start searching at item I.D.s
        if (item_ID != null && item_ID)
            start_id = ITEM_GAPS[0][0] + 1;
        for (int check_id = start_id; check_id < ITEM_IDS.length; check_id++) {
            if (ITEM_IDS[check_id] != null)
                for (int check_data = 0; check_data < ITEM_IDS[check_id].length; check_data++)
                    if (ITEM_IDS[check_id][check_data] != null)
                        for (int i = 0; i < ITEM_IDS[check_id][check_data].length; i++) {
                            boolean contains_query = true;
                            String[] nothing_words = { "a", "an", "the", "some", "of", "o'", "with", "for", "in", "on" };
                            for (String word : item_name)
                                /* if word starts and ends with parentheses, it's a data suffix, so ignore it in the search; also ignore nothing words like prepositions and
                                 * articles */
                                if (!(word.startsWith("(") && word.endsWith(")")) && !myPluginUtils.contains(nothing_words, word.toLowerCase())
                                        && !ITEM_IDS[check_id][check_data][i].toLowerCase().contains(word.toLowerCase())) {
                                    contains_query = false;
                                    break;
                                }
                            // translation of this if statement: if the item contains the query and either we haven't found another result yet, the old result
                            // has a longer name than this new one, or the length of the names is the same but this new result is an item I.D. while the old one
                            // is a block I.D. and item_ID is null, then change the current result to this new item
                            if (contains_query
                                    && (result_id == null || ITEM_IDS[result_id][result_data][result_i].length() > ITEM_IDS[check_id][check_data][i].length() || (ITEM_IDS[result_id][result_data][result_i]
                                            .equals(ITEM_IDS[check_id][check_data][i])
                                            && item_ID == null && result_id < check_id))) {
                                result_id = check_id;
                                result_data = check_data;
                                result_i = i;
                            }
                        }
            // if ITEM_IDS is false, we don't want item I.D.s, so stop checking after we have checked the first part, the block I.D.s
            if (check_id > ITEM_GAPS[0][0] && item_ID != null && !item_ID)
                break;
        }
        // if we returned no results, it's possible that the object was "something with the I.D. [id](":"[data])"
        if (result_id == null || result_data == null)
            if (item_name[0].equalsIgnoreCase("something") && item_name[1].equalsIgnoreCase("with") && item_name[2].equalsIgnoreCase("the")
                    && item_name[3].equalsIgnoreCase("I.D."))
                try {
                    // try reading it as "something with the I.D. [id]"
                    result_id = Integer.parseInt(item_name[4]);
                    result_data = -1;
                    return new Integer[] { result_id, result_data };
                } catch (NumberFormatException exception) {
                    try {
                        // try reading it as "something with the I.D. [id]:[data]"
                        String[] id_and_data = item_name[4].split(":");
                        if (id_and_data.length != 2) {
                            console.sendMessage(ChatColor.DARK_RED + "Aww! Something went wrong! I couldn't get the I.D. and data from this object name.");
                            String item = "";
                            for (String word : item_name)
                                item += word + " ";
                            // the substring() here eliminates the extra space at the end
                            console.sendMessage(ChatColor.WHITE + "\"" + item.substring(0, item.length() - 1) + "\"");
                            return null;
                        }
                        result_id = Integer.parseInt(id_and_data[0]);
                        result_data = Integer.parseInt(id_and_data[1]);
                        return new Integer[] { result_id, result_data };
                    } catch (NumberFormatException exception2) {
                        console.sendMessage(ChatColor.DARK_RED + "Darn! Something went wrong! I couldn't get the I.D. and data from this object name.");
                        String item = "";
                        for (String word : item_name)
                            item += word + " ";
                        // the substring() here eliminates the extra space at the end
                        console.sendMessage(ChatColor.WHITE + "\"" + item.substring(0, item.length() - 1) + "\"");
                        return null;
                    }
                }
            else
                return null;
        else {
            // subtract 1 from the data to get the real data (remember: [0] is the general name and [1] is data = 0)
            result_data -= 1;
            // now we need to adjust the final result based on the gaps in I.D.s
            // for the I.D. gaps
            for (short[] gap : ITEM_GAPS)
                if (result_id > gap[0])
                    result_id += (gap[1] - gap[0] - 1);
                else
                    break;
            // if the item name contained a data suffix, read the data suffix to get the real data value
            if (item_name.length > 1 && item_name[item_name.length - 1].startsWith("(") && item_name[item_name.length - 1].endsWith(")"))
                try {
                    result_data = Integer.parseInt(item_name[item_name.length - 1].substring(1, item_name[item_name.length - 1].length() - 1));
                } catch (NumberFormatException exception) {
                    console.sendMessage(ChatColor.DARK_RED + "Oh, nos! I got an error trying to read the data suffix on this item name!");
                    String item = "";
                    for (String word : item_name)
                        item += word + " ";
                    // the substring() here eliminates the extra space at the end
                    console.sendMessage(ChatColor.WHITE + "\"" + item.substring(0, item.length() - 1) + "\"");
                    console.sendMessage(ChatColor.DARK_RED + "I read " + ChatColor.WHITE + "\""
                            + item_name[item_name.length - 1].substring(1, item_name[item_name.length - 1].length() - 1) + "\"" + ChatColor.DARK_RED
                            + " as the data value in the data suffix.");
                    exception.printStackTrace();
                }
            // only adjust the result data if there was no data suffix to get the true data from
            else {
                // for the potion data values gaps
                if (result_id == 373)
                    for (short[] gap : POTION_DATA_GAPS)
                        if (result_data > gap[0])
                            result_data += (gap[1] - gap[0] - 1);
                        else
                            break;
                // for the spawn egg data values gaps
                else if (result_id == 383)
                    for (short[] gap : SPAWN_EGG_DATA_GAPS)
                        if (result_data > gap[0])
                            result_data += (gap[1] - gap[0] - 1);
                        else
                            break;
            }
        }
        return new Integer[] { result_id, result_data };
    }

    /** This method returns the name of the item specified by the item or block type I.D. and data given.
     * 
     * @param id
     *            is the item or block type I.D.
     * @param data
     *            is the numerical data value for the item or block.
     * @param give_data_suffix
     *            specifies whether or not the name of the item should include the numerical data value at the end of the item name in parentheses (e.g.
     *            "a trapdoor <b>(16)</b>"). For logging purposes in myGuardDog, for example, we should be as specific as possible on information about the item, so this
     *            argument should be <b>true</b>. However, for messages to users for commands like <i>/id</i>, the data suffix is not helpful and looks awkward, so this
     *            argument should be <b>false</b>.
     * @param singular
     *            specifies whether the item name returned should be returned in the singular form (e.g. "a lever") or in the plural form (e.g. "levers"). Non-countable items
     *            like grass are the same as their plural forms, but with "some" at the beginning ("grass" --> "some grass").
     * @param without_article
     *            specifies whether or not the article should be excluded from the item name returned. Plural item names are preceded by "some"; singular item names can be
     *            preceded by "some", "a", "an", or "the".
     * @return the name of the item specified by the item or block type I.D. and data given.
     * @see {@link #getItemName(Block, boolean, boolean) getItemName(Block, boolean, boolean)} and {@link #getItemName(ItemStack, boolean, boolean) getItemName(ItemStack,
     *      boolean, boolean)} */
    public static String getItemName(int id, int data, boolean give_data_suffix, boolean singular, boolean without_article) {
        // return null if the potion data is inside a gap
        if (id == 373)
            for (short[] gap : POTION_DATA_GAPS)
                if (data > gap[0] && data < gap[1])
                    return null;
        // return null if the spawn egg data is inside a gap
        if (id == 383)
            for (short[] gap : SPAWN_EGG_DATA_GAPS)
                if (data > gap[0] && data < gap[1])
                    return null;
        // return null if the I.D. is inside a gap
        for (short[] gap : ITEM_GAPS)
            if (id > gap[0] && id < gap[1])
                return null;
        // we need to adjust the query I.D.s based on the gaps in I.D.s for the potion data values gaps
        if (id == 373)
            for (int i = POTION_DATA_GAPS.length - 1; i >= 0; i--)
                if (data >= POTION_DATA_GAPS[i][1])
                    data -= (POTION_DATA_GAPS[i][1] - POTION_DATA_GAPS[i][0] - 1);
        // for the spawn egg data values gaps
        if (id == 383)
            for (int i = SPAWN_EGG_DATA_GAPS.length - 1; i >= 0; i--)
                if (data >= SPAWN_EGG_DATA_GAPS[i][1])
                    data -= (SPAWN_EGG_DATA_GAPS[i][1] - SPAWN_EGG_DATA_GAPS[i][0] - 1);
        // for the item gaps
        for (int i = ITEM_GAPS.length - 1; i >= 0; i--)
            if (id >= ITEM_GAPS[i][1])
                id -= (ITEM_GAPS[i][1] - ITEM_GAPS[i][0] - 1);
        int sing_plur = 0;
        if (singular)
            sing_plur = 1;
        String item = null;
        // the Exceptions in this block of code can be ArrayIndexOutOfBoundsExceptions or NullPointerExceptions
        try {
            // try using the data and I.D. given
            item = ITEM_IDS[id][data + 1][sing_plur];
        } catch (ArrayIndexOutOfBoundsException exception) {
            // try first subtracting the data by 8 until we get a result
            for (int temp_data = data; temp_data >= 8; temp_data -= 8) {
                try {
                    item = ITEM_IDS[id][temp_data + 1][sing_plur];
                    break;
                } catch (Exception exception2) {
                    //
                }
            }
            // if that fails, try subtracting the data by 4 until we get a result
            if (item == null)
                for (int temp_data = data; temp_data >= 4; temp_data -= 8) {
                    try {
                        item = ITEM_IDS[id][temp_data + 1][sing_plur];
                        break;
                    } catch (Exception exception2) {
                        //
                    }
                }
            // if that fails, use the general term
            if (item == null)
                try {
                    item = ITEM_IDS[id][0][sing_plur];
                } catch (Exception exception2) {
                    return null;
                }
            if (item != null && give_data_suffix && data > 0)
                item += " (" + data + ")";
        } catch (NullPointerException exception) {
            return null;
        }
        if (item == null)
            return null;
        // if the item is singular and we want no article, remove the preexisting article
        if (singular && without_article)
            item = item.substring(item.split(" ")[0].length() + 1);
        // if the item is plural and we want an article, just add a "some" to the beginning
        else if (!singular && !without_article)
            item = "some " + item;
        return item;
    }

    /** This method returns a two-item Integer array or <b>null</b>. <tt>[0]</tt> is the I.D. of the entity given by <tt>entity_name</tt>. <tt>[1]</tt> is the data value of the
     * item given, e.g. 1 for charged creepers (because all creepers have the I.D. 50, but a data value of 1 refers to charged creepers specifically). If
     * <tt><b>entity_name</b></tt> specifies a general item name such as "creepers", the data value returned will be -1.
     * 
     * @param entity_name
     *            is the name of the entity that was specified split into separate words.
     * @return the I.D. and numerical data value for the item given by name in <tt><b>entity_name</tt></b> in a two-item Integer array or <tt><b>null</b></tt> if the item
     *         specified does not exist.
     * @see {@link #getEntityIdAndData(String) getEntityIdAndData(String)}, {@link #getEntityIdAndDataString(String[]) getEntityIdAndDataString(String[])}, and
     *      {@link #getEntityIdAndDataString(String) getEntityIdAndDataString(String)}
     * @NOTE This method returns both the I.D. and the data value of an entity based on the entity's name because it encourages the programmer to only use this method once as
     *       necessary, not once to get the I.D. and again to get the data. It is a long, somewhat complex method and it must search through hundreds of Strings in the
     *       <tt>ENTITY_IDS</tt> array to find a match. This method should only be called when necessary and results returned by this method should be stored in a variable if
     *       needed more than once; do not simply call this method a second time. */
    public static Integer[] getEntityIdAndData(String[] entity_name) {
        Integer result_id = null, result_data = null, result_i = null;
        for (int id = 0; id < ENTITY_IDS.length; id++)
            if (ENTITY_IDS[id] != null)
                for (int data = 0; data < ENTITY_IDS[id].length; data++)
                    if (ENTITY_IDS[id][data] != null)
                        for (int i = 0; i < ENTITY_IDS[id][data].length; i++) {
                            boolean contains_query = true;
                            for (String word : entity_name)
                                // if word starts and ends with parentheses, it's a data suffix, so ignore it in the search; also ignore articles
                                if (!(word.startsWith("(") && word.endsWith(")")) && !word.equalsIgnoreCase("a") && !word.equalsIgnoreCase("an")
                                        && !word.equalsIgnoreCase("the") && !word.equalsIgnoreCase("some")
                                        && !ENTITY_IDS[id][data][i].toLowerCase().contains(word.toLowerCase())) {
                                    contains_query = false;
                                    break;
                                }
                            // translation of this if statement: if the entity contains the query and either we haven't found another result yet, the old result
                            // has a longer name than this new one, or the length of the names is the same but this new result is an entity I.D. while the old
                            // one is a block I.D., then change the current result to this new entity
                            if (contains_query
                                    && (result_id == null || ENTITY_IDS[result_id][result_data][result_i].length() > ENTITY_IDS[id][data][i].length() || (ENTITY_IDS[result_id][result_data][result_i]
                                            .length() == ENTITY_IDS[id][data][i].length()
                                            && result_id < 256 && id >= 256))) {
                                result_id = id;
                                result_data = data;
                                result_i = i;
                            }
                        }
        if (result_id == null || result_data == null)
            if (entity_name[0].equalsIgnoreCase("something") && entity_name[1].equalsIgnoreCase("with") && entity_name[2].equalsIgnoreCase("the")
                    && entity_name[3].equalsIgnoreCase("I.D."))
                try {
                    // try reading it as "something with the I.D. [id]"
                    result_id = Integer.parseInt(entity_name[4]);
                    result_data = 0;
                    return new Integer[] { result_id, result_data };
                } catch (NumberFormatException exception) {
                    try {
                        // try reading it as "something with the I.D. [id]:[data]"
                        String[] id_and_data = entity_name[4].split(":");
                        if (id_and_data.length != 2) {
                            console.sendMessage(ChatColor.DARK_RED + "Aww! Something went wrong! I couldn't get the I.D. and data from this object name.");
                            String entity = "";
                            for (String word : entity_name)
                                entity += word + " ";
                            // the substring() here eliminates the extra space at the end
                            console.sendMessage(ChatColor.WHITE + "\"" + entity.substring(0, entity.length() - 1) + "\"");
                            return null;
                        }
                        result_id = Integer.parseInt(id_and_data[0]);
                        result_data = Integer.parseInt(id_and_data[1]);
                        return new Integer[] { result_id, result_data };
                    } catch (NumberFormatException exception2) {
                        console.sendMessage(ChatColor.DARK_RED + "Darn! Something went wrong! I couldn't get the I.D. and data from this object name.");
                        String entity = "";
                        for (String word : entity_name)
                            entity += word + " ";
                        // the substring() here eliminates the extra space at the end
                        console.sendMessage(ChatColor.WHITE + "\"" + entity.substring(0, entity.length() - 1) + "\"");
                        return null;
                    }
                }
            else
                return null;
        else {
            // subtract 1 from the data to get the real data (remember: [0] is the general name and [1] is data = 0)
            result_data -= 1;
            // if the entity name contained a data suffix, read the data suffix to get the real data value
            if (entity_name.length > 1 && entity_name[entity_name.length - 1].startsWith("(") && entity_name[entity_name.length - 1].endsWith(")"))
                try {
                    result_data = Integer.parseInt(entity_name[entity_name.length - 1].substring(1, entity_name[entity_name.length - 1].length() - 1));
                } catch (NumberFormatException exception) {
                    console.sendMessage(ChatColor.DARK_RED + "Oh, nos! I got an error trying to read the data suffix on this item name!");
                    String entity = "";
                    for (String word : entity_name)
                        entity += word + " ";
                    // the substring() here eliminates the extra space at the end
                    console.sendMessage(ChatColor.WHITE + "\"" + entity.substring(0, entity.length() - 1) + "\"");
                    console.sendMessage(ChatColor.DARK_RED + "I read " + ChatColor.WHITE + "\""
                            + entity_name[entity_name.length - 1].substring(1, entity_name[entity_name.length - 1].length() - 1) + "\"" + ChatColor.DARK_RED
                            + " as the data value in the data suffix.");
                    exception.printStackTrace();
                }
            // now we need to adjust the final result based on the gaps in I.D.s
            for (short[] gap : ENTITY_GAPS)
                if (result_id > gap[0])
                    result_id += (gap[1] - gap[0] - 1);
                else
                    break;
        }
        return new Integer[] { result_id, result_data };
    }

    /** This method returns the name of the entity specified by the I.D. and data given.
     * 
     * @param id
     *            is the entity type I.D.
     * @param data
     *            is the numerical data value for the entity. Data is only used for creepers states (charged vs. non-charged), villagers (professions), and sheep (colors).
     *            Giving a data value of -1 will result in the general name for such an entity, e.g. "sheep" rather than "white sheep" or "orange sheep".
     * @param give_data_suffix
     *            specifies whether or not the name of the entity should include the numerical data value at the end of the item name in parentheses (e.g.
     *            "a trapdoor <b>(16)</b>"). For logging purposes in myGuardDog, for example, we should be as specific as possible on information about the item, so this
     *            argument should be <b>true</b>. However, for messages to users for commands like <i>/eid</i>, the data suffix is not helpful and looks awkward, so this
     *            argument should be <b>false</b>.
     * @param singular
     *            specifies whether the entity name returned should be returned in the singular form (e.g. "a creeper") or in the plural form (e.g. "creepers"). Singular forms
     *            include an article at the beginning.
     * @param without_article
     *            specifies whether or not the article should be excluded from the item name returned. Plural item names are preceded by "some"; singular item names can be
     *            preceded by "some", "a", "an", or "the".
     * @return the name of the entity specified by the I.D. and data given.
     * @see {@link #getEntityName(Entity, boolean, boolean) getEntityName(Entity, boolean, boolean)} */
    public static String getEntityName(int id, int data, boolean give_data_suffix, boolean singular, boolean without_article) {
        // return null if the I.D. is inside a gap
        for (short[] gap : ENTITY_GAPS)
            if (id > gap[0] && id < gap[1])
                return null;
        // for the entity gaps
        for (int i = ENTITY_GAPS.length - 1; i >= 0; i--)
            if (id >= ENTITY_GAPS[i][1])
                id -= (ENTITY_GAPS[i][1] - ENTITY_GAPS[i][0] - 1);
        int sing_plur = 0;
        if (singular)
            sing_plur = 1;
        String entity = null;
        // the Exceptions in this block of code can be ArrayIndexOutOfBoundsExceptions or NullPointerExceptions
        try {
            // try using the data and I.D. given
            entity = ENTITY_IDS[id][data + 1][sing_plur];
        } catch (ArrayIndexOutOfBoundsException exception) {
            try {
                // if that doesn't work, try substracting 4 from the data until we can't any more and try again
                entity = ENTITY_IDS[id][data % 4 + 1][sing_plur];
                if (entity != null && give_data_suffix && data > 0)
                    entity += " (" + data + ")";
            } catch (Exception exception2) {
                try {
                    // if that doesn't work, try giving the general name for the entity with the I.D.
                    entity = ENTITY_IDS[id][0][sing_plur];
                    if (entity != null && give_data_suffix && data > 0)
                        entity += " (" + data + ")";
                } catch (Exception exception3) {
                    //
                }
            }
        } catch (NullPointerException exception) {
            //
        }
        if (entity == null)
            return null;
        // if the item is singular and we want no article, remove the preexisting article
        if (singular && without_article)
            entity = entity.substring(entity.split(" ")[0].length() + 1);
        // if the item is plural and we want an article, just add a "some" to the beginning
        else if (!singular && !without_article)
            entity = "some " + entity;
        return entity;
    }

    public static Integer getEnchantmentId(String[] enchantment_name) {
        Integer result_id = null, result_i = null;
        for (int id = 0; id < ENCHANTMENT_IDS.length; id++)
            if (ENCHANTMENT_IDS[id] != null)
                for (int i = 0; i < ENCHANTMENT_IDS[id].length; i++) {
                    boolean contains_query = true;
                    for (String word : enchantment_name)
                        // if word starts and ends with parentheses, it's a data suffix, so ignore it in the search; also ignore articles
                        if (!(word.startsWith("(") && word.endsWith(")")) && !word.equalsIgnoreCase("a") && !word.equalsIgnoreCase("an") && !word.equalsIgnoreCase("the")
                                && !word.equalsIgnoreCase("some") && !ENCHANTMENT_IDS[id][i].toLowerCase().contains(word.toLowerCase())) {
                            contains_query = false;
                            break;
                        }
                    // translation of this if statement: if the entity contains the query and either we haven't found another result yet, the old result
                    // has a longer name than this new one, or the length of the names is the same but this new result is an entity I.D. while the old
                    // one is a block I.D., then change the current result to this new entity
                    if (contains_query
                            && (result_id == null || ENCHANTMENT_IDS[result_id][result_i].length() > ENCHANTMENT_IDS[id][i].length() || (ENCHANTMENT_IDS[result_id][result_i]
                                    .length() == ENCHANTMENT_IDS[id][i].length()
                                    && result_id < 256 && id >= 256))) {
                        result_id = id;
                        result_i = i;
                    }
                }
        // if we returned no results, it's possible that the object was "something with the I.D. [id](":"[data])"
        if (result_id == null)
            if (enchantment_name.length > 3 && enchantment_name[0].equalsIgnoreCase("something") && enchantment_name[1].equalsIgnoreCase("with")
                    && enchantment_name[2].equalsIgnoreCase("the") && enchantment_name[3].equalsIgnoreCase("I.D."))
                try {
                    // try reading it as "something with the I.D. [id]"
                    result_id = Integer.parseInt(enchantment_name[4]);
                    return result_id;
                } catch (NumberFormatException exception) {
                    try {
                        // try reading it as "something with the I.D. [id]:[data]"
                        String[] id_and_data = enchantment_name[4].split(":");
                        if (id_and_data.length != 2) {
                            console.sendMessage(ChatColor.DARK_RED + "Aww! Something went wrong! I couldn't get the I.D. and data from this object name.");
                            String item = "";
                            for (String word : enchantment_name)
                                item += word + " ";
                            // the substring() here eliminates the extra space at the end
                            console.sendMessage(ChatColor.WHITE + "\"" + item.substring(0, item.length() - 1) + "\"");
                            return null;
                        }
                        result_id = Integer.parseInt(id_and_data[0]);
                        return result_id;
                    } catch (NumberFormatException exception2) {
                        console.sendMessage(ChatColor.DARK_RED + "Darn! Something went wrong! I couldn't get the I.D. and data from this object name.");
                        String item = "";
                        for (String word : enchantment_name)
                            item += word + " ";
                        // the substring() here eliminates the extra space at the end
                        console.sendMessage(ChatColor.WHITE + "\"" + item.substring(0, item.length() - 1) + "\"");
                        return null;
                    }
                }
            else
                return null;
        else {
            // now we need to adjust the final result based on the gaps in I.D.s for the I.D. gaps
            for (short[] gap : ENCHANTMENT_GAPS)
                if (result_id > gap[0])
                    result_id += (gap[1] - gap[0] - 1);
                else
                    break;
        }
        return result_id;
    }

    public static String getEnchantmentName(int id) {
        // return null if the I.D. is inside a gap
        for (short[] gap : ENCHANTMENT_GAPS)
            if (id > gap[0] && id < gap[1])
                return null;
        // account for the gaps in the Enchantment I.D.s
        for (int i = ENCHANTMENT_GAPS.length - 1; i >= 0; i--)
            if (id >= ENCHANTMENT_GAPS[i][1])
                id -= (ENCHANTMENT_GAPS[i][1] - ENCHANTMENT_GAPS[i][0] - 1);
        String enchantment = null;
        // the Exceptions in this block of code can be ArrayIndexOutOfBoundsExceptions or NullPointerExceptions
        try {
            // try using the data and I.D. given
            enchantment = ENCHANTMENT_IDS[id][0];
        } catch (ArrayIndexOutOfBoundsException exception) {
            return null;
        } catch (NullPointerException exception) {
            return null;
        }
        if (enchantment == null)
            return null;
        return enchantment;
    }

    public static Enchantment getEnchantment(String[] name) {
        Integer id = getEnchantmentId(name);
        if (id == null)
            return null;
        return Enchantment.getById(id);
    }

    public static String getEnchantmentFullName(Enchantment enchantment, int level) {
        String name = myPluginWiki.getEnchantmentName(enchantment);
        if (level > 1 || (level == 1 && enchantment.getMaxLevel() > 1))
            name += " " + myPluginUtils.integerToRomanNumeral(level);
        return name;
    }

    public static Block getOtherHalfOfLargeChest(Block first_half) {
        if (first_half.getType() != Material.CHEST && first_half.getType() != Material.TRAPPED_CHEST)
            return null;
        BlockFace[] relevant_block_faces = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST };
        for (BlockFace face : relevant_block_faces)
            if (first_half.getRelative(face).getType() == first_half.getType())
                return first_half.getRelative(face);
        return null;
    }

    /** <b>This method has not been written yet!</b> This method returns a four-line String describing the recipe for crafting the specified item indicated by the provided I.D.
     * This String can be color coded and displayed in the Minecraft chat or console to describe how to craft the item indicated.
     * 
     * @param id
     *            is the I.D. of the item or block for which the recipe was requested.
     * @param data
     *            is the numerical data value for the item or block. A value of -1 for <b><tt>data</b></tt> will cause this method to return the general recipe for the general
     *            item.
     * @return a four-line String describing the recipe for crafting the specified item indicated by the provided I.D.
     * @see {@link #getRecipe(String) getRecipe(String)} and {@link #getRecipe(ItemStack) getRecipe(ItemStack)} */
    public static String getRecipe(int id, int data) {
        // TODO
        return ChatColor.GOLD + "Coming soon to a server near you!";
    }

    /** This method will tell whether or not a certain block will break if the block that it is attached to is broken. No data value is required as input for this method
     * because items with the same I.D. consistently have this property in common.
     * 
     * @param id
     *            is the I.D. of the block that needs to be checked for the "must be attached" property.
     * @param bottom_only
     *            indicates whether the method should return <b>true</b> only <b>1)</b> if the item is one that must be attached on the bottom only like redstone wire or a
     *            lily pad (indicated by a <b>true</b> value), <b>2)</b> if the item is one that can be attached sideways like a torch or a wall sign (indicated by a
     *            <b>false</b> value), or <b>3)</b> if the item needs to be attached on the bottom or sideways (indicated by a <b>null</b> value).
     * @return <b>true</b> if the block given by the I.D. will break if the block it is attached to breaks and it attaches in the way indicated by <b> <tt>bottom_only</tt>
     *         </b>, <b>false</b> if the block does not need to be attached to another block or not in the way specified by <tt><b>bottom_only</tt></b>, and <b>null</b> if the
     *         I.D. given does not apply to a block at all.
     * @see {@link #mustBeAttached(String, Boolean) mustBeAttached(String, Boolean)} and {@link #mustBeAttached(Block, Boolean) mustBeAttached(Block, Boolean)} */
    public static Boolean mustBeAttached(int id, Boolean bottom_only) {
        // return null if the I.D. doesn't belong to anything or is an item I.D. instead of a block I.D.
        if (getItemName(id, -1, false, false, true) == null || id >= 256)
            return null;
        if (bottom_only == null || bottom_only)
            for (int must_be_attached_bottom_only_ID : MUST_BE_ATTACHED_BOTTOM_ONLY_IDS)
                if (must_be_attached_bottom_only_ID == id)
                    return true;
        if (bottom_only == null || !bottom_only)
            for (int must_be_attached_can_be_sideways_ID : MUST_BE_ATTACHED_CAN_BE_SIDEWAYS_IDS)
                if (must_be_attached_can_be_sideways_ID == id)
                    return true;
        return false;
    }

    /** This method will tell whether or not a certain block can be locked (meaning that it's either a container--a block that contain other items, e.g. a chest--or a switch--a
     * block that can be pressed or toggled on and off, e.g. a button--or a portal block--a block that can be opened and closed, e.g. a door). No data value is required as
     * input for this method because items with the same I.D. consistently have this property in common.
     * 
     * @param id
     *            is the I.D. of the block that needs to be checked for the "lockable" property.
     * @param has_inventory
     *            indicates whether the method should return <b>true</b> only <b>1)</b> if the block is a container (indicated by a <b>true</b> value), <b>2)</b> if the block
     *            is not a container (indicated by a <b>false</b> value), or <b>3)</b> if the block is any kind of lockable block (indicated by a <b>null</b> value).
     * @return <b>true</b> if the block given by the I.D. can be locked and it does or does not have an inventory in the way indicated by <b> <tt>has_inventory</tt></b>,
     *         <b>false</b> if the block cannot be locked or does or does not have an inventory opposite the requirement indicated by <tt><b>has_inventory</tt></b>, and
     *         <b>null</b> if the I.D. given does not apply to a block at all.
     * @see {@link #isLockable(String, Boolean) isLockable(String, Boolean)} and {@link #isLockable(Block, Boolean) isLockable(Block, Boolean)} */
    public static Boolean isLockable(int id) {
        if (getItemName(id, -1, false, true, true) == null)
            return null;
        else if (id >= 256)
            return false;
        for (int lockable : LOCKABLE_CONTAINER_IDS)
            // in LOCKABLE_CONTAINER_IDS, all the values are positive or negative depending on whether or not they can store items when the player exits the
            // block's inventory; therefore, we have to take the absolute value of the stored lockable container I.D. here
            if (Math.abs(lockable) == id)
                return true;
        for (int lockable : LOCKABLE_PORTAL_IDS)
            if (lockable == id)
                return true;
        for (int lockable : LOCKABLE_SWITCH_IDS)
            if (lockable == id)
                return true;
        return false;
    }

    public static Boolean isContainer(int id, Boolean can_store) {
        if (getItemName(id, -1, false, true, true) == null)
            return null;
        else if (id >= 256)
            return false;
        for (int lockable : LOCKABLE_CONTAINER_IDS)
            // in LOCKABLE_CONTAINER_IDS, all the values are positive or negative depending on whether or not they can store items when the player exits the
            // block's inventory; therefore, we have to take the absolute value of the stored lockable container I.D. here
            if (Math.abs(lockable) == id)
                if (can_store == null || can_store && lockable > 0 || !can_store && lockable < 0)
                    return true;
                else
                    return false;
        return false;
    }

    public static Boolean isPortal(int id) {
        if (getItemName(id, -1, false, true, true) == null)
            return null;
        else if (id >= 256)
            return false;
        for (int lockable : LOCKABLE_PORTAL_IDS)
            if (lockable == id)
                return true;
        return false;
    }

    public static Boolean isSwitch(int id) {
        if (getItemName(id, -1, false, true, true) == null)
            return null;
        else if (id >= 256)
            return false;
        for (int lockable : LOCKABLE_SWITCH_IDS)
            if (lockable == id)
                return true;
        return false;
    }

    /** This method will tell whether or not a certain block is solid. Mobs can walk on solid blocks without falling through them (e.g. grass blocks) while they simply fall
     * through non-solid blocks (e.g. signs). No data value is required as input for this method because items with the same I.D. consistently have this property in common.
     * 
     * @param id
     *            is the I.D. of the block that needs to be checked for the "solid" property.
     * @return <b>true</b> if the block given by the I.D. is solid, <b>false</b> if the block given is not solid, or <b>null</b> if the I.D. given does not apply to a block at
     *         all. */
    public static Boolean isSolid(int id) {
        if (getItemName(id, -1, false, true, true) == null)
            return null;
        for (int non_solid_ID : NON_SOLID_BLOCK_IDS)
            if (non_solid_ID == id)
                return false;
        return true;
    }

    /** This method checks to see if the given block is not the height of a full block. This can be true if <b>1)</b> the block is a half slab in the lower position, <b>2)</b>
     * the block is less than one full block tall, or <b>3)</b> the block directly below the given block is more than one full block tall such as a fence or fence gate.
     * 
     * @param block
     *            is the Block that is being checked for partial height.
     * @return <b>true</b> if <b><tt>block</b></tt> is */
    public static boolean topsAtPartialBlockHeight(Block block) {
        // if the block is a half slab, but it's in the higher position (data > 8), then it's not at half height, so return false
        if (block.getTypeId() == 44 && block.getData() >= 8)
            return false;
        for (int solid_partial_height_block_ID : SOLID_PARTIAL_HEIGHT_BLOCK_IDS)
            if (solid_partial_height_block_ID == block.getTypeId())
                return true;
        Block lower_block = block.getRelative(BlockFace.DOWN);
        if (lower_block == null)
            return false;
        for (int fence_height_block_ID : FENCE_HEIGHT_BLOCK_IDS)
            if (fence_height_block_ID == lower_block.getTypeId())
                return true;
        return false;
    }

    public static Boolean isDamageable(int id) {
        if (getItemName(id, 0, true, true, true) == null)
            return null;
        for (short[] damageable : DAMAGEABLE_ITEM_IDS)
            if (damageable[0] == id)
                return true;
        return false;
    }

    public static Boolean isRepairableWithSomethingBesidesItself(int id) {
        if (getItemName(id, 0, true, true, true) == null)
            return null;
        for (short[] damageable : DAMAGEABLE_ITEM_IDS)
            if (damageable[0] == id)
                if (damageable.length > 1)
                    return true;
                else
                    return false;
        return false;
    }

    public static Boolean isRepairableWith(int id, int id2) {
        if (getItemName(id, 0, true, true, true) == null || getItemName(id2, 0, true, true, true) == null)
            return null;
        for (short[] damageable : DAMAGEABLE_ITEM_IDS)
            if (damageable[0] == id)
                if (damageable.length > 1 && damageable[1] == id2)
                    return true;
                else
                    return false;
        return false;
    }

    // alternate input, output, or name methods
    /** This method returns a two-item Integer array or <b>null</b>. <tt>[0]</tt> is the I.D. of the item given by <tt>item_name</tt>. <tt>[1]</tt> is the data value of the
     * item given, e.g. 2 for birch wood (because all logs have the I.D. 17, but a data value of 2 refers to birch wood specifically). If <tt><b>item_name</b></tt> specifies a
     * general item name such as "logs", the data value returned will be -1.
     * 
     * @param item_name
     *            is the name of the item or block type that was specified split into separate words.
     * @param item_ID
     *            is <b>true</b> if this method should only return item I.D.s and not block type I.D.s, <b>false</b> if this method should only return block type I.D.s and not
     *            item I.D.s, or <b>null</b> if it should return either item I.D.s or block type I.D.s, in which case it will proritize item I.D.s over block type I.D.s.
     * @return the I.D. and numerical data value for the item given by name in <tt><b>item_name</tt></b> in a two-item Integer array or <tt><b>null</b></tt> if <b>1)</b> the
     *         item specified does not exist or <b>2)</b> the object specified is an item, not a block type, and it was specified in <tt><b>item_ID</b></tt> that this method
     *         should only return block types or vice versa.
     * @NOTE This method returns both the I.D. and the data value of an item based on the item's name because it encourages the programmer to only use this method once as
     *       necessary, not once to get the I.D. and again to get the data. It is a long, somewhat complex method and it must search through hundreds and hundreds of Strings
     *       in the <tt>ITEM_IDS</tt> array to find a match. This method should only be called when necessary and results returned by this method should be stored in a
     *       variable if needed more than once; do not simply call this method a second time.
     * @see {@link #getItemIdAndData(String[], Boolean) getItemIdAndData(String[], Boolean)}, {@link #getItemIdAndDataString(String[], Boolean)
     *      getItemIdAndDataString(String[], Boolean)}, and {@link #getItemIdAndDataString(String, Boolean) getItemIdAndDataString(String, Boolean)} */
    public static Integer[] getItemIdAndData(String item_name, Boolean item_ID) {
        return getItemIdAndData(item_name.replaceAll("_", " ").split(" "), item_ID);
    }

    /** This method returns a String describing the id and data of the item specified or <b>null</b>.
     * 
     * @param item_name
     *            is the name of the item or block type that was specified split into separate words.
     * @param item_ID
     *            is <b>true</b> if this method should only return item I.D.s and not block type I.D.s, <b>false</b> if this method should only return block type I.D.s and not
     *            item I.D.s, or <b>null</b> if it should return either item I.D.s or block type I.D.s, in which case it will proritize item I.D.s over block type I.D.s.
     * @return the I.D. and numerical data value for the item given by name in <tt><b>item_name</tt></b> in a String formatted as "[id]" if data < 1 or "[id]:[data]" otherwise
     *         or <tt><b>null</b></tt> if <b>1)</b> the item specified does not exist or <b>2)</b> the object specified is an item, not a block type, and it was specified in
     *         <tt><b>item_ID</b></tt> that this method should only return block types or vice versa.
     * @see {@link #getItemIdAndData(String[], Boolean) getItemIdAndData(String[], Boolean)}, {@link #getItemIdAndData(String, Boolean) getItemIdAndData(String, Boolean)}, and
     *      {@link #getItemIdAndDataString(String, Boolean) getItemIdAndDataString(String, Boolean)} */
    public static String getItemIdAndDataString(String[] item_name, Boolean item_ID) {
        Integer[] id_and_data = getItemIdAndData(item_name, item_ID);
        if (id_and_data == null)
            return null;
        String result = String.valueOf(id_and_data[0]);
        if (id_and_data[1] > 0)
            result += ":" + id_and_data[1];
        return result;
    }

    /** This method returns a String describing the id and data of the item specified or <b>null</b>. The String is equivalent to <tt>String.valueOf(</tt>the I.D. of the item
     * or block specified<tt>)</tt> if data < 1, but if data > 0, the String is formatted as "[id]:[data]". If <tt><b>item_name</b></tt> specifies a general item name such as
     * "logs", the data value returned will be -1; therefore, no data will be included in the String returned.
     * 
     * @param item_name
     *            is the name of the item or block type that was specified split into separate words.
     * @param item_ID
     *            is <b>true</b> if this method should only return item I.D.s and not block type I.D.s, <b>false</b> if this method should only return block type I.D.s and not
     *            item I.D.s, or <b>null</b> if it should return either item I.D.s or block type I.D.s, in which case it will proritize item I.D.s over block type I.D.s.
     * @return the I.D. and numerical data value for the item given by name in <tt><b>item_name</tt></b> in a String formatted as "[id]" if data < 1 or "[id]:[data]" otherwise
     *         or <tt><b>null</b></tt> if <b>1)</b> the item specified does not exist or <b>2)</b> the object specified is an item, not a block type, and it was specified in
     *         <tt><b>item_ID</b></tt> that this method should only return block types or vice versa.
     * @see {@link #getItemIdAndData(String[], Boolean) getItemIdAndData(String[], Boolean)}, {@link #getItemIdAndData(String, Boolean) getItemIdAndData(String, Boolean)}, and
     *      {@link #getItemIdAndDataString(String[], Boolean) getItemIdAndDataString(String[], Boolean)} */
    public static String getItemIdAndDataString(String item_name, Boolean item_ID) {
        return getItemIdAndDataString(item_name.replaceAll("_", " ").split(" "), item_ID);
    }

    /** This method returns the name of the item given.
     * 
     * @param item
     *            is the ItemStack which is being named.
     * @param give_data_suffix
     *            specifies whether or not the name of the item should include the numerical data value at the end of the item name in parentheses (e.g.
     *            "a trapdoor <b>(16)</b>"). For logging purposes in myGuardDog, for example, we should be as specific as possible on information about the item, so this
     *            argument should be <b>true</b>. However, for messages to users for commands like <i>/id</i>, the data suffix is not helpful and looks awkward, so this
     *            argument should be <b>false</b>.
     * @param singular
     *            specifies whether the item name returned should be returned in the singular form (e.g. "a lever") or in the plural form (e.g. "levers"). Singular forms
     *            include an article at the beginning. Non-countable items like grass are the same as their plural forms, but with "some" at the beginning ("grass" -->
     *            "some grass").
     * @param without_article
     *            specifies whether or not the article should be excluded from the item name returned. Plural item names are preceded by "some"; singular item names can be
     *            preceded by "some", "a", "an", or "the".
     * @return the name of the item specified by the item or block type I.D. and data given.
     * @see {@link #getItemName(int, int, boolean, boolean, boolean) getItemName(int, int, boolean, boolean, boolean)} and {@link #getItemName(Block, boolean, boolean)
     *      getItemName(Block, boolean, boolean)} */
    public static String getItemName(ItemStack item, boolean give_data_suffix, boolean singular, boolean without_article) {
        return getItemName(item.getTypeId(), item.getData().getData(), give_data_suffix, singular, without_article);
    }

    /** This method returns the name of the block given.
     * 
     * @param block
     *            is the Block which is being named.
     * @param give_data_suffix
     *            specifies whether or not the name of the item should include the numerical data value at the end of the item name in parentheses (e.g.
     *            "a trapdoor <b>(16)</b>"). For logging purposes in myGuardDog, for example, we should be as specific as possible on information about the item, so this
     *            argument should be <b>true</b>. However, for messages to users for commands like <i>/id</i>, the data suffix is not helpful and looks awkward, so this
     *            argument should be <b>false</b>.
     * @param singular
     *            specifies whether the item name returned should be returned in the singular form (e.g. "a lever") or in the plural form (e.g. "levers"). Singular forms
     *            include an article at the beginning. Non-countable items like grass are the same as their plural forms, but with "some" at the beginning ("grass" -->
     *            "some grass").
     * @param without_article
     *            specifies whether or not the article should be excluded from the item name returned. Plural item names are preceded by "some"; singular item names can be
     *            preceded by "some", "a", "an", or "the".
     * @return the name of the item specified by the item or block type I.D. and data given.
     * @see {@link #getItemName(int, int, boolean, boolean, boolean) getItemName(int, int, boolean, boolean, boolean)} and {@link #getItemName(ItemStack, boolean, boolean)
     *      getItemName(ItemStack, boolean, boolean)} */
    public static String getItemName(Block block, boolean give_data_suffix, boolean singular, boolean without_article) {
        return getItemName(block.getTypeId(), block.getData(), give_data_suffix, singular, without_article);
    }

    /** This method returns a two-item Integer array or <b>null</b>. <tt>[0]</tt> is the I.D. of the entity given by <tt>entity_name</tt>. <tt>[1]</tt> is the data value of the
     * item given, e.g. 1 for charged creepers (because all creepers have the I.D. 50, but a data value of 1 refers to charged creepers specifically). If
     * <tt><b>entity_name</b></tt> specifies a general item name such as "creepers", the data value returned will be -1.
     * 
     * @param entity_name
     *            is the name of the entity that was specified.
     * @return the I.D. and numerical data value for the item given by name in <tt><b>entity_name</tt></b> in a two-item Integer array or <tt><b>null</b></tt> if the item
     *         specified does not exist.
     * @see {@link #getEntityIdAndData(String[]) getEntityIdAndData(String[])}, {@link #getEntityIdAndDataString(String[]) getEntityIdAndDataString(String[])}, and
     *      {@link #getEntityIdAndDataString(String) getEntityIdAndDataString(String)}
     * @NOTE This method returns both the I.D. and the data value of an entity based on the entity's name because it encourages the programmer to only use this method once as
     *       necessary, not once to get the I.D. and again to get the data. It is a long, somewhat complex method and it must search through hundreds of Strings in the
     *       <tt>ENTITY_IDS</tt> array to find a match. This method should only be called when necessary and results returned by this method should be stored in a variable if
     *       needed more than once; do not simply call this method a second time. */
    public static Integer[] getEntityIdAndData(String entity_name) {
        return getEntityIdAndData(entity_name.replaceAll("_", " ").split(" "));
    }

    /** This method returns a String describing the id and data of the entity specified or <b>null</b>. The String is equivalent to <tt>String.valueOf(</tt>the I.D. of the item
     * or block specified<tt>)</tt> if data < 1, but if data > 0, the String is formatted as "[id]:[data]". If <tt><b>entity_name</b></tt> specifies a general item name such
     * as "thrown potions", the data value returned will be -1; therefore, no data will be included in the String returned.
     * 
     * @param entity_name
     *            is the name of the item or block type that was specified.
     * @return the I.D. and numerical data value for the entity given by name in <tt><b>entity_name</tt></b> in a String formatted as "[id]" if data < 1 or "[id]:[data]"
     *         otherwise or <tt><b>null</b></tt> if the entity specified does not exist.
     * @see {@link #getEntityIdAndData(String[], Boolean) getEntityIdAndData(String[], Boolean)}, {@link #getEntityIdAndData(String, Boolean) getEntityIdAndData(String,
     *      Boolean)}, and {@link #getEntityIdAndDataString(String, Boolean) getEntityIdAndDataString(String, Boolean)} */
    public static String getEntityIdAndDataString(String[] entity_name) {
        Integer[] id_and_data = getEntityIdAndData(entity_name);
        if (id_and_data == null)
            return null;
        String result = String.valueOf(id_and_data[0]);
        if (id_and_data[1] > 0)
            result += ":" + id_and_data[1];
        return result;
    }

    /** This method returns a String describing the id and data of the entity specified or <b>null</b>. The String is equivalent to <tt>String.valueOf(</tt>the I.D. of the item
     * or block specified<tt>)</tt> if data < 1, but if data > 0, the String is formatted as "[id]:[data]". If <tt><b>entity_name</b></tt> specifies a general item name such
     * as "thrown potions", the data value returned will be -1; therefore, no data will be included in the String returned.
     * 
     * @param entity_name
     *            is the name of the item or block type that was specified.
     * @return the I.D. and numerical data value for the entity given by name in <tt><b>entity_name</tt></b> in a String formatted as "[id]" if data < 1 or "[id]:[data]"
     *         otherwise or <tt><b>null</b></tt> if the entity specified does not exist.
     * @see {@link #getEntityIdAndData(String[], Boolean) getEntityIdAndData(String[], Boolean)}, {@link #getEntityIdAndData(String, Boolean) getEntityIdAndData(String,
     *      Boolean)}, and {@link #getEntityIdAndDataString(String[], Boolean) getEntityIdAndDataString(String[], Boolean)} */
    public static String getEntityIdAndDataString(String entity_name) {
        return getEntityIdAndDataString(entity_name.replaceAll("_", " ").split(" "));
    }

    /** This method returns the name of the entity specified.
     * 
     * @param entity
     *            is the Entity that will be named.
     * @param give_data_suffix
     *            specifies whether or not the name of the entity should include the numerical data value at the end of the item name in parentheses (e.g.
     *            "a trapdoor <b>(16)</b>"). For logging purposes in myGuardDog, for example, we should be as specific as possible on information about the item, so this
     *            argument should be <b>true</b>. However, for messages to users for commands like <i>/eid</i>, the data suffix is not helpful and looks awkward, so this
     *            argument should be <b>false</b>.
     * @param singular
     *            specifies whether the entity name returned should be returned in the singular form (e.g. "a creeper") or in the plural form (e.g. "creepers"). Singular forms
     *            include an article at the beginning.
     * @param without_article
     *            specifies whether or not the article should be excluded from the item name returned. Plural item names are preceded by "some"; singular item names can be
     *            preceded by "some", "a", "an", or "the".
     * @return the name of the entity specified.
     * @see {@link #getEntityName(int, int, boolean, boolean) getEntityName(int, int, boolean, boolean)} */
    public static String getEntityName(Entity entity, boolean give_data_suffix, boolean singular, boolean without_article) {
        int data = -1;
        if (entity.getType() == EntityType.VILLAGER)
            data = ((Villager) entity).getProfession().getId();
        else if (entity.getType() == EntityType.CREEPER)
            if (!((Creeper) entity).isPowered())
                data = 0;
            else
                data = 1;
        else if (entity.getType() == EntityType.SHEEP)
            // the data for the sheep is organized in the same way as the wool data; dye data goes in the opposite direction
            data = ((Sheep) entity).getColor().getWoolData();
        else if (entity.getType() == EntityType.PAINTING)
            data = ((Painting) entity).getAttachedFace().ordinal();
        else if (entity.getType() == EntityType.ITEM_FRAME)
            data = ((ItemFrame) entity).getAttachedFace().ordinal();
        return getEntityName(entity.getType().getTypeId(), data, give_data_suffix, singular, without_article);
    }

    /** This method returns the name of the entity type specified.
     * 
     * @param type
     *            is the EntityType that will be named. EntityTypes cannot contain special data such as sheep color or villager profession, so if possible, consider using
     *            {@link #getEntityName(Entity, boolean, boolean, boolean) getEntityName(Entity, boolean, boolean, boolean)} instead.
     * @param give_data_suffix
     *            specifies whether or not the name of the entity should include the numerical data value at the end of the item name in parentheses (e.g.
     *            "a trapdoor <b>(16)</b>"). For logging purposes in myGuardDog, for example, we should be as specific as possible on information about the item, so this
     *            argument should be <b>true</b>. However, for messages to users for commands like <i>/eid</i>, the data suffix is not helpful and looks awkward, so this
     *            argument should be <b>false</b>.
     * @param singular
     *            specifies whether the entity name returned should be returned in the singular form (e.g. "a creeper") or in the plural form (e.g. "creepers"). Singular forms
     *            include an article at the beginning.
     * @param without_article
     *            specifies whether or not the article should be excluded from the item name returned. Plural item names are preceded by "some"; singular item names can be
     *            preceded by "some", "a", "an", or "the".
     * @return the name of the entity type specified.
     * @see {@link #getEntityName(int, int, boolean, boolean) getEntityName(int, int, boolean, boolean)} */
    public static String getEntityName(EntityType type, boolean give_data_suffix, boolean singular, boolean without_article) {
        return getEntityName(type.getTypeId(), -1, give_data_suffix, singular, without_article);
    }

    public static String getEntityName(String incomplete_name, boolean give_data_suffix, boolean singular, boolean without_article) {
        Integer[] id_and_data = getEntityIdAndData(incomplete_name);
        if (id_and_data == null)
            return null;
        return getEntityName(id_and_data[0], id_and_data[1], give_data_suffix, singular, without_article);
    }

    public static Integer getEnchantmentId(Enchantment enchantment) {
        return enchantment.getId();
    }

    public static String getEnchantmentName(Enchantment enchantment) {
        return getEnchantmentName(enchantment.getId());
    }

    public static Integer getEnchantmentId(String name) {
        return getEnchantmentId(name.split(" "));
    }

    public static Enchantment getEnchantment(String name) {
        return getEnchantment(name.split(" "));
    }

    public static Enchantment getEnchantment(int id) {
        return Enchantment.getById(id);
    }

    /** This method returns a four-line String describing the recipe for crafting the specified item indicated by the provided name. This String can be color coded and
     * displayed in the Minecraft chat or console to describe how to craft the item indicated.
     * 
     * @param item_name
     *            is the name of the item specified
     * @return a four-line String describing the recipe for crafting the specified item indicated by the provided I.D.
     * @see {@link #getRecipe(int, int) getRecipe(int, int)} and {@link #getRecipe(ItemStack) getRecipe(ItemStack)} */
    public static String getRecipe(String item_name) {
        Integer[] id_and_data = getItemIdAndData(item_name, null);
        if (id_and_data == null)
            return null;
        return getRecipe(id_and_data[0], id_and_data[1]);
    }

    /** This method returns a four-line String describing the recipe for crafting the specified item. This String can be color coded and displayed in the Minecraft chat or
     * console to describe how to craft the item indicated.
     * 
     * @param item
     *            is the item specified.
     * @return a four-line String describing the recipe for crafting the specified item indicated by the provided I.D.
     * @see {@link #getRecipe(int, int) getRecipe(int, int)} and {@link #getRecipe(String) getRecipe(String)} */
    public static String getRecipe(ItemStack item) {
        return getRecipe(item.getTypeId(), item.getData().getData());
    }

    /** This method will tell whether or not the block specified will break if the block that it is attached to is broken.
     * 
     * @param block
     *            is the block that needs to be checked for the "must be attached" property.
     * @param bottom_only
     *            indicates whether the method should return <b>true</b> only <b>1)</b> if the item is one that must be attached on the bottom only like redstone wire or a
     *            lily pad (indicated by a <b>true</b> value), <b>2)</b> if the item is one that can be attached sideways like a torch or a wall sign (indicated by a
     *            <b>false</b> value), or <b>3)</b> if the item needs to be attached on the bottom or sideways (indicated by a <b>null</b> value).
     * @return <b>true</b> if the block given by the I.D. will break if the block it is attached to breaks and it attaches in the way indicated by <b> <tt>bottom_only</tt></b>
     *         or <b>false</b> if the block does not need to be attached to another block or not in the way specified by <tt><b>bottom_only</tt></b>.
     * @see {@link #mustBeAttached(int, Boolean) mustBeAttached(int, Boolean)} and {@link #mustBeAttached(String, Boolean) mustBeAttached(String, Boolean)} */
    public static Boolean mustBeAttached(Block block, Boolean bottom_only) {
        return mustBeAttached(block.getTypeId(), bottom_only);
    }

    /** This method will tell whether or not the block specified by the given name will break if the block that it is attached to is broken.
     * 
     * @param item_name
     *            is the name of the block that needs to be checked for the "must be attached" property.
     * @param bottom_only
     *            indicates whether the method should return <b>true</b> only <b>1)</b> if the item is one that must be attached on the bottom only like redstone wire or a
     *            lily pad (indicated by a <b>true</b> value), <b>2)</b> if the item is one that can be attached sideways like a torch or a wall sign (indicated by a
     *            <b>false</b> value), or <b>3)</b> if the item needs to be attached on the bottom or sideways (indicated by a <b>null</b> value).
     * @return <b>true</b> if the block given by the I.D. will break if the block it is attached to breaks and it attaches in the way indicated by <b> <tt>bottom_only</tt></b>
     *         or <b>false</b> if the block does not need to be attached to another block or not in the way specified by <tt><b>bottom_only</tt></b>.
     * @see {@link #mustBeAttached(int, Boolean) mustBeAttached(int, Boolean)} and {@link #mustBeAttached(Block, Boolean) mustBeAttached(Block, Boolean)} */
    public static Boolean mustBeAttached(String item_name, Boolean bottom_only) {
        Integer[] id = getItemIdAndData(item_name, false);
        if (id == null)
            return null;
        else
            return mustBeAttached(id[0], bottom_only);
    }

    /** This method will tell whether or not a certain block will break if water or lava flows to it.
     * 
     * @param block
     *            is the block that needs to be checked for the "can be broken by liquids" property.
     * @return <b>true</b> if the block given will break if water or lava flows to it or <b>false</b> if the block will hold back water or lava.
     * @see {@link #canBeBrokenByLiquids(String) canBeBrokenByLiquids(String)} and {@link #canBeBrokenByLiquids(Block) canBeBrokenByLiquids(Block)} */
    public static Boolean canBeBrokenByLiquids(Block block) {
        return canBeBrokenByLiquids(block.getTypeId());
    }

    /** This method will tell whether or not a certain block will break if water or lava flows to it.
     * 
     * @param item_name
     *            is the name of the block that needs to be checked for the "can be broken by liquids" property.
     * @return <b>true</b> if the block given by the name will break if water or lava flows to it or <b>false</b> if the block will hold back water or lava.
     * @see {@link #canBeBrokenByLiquids(int) canBeBrokenByLiquids(int)} and {@link #canBeBrokenByLiquids(Block) canBeBrokenByLiquids(Block)} */
    public static Boolean canBeBrokenByLiquids(String item_name) {
        return canBeBrokenByLiquids(getItemIdAndData(item_name, false)[0]);
    }

    /** This method will tell whether or not a certain block can be locked (meaning that it's either a container--a block that contain other items, e.g. a chest--or a switch--a
     * block that can be pressed or toggled on and off, e.g. a button--or a portal block--a block that can be opened and closed, e.g. a door).
     * 
     * @param block
     *            is the block that needs to be checked for the "lockable" property.
     * @param has_inventory
     *            indicates whether the method should return <b>true</b> only <b>1)</b> if the block is a container (indicated by a <b>true</b> value), <b>2)</b> if the block
     *            is not a container (indicated by a <b>false</b> value), or <b>3)</b> if the block is any kind of lockable block (indicated by a <b>null</b> value).
     * @return <b>true</b> if the block can be locked and it does or does not have an inventory in the way indicated by <b> <tt>has_inventory</tt></b> or <b>false</b> if the
     *         block cannot be locked or does or does not have an inventory opposite the requirement indicated by <tt><b>has_inventory</tt> </b>.
     * @see {@link #isLockable(int, Boolean) isLockable(int, Boolean)} and {@link #isLockable(String, Boolean) isLockable(String, Boolean)} */
    public static Boolean isLockable(Block block) {
        return isLockable(block.getTypeId());
    }

    /** This method will tell whether or not a certain block can be locked (meaning that it's either a container--a block that contain other items, e.g. a chest--or a switch--a
     * block that can be pressed or toggled on and off, e.g. a button--or a portal block--a block that can be opened and closed, e.g. a door).
     * 
     * @param block_name
     *            is the name of the type of block that needs to be checked for the "lockable" property.
     * @param has_inventory
     *            indicates whether the method should return <b>true</b> only <b>1)</b> if the block is a container (indicated by a <b>true</b> value), <b>2)</b> if the block
     *            is not a container (indicated by a <b>false</b> value), or <b>3)</b> if the block is any kind of lockable block (indicated by a <b>null</b> value).
     * @return <b>true</b> if the block can be locked and it does or does not have an inventory in the way indicated by <b> <tt>has_inventory</tt></b>, <b>false</b> if the
     *         block cannot be locked or does or does not have an inventory opposite the requirement indicated by <tt><b>has_inventory</tt> </b>, and <b>null</b> if the I.D.
     *         given does not apply to a block at all.
     * @see {@link #isLockable(int, Boolean) isLockable(int, Boolean)} and {@link #isLockable(Block, Boolean) isLockable(Block, Boolean)} */
    public static Boolean isLockable(String block_name) {
        Integer[] id_and_data = getItemIdAndData(block_name, false);
        if (id_and_data == null)
            return null;
        return isLockable(id_and_data[0]);
    }

    public static Boolean isContainer(Block block, Boolean can_store) {
        return isContainer(block.getTypeId(), can_store);
    }

    public static Boolean isContainer(String block_name, Boolean can_store) {
        Integer[] id_and_data = getItemIdAndData(block_name, false);
        if (id_and_data == null)
            return null;
        return isContainer(id_and_data[0], can_store);
    }

    public static Boolean isContainer(int id) {
        return isContainer(id, null);
    }

    public static Boolean isContainer(Block block) {
        return isContainer(block.getTypeId(), null);
    }

    public static Boolean isContainer(String block_name) {
        return isContainer(block_name, null);
    }

    public static Boolean isPortal(Block block) {
        return isPortal(block.getTypeId());
    }

    public static Boolean isPortal(String block_name) {
        Integer[] id_and_data = getItemIdAndData(block_name, false);
        if (id_and_data == null)
            return null;
        return isPortal(id_and_data[0]);
    }

    public static Boolean isSwitch(Block block) {
        return isSwitch(block.getTypeId());
    }

    public static Boolean isSwitch(String block_name) {
        Integer[] id_and_data = getItemIdAndData(block_name, false);
        if (id_and_data == null)
            return null;
        return isSwitch(id_and_data[0]);
    }

    public static boolean isDamageable(ItemStack item) {
        Boolean result = isDamageable(item.getTypeId());
        if (result == null) {
            myPluginUtils.tellOps(ChatColor.DARK_RED + "Someone just tried to see if this item was damageable, but I don't know what this item is! It has the I.D. "
                    + item.getTypeId() + ". Is myPluginWiki up to date?", true);
            return false;
        }
        return result;
    }

    public static Boolean isRepairable(int id) {
        return isDamageable(id);
    }

    public static boolean isRepairable(ItemStack item) {
        return isDamageable(item);
    }

    public static boolean isRepairableWithSomethingBesidesItself(ItemStack item) {
        Boolean result = isRepairableWithSomethingBesidesItself(item.getTypeId());
        if (result == null) {
            myPluginUtils.tellOps(ChatColor.DARK_RED
                    + "Someone just tried to see if this item was repairable with something besides itself, but I don't know what this item is! It has the I.D. "
                    + item.getTypeId() + ". Is myPluginWiki up to date?", true);
            return false;
        }
        return result;
    }

    public static boolean isRepairableWith(ItemStack item, ItemStack item2) {
        Boolean result = isRepairableWith(item.getTypeId(), item2.getTypeId());
        if (result == null) {
            myPluginUtils.tellOps(ChatColor.DARK_RED
                    + "Someone just tried to see if this item was repairable with this other item, but I don't know what this item is! One item has the I.D. "
                    + item.getTypeId() + " and the other has the I.D. " + item2.getTypeId() + ". Is myPluginWiki up to date?", true);
            return false;
        }
        return result;
    }

    public static boolean isRepairableWith(ItemStack item, Block block2) {
        Boolean result = isRepairableWith(item.getTypeId(), block2.getTypeId());
        if (result == null) {
            myPluginUtils.tellOps(ChatColor.DARK_RED
                    + "Someone just tried to see if this item was repairable with this block, but I don't know what this item is! The item has the I.D. " + item.getTypeId()
                    + " and the block has the I.D. " + block2.getTypeId() + ". Is myPluginWiki up to date?", true);
            return false;
        }
        return result;
    }

    public static Boolean isRepairableWith(ItemStack item, int id2) {
        return isRepairableWith(item.getTypeId(), id2);
    }

    public static Boolean isRepairableWith(int id, ItemStack item2) {
        return isRepairableWith(id, item2.getTypeId());
    }

    public static Boolean isRepairableWith(int id, Block block2) {
        return isRepairableWith(id, block2.getTypeId());
    }

    // loading
    public void loadTheConfig(CommandSender sender) {
        // check the config file
        File config_file = new File(getDataFolder(), "config.txt");
        try {
            if (!config_file.exists()) {
                getDataFolder().mkdir();
                sender.sendMessage(COLOR + "I couldn't find a config.txt file. I'll make a new one.");
                config_file.createNewFile();
                saveTheConfig(sender, false);
                return;
            }
            // read the config.txt file
            BufferedReader in = new BufferedReader(new FileReader(config_file));
            String save_line = in.readLine();
            while (save_line != null) {
                // skip empty lines
                while (save_line != null && save_line.trim().equals(""))
                    save_line = in.readLine();
                if (save_line == null)
                    break;
                save_line = save_line.trim();
                if (save_line.startsWith("Do you want myPluginWiki to check for updates every time it is enabled?"))
                    auto_update = myPluginUtils.getResponse(save_line.substring(71), in.readLine(), "Right now, myPluginWiki will auto-update.");
                save_line = in.readLine();
            }
            in.close();
        } catch (IOException exception) {
            sender.sendMessage(ChatColor.DARK_RED + "Oh, goodness me! An IOEcxeption in config.txt!");
            exception.printStackTrace();
        }
        saveTheConfig(sender, false);
        sender.sendMessage(COLOR + "Your configurations have been loaded.");
        if (sender instanceof Player)
            console.sendMessage(COLOR + sender.getName() + " loaded the myPluginWiki config from file.");
    }

    // saving
    public void saveTheConfig(CommandSender sender, boolean display_message) {
        File config_file = new File(getDataFolder(), "config.txt");
        // save the configurations
        try {
            if (!config_file.exists()) {
                getDataFolder().mkdir();
                sender.sendMessage(COLOR + "I couldn't find a config.txt file. I'll make a new one.");
                config_file.createNewFile();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(config_file));
            out.write("Do you want myPluginWiki to check for updates every time it is enabled? ");
            out.newLine();
            if (auto_update)
                out.write("   Right now, myPluginWiki will auto-update.");
            else
                out.write("   Right now, myPluginWiki will not auto-update! I REALLY think you should let it auto-update!");
            out.close();
        } catch (IOException exception) {
            sender.sendMessage(ChatColor.DARK_RED + "I got an IOException while trying to save your configurations.");
            exception.printStackTrace();
            return;
        }
        if (display_message) {
            sender.sendMessage(COLOR + "Your configurations have been saved.");
            if (sender instanceof Player)
                console.sendMessage(COLOR + ((Player) sender).getName() + " saved the server's configurations to file.");
        }
    }

    // plugin commands
    @SuppressWarnings("resource")
    private void checkForUpdates(CommandSender sender) {
        URL url = null;
        try {
            url = new URL("http://dev.bukkit.org/server-mods/realdrummers-mypluginwiki/files.rss/");
        } catch (MalformedURLException exception) {
            sender.sendMessage(ChatColor.DARK_RED + "I've never seen a U.R.L. like this in any of my readings!");
        }
        if (url != null) {
            String new_version_name = null, new_version_link = null;
            try {
                // Set header values intial to the empty string
                String title = "";
                String link = "";
                // First create a new XMLInputFactory
                XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                // Setup a new eventReader
                InputStream in = null;
                try {
                    in = url.openStream();
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "Our Internet resources on BukkitDev are not working.");
                    return;
                }
                XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
                // Read the XML document
                while (eventReader.hasNext()) {
                    XMLEvent event = eventReader.nextEvent();
                    if (event.isStartElement()) {
                        if (event.asStartElement().getName().getLocalPart().equals("title")) {
                            event = eventReader.nextEvent();
                            title = event.asCharacters().getData();
                            continue;
                        }
                        if (event.asStartElement().getName().getLocalPart().equals("link")) {
                            event = eventReader.nextEvent();
                            link = event.asCharacters().getData();
                            continue;
                        }
                    } else if (event.isEndElement()) {
                        if (event.asEndElement().getName().getLocalPart().equals("item")) {
                            new_version_name = title;
                            new_version_link = link;
                            // All done, we don't need to know about older
                            // files.
                            break;
                        }
                    }
                }
            } catch (XMLStreamException exception) {
                sender.sendMessage(ChatColor.DARK_RED + "I'm afraid that we have encountered a knowledge-hating XMLStreamException.");
                return;
            }
            boolean new_version_is_out = false;
            String version = getDescription().getVersion(), newest_online_version = "";
            if (new_version_name == null) {
                myPluginUtils.tellOps(ChatColor.DARK_RED + "Something seems to have gone awry while trying to retrieve the newest version of myPluginWiki.", true);
                return;
            }
            if (new_version_name.split("v").length == 2) {
                newest_online_version = new_version_name.split("v")[new_version_name.split("v").length - 1].split(" ")[0];
                // get the newest file's version number
                if (!version.contains("-DEV") && !version.contains("-PRE") && !version.equalsIgnoreCase(newest_online_version))
                    try {
                        if (Double.parseDouble(version) < Double.parseDouble(newest_online_version))
                            new_version_is_out = true;
                    } catch (NumberFormatException exception) {
                        //
                    }
            } else
                sender.sendMessage(ChatColor.RED + "Oh, no! REALDrummer didn't properly catalog the newest version of myPluginWiki! Please tell him to fix it immediately!");
            if (new_version_is_out) {
                String fileLink = null;
                try {
                    // Open a connection to the page
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(new_version_link).openConnection().getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null)
                        // Search for the download link
                        if (line.contains("<li class=\"user-action user-action-download\">"))
                            // Get the raw link
                            fileLink = line.split("<a href=\"")[1].split("\">Download</a>")[0];
                    reader.close();
                    reader = null;
                } catch (Exception exception) {
                    sender.sendMessage(ChatColor.DARK_RED + "Our Internet resources on BukkitDev are not working.");
                    exception.printStackTrace();
                    return;
                }
                if (fileLink != null) {
                    if (!new File(this.getDataFolder(), "myPluginWiki.jar").exists()) {
                        BufferedInputStream in = null;
                        FileOutputStream fout = null;
                        try {
                            getDataFolder().mkdirs();
                            // download the file
                            url = new URL(fileLink);
                            in = new BufferedInputStream(url.openStream());
                            fout = new FileOutputStream(this.getDataFolder().getAbsolutePath() + "/myPluginWiki.jar");
                            byte[] data = new byte[1024];
                            int count;
                            while ((count = in.read(data, 0, 1024)) != -1)
                                fout.write(data, 0, count);
                            myPluginUtils.tellOps(COLOR + "" + ChatColor.UNDERLINE + "The myPluginWiki library has increased its stores! It's now at v"
                                    + newest_online_version
                                    + ". Please replace your old myPluginWiki with the new one in your data folder and we'll increase our stores of information!", true);
                        } catch (Exception ex) {
                            sender.sendMessage(ChatColor.DARK_RED + "Oh, no! It seems myPluginWiki v" + newest_online_version
                                    + " has been released, but I can't retrieve the new information stores from BukkitDev! I'm afraid you'll have to go get it yourself.");
                        } finally {
                            try {
                                if (in != null)
                                    in.close();
                                if (fout != null)
                                    fout.close();
                            } catch (Exception ex) {
                                //
                            }
                        }
                    } else
                        sender.sendMessage(ChatColor.RED
                                + "Hey, all those library books are still lying useless in your data folder! Please put your new myPluginWiki on the server so that we can expand our knowledge of Minecraft!");
                }
            } else
                sender.sendMessage(COLOR + "No new books have been added to the library that is myPluginWiki.");
        }
    }

    private void getRecipe(CommandSender sender) {
        // assemble the query
        String query = "";
        for (String parameter : parameters)
            query = query + parameter.toLowerCase() + " ";
        query = query.substring(0, query.length() - 1);
        // get the item's I.D. and data
        int id = -1, data = -1;
        try {
            id = Integer.parseInt(query);
        } catch (NumberFormatException exception) {
            if (query.split(":").length == 2) {
                try {
                    id = Integer.parseInt(query.split(":")[0]);
                    data = Integer.parseInt(query.split(":")[1]);
                } catch (NumberFormatException exception2) {
                    id = -1;
                    data = -1;
                    Integer[] temp = getItemIdAndData(query, true);
                    if (temp[0] != null) {
                        id = temp[0];
                        data = temp[1];
                    }
                }
            } else {
                Integer[] temp = getItemIdAndData(query, true);
                if (temp[0] != null) {
                    id = temp[0];
                    data = temp[1];
                }
            }
        }
        String recipe = getRecipe(id, data), item_name = getItemName(id, data, false, false, true);
        if (recipe != null)
            sender.sendMessage(myPluginUtils.colorCode(recipe));
        else if (item_name != null)
            sender.sendMessage(COLOR + "You can't craft " + item_name + "!");
        else if (query.toLowerCase().startsWith("a") || query.toLowerCase().startsWith("e") || query.toLowerCase().startsWith("i") || query.toLowerCase().startsWith("o")
                || query.toLowerCase().startsWith("u"))
            sender.sendMessage(ChatColor.RED + "Sorry, but I don't know what an \"" + query + "\" is.");
        else
            sender.sendMessage(ChatColor.RED + "Sorry, but I don't know what a \"" + query + "\" is.");
    }

    private void id(CommandSender sender) {
        if (parameters.length == 0 || parameters[0].equalsIgnoreCase("this") || parameters[0].equalsIgnoreCase("that"))
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Block block = player.getTargetBlock(null, 1024);
                String block_name = getItemName(block, false, true, true), id_and_data = String.valueOf(block.getTypeId());
                if (block.getData() > 0)
                    id_and_data += ":" + block.getData();
                // send the message
                if (block_name != null)
                    player.sendMessage(COLOR + "That " + block_name + " you're pointing at has the I.D. " + id_and_data + ".");
                else {
                    player.sendMessage(ChatColor.RED + "Uh...what in the world " + ChatColor.ITALIC + "is" + ChatColor.RED + " that thing you're pointing at?");
                    player.sendMessage(ChatColor.RED + "Well, whatever it is, it has the I.D. " + id_and_data + ".");
                }
                String item_name = getItemName(player.getItemInHand(), false, player.getItemInHand().getAmount() <= 1, true);
                id_and_data = String.valueOf(player.getItemInHand().getTypeId());
                if (player.getItemInHand().getData().getData() > 0)
                    id_and_data += ":" + player.getItemInHand().getData().getData();
                // send the message
                if (item_name != null)
                    if (player.getItemInHand().getAmount() > 1)
                        player.sendMessage(COLOR + "Those " + item_name + " you're holding have the I.D. " + id_and_data + ".");
                    else
                        player.sendMessage(COLOR + "That " + item_name + " you're holding has the I.D. " + id_and_data + ".");
                else {
                    if (player.getItemInHand().getAmount() > 1)
                        player.sendMessage(ChatColor.RED + "Uh...what in the world " + ChatColor.ITALIC + "are" + ChatColor.RED + " those things you're holding?");
                    else
                        player.sendMessage(ChatColor.RED + "Uh...what in the world " + ChatColor.ITALIC + "is" + ChatColor.RED + " that thing you're holding?");
                    player.sendMessage(ChatColor.RED + "Well, whatever it is, it has the I.D. " + id_and_data + ".");
                }
            } else
                sender.sendMessage(ChatColor.RED + "You forgot to tell me what item or I.D. you want identified!");
        else {
            String query = "";
            for (String parameter : parameters)
                if (query.equals(""))
                    query = parameter;
                else
                    query += " " + parameter;
            // for simple I.D. queries
            try {
                int id = Integer.parseInt(query);
                String item_name = getItemName(id, -1, false, false, true);
                if (item_name != null)
                    // if the singular form uses the "some" artcile or the item name ends in "s" but not "ss" (like "wooden planks", but not like
                    // "grass"), the item name is a true plural
                    if (!getItemName(id, -1, false, true, false).startsWith("some ") || (item_name.endsWith("s") && !item_name.endsWith("ss")))
                        sender.sendMessage(COLOR + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " have the I.D. " + id + ".");
                    else
                        sender.sendMessage(COLOR + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " has the I.D. " + id + ".");
                else
                    sender.sendMessage(ChatColor.RED + "No item has the I.D. " + id + ".");
            } catch (NumberFormatException exception) {
                try {
                    String[] temp = query.split(":");
                    if (temp.length == 2) {
                        // for "[id]:[data]" queries
                        int id = Integer.parseInt(temp[0]), data = Integer.parseInt(temp[1]);
                        String item_name = getItemName(id, data, false, false, true);
                        // send the message
                        if (item_name != null)
                            // if the singular form uses the "some" artcile or the item name ends in "s" but not "ss" (like "wooden planks", but not like
                            // "grass"), the item name is a true plural
                            if (!getItemName(id, data, false, true, false).startsWith("some ") || (item_name.endsWith("s") && !item_name.endsWith("ss")))
                                sender.sendMessage(COLOR + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " have the I.D. " + query + ".");
                            else
                                sender.sendMessage(COLOR + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " has the I.D. " + query + ".");
                        else
                            sender.sendMessage(ChatColor.RED + "No item has the I.D. " + query + ".");
                    } else {
                        // for word queries
                        Integer[] id_and_data = getItemIdAndData(query, null);
                        if (id_and_data == null) {
                            if (query.toLowerCase().startsWith("a") || query.toLowerCase().startsWith("e") || query.toLowerCase().startsWith("i")
                                    || query.toLowerCase().startsWith("o") || query.toLowerCase().startsWith("u"))
                                sender.sendMessage(ChatColor.RED + "Sorry, but I don't know what an \"" + query + "\" is.");
                            else
                                sender.sendMessage(ChatColor.RED + "Sorry, but I don't know what a \"" + query + "\" is.");
                            return;
                        }
                        // this part seems odd because it seems like it's a long roundabout way to get item_name. You might think: isn't item_name the same as
                        // query? Wrong. A query can (and probably is) just a few letters from the name of the item. By finding the id, then using that to get
                        // the name, it's an effective autocompletion of the item name.
                        String item_name = getItemName(id_and_data[0], id_and_data[1], false, false, true), id_and_data_term = String.valueOf(id_and_data[0]);
                        if (id_and_data[1] > 0)
                            id_and_data_term += ":" + id_and_data[1];
                        // if it found it, send the message
                        if (!getItemName(id_and_data[0], id_and_data[1], false, true, false).startsWith("some ") || (item_name.endsWith("s") && !item_name.endsWith("ss")))
                            sender.sendMessage(COLOR + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " have the I.D. " + id_and_data_term + ".");
                        else
                            sender.sendMessage(COLOR + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " has the I.D. " + id_and_data_term + ".");
                    }
                } catch (NumberFormatException e) {
                    // for word queries
                    Integer[] id_and_data = getItemIdAndData(query, null);
                    if (id_and_data == null) {
                        if (query.toLowerCase().startsWith("a") || query.toLowerCase().startsWith("e") || query.toLowerCase().startsWith("i")
                                || query.toLowerCase().startsWith("o") || query.toLowerCase().startsWith("u"))
                            sender.sendMessage(ChatColor.RED + "Sorry, but I don't know what an \"" + query + "\" is.");
                        else
                            sender.sendMessage(ChatColor.RED + "Sorry, but I don't know what a \"" + query + "\" is.");
                        return;
                    }
                    // this part seems odd because it seems like it's a long roundabout way to get item_name. You might think: isn't item_name the same as
                    // query? Wrong. A query can (and probably is) just a few letters from the name of the item. By finding the id, then using that to get
                    // the name, it's an effective autocompletion of the item name.
                    String item_name = getItemName(id_and_data[0], id_and_data[1], false, false, true), id_and_data_term = String.valueOf(id_and_data[0]);
                    if (id_and_data[1] > 0)
                        id_and_data_term += ":" + id_and_data[1];
                    // if it found it, send the message
                    if (!getItemName(id_and_data[0], id_and_data[1], false, true, false).startsWith("some ") || (item_name.endsWith("s") && !item_name.endsWith("ss")))
                        sender.sendMessage(COLOR + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " have the I.D. " + id_and_data_term + ".");
                    else
                        sender.sendMessage(COLOR + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " has the I.D. " + id_and_data_term + ".");
                }
            }
        }
    }

}