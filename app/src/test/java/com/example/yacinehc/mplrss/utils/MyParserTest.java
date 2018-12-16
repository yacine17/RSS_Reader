package com.example.yacinehc.mplrss.utils;

import com.example.yacinehc.mplrss.model.RSS;
import com.example.yacinehc.mplrss.model.RssItem;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotEquals;

public class MyParserTest {

    @Test
    public void getItems() {
        RSS rss = new RSS("", "", "", "src/HomePage.xml", null);

        List<RssItem> list = MyParser.getItems(rss);

        assertNotEquals(0, list.size());
    }
}