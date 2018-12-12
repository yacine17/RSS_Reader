package com.example.yacinehc.mplrss;

import com.example.yacinehc.mplrss.model.RSS;

import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MyParser {

    public static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            domain = domain.startsWith("www.") ? domain.substring(4) : domain;
            return domain.substring(0, 1).toUpperCase() + domain.substring(1);
        } catch (URISyntaxException e) {
            System.out.println("Erreur à la syntaxe de l'URI : " + url);
        }
        return "";
    }

    public static RSS getRss(String filePath) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document document = documentBuilder.parse(filePath);

            String title = document.getElementsByTagName("title").item(0).getTextContent();
            String link = document.getElementsByTagName("link").item(0).getTextContent();
            NodeList descriptions = document.getElementsByTagName("description");
            String description = "";
            if (descriptions.getLength() > 0) {
                description = StringEscapeUtils.unescapeXml(descriptions.item(0).getTextContent());
                description = StringEscapeUtils.unescapeXml(description);
                System.out.println("description = " + description);
            }

            return new RSS(null, getDomainName(link) + " : " + title, description);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
