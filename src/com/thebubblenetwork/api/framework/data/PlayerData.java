package com.thebubblenetwork.api.framework.data;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.data.api.DataObject;
import com.thebubblenetwork.api.framework.util.sql.SQLUtil;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Jacob on 31/12/2015.
 */
public class PlayerData extends DataObject {

    public static final String RANKBASE = "rank", STATSBASE = "stats", FRIENDSBASE = "friends", ITEMSBASE = "items",
            KITBASE = "kits", CURRENCYBASE = "currency", TOKENS = "tokens", MAINRANK = RANKBASE + ".mainrank",
            SUBRANKS = RANKBASE + ".subranks", FRIENDSLIST = FRIENDSBASE + ".list", FRIENDSPENDINGLIST = FRIENDSBASE
            + ".pending", FRIENDSINVITELIST = FRIENDSBASE + ".invited";

    public static String table = "playerdata";
    private UUID u;

    public PlayerData(UUID u, Map loaded) {
        super(loaded);
        this.u = u;
    }

    public static PlayerData loadData(UUID load) throws SQLException, ClassNotFoundException {
        return new PlayerData(load, loadData(SQLUtil.query(BubbleNetwork.getInstance().getConnection(), table, "*",
                                                           new SQLUtil.WhereVar("uuid", load))));
    }

    public UUID getUUID() {
        return u;
    }
}
