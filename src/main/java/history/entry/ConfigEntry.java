package history.entry;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.type.Player;
import mindustry.game.EventType;
import mindustry.world.Block;

import java.util.HashMap;

public class ConfigEntry implements HistoryEntry {
    private static final HashMap<String, String> itemAndFluidIcons = new HashMap<String, String>() {{
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

    private static final String[] commands = {"[red]attack[white]", "[yellow]retreat[white]", "[orange]rally[white]"};

    public Player player;
    public Block block;
    public int value;
    public boolean connect = false;

    public ConfigEntry(EventType.TapConfigEvent event) {
        this(event, true);
    }

    public ConfigEntry(EventType.TapConfigEvent event, boolean connect) {
        this.player = event.player;
        this.block = event.tile.block();
        this.value = event.value;
        this.connect = connect;
    }

    @Override
    public String getMessage(boolean isAdmin) {
        String id;

        if (isAdmin) id = "[white] (UUID: [scarlet]" + player.uuid + "[white])";
        else id = "[white] (ID: [scarlet]" + player.id + "[white])";

        if (block == Blocks.powerNode || block == Blocks.powerNodeLarge || block == Blocks.powerSource || block == Blocks.powerVoid || block == Blocks.surgeTower || block == Blocks.phaseConduit || block == Blocks.phaseConveyor || block == Blocks.bridgeConduit || block == Blocks.itemBridge || block == Blocks.massDriver) {
            if (connect) {
                if (value == -1) return "[orange]~ [white]" + player.name + id + " [red]disconnected[white] this " + block.name;
                return "[orange]~ [white]" + player.name + id + " [green]connected[white] this " + block.name + " to [purple]" + value + "[white]";
            } else {
                return "[orange]~ [white]" + player.name + id + " [red]disconnected[white] this " + block.name + " from [purple]" + value + "[white]";
            }
        } else if (block == Blocks.commandCenter) {
            return "[orange]~ [white]" + player.name + id + " commanded units to " + commands[value] + "[white]";
        } else if (block == Blocks.liquidSource) {
            if (value == -1) return "[orange]~ [white]" + player.name + id + " changed config back to default";
            return "[orange]~ [white]" + player.name + id + " changed config to " + itemAndFluidIcons.get(Vars.content.liquid(value).name);
        } else {
            if (value == -1) return "[orange]~ [white]" + player.name + id + " changed config back to default";
            return "[orange]~ [white]" + player.name + id + " changed config to " + itemAndFluidIcons.get(Vars.content.item(value).name);
        }
    }
}
