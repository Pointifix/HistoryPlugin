package history;

import arc.*;
import arc.struct.Array;
import arc.util.*;
import mindustry.*;
import mindustry.entities.type.*;
import mindustry.game.EventType.*;
import mindustry.gen.Call;
import mindustry.plugin.Plugin;
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
            HistoryEntry historyEntry = new HistoryEntry(blockBuildEndEvent.player, blockBuildEndEvent.tile.block(), blockBuildEndEvent.breaking);

            Array<Tile> linkedTile = blockBuildEndEvent.tile.getLinkedTiles(new Array<>());
            for (Tile tile : linkedTile) {
                worldHistory[tile.x][tile.y].add(historyEntry);
            }
        });

        Events.on(TapEvent.class, tapEvent -> {
            if (activeHistoryPlayers.contains(tapEvent.player)) {
                LimitedQueue<HistoryEntry> tileHistory = worldHistory[tapEvent.tile.x][tapEvent.tile.y];

                String message = "[yellow]History of Block (" + tapEvent.tile.x + "," + tapEvent.tile.y + ")";

                for (HistoryEntry historyEntry : tileHistory) {
                    if (historyEntry.breaking) {
                        if(tapEvent.player.isAdmin){
                            message += "\n[red]- [white]" + historyEntry.player.name + "[white] (UUID: [scarlet]" + historyEntry.player.uuid + "[white]) broke this tile";
                        }else{
                            message += "\n[red]- [white]" + historyEntry.player.name + "[white] (ID: [scarlet]" + historyEntry.player.id + "[white]) broke this tile";
                        }
                    } else {
                        if(tapEvent.player.isAdmin) {
                            message += "\n[green]+ [white]" + historyEntry.player.name + "[white] (UUID: [scarlet]" + historyEntry.player.uuid + "[white]) placed [purple]" + historyEntry.block + "[white]";
                        }else{
                            message += "\n[green]+ [white]" + historyEntry.player.name + "[white] (ID: [scarlet]" + historyEntry.player.id + "[white]) placed [purple]" + historyEntry.block + "[white]";
                        }
                    }
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
            if (!adminsOnly || player.isAdmin) {
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
