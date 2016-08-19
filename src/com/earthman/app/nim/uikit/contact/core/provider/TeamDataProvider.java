package com.earthman.app.nim.uikit.contact.core.provider;

import java.util.ArrayList;
import java.util.List;

import com.earthman.app.nim.uikit.cache.TeamDataCache;
import com.earthman.app.nim.uikit.contact.core.item.AbsContactItem;
import com.earthman.app.nim.uikit.contact.core.item.ContactItem;
import com.earthman.app.nim.uikit.contact.core.item.ItemTypes;
import com.earthman.app.nim.uikit.contact.core.model.ContactGroupStrategy;
import com.earthman.app.nim.uikit.contact.core.model.TeamContact;
import com.earthman.app.nim.uikit.contact.core.query.TextComparator;
import com.earthman.app.nim.uikit.contact.core.query.TextQuery;
import com.netease.nimlib.sdk.team.model.Team;

/**
 * 群数据源提供者
 * <p/>
 * Created by huangjun on 2015/3/1.
 */
public class TeamDataProvider {
    public static final List<AbsContactItem> provide(TextQuery query, int itemType) {
        List<TeamContact> sources = query(query, itemType);
        List<AbsContactItem> items = new ArrayList<AbsContactItem>(sources.size());
        for (TeamContact t : sources) {
            items.add(createTeamItem(t));
        }

        return items;
    }

    private static AbsContactItem createTeamItem(TeamContact team) {
        return new ContactItem(team, ItemTypes.TEAM) {
            @Override
            public int compareTo(ContactItem item) {
                return compareTeam((TeamContact) getContact(), (TeamContact) (item.getContact()));
            }

            @Override
            public String belongsGroup() {
                return ContactGroupStrategy.GROUP_TEAM;
            }
        };
    }

    private static int compareTeam(TeamContact lhs, TeamContact rhs) {
        return TextComparator.compareIgnoreCase(lhs.getDisplayName(), rhs.getDisplayName());
    }

    /**
     * * 数据查询
     */
    private static final List<TeamContact> query(TextQuery query, int itemType) {
        List<Team> teams;
        if (itemType == ItemTypes.TEAMS.ADVANCED_TEAM) {
            teams = TeamDataCache.getInstance().getAllAdvancedTeams();
        } else if (itemType == ItemTypes.TEAMS.NORMAL_TEAM) {
            teams = TeamDataCache.getInstance().getAllNormalTeams();
        } else {
            teams = TeamDataCache.getInstance().getAllTeams();
        }

        List<TeamContact> contacts = new ArrayList<TeamContact>();
        for (Team t : teams) {
            if (query == null || ContactSearch.hitTeam(t, query)) {
                contacts.add(new TeamContact(t));
            }
        }

        return contacts;
    }
}