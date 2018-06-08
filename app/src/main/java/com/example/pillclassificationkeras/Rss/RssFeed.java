package com.example.pillclassificationkeras.Rss;

import java.util.List;

public class RssFeed {

    private List<RssItem> mItems;

    public List<RssItem> getItems() {
        return mItems;
    }

    void setItems(List<RssItem> items) {
        mItems = items;
    }
}
