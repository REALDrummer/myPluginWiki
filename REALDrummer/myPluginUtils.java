package REALDrummer;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("deprecation")
public class myPluginUtils {
    public static final String[] borders = { "[]", "\\/", "\"*", "_^", "-=", ":;", "&%", "#@", ",.", "<>", "~$", ")(", "+-", "|o" };

    /** This method sends a given message to everyone who is currently debugging this plugin. Players and the console can enter debugging mode using <i>/mUW debug</i>.
     * 
     * @param message
     *            is the <tt>String</tt> that will be sent as a message to any users currently debugging this plugin. */
    public static void debug(String message) {
        if (myPluginWiki.debuggers.size() == 0)
            return;
        if (myPluginWiki.debuggers.contains("console")) {
            myPluginWiki.console.sendMessage(myPluginWiki.COLOR + message);
            if (myPluginWiki.debuggers.size() == 1)
                return;
        }
        for (Player player : myPluginWiki.server.getOnlinePlayers())
            if (myPluginWiki.debuggers.contains(player.getName()))
                player.sendMessage(myPluginWiki.COLOR + message);
    }

    /** This method determines whether or not a given array contains a given Object.
     * 
     * @param objects
     *            is the array that will be searched.
     * @param target
     *            is the Object that <b><tt>objects</b></tt> may contain.
     * @return <b>true</b> if <b><tt>object</b></tt> contains <b><tt>target</b></tt>; <b>false</b> otherwise. */
    public static boolean contains(Object[] objects, Object target) {
        for (Object object : objects)
            if (object.equals(target))
                return true;
        return false;
    }

    /** This method uses the given <tt>Player</tt>'s location to calculate the block that they're pointing at. It works like the <tt>Player.getTargetBlock()</tt> method from
     * CraftBukkit, but it's better because it can skip non-solid blocks (optionally not switches) and it can see much further (max 500 blocks).
     * 
     * @param player
     *            is the <tt>Player</tt> that will be analyzed by this method to find the target block.
     * @param skip_switches
     *            will ignore all non-solid blocks (such as air, buttons, signs, and anything else non-liquid that you can walk through) if <b>true</b> and will ignore all
     *            non-solid blocks except for switch blocks (signs, buttons, pressure plates, etc.) if <b>false</b>.
     * @return the block that <tt><b>player</b></tt> is poitning at. */
    public static Block getTargetBlock(Player player) {
        // d is for distance from the player's eye location
        for (int d = 0; d < 5000; d++) {
            double yaw = player.getLocation().getYaw(), pitch = player.getLocation().getPitch();
            Location location =
                    new Location(player.getWorld(), player.getLocation().getX() + d / 10.0 * Math.cos(Math.toRadians(yaw + 90)) * Math.cos(Math.toRadians(-pitch)), player
                            .getEyeLocation().getY()
                            + d / 10.0 * Math.sin(Math.toRadians(-pitch)), player.getLocation().getZ() + d / 10.0 * Math.sin(Math.toRadians(yaw + 90))
                            * Math.cos(Math.toRadians(-pitch)));
            Block block = location.getBlock();
            // make sure the location isn't outside the bounds of the world
            if (block == null || Math.abs(location.getBlockX()) >= 2000000 || Math.abs(location.getBlockZ()) >= 2000000 || location.getY() < 0
                    || location.getY() > location.getWorld().getMaxHeight()) {
                debug("No good target found; search ended at " + writeLocation(location, true, true));
                return null;
            }
            // make sure the location is either not a non-solid block or, if we're not skipping switches, a switch
            if (!contains(myPluginWiki.NON_SOLID_BLOCK_IDS, (short) block.getTypeId())) {
                debug("found target block at " + writeLocation(block.getLocation(), true, true));
                return block;
            }
        }
        return null;
    }

    /** This method sends a given message to every operator currently on the server as well as to the console.
     * 
     * @param message
     *            is the message that will be sent to all operators and the console. <b><tt>Message</b></tt> will be color coded using myPluginUtils's
     *            {@link #colorCode(String) colorCode(String)} method.
     * @param also_tell_console
     *            indicates whether or not <b><tt>message</b></tt> will also be sent to the console.
     * @param exempt_ops
     *            is an optional parameter in which you may list any ops by exact username that should not receive <b><tt>message</b></tt>. */
    public static void tellOps(String message, boolean also_tell_console, String... exempt_ops) {
        for (Player player : myPluginWiki.server.getOnlinePlayers())
            if (player.isOp() && !contains(exempt_ops, player.getName()))
                player.sendMessage(colorCode(message));
        if (also_tell_console)
            myPluginWiki.console.sendMessage(colorCode(message));
    }

    public static void processException(Plugin plugin, String message, Throwable e) {
        // TODO: test processing "Caused by" scenarios
        tellOps(ChatColor.DARK_RED + message, true);
        /* skip stack trace lines until we get to the part with explicit line numbers and class names that don't come from Java's standard libraries; the stuff we're skipping
         * is anything that comes from the native Java code with no line numbers or class names that will help us pinpoint the issue */
        int lines_to_skip = 0;
        while (lines_to_skip < e.getStackTrace().length
                && (e.getStackTrace()[lines_to_skip].getLineNumber() < 0 || e.getStackTrace()[lines_to_skip].getClassName().startsWith("java")))
            lines_to_skip++;
        while (e != null) {
            // output a maximum of three lines of the stack trace
            tellOps(ChatColor.DARK_RED + e.getClass().getName().substring(e.getClass().getName().lastIndexOf('.') + 1) + " at line "
                    + e.getStackTrace()[lines_to_skip].getLineNumber() + " of " + e.getStackTrace()[lines_to_skip].getClassName() + ".java (" + plugin.getName() + ")", true);
            if (lines_to_skip + 1 < e.getStackTrace().length)
                tellOps(ChatColor.DARK_RED + "  ...and at line " + e.getStackTrace()[lines_to_skip + 1].getLineNumber() + " of "
                        + e.getStackTrace()[lines_to_skip + 1].getClassName() + ".java (" + plugin.getName() + ")", true);
            if (lines_to_skip + 2 < e.getStackTrace().length)
                tellOps(ChatColor.DARK_RED + "  ...and at line " + e.getStackTrace()[lines_to_skip + 2].getLineNumber() + " of "
                        + e.getStackTrace()[lines_to_skip + 2].getClassName() + ".java (" + plugin.getName() + ")", true);
            e = e.getCause();
            if (e != null)
                tellOps(ChatColor.DARK_RED + "...which was caused by:", true);
        }
    }

    /** This method replaces every instance of each String given in the text given with another String. This method has a few advantages over Java's standard
     * <tt>String.replaceAll(String, String)</tt> method: <b>1)</b> this method can replace multiple Strings with other Strings using a single method while
     * <tt>String.replaceAll(String, String)</tt> only has the ability to replace one String with one other String and <b>2)</b> this method treats brackets ("[]"), hyphens
     * ("-"), braces ("{}"), and other symbols normally whereas many of these symbols have special meanings in <tt>String.replaceAll(String, String)</tt>.
     * 
     * @param text
     *            is the text that must be modified.
     * @param changes
     *            are the changes that must be made to <b><tt>text</b></tt>. Every even-numbered item in this list will be replaced by the next (odd-numbered) String given;
     *            for example, if the four parameters given for <b><tt>changes</b></tt> are <tt>replaceAll(...,"wierd", "weird", "[player]", player.getName())</tt>, this
     *            method will replace all instances of "wierd" with "weird" and all instances of "[player]" with <tt>player.getName()</tt> in <b><tt>text</b></tt>.
     * @return <b><tt>text</b></tt> will all modifications given in <b><tt>changes</b></tt> made. */
    public static String replaceAll(String text, String... changes) {
        if (changes.length == 0)
            return text;
        for (int j = 0; j < changes.length; j += 2) {
            if (!text.toLowerCase().contains(changes[j].toLowerCase()))
                return text;
            for (int i = 0; text.length() >= i + changes[j].length(); i++) {
                if (text.substring(i, i + changes[j].length()).equalsIgnoreCase(changes[j])) {
                    text = text.substring(0, i) + changes[j + 1] + text.substring(i + changes[j].length());
                    i += changes[j + 1].length() - 1;
                }
                if (!text.toLowerCase().contains(changes[j].toLowerCase()))
                    break;
            }
        }
        return text;
    }

    /** This method combines all of the given <tt>String</tt>s array into a single String.
     * 
     * @param strings
     *            is the list of <tt>String</tt>s that will be combined into a signel <tt>String</tt>.
     * @param separator
     *            is the String used to separate the different <tt>String</tt>s, e.g. ", " in the list "apple, orange, lemon, melon"
     * @param indices
     *            is an optional parameter that can be used to select a range of indices in the array <b> <tt>strings</b></tt>. If one index is given, it will be used as the
     *            minimum index (inclusive) for parsing <b><tt>strings</b></tt> for adding pieces to the resultant <tt>String</tt>. If two indices are given, the first index
     *            is used as the minimum index (inclusive) and the second is used as the maximum (non-inclusive).
     * @return the <tt>String</tt> constructed by putting all the <tt>String</tt>s in <b><tt>strings</tt></b> together into one <tt>String</tt>. */
    public static String combine(String[] strings, String separator, int... indices) {
        if (separator == null)
            separator = "";
        int start_index = 0, end_index = strings.length;
        if (indices.length > 0) {
            start_index = indices[0];
            if (indices.length > 1)
                end_index = indices[1];
        }
        String combination = "";
        for (int i = start_index; i < end_index; i++) {
            try {
                combination += strings[i];
            } catch (ArrayIndexOutOfBoundsException e) {
                processException(myPluginWiki.mPW, "Someone gave me bad indices!", e);
            }
            if (i < end_index - 1)
                combination += separator;
        }
        return combination;
    }

    /** This method returns a grammatically correct list that contains all of the items given in a String array.
     * 
     * @param objects
     *            is the String array which will be written into a list.
     * @param options
     *            is an optional parameter that can allow the user to customize the String used to separate items in a 3+-item list (which is ", " by default) and/or the
     *            String used to separate the items in a 2-item list or the last item in a 3+-item list from the rest (which is "and" by default). The first option (
     *            <tt>[0]</tt>) is the separator String; the second option (<tt>[1]</tt>) is the final conjunction String.
     * @return a grammatically correct list of the objects in <b><tt>objects</b></tt>. */
    public static String writeArray(Object[] objects, String... options) {
        String separator = ", ", final_conjunction = "and";
        if (options.length > 0 && options[0] != null)
            separator = options[0];
        if (options.length > 1 && options[1] != null)
            final_conjunction = options[1];
        if (objects.length == 0)
            return "";
        else if (objects.length == 1)
            return String.valueOf(objects[0]);
        else if (objects.length == 2)
            return objects[0] + " " + final_conjunction + " " + objects[1];
        else {
            String list = "";
            for (int i = 0; i < objects.length; i++) {
                list += objects[i];
                if (i < objects.length - 1) {
                    list += separator;
                    if (i == objects.length - 2)
                        list += final_conjunction + " ";
                }
            }
            return list;
        }
    }

    public static String writeArrayList(ArrayList<String> objects, String... options) {
        String[] strings = new String[objects.size()];
        for (int i = 0; i < objects.size(); i++)
            if (objects.get(i) instanceof String)
                strings[i] = objects.get(i);
            else
                return null;
        return writeArray(strings);
    }

    /** This method separates items in a properly formatted list into individual Strings.
     * 
     * @param list
     *            is the String which will be divided into separate Strings by deconstructing the list framework.
     * @param options
     *            are optional parameters used to change the separator String and the final conjuction String. (By default, these are ", " and "and", respectively.) The first
     *            item is the separator String (the String used to separate the items in the list); the second item is a final conjunction String, a String which may be
     *            attached to the beginning of the last item in the list.
     * @return a String[] of all of the different items in the list given. */
    public static String[] readArray(String list, String... options) {
        String[] objects = null;
        String separator = ", ", final_conjunction = "and";
        if (options.length > 0 && options[0] != null)
            separator = options[0];
        if (options.length > 1 && options[1] != null)
            final_conjunction = options[1];
        // for 3+-item lists
        if (list.contains(separator)) {
            objects = list.split(separator);
            // remove the final conjunction (usually "and") at the beginning of the list object
            objects[objects.length - 1] = objects[objects.length - 1].substring(final_conjunction.length() + 1);
        }
        // for 2-item lists
        else if (list.contains(" " + final_conjunction + " "))
            return list.split(" " + final_conjunction + " ");
        // for 1-item lists
        else
            return new String[] { list };
        return objects;
    }

    public static ArrayList<String> readArrayList(String list, String... options) {
        String[] array = readArray(list, options);
        if (array == null)
            return null;
        ArrayList<String> product = new ArrayList<String>();
        for (String list_item : array)
            product.add(list_item);
        return product;
    }

    public static String writeLocation(Location location, boolean use_block_coordinates, boolean include_pitch_and_yaw) {
        // location format: ([x], [y], [z]) (facing ([pitch], [yaw])) in "[world]"
        String string = "(";
        if (use_block_coordinates)
            string += location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ") ";
        else
            string += location.getX() + ", " + location.getY() + ", " + location.getZ() + ") ";
        if (include_pitch_and_yaw && (location.getPitch() != 0 || location.getYaw() != 0))
            string += "facing (" + location.getPitch() + ", " + location.getYaw() + ") ";
        return string + "in \"" + location.getWorld().getWorldFolder().getName() + "\"";
    }

    public static Location readLocation(String string) {
        // location format: ([x], [y], [z]) ([facing/aiming at] ([pitch], [yaw])) in "[world]"
        String[] coordinates = string.substring(1, string.indexOf(')')).split(", ");
        float[] facing_coordinates = new float[] { 0, 0 };
        if (string.contains("facing") || string.contains("aiming at")) {
            String[] facing_coordinates_string;
            if (string.contains("facing"))
                facing_coordinates_string = string.substring(string.indexOf(" facing ") + 9, string.lastIndexOf(')')).split(", ");
            else
                facing_coordinates_string = string.substring(string.indexOf(" aiming at ") + 12, string.lastIndexOf(')')).split(", ");
            try {
                facing_coordinates = new float[] { Float.parseFloat(facing_coordinates_string[0]), Float.parseFloat(facing_coordinates_string[1]) };
            } catch (NumberFormatException e) {
                tellOps(ChatColor.DARK_RED + "I got an error trying to read the direction on this location String!\n" + ChatColor.WHITE + "\"" + string + "\"\n"
                        + "I read these as the coordinates: " + ChatColor.WHITE + "\"" + facing_coordinates[0] + "\", \"" + facing_coordinates[1] + "\"", true);
                return null;
            }
        }
        World world = myPluginWiki.server.getWorld(string.substring(string.indexOf(" in \"") + 5, string.lastIndexOf('"')));
        if (world == null) {
            tellOps(ChatColor.DARK_RED + "I got an error trying to read the world on this location String!\n" + ChatColor.WHITE + "\"" + string + "\"\n"
                    + "I read this as the world name: " + ChatColor.WHITE + "\"" + string.substring(string.indexOf(" in \"") + 5, string.length() - 1) + "\"", true);
            return null;
        }
        try {
            return new Location(world, Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]), Double.parseDouble(coordinates[2]), facing_coordinates[1],
                    facing_coordinates[0]);
        } catch (NumberFormatException e) {
            tellOps(ChatColor.DARK_RED + "I got an error trying to read this location String!\n" + ChatColor.WHITE + "\"" + string + "\"\n"
                    + "I read these as the coordinates: " + ChatColor.WHITE + "\"" + coordinates[0] + "\", \"" + coordinates[1] + "\", \"" + coordinates[2] + "\"", true);
            return null;
        }
    }

    public static int romanNumeralToInteger(String roman_numeral) {
        int value = 0;
        char[] chars = new char[] { 'M', 'D', 'C', 'L', 'X', 'V', 'I' };
        int[] values = new int[] { 1000, 500, 100, 50, 10, 5, 1 };
        while (roman_numeral.length() > 0) {
            char[] digits = roman_numeral.trim().toUpperCase().toCharArray();
            int digit_value = 0;
            for (int i = 0; i < chars.length; i++)
                if (digits[0] == chars[i])
                    digit_value = values[i];
            if (digit_value == 0)
                return 0;
            int zeroless = digit_value;
            while (zeroless >= 10)
                zeroless = zeroless / 10;
            if (digits[0] != chars[0] && zeroless == 1 && digits.length > 1) {
                // if the digit value starts with a 1 and it's not 'M', it could be being used to subtract from the subsequent digit (e.g. "IV"); however, this
                // can only be true if the subsequent digit has a greater value than the current one
                int next_digit_value = 0;
                for (int i = 0; i < chars.length; i++)
                    if (digits[1] == chars[i])
                        next_digit_value = values[i];
                if (next_digit_value == 0)
                    return 0;
                // so, if the current digit's value is less than the subsequent digit's value, the current digit's value must be subtracted, not added
                if (next_digit_value > digit_value)
                    value -= digit_value;
                else
                    value += digit_value;
            } else
                value += digit_value;
            roman_numeral = roman_numeral.substring(1).toLowerCase();
        }
        return value;
    }

    public static String integerToRomanNumeral(int value) {
        String roman_numeral = "";
        String[] chars = new String[] { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };
        int[] values = new int[] { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
        for (int i = 0; i < chars.length; i++)
            while (value >= values[i]) {
                roman_numeral += chars[i];
                value -= values[i];
            }
        return roman_numeral;
    }

    /** This is a simple auto-complete method that can take the first few letters of a player's name and return the full name of the player. It prioritizes in two ways:
     * <b>1)</b> it gives online players priority over offline players and <b>2)</b> it gives shorter names priority over longer usernames because if a player tries to
     * designate a player and this plugin returns a different name than the user meant that starts with the same letters, the user can add more letters to get the longer
     * username instead. If these priorities were reversed, then there would be no way to specify a user whose username is the first part of another username, e.g. "Jeb" and
     * "Jebs_bro". This matching is <i>not</i> case-sensitive.
     * 
     * @param name
     *            is the String that represents the first few letters of a username that needs to be auto-completed.
     * @return the completed username that begins with <b><tt>name</b></tt> (<i>not</i> case-sensitive) */
    public static String getFullName(String name) {
        String full_name = null;
        for (Player possible_owner : myPluginWiki.server.getOnlinePlayers())
            // if this player's name also matches and it shorter, return it instead becuase if someone is using an autocompleted command, we need to make sure
            // to get the shortest name because if they meant to use the longer username, they can remedy this by adding more letters to the parameter; however,
            // if they meant to do a shorter username and the auto-complete finds the longer one first, they're screwed
            if (possible_owner.getName().toLowerCase().startsWith(name.toLowerCase()) && (full_name == null || full_name.length() > possible_owner.getName().length()))
                full_name = possible_owner.getName();
        for (OfflinePlayer possible_owner : myPluginWiki.server.getOfflinePlayers())
            if (possible_owner.getName().toLowerCase().startsWith(name.toLowerCase()) && (full_name == null || full_name.length() > possible_owner.getName().length()))
                full_name = possible_owner.getName();
        return full_name;
    }

    /** This method is used to interpret the answers to questions.
     * 
     * @param unformatted_response
     *            is the raw String message that will be formatted in this message to be all lower case with no punctuation and analyzed for a "yes" or "no" answer.
     * @param current_status_line
     *            is for use with the <tt>config.txt</tt> questions only; it allows this method to default to the current status of a configuration if no answer is given to a
     *            <tt>config.txt</tt> question.
     * @param current_status_is_true_message
     *            is for use with the <tt>config.txt</tt> questions only; it allows this method to compare <b>current_status_line</b> to this message to determine whether or
     *            not the current status of the configuration handled by this config question is <b>true</b> or <b>false</b>.
     * @return <b>for chat responses:</b> <b>true</b> if the response matches one of the words or phrases in <tt>yeses</tt>, <b>false</b> if the response matches one of the
     *         words or phrases in <tt>nos</tt>, or <b>null</b> if the message did not seem to answer the question. <b>for <tt>config.txt</tt> question responses:</b>
     *         <b>true</b> if the answer to the question matches one of the words or phrases in <tt>yeses</tt>, <b>false</b> if the answer to the question matches one of the
     *         words or phrases in <tt>nos</tt>. If there is no answer to the question or the answer does not match a "yes" or a "no" response, it will return <b>true</b> if
     *         <b><tt>current_status_line</tt></b> matches <b> <tt>current_status_is_true_message</tt></b> or <b>false</b> if it does not. */
    public static Boolean getResponse(String unformatted_response, String current_status_line, String current_status_is_true_message) {
        String[] yeses =
                { "yes", "yea", "yep", "ja", "sure", "why not", "ok", "do it", "fine", "whatever", "w/e", "very well", "accept", "tpa", "cool", "hell yeah", "hells yeah",
                        "hells yes", "come", "k ", "kk" }, nos =
                { "no ", "nah", "nope", "no thanks", "no don't", "shut up", "ignore", "it's not", "its not", "creeper", "unsafe", "wait", "one ", "1 " };
        boolean said_yes = false, said_no = false;
        String formatted_response = unformatted_response;
        // elimiate unnecessary spaces and punctuation
        while (formatted_response.startsWith(" "))
            formatted_response = formatted_response.substring(1);
        while (formatted_response.endsWith(" "))
            formatted_response = formatted_response.substring(0, formatted_response.length() - 1);
        formatted_response = formatted_response.toLowerCase();
        // check their response
        for (String yes : yeses)
            if (formatted_response.startsWith(yes))
                said_yes = true;
        if (said_yes)
            return true;
        else {
            for (String no : nos)
                if (formatted_response.startsWith(no))
                    said_no = true;
            if (said_no)
                return false;
            else if (current_status_line != null)
                if (current_status_line.trim().startsWith(current_status_is_true_message))
                    return true;
                else
                    return false;
            else
                return null;
        }
    }

    /** This method can translate a String of time terms and values to a single int time in milliseconds (ms). It can interpret a variety of formats from "2d 3s 4m" to
     * "2 days, 4 minutes, and 3 seconds" to "2.375 minutes + 5.369s & 3.29days". Punctuation is irrelevant. Spelling is irrelevant as long as the time terms begin with the
     * correct letter. Order of values is irrelevant. (Days can come before seconds, after seconds, or both.) Repetition of values is irrelevant; all terms are simply
     * converted to ms and summed. Integers and decimal numbers are equally readable. The highest time value it can read is days; it cannot read years or months (to avoid the
     * complications of months' different numbers of days and leap years).
     * 
     * @param written
     *            is the String to be translated into a time in milliseconds (ms).
     * @return the time given by the String <b><tt>written</b></tt> translated into milliseconds (ms). */
    public static int translateStringtoTimeInms(String written) {
        int time = 0;
        String[] temp = written.split(" ");
        ArrayList<String> words = new ArrayList<String>();
        for (String word : temp)
            if (!word.equalsIgnoreCase("and") && !word.equalsIgnoreCase("&"))
                words.add(word.toLowerCase().replaceAll(",", ""));
        while (words.size() > 0) {
            // for formats like "2 days 3 minutes 5.57 seconds" or "3 d 5 m 12 s"
            try {
                double amount = Double.parseDouble(words.get(0));
                if (words.get(0).contains("d") || words.get(0).contains("h") || words.get(0).contains("m") || words.get(0).contains("s"))
                    throw new NumberFormatException();
                int factor = 0;
                if (words.size() > 1) {
                    if (words.get(1).startsWith("d"))
                        factor = 86400000;
                    else if (words.get(1).startsWith("h"))
                        factor = 3600000;
                    else if (words.get(1).startsWith("m"))
                        factor = 60000;
                    else if (words.get(1).startsWith("s"))
                        factor = 1000;
                    if (factor > 0)
                        // since a double of, say, 1.0 is actually 0.99999..., (int)ing it will reduce exact numbers by one, so I added 0.1 to it to avoid that.
                        time = time + (int) (amount * factor + 0.1);
                    words.remove(0);
                    words.remove(0);
                } else
                    words.remove(0);
            } catch (NumberFormatException e) {
                // if there's no space between the time and units, e.g. "2h, 5m, 25s" or "4hours, 3min, 2.265secs"
                double amount = 0;
                int factor = 0;
                try {
                    if (words.get(0).contains("d") && (!words.get(0).contains("s") || words.get(0).indexOf("s") > words.get(0).indexOf("d"))) {
                        amount = Double.parseDouble(words.get(0).split("d")[0]);
                        myPluginWiki.console.sendMessage("amount should=" + words.get(0).split("d")[0]);
                        factor = 86400000;
                    } else if (words.get(0).contains("h")) {
                        amount = Double.parseDouble(words.get(0).split("h")[0]);
                        factor = 3600000;
                    } else if (words.get(0).contains("m")) {
                        amount = Double.parseDouble(words.get(0).split("m")[0]);
                        factor = 60000;
                    } else if (words.get(0).contains("s")) {
                        amount = Double.parseDouble(words.get(0).split("s")[0]);
                        factor = 1000;
                    }
                    if (factor > 0)
                        // since a double of, say, 1.0 is actually 0.99999..., (int)ing it will reduce exact numbers by one, so I added 0.1 to it to avoid that.
                        time = time + (int) (amount * factor + 0.1);
                } catch (NumberFormatException e2) {
                    //
                }
                words.remove(0);
            }
        }
        return time;
    }

    /** This method is the inverse counterpart to the {@link #translateTimeInmsToString(long, boolean) translateStringToTimeInms()} method. It can construct a String to
     * describe an amount of time in ms in an elegant format that is readable by the aforementioned counterpart method as well as human readers.
     * 
     * @param time
     *            is the time in milliseconds (ms) that is to be translated into a readable phrase.
     * @param round_seconds
     *            determines whether or not the number of seconds should be rounded to make the phrase more elegant and readable to humans. This parameter is normally false if
     *            this method is used to save data for the plugin because we want to be as specific as possible; however, for messages sent to players in game, dropping excess
     *            decimal places makes the phrase more friendly and readable.
     * @return a String describing <b><tt>time</b></tt> */
    public static String translateTimeInmsToString(int time, boolean round_seconds) {
        // get the values (e.g. "2 days" or "55.7 seconds")
        ArrayList<String> values = new ArrayList<String>();
        if (time > 86400000) {
            if ((int) (time / 86400000) > 1)
                values.add((int) (time / 86400000) + " days");
            else
                values.add("1 day");
            time = time % 86400000;
        }
        if (time > 3600000) {
            if ((int) (time / 3600000) > 1)
                values.add((int) (time / 3600000) + " hours");
            else
                values.add("1 hour");
            time = time % 3600000;
        }
        if (time > 60000) {
            if ((int) (time / 60000) > 1)
                values.add((int) (time / 60000) + " minutes");
            else
                values.add("1 minute");
            time = time % 60000;
        }
        // add a seconds value if there is still time remaining or if there are no other values
        if (time > 0 || values.size() == 0)
            // if you have partial seconds and !round_seconds, it's written as a double so it doesn't truncate the decimals
            if ((time / 1000.0) != (time / 1000) && !round_seconds)
                values.add((time / 1000.0) + " seconds");
            // if seconds are a whole number, just write it as a whole number (integer)
            else if (Math.round(time / 1000) > 1)
                values.add(Math.round(time / 1000) + " seconds");
            else
                values.add("1 second");
        // if there are two or more values, add an "and"
        if (values.size() >= 2)
            values.add(values.size() - 1, "and");
        // assemble the final String
        String written = "";
        for (int i = 0; i < values.size(); i++) {
            // add spaces as needed
            if (i > 0)
                written = written + " ";
            written = written + values.get(i);
            // add commas as needed
            if (values.size() >= 4 && i < values.size() - 1 && !values.get(i).equals("and"))
                written = written + ",";
        }
        if (!written.equals(""))
            return written;
        else
            return null;
    }

    /** This method actiavtes any color codes in a given String and returns the message with color codes eliminated from the text and colors added to the text. This method is
     * necessary because it does two (2) things that <a href="ChatColor#translateAlternateColorCodes(char, String)">CraftBukkit's color code translating method</a> cannot.
     * <b>1)</b> It rearranges color codes in the text to ensure that every one is used. With CraftBukkit's standard methods, any formatting color codes (e.g. &k for magic or
     * &l for bold) that <i>precede</i> color color codes (e.g. &a for light green or &4 for dark red) are automatically cancelled, but if the formatting color codes comes
     * <i>after</i> the color color code, the following text will be colored AND formatted. This method can simply switch the places of the formatting and color color codes in
     * these instances to ensure that both are used (e.g. "&k&4", which normally results in dark red text, becomes "&4&k", which results in dark red magic text). <b>2)</b> It
     * allows the use of anti-color codes, an invention of mine. Anti-color codes use percent symbols (%) in place of ampersands (&) and work in the opposite way of normal
     * color codes. They allow the user to cancel one coloring or formatting in text without having to rewrite all of the previous color codes. For example, normally to change
     * from a dark red, magic, bold text ("&4&k&l") to a dark red magic text ("&4&k"), you would have to use "&4&k"; with this feature, however, you can simply use "%l" to
     * cancel the bold formatting. This feature is essential for the AutoCorrect abilities; for example, the profanity filter must have the ability to execute a magic color
     * code, but then cancel it without losing any colors designated by the sender earlier in the message. Without this ability, the white color code ("&f") could perhaps be
     * used to cancel the magic formatting, but in a red message containing a profanity, that would result in the rest of the message after the covered up profanity being
     * white.
     * 
     * @param text
     *            is the string that must be color coded.
     * @return the String colored according to the color codes given */
    public static String colorCode(String text) {
        text = "&f" + text;
        // put color codes in the right order if they're next to each other
        for (int i = 0; i < text.length() - 3; i++)
            if (isColorCode(text.substring(i, i + 2), false, true) && isColorCode(text.substring(i + 2, i + 4), true, true))
                text = text.substring(0, i) + text.substring(i + 2, i + 4) + text.substring(i, i + 2) + text.substring(i + 4);
        // replace all anti color codes with non antis
        String current_color_code = "";
        for (int i = 0; i < text.length() - 1; i++) {
            if (isColorCode(text.substring(i, i + 2), null, true))
                current_color_code = current_color_code + text.substring(i, i + 2);
            else if (isColorCode(text.substring(i, i + 2), null, false)) {
                while (text.length() > i + 2 && isColorCode(text.substring(i, i + 2), null, false)) {
                    current_color_code = current_color_code.replaceAll("&" + text.substring(i + 1, i + 2), "");
                    if (current_color_code.equals(""))
                        current_color_code = "&f";
                    text = text.substring(0, i) + text.substring(i + 2);
                }
                text = text.substring(0, i) + current_color_code + text.substring(i);
            }
        }
        String colored_text = ChatColor.translateAlternateColorCodes('&', text);
        return colored_text;
    }

    /** This method can determine whether or not a String is a color code or not and what type or color code it is (formatting vs. color color codes and/or normal vs.
     * anti-color codes).
     * 
     * @param text
     *            is the two-character String that this method analyzes to see whether or not it is a color code.
     * @param true_non_formatting_null_either
     *            is a Boolean that can have three values. <b>true</b> means that the color code must be non-formatting, e.g. "&a" (light green) or "&4" (dark red).
     *            <b>false</b> means that the color code must be formatting, e.g. "&k" for magic or "&l" for bold. <b>null</b> means that it can be either a formatting or
     *            non-formatting color code to return true.
     * @param true_non_anti_null_either
     *            works similarly to true_non_formatting_null_either, but for anti-color codes vs. normal color codes. "true" means that the color code must <i>not</i> be an
     *            anti-color code.
     * @return true if the String is a color code and the other standards set by the Boolean parameters are met; false otherwise */
    public static Boolean isColorCode(String text, Boolean true_non_formatting_null_either, Boolean true_non_anti_null_either) {
        if (!text.startsWith("&") && !text.startsWith("%"))
            return false;
        if (true_non_anti_null_either != null)
            if (true_non_anti_null_either && text.startsWith("%"))
                return false;
            else if (!true_non_anti_null_either && text.startsWith("&"))
                return false;
        if (true_non_formatting_null_either == null || true_non_formatting_null_either)
            for (String color_color_code_char : myPluginWiki.COLOR_COLOR_CODE_CHARS)
                if (text.substring(1, 2).equalsIgnoreCase(color_color_code_char))
                    return true;
        if (true_non_formatting_null_either == null || !true_non_formatting_null_either)
            for (String formatting_color_code_char : myPluginWiki.FORMATTING_COLOR_CODE_CHARS)
                if (text.substring(1, 2).equalsIgnoreCase(formatting_color_code_char))
                    return true;
        return false;
    }

    /** This method can remove all color codes from a given String, including anti-color codes, which are not recognized by Bukkit's <tt>ChatColor.stripColor()</tt> method.
     * 
     * @param text
     *            is the String that must have its color codeds removed.
     * @return <b><tt>text</b></tt> without color codes. */
    public static String decolor(String text) {
        if (!text.contains("&") && !text.contains("%"))
            return text;
        for (int i = 0; i < text.length() - 2; i++) {
            if (isColorCode(text.substring(i, i + 2), null, null)) {
                if (i + 2 < text.length())
                    text = text.substring(0, i) + text.substring(i + 2);
                else
                    text = text.substring(0, i);
                i -= 2;
            }
        }
        return text;
    }

    public static HashMap<Enchantment, Integer> getEnchantments(ItemStack item) {
        HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
        // .getEnchants() doesn't work on enchanted books, so I have to read the metadata included in .toString() to determine the enchantment on the book
        if (item.getType() == Material.ENCHANTED_BOOK) {
            String enchantment_name = item.toString().substring(item.toString().indexOf("stored-enchants={") + 17);
            enchantment_name = enchantment_name.substring(0, enchantment_name.indexOf("}"));
            int level = 0;
            try {
                level = Integer.parseInt(enchantment_name.split("=")[1]);
            } catch (NumberFormatException e) {
                return null;
            }
            Enchantment enchantment = Enchantment.getByName(enchantment_name.split("=")[0]);
            if (enchantment != null) {
                enchantments.put(enchantment, level);
            } else
                return null;
        } else
            for (Enchantment ench : item.getEnchantments().keySet())
                enchantments.put(ench, Integer.valueOf(item.getEnchantmentLevel(ench)));
        return enchantments;
    }

    public static Integer levelToXp(int level) {
        // from the Minecraft Wiki (where x is the level and the result is the amount of total experience required to reach level x):
        // "for x≤16: 17x
        // for 15≤x≤31: 1.5x²-29.5x+360
        // for x≥30: 3.5x²-151.5x+2220"
        if (level < 0)
            return null;
        else if (level >= 30)
            return (int) (3.5 * Math.pow(level, 2) - 151.5 * level + 2220);
        else if (level <= 16)
            return 17 * level;
        else
            return (int) (1.5 * Math.pow(level, 2) - 29.5 * level + 360);
    }

    public static Integer xpToLevel(int xp) {
        Double level = xpToExactLevel(xp);
        if (level == null)
            return null;
        else
            // round down the exact level value to get the true level
            return (int) xpToExactLevel(xp).doubleValue();
    }

    public static Double xpToExactLevel(int xp) {
        // from the Minecraft Wiki (where x is the level and the result is the amount of total experience required to reach level x):
        // "for x≤16: 17x
        // for 15≤x≤31: 1.5x²-29.5x+360
        // for x≥30: 3.5x²-151.5x+2220"
        // for this method, I have rearranged the equations using the quadratic solution equation to find that (√ = square root) (±s changed to +s to ensure
        // positive results):
        // for level≤16 (xp≤272): xp/17
        // for 15≤level≤31 (255≤xp≤887): (29.5 + √(6*xp - 1,289.75))/3
        // for level≥30 (xp≥825): (151.5 + √(14*xp - 8,127.75))/7
        if (xp < 0)
            return null;
        else if (xp >= 825)
            return (151.5 + Math.sqrt(14 * xp - 8127.75)) / 7.0;
        else if (xp <= 272)
            return xp / 17.0;
        else
            return (29.5 + Math.sqrt(6 * xp - 1289.75)) / 3.0;

    }

    public static boolean isBorder(String test) {
        if (test.length() == 40) {
            for (String border : borders)
                if (test.contains(border)) {
                    test = replaceAll(test, border, "");
                    break;
                }
            if (test.equals(""))
                return true;
        }
        return false;
    }

    public static String border() {
        String border_unit = borders[(int) (Math.random() * borders.length)], border = "";
        for (int i = 0; i < 20; i++)
            border += border_unit;
        return border;
    }

    public static void displayLoadConfirmation(CommandSender sender, ChatColor color, int number_of_objects, String object, String... settings_type) {
        if (sender == null)
            if (number_of_objects > 1)
                if (settings_type.length == 0)
                    tellOps(color + "Your " + number_of_objects + " " + object + "s have been loaded.", true);
                else
                    tellOps(color + "Your " + number_of_objects + " " + object + "s and your " + settings_type[0] + " settings have been loaded.", true);
            else if (number_of_objects == 1)
                if (settings_type.length == 0)
                    tellOps(color + "Your 1 " + object + " has been loaded.", true);
                else
                    tellOps(color + "Your 1 " + object + " and your " + settings_type[0] + " settings have been loaded.", true);
            else if (settings_type.length == 0)
                tellOps(color + "You have no " + object + "s to load!", true);
            else
                tellOps(color + "Your " + settings_type[0] + " settings have been loaded.", true);
        else {
            String sender_name = "Someone on the console";
            if (sender instanceof Player)
                sender_name = ((Player) sender).getName();
            if (number_of_objects > 1)
                if (settings_type.length == 0) {
                    sender.sendMessage(color + "Your " + number_of_objects + " " + object + "s have been loaded.");
                    tellOps(color + sender_name + " loaded " + number_of_objects + " " + object + "s to file.", sender instanceof Player, sender.getName());
                } else {
                    sender.sendMessage(color + "Your " + number_of_objects + " " + object + "s and your " + settings_type[0] + " have been loaded.");
                    tellOps(color + sender_name + " loaded " + number_of_objects + " " + object + "s to file.", sender instanceof Player, sender.getName());
                }
            else if (number_of_objects == 1)
                if (settings_type.length == 0) {
                    sender.sendMessage(color + "Your 1 " + object + " has been loaded.");
                    tellOps(color + sender_name + " loaded the server's 1 " + object + " to file.", sender instanceof Player, sender.getName());
                } else {
                    sender.sendMessage(color + "Your 1 " + object + " and your " + settings_type[0] + " settings have been loaded.");
                    tellOps(color + sender_name + " loaded the server's 1 " + object + " to file.", sender instanceof Player, sender.getName());
                }
            else if (settings_type.length == 0) {
                sender.sendMessage(color + "You have no " + object + "s to load!");
                tellOps(color + sender_name + " tried to load the server's " + object + "s to file, but there were no " + object + "s on the server to load.",
                        sender instanceof Player, sender.getName());
            } else {
                sender.sendMessage(color + "Your " + settings_type[0] + " settings have been loaded.");
                tellOps(color + sender_name + " loaded your " + settings_type[0] + " settings.", sender instanceof Player, sender.getName());
            }
        }
    }

    public static void displaySaveConfirmation(CommandSender sender, ChatColor color, int number_of_objects, String object, String... settings_type) {
        if (sender == null)
            if (number_of_objects > 1)
                if (settings_type.length == 0)
                    tellOps(color + "Your " + number_of_objects + " " + object + "s have been saved.", true);
                else
                    tellOps(color + "Your " + number_of_objects + " " + object + "s and your " + settings_type[0] + " settings have been saved.", true);
            else if (number_of_objects == 1)
                if (settings_type.length == 0)
                    tellOps(color + "Your 1 " + object + " has been saved.", true);
                else
                    tellOps(color + "Your 1 " + object + " and your " + settings_type[0] + " settings have been saved.", true);
            else if (settings_type.length == 0)
                tellOps(color + "You have no " + object + "s to save!", true);
            else
                tellOps(color + "Your " + settings_type[0] + " settings have been saved.", true);
        else {
            String sender_name = "Someone on the console";
            if (sender instanceof Player)
                sender_name = ((Player) sender).getName();
            if (number_of_objects > 1)
                if (settings_type.length == 0) {
                    sender.sendMessage(color + "Your " + number_of_objects + " " + object + "s have been saved.");
                    tellOps(color + sender_name + " saved " + number_of_objects + " " + object + "s to file.", sender instanceof Player, sender.getName());
                } else {
                    sender.sendMessage(color + "Your " + number_of_objects + " " + object + "s and your " + settings_type[0] + " have been saved.");
                    tellOps(color + sender_name + " saved " + number_of_objects + " " + object + "s to file.", sender instanceof Player, sender.getName());
                }
            else if (number_of_objects == 1)
                if (settings_type.length == 0) {
                    sender.sendMessage(color + "Your 1 " + object + " has been saved.");
                    tellOps(color + sender_name + " saved the server's 1 " + object + " to file.", sender instanceof Player, sender.getName());
                } else {
                    sender.sendMessage(color + "Your 1 " + object + " and your " + settings_type[0] + " settings have been saved.");
                    tellOps(color + sender_name + " saved the server's 1 " + object + " to file.", sender instanceof Player, sender.getName());
                }
            else if (settings_type.length == 0) {
                sender.sendMessage(color + "You have no " + object + "s to save!");
                tellOps(color + sender_name + " tried to save the server's " + object + "s to file, but there were no " + object + "s on the server to save.",
                        sender instanceof Player, sender.getName());
            } else {
                sender.sendMessage(color + "Your " + settings_type[0] + " settings have been saved.");
                tellOps(color + sender_name + " saved your " + settings_type[0] + " settings.", sender instanceof Player, sender.getName());
            }
        }
    }

    // TODO
    // public static Boolean comesBefore(String time1, String time2)

    // alternate input/output methods
    public static String writeLocation(Entity entity, boolean use_block_coordinates, boolean include_pitch_and_yaw) {
        return writeLocation(entity.getLocation(), use_block_coordinates, include_pitch_and_yaw);
    }

    public static String writeLocation(Block block) {
        return writeLocation(block.getLocation(), true, false);
    }

    /** This method is used to interpret the answers to questions.
     * 
     * @param unformatted_response
     *            is the raw String message that will be formatted in this message to be all lower case with no punctuation and analyzed for a "yes" or "no" answer.
     * @return <b>true</b> if the response matches one of the words or phrases in <tt>yeses</tt>, <b>false</b> if the response matches one of the words or phrases in
     *         <tt>nos</tt>, or <b>null</b> if the message did not seem to answer the question. */
    public static Boolean getResponse(String unformatted_response) {
        return getResponse(unformatted_response, null, null);
    }

    public static byte getDoorTopData(Block bottom_door_block) {
        if (bottom_door_block.getType() != Material.WOODEN_DOOR && bottom_door_block.getType() != Material.IRON_DOOR_BLOCK)
            return -1;
        byte direction_data = bottom_door_block.getData();
        // if data>=4, the door was open, so just subtract 4 to check for the door's direction more easily
        if (direction_data >= 4)
            direction_data -= 4;
        // find the block face that needs to be checked for a door based on the direction of the current door; this will be used to determine whether or not the
        // current door will be the second door in a double door formation or simply a single door
        BlockFace face_to_check = BlockFace.NORTH;
        if (direction_data == 1)
            face_to_check = BlockFace.EAST;
        else if (direction_data == 2)
            face_to_check = BlockFace.SOUTH;
        else if (direction_data == 3)
            face_to_check = BlockFace.WEST;
        Block relevant_adjacent_door_block = bottom_door_block.getRelative(face_to_check);
        // if the block next to the bottom of the door is not a door, check the one next to the top of the door; the door may become a double door even if the
        // door adjacent to it is one block above it
        if (relevant_adjacent_door_block.getType() != Material.WOODEN_DOOR && relevant_adjacent_door_block.getType() != Material.IRON_DOOR_BLOCK)
            relevant_adjacent_door_block = relevant_adjacent_door_block.getRelative(BlockFace.UP);
        // if the relevant adjacent door block is not a door block at all or not the same type of door, the new door will not be a double door
        if (relevant_adjacent_door_block.getType() == Material.WOODEN_DOOR && bottom_door_block.getType() == Material.WOODEN_DOOR
                || relevant_adjacent_door_block.getType() == Material.IRON_DOOR_BLOCK && bottom_door_block.getType() == Material.IRON_DOOR_BLOCK) {
            // if the adjacent door block is already a double door, the current door will not be a double door.
            // if the adjacent door block is the top of a non-double door, there is still a chance the current door could be a double door; the door may become
            // a double door even if the door adjacent to it is one block below it
            if (relevant_adjacent_door_block.getData() == 8)
                relevant_adjacent_door_block = relevant_adjacent_door_block.getRelative(BlockFace.DOWN);
            // double-check to make sure that the relevant adjacent door block is still a door block; with WorldEdit or other world modifying tools, the top
            // half of a door might not have a bottom half
            if (relevant_adjacent_door_block.getType() == Material.WOODEN_DOOR && bottom_door_block.getType() == Material.WOODEN_DOOR
                    || relevant_adjacent_door_block.getType() == Material.IRON_DOOR_BLOCK && bottom_door_block.getType() == Material.IRON_DOOR_BLOCK)
                // finally, if the direction of both doors match, then it looks like we'll have a double door
                if (relevant_adjacent_door_block.getData() % 4 == bottom_door_block.getData() % 4)
                    // data=9 signifies a double door (as opposed to data=8, which is the top of a regular door)
                    return 9;
        }
        return 8;
    }

    public static Integer levelToExp(int level) {
        return levelToXp(level);
    }

    public static Integer expToLevel(int exp) {
        return xpToLevel(exp);
    }

    public static Double expToExactLevel(int exp) {
        return xpToExactLevel(exp);
    }

    public static String combine(String[] strings) {
        return combine(strings, "");
    }

    public static String combine(String[] strings, int... indices) {
        return combine(strings, "", indices);
    }
}
