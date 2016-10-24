package com.wanglinkeji.wanglin.model;

import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 * @邻居Model
 */

public class AtItemModel {

    public static List<AtItemModel> list_AtItem;

    public static AtItemModel current_AtItem;

    private int startIndex;

    private int endIndex;

    private String content;

    private int fromWhat;

    private int AtId;

    public AtItemModel(){
    }

    public AtItemModel(int startIndex, int endIndex, String content){
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.content = content;
    }

    public int getAtId() {
        return AtId;
    }

    public void setAtId(int atId) {
        AtId = atId;
    }

    public int getFromWhat() {
        return fromWhat;
    }

    public void setFromWhat(int fromWhat) {
        this.fromWhat = fromWhat;
    }

    public void setStartIndex(int startIndex){
        this.startIndex = startIndex;
    }

    public int getStartIndex(){
        return startIndex;
    }

    public void setEndIndex(int endIndex){
        this.endIndex = endIndex;
    }

    public int getEndIndex(){
        return endIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
