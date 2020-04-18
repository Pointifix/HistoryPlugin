package history;

import mindustry.entities.type.Player;
import mindustry.world.Block;

public class HistoryEntry {
    public Player player;
    public Block block;
    public boolean breaking;

    public HistoryEntry(Player player, Block block, boolean breaking) {
        this.player = player;
        this.block = block;
        this.breaking = breaking;
    }
}
