package history.entry;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.gen.Player;
import mindustry.world.Block;
import mindustry.world.Tile;

import java.util.HashMap;

public class ConfigEntry implements HistoryEntry {
    private static final HashMap<String, String> itemAndFluidIcons = new HashMap<>() {{
        put("copper", "\uF838");
        put("lead", "\uF837");
        put("metaglass", "\uF836");
        put("graphite", "\uF835");
        put("sand", "\uF834");
        put("coal", "\uF833");
        put("titanium", "\uF832");
        put("thorium", "\uF831");
        put("scrap", "\uF830");
        put("silicon", "\uF82F");
        put("plastanium", "\uF82E");
        put("phase-fabric", "\uF82D");
        put("surge-alloy", "\uF82C");
        put("spore-pod", "\uF82B");
        put("blast-compound", "\uF82A");
        put("pyratite", "\uF829");

        put("water", "\uF828");
        put("slag", "\uF827");
        put("oil", "\uF826");
        put("cryofluid", "\uF825");
    }};

    private static final HashMap<String, String> commands = new HashMap<>() {{
        put("attack", "[red]\uE865[white]");
        put("idle", "[yellow]\uE815[white]");
        put("rally", "[orange]\uE86C[white]");
    }};

    private static final HashMap<Integer, String> unitsGround = new HashMap<>() {{
        put(0, "\uF800");
        put(1, "\uF7FA");
        put(2, "\uF7FD");
    }};

    private static final HashMap<Integer, String> unitsAir = new HashMap<>() {{
        put(0, "\uF7F6");
        put(1, "\uF7F1");
    }};

    private static final HashMap<Integer, String> unitsWater = new HashMap<>() {{
        put(0, "\uF7E7");
    }};

    public Player player;
    public Block block;
    public Object value;
    public boolean connect;


    public ConfigEntry(EventType.ConfigEvent event) {
        this(event, true);
    }

    public ConfigEntry(EventType.ConfigEvent event, boolean connect) {
        this.player = event.player;
        this.block = event.tile.block();
        this.value = event.value;
        this.connect = connect;
    }



    @Override
    public String getMessage(boolean isAdmin) {
        String id;

        if (isAdmin) id = "[white] (UUID: [scarlet]" + player.uuid() + "[white])";
        else id = "[white] (ID: [scarlet]" + player.id + "[white])";

        if (block == Blocks.powerNode || block == Blocks.powerNodeLarge || block == Blocks.powerSource || block == Blocks.powerVoid || block == Blocks.surgeTower || block == Blocks.phaseConduit || block == Blocks.phaseConveyor || block == Blocks.bridgeConduit || block == Blocks.itemBridge || block == Blocks.massDriver) {
            if (connect) {
                if (value == null || (Integer)value < 0) return "[orange]~ [white]" + player.name + id + " [red]disconnected[white] this " + block.name;
                Tile tile = Vars.world.tile((Integer) value);
                return "[orange]~ [white]" + player.name + id + " [green]connected[white] this " + block.name + " to [purple]" + tile.x + "," + tile.y + "[white]";
            } else {
                if (value == null || (Integer)value < 0) return "[orange]~ [white]" + player.name + id + " [red]disconnected[white] this " + block.name;
                Tile tile = Vars.world.tile((Integer) value);
                return "[orange]~ [white]" + player.name + id + " [red]disconnected[white] this " + block.name + " from [purple]" + tile.x + "," + tile.y + "[white]";
            }
        } else if (block == Blocks.commandCenter) {
            return "[orange]~ [white]" + player.name + id + " commanded units to " + commands.get(value.toString()) + "[white]";
        } else if (block == Blocks.liquidSource) {
            if (value == null) return "[orange]~ [white]" + player.name + id + " changed config back to default";
            return "[orange]~ [white]" + player.name + id + " changed config to " + itemAndFluidIcons.get(value.toString());
        } else if (block == Blocks.sorter ||block == Blocks.itemSource || block == Blocks.invertedSorter) {
            if (value == null) return "[orange]~ [white]" + player.name + id + " changed config back to default";
            return "[orange]~ [white]" + player.name + id + " changed config to " + itemAndFluidIcons.get(value.toString());
        } else if (block == Blocks.groundFactory) {
            if (value == null || (Integer)value < 0) return "[orange]~ [white]" + player.name + id + " changed config back to default";
            return "[orange]~ [white]" + player.name + id + " changed config to " + unitsGround.get(value);
        } else if (block == Blocks.airFactory) {
            if (value == null || (Integer)value < 0) return "[orange]~ [white]" + player.name + id + " changed config back to default";
            return "[orange]~ [white]" + player.name + id + " changed config to " + unitsAir.get(value);
        } else if (block == Blocks.navalFactory) {
            if (value == null || (Integer)value < 0) return "[orange]~ [white]" + player.name + id + " changed config back to default";
            return "[orange]~ [white]" + player.name + id + " changed config to " + unitsWater.get(value);
        } else {
            return "[orange]~ [white]" + player.name + id + " changed config to " + value;
        }
    }
}
