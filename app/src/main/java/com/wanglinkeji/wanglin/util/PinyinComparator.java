package com.wanglinkeji.wanglin.util;

import com.wanglinkeji.wanglin.model.UserFriendModel;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/9/23.
 */

public class PinyinComparator implements Comparator<UserFriendModel> {
    public int compare(UserFriendModel o1, UserFriendModel o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }
}
