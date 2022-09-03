package org.alexdev.havana.game.games.battleball.events;

import org.alexdev.havana.game.games.GameEvent;
import org.alexdev.havana.game.games.battleball.BattleBallPowerUp;
import org.alexdev.havana.game.games.enums.GameEventType;
import org.alexdev.havana.game.games.player.GamePlayer;
import org.alexdev.havana.server.netty.streams.NettyResponse;

public class ActivatePowerUpEvent extends GameEvent {
    private final BattleBallPowerUp powerUp;
    private final GamePlayer gamePlayer;

    public ActivatePowerUpEvent(GamePlayer gamePlayer, BattleBallPowerUp powerUp) {
        super(GameEventType.BATTLEBALL_POWERUP_ACTIVATE);
        this.gamePlayer = gamePlayer;
        this.powerUp = powerUp;
    }

    @Override
    public void serialiseEvent(NettyResponse response) {
        response.writeInt(this.gamePlayer.getObjectId());
        response.writeInt(this.powerUp.getId());
        response.writeInt(this.gamePlayer.getPlayer().getRoomUser().getPosition().getRotation());
        response.writeInt(this.powerUp.getPowerType().getPowerUpId());
    }
}
