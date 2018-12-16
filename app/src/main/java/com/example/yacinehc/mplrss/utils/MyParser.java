package com.example.yacinehc.mplrss.utils;

import com.example.yacinehc.mplrss.model.RSS;
import com.example.yacinehc.mplrss.model.RssItem;

import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MyParser {

    public static RSS getRss(String filePath) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document document = documentBuilder.parse(filePath);

            String title = document.getElementsByTagName("title").item(0).getTextContent();
            NodeList descriptions = document.getElementsByTagName("description");
            String description = "";

            if (descriptions.getLength() > 0) {
                description = StringEscapeUtils.unescapeXml(descriptions.item(0).getTextContent());
                description = StringEscapeUtils.unescapeXml(description);
                System.out.println("description = " + description);
            }

            return new RSS(null, title, description, filePath.replace("file://", ""), LocalDateTime.now());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<RssItem> getItems(RSS rss) {
        File file = new File(rss.getPath());
        List<RssItem> rssItems = new ArrayList<>();

        if (file.exists()) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
                Document document = documentBuilder.parse(file);

                NodeList items = document.getElementsByTagName("item");

                for (int i = 0; i < items.getLength(); i++) {
                    Element element = (Element) items.item(i);

                    String title = element.getElementsByTagName("title").item(0).getTextContent();
                    String description = element.getElementsByTagName("description").item(0).getTextContent();
                    String link = element.getElementsByTagName("link").item(0).getTextContent();
                    String pubDate = element.getElementsByTagName("pubDate").item(0).getTextContent();

                    RssItem rssItem = new RssItem(link, title, description, pubDate);
                    rssItems.add(rssItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rssItems;
    }
}
