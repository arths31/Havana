package org.alexdev.havana.messages.incoming.trade;

import org.alexdev.havana.dao.mysql.ItemDao;
import org.alexdev.havana.game.item.Item;
import org.alexdev.havana.game.player.Player;
import org.alexdev.havana.game.room.Room;
import org.alexdev.havana.game.room.managers.RoomTradeManager;
import org.alexdev.havana.messages.outgoing.trade.ITEM_NOT_TRADABLE;
import org.alexdev.havana.messages.outgoing.trade.TRADEACCEPT;
import org.alexdev.havana.messages.types.MessageEvent;
import org.alexdev.havana.server.netty.streams.NettyRequest;

import java.util.List;

public class TRADE_REMOVE_ITEM implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (player.getRoomUser().getTradePartner() == null) {
            return;
        }

        if (!player.getDetails().isTradeEnabled() || !player.getRoomUser().getTradePartner().getDetails().isTradeEnabled()) {
            return;
        }

        if (player.getRoomUser().isTradeConfirmed() && player.getRoomUser().getTradePartner().getRoomUser().isTradeConfirmed()) {
            return;
        }

        if (player.getRoomUser().hasAcceptedTrade() && player.getRoomUser().getTradePartner().getRoomUser().hasAcceptedTrade()) {
            return;
        }

        int itemId = reader.readInt();//Integer.parseInt(reader.contents());
        Item inventoryItem = player.getInventory().getItem(itemId);

        if (inventoryItem == null) {
            return;
        }

        if (!inventoryItem.getDefinition().isTradable()) {
            player.send(new ITEM_NOT_TRADABLE());
            return;
        }

        player.getRoomUser().getTradeItems().remove(inventoryItem);
        ItemDao.updateTradeState(inventoryItem, false);

        for (Player tradeUser : List.of(player, player.getRoomUser().getTradePartner())) {
            if (tradeUser.getRoomUser().hasAcceptedTrade()) {
                tradeUser.getRoomUser().setTradeAccept(false);

                tradeUser.send(new TRADEACCEPT(tradeUser.getDetails().getId(), tradeUser.getRoomUser().hasAcceptedTrade()));
                tradeUser.getRoomUser().getTradePartner().send(new TRADEACCEPT(tradeUser.getDetails().getId(), tradeUser.getRoomUser().hasAcceptedTrade()));
            }
        }

        RoomTradeManager.refreshWindow(player);
        RoomTradeManager.refreshWindow(player.getRoomUser().getTradePartner());
    }
}
