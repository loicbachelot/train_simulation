package test;

import data.Line;
import factory.XMLReader;

/**
 * Created by Adrien on 25/01/2017.
 */
public class XMLReaderTest {

    public static void main(String[] args) {
        XMLReader xmlReader = new XMLReader("BOnTime_test/linev11.xml");
        xmlReader.buildData();
        Line l = xmlReader.getLine();
    }
}
