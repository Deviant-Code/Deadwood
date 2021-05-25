package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedList;

//Used to parse board.xml cards.xml for Deadwood
public class ParseXML {

    public static LinkedList<SceneCard> parseCards () {
        LinkedList<SceneCard> sceneCards = new LinkedList<SceneCard>();
        
        String filePath = "view/resources/cards.xml";
        File xmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("card");

            for (int i = 0; i < nodeList.getLength(); i++) {
                sceneCards.add(parseSceneCard((Element)nodeList.item(i)));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sceneCards;
    }


    private static SceneCard parseSceneCard(Element cardElement) {
        SceneCard sc;

        // get card attributes
        String name = cardElement.getAttribute("name");
        int budget = Integer.parseInt(cardElement.getAttribute("budget"));
        String imgPath = "view/resources/cards/" + cardElement.getAttribute("img");
        
        // get card's scene child info
        NodeList sceneNodes = cardElement.getElementsByTagName("scene"); 
        Element sceneElement = (Element)(sceneNodes.item(0));
        int sceneNum = Integer.parseInt(sceneElement.getAttribute("number"));
        String description = sceneElement.getTextContent();
        
        // create scene card with the required info
        sc = new SceneCard(sceneNum, name, budget, description, imgPath);

        // get card's role child info
        NodeList roleNodes = cardElement.getElementsByTagName("part"); 
        for (int i = 0; i < roleNodes.getLength(); i++) {
            sc.addRole(parseRole((Element)roleNodes.item(i), true));
        }

        return sc;
    }

    private static Role parseRole (Element roleElement, boolean onCard) {
        Role r;

        // get role attributes
        String title = roleElement.getAttribute("name");
        int difficulty = Integer.parseInt(roleElement.getAttribute("level"));
        
        // get role's area child info
        NodeList areaNodes = roleElement.getElementsByTagName("area"); 
        Element areaElement = (Element)(areaNodes.item(0));
        int x = Integer.parseInt(areaElement.getAttribute("x"));
        int y = Integer.parseInt(areaElement.getAttribute("y"));
        int h = Integer.parseInt(areaElement.getAttribute("h"));
        int w = Integer.parseInt(areaElement.getAttribute("w"));
        
        // get role's line child info
        NodeList lineNodes = roleElement.getElementsByTagName("line"); 
        Element lineElement = (Element)(lineNodes.item(0));
        String line = lineElement.getTextContent();
        
        // create scene card with the required info
        r = new Role(difficulty, title, line, onCard);

        return r;
    }

    public static LinkedList<Location> parseLocations() {
        LinkedList<Location> locations = new LinkedList<>();

        String filePath = "view/resources/board.xml";
        File xmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // parses and adds trailer to location list
            NodeList trailerNodes = doc.getElementsByTagName("trailer");
            locations.add(parseTrailer((Element)trailerNodes.item(0)));
            
            // parses and adds office to location list
            NodeList officeNodes = doc.getElementsByTagName("office");
            locations.add(parseOffice((Element)officeNodes.item(0)));

            // parses and adds all acting stages to location list
            NodeList setNodes = doc.getElementsByTagName("set");
            for (int i = 0; i < setNodes.getLength(); i++) {
                locations.add(parseActingStage((Element)setNodes.item(i)));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locations;
    }

    private static Location parseTrailer (Element trailerEl) {
        Trailer trailer = new Trailer();
        addArea (trailerEl, trailer);
        addNeighbors (trailerEl, trailer);
        return trailer;
    }

    private static Location parseOffice (Element officeEl) {
        CastingOffice co = new CastingOffice();
        addArea (officeEl, co);
        addNeighbors (officeEl, co);
        return co;
    }

    private static Location parseActingStage (Element locEl) {
        ActingStage ac;

        String name = locEl.getAttribute("name");
        NodeList takeNodes = ((Element)(locEl.getElementsByTagName("takes").item(0))).getElementsByTagName("take"); 
        ac = new ActingStage(name, takeNodes.getLength());

        addArea (locEl, ac);
        addNeighbors (locEl, ac);

        // get card's role child info
        NodeList roleNodes = ((Element)locEl.getElementsByTagName("parts").item(0)).getElementsByTagName("part"); 
        for (int i = 0; i < roleNodes.getLength(); i++) {
            ac.addRole(parseRole((Element)roleNodes.item(i), false));
        }

        return ac;
    }

    private static void addArea (Element locEl, Location l) {
        NodeList areaNodes = locEl.getElementsByTagName("area"); 
        Element areaElement = (Element)(areaNodes.item(0));
        int x = Integer.parseInt(areaElement.getAttribute("x"));
        int y = Integer.parseInt(areaElement.getAttribute("y"));
        int h = Integer.parseInt(areaElement.getAttribute("h"));
        int w = Integer.parseInt(areaElement.getAttribute("w"));
    }

    private static void addNeighbors (Element locEl, Location l) {
        NodeList neighborNodes = ((Element)(locEl.getElementsByTagName("neighbors").item(0))).getElementsByTagName("neighbor"); 
        for (int i = 0; i < neighborNodes.getLength(); i++) {
            String neighborName = ((Element)neighborNodes.item(i)).getAttribute("name");
            l.addNeighbor(neighborName);
        }
    }
}