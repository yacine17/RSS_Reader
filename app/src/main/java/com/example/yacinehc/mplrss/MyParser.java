package com.example.yacinehc.mplrss;

import com.example.yacinehc.mplrss.model.RSS;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MyParser {


    public static RSS getRss(String filePath) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document document = documentBuilder.parse(filePath);
            String title = document.getElementsByTagName("title").item(0).getTextContent();
            String link = document.getElementsByTagName("link").item(0).getTextContent();
            NodeList descriptions = document.getElementsByTagName("descritpion");
            String description = "";
            if (descriptions.getLength() > 0) {
                description = descriptions.item(0).getTextContent();
            }
            return new RSS(title, link, description);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
