package org.alexdev.havana.game.games.battleball.powerups;

import org.alexdev.havana.game.GameScheduler;
import org.alexdev.havana.game.games.battleball.BattleBallGame;
import org.alexdev.havana.game.games.battleball.enums.BattleBallPlayerState;
import org.alexdev.havana.game.games.battleball.objects.PlayerUpdateObject;
import org.alexdev.havana.game.games.enums.GameState;
import org.alexdev.havana.game.games.player.GamePlayer;
import org.alexdev.havana.game.room.Room;

import java.util.concurrent.TimeUnit;

public class VacuumHandle {
    public static void handle(BattleBallGame game, GamePlayer gamePlayer, Room room) {
        gamePlayer.setPlayerState(BattleBallPlayerState.CLEANING_TILES);
        game.addObjectToQueue(new PlayerUpdateObject(gamePlayer));

        GameScheduler.getInstance().getService().schedule(() -> {
            if (game.getGameState() == GameState.ENDED) {
                return;
            }

            if (gamePlayer.getPlayerState() != BattleBallPlayerState.CLEANING_TILES) {
                return;
            }

            gamePlayer.setPlayerState(BattleBallPlayerState.NORMAL);
            game.addObjectToQueue(new PlayerUpdateObject(gamePlayer));
        }, 10, TimeUnit.SECONDS);
    }
}
