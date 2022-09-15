package history;

import arc.Events;
import arc.struct.Seq;
import arc.util.CommandHandler;
import arc.util.Log;
import history.entry.BlockEntry;
import history.entry.ConfigEntry;
import history.entry.HistoryEntry;
import mindustry.Vars;
import mindustry.game.EventType.BlockBuildEndEvent;
import mindustry.game.EventType.ConfigEvent;
import mindustry.game.EventType.TapEvent;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import mindustry.world.Tile;

import java.util.ArrayList;

public class HistoryPlugin extends Plugin {
    private Config config;
    private LimitedQueue<HistoryEntry> worldHistory[][];
    private ArrayList<Player> activeHistoryPlayers = new ArrayList<>();
    private boolean adminsOnly;

    public HistoryPlugin() {
        config = new Config();

        adminsOnly = config.getBoolean("adminsOnly");

        Events.on(WorldLoadEvent.class, worldLoadEvent -> {
            worldHistory = new LimitedQueue[Vars.world.width()][Vars.world.height()];

            for (int x = 0; x < Vars.world.width(); x++) {
                for (int y = 0; y < Vars.world.height(); y++) {
                    worldHistory[x][y] = new LimitedQueue<>(config.getInt("historyLimit"));
                }
            }
        });

        Events.on(BlockBuildEndEvent.class, blockBuildEndEvent -> {
            HistoryEntry historyEntry = new BlockEntry(blockBuildEndEvent);

            Seq<Tile> linkedTile = blockBuildEndEvent.tile.getLinkedTiles(new Seq<>());
            for (Tile tile : linkedTile) {
                worldHistory[tile.x][tile.y].add(historyEntry);
            }
        });

        Events.on(ConfigEvent.class, configEvent -> {
            if (configEvent.player == null) return;

            LimitedQueue<HistoryEntry> tileHistory = worldHistory[configEvent.tile.tileX()][configEvent.tile.tileY()];
            boolean connect = true;

            if (!tileHistory.isEmpty() && tileHistory.getLast() instanceof ConfigEntry) {
                ConfigEntry lastConfigEntry = ((ConfigEntry) tileHistory.getLast());

                connect = lastConfigEntry.value != null && !lastConfigEntry.value.equals(configEvent.value) && lastConfigEntry.connect;
            }

            HistoryEntry historyEntry = new ConfigEntry(configEvent, connect);

            Seq<Tile> linkedTile = configEvent.tile.tile().getLinkedTiles(new Seq<>());
            for (Tile tile : linkedTile) {
                worldHistory[tile.x][tile.y].add(historyEntry);
            }
        });

        Events.on(TapEvent.class, tapEvent -> {
            if (activeHistoryPlayers.contains(tapEvent.player)) {
                LimitedQueue<HistoryEntry> tileHistory = worldHistory[tapEvent.tile.x][tapEvent.tile.y];

                String message = "[yellow]History of Block (" + tapEvent.tile.x + "," + tapEvent.tile.y + ")";

                if (tileHistory.isOverflown()) message += "\n[white]... too many entries";
                for (HistoryEntry historyEntry : tileHistory) {
                    message += "\n" + historyEntry.getMessage(tapEvent.player.admin());
                }
                if (tileHistory.isEmpty()) message += "\n[royal]* [white]no entries";

                tapEvent.player.sendMessage(message);
            }
        });

        Log.info("History Plugin successfully loaded...");
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("history", "Toggle history display when clicking on a tile", (args, player) -> {
            if (!adminsOnly || player.admin()) {
                if (activeHistoryPlayers.contains(player)) {
                    activeHistoryPlayers.remove(player);
                    player.sendMessage("[red]Disabled [yellow]history mode.");
                } else {
                    activeHistoryPlayers.add(player);
                    player.sendMessage("[green]Enabled [yellow]history mode. Click on any tile to view its history");
                }
            } else {
                player.sendMessage("[red]You dont have the permission to execute this command.");
            }
        });
    }
}
