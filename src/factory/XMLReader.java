package factory;

import data.Branch;
import data.Line;
import data.Station;
import data.Train;
import log.LoggerUtility;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Adrien on 25/01/2017.
 */
public class XMLReader {

    private final static Logger logger = LoggerUtility.getLogger(XMLReader.class);
    private Element rootElt;
    private XPath path;
    private Line line;
    private ArrayList<Train> trainList = new ArrayList<Train>();

    /**
     * @param file is the file where the XML is read
     */
    public XMLReader(String file) {
        Document dom = parseDom(file);
        rootElt = dom.getDocumentElement();
        XPathFactory xpf = XPathFactory.newInstance();
        this.path = xpf.newXPath();
    }

    /**
     * @param file is the file where it is parsed
     * @return the document parsed
     */
    private static Document parseDom(String file) {
        try {
            // création d'une fabrique de parseurs
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            //fabrique.setValidating(true); //si l’on veut vérifier une DTD TODO

            // création d'un parseur
            DocumentBuilder builder = factory.newDocumentBuilder();

            // transformation d'un fichier XML en DOM
            File xml = new File(file);
            Document document = builder.parse(xml);

            return document;

        } catch (ParserConfigurationException pce) {
            logger.error("Erreur de configuration du parseur DOM");
        } catch (SAXException se) {
            logger.error("Erreur lors du parsing du document");
        } catch (IOException ioe) {
            logger.error("Erreur d'entrée/sortie");
        }
        return null;
    }

    public void buildData() {
        try {
            char lineID = ((String) path.evaluate("/line/@id", rootElt, XPathConstants.STRING)).charAt(0);
            Line line = Line.getInstance();
            logger.trace("Line " + lineID);
            NodeList branchList = (NodeList) path.evaluate("/line/branch", rootElt, XPathConstants.NODESET);
            for (int i = 0; i < branchList.getLength(); i++) {
                Node currentBranch = branchList.item(i);

                String idBranch = (path.evaluate("@id", currentBranch));
                String upBranchId = (path.evaluate("upBranchId", currentBranch));
                String downBranchId = (path.evaluate("downBranchId", currentBranch));
                String backBranchId = (path.evaluate("backBranchId", currentBranch));
                int cantonLength = ((Double) path.evaluate("@cantonlength", currentBranch, XPathConstants.NUMBER)).intValue();
                Branch branch = new Branch(idBranch, upBranchId, downBranchId, backBranchId);
                line.addBranch(branch);

                NodeList trainTypeListBranch = (NodeList) path.evaluate("trainTypeBranch/type", currentBranch, XPathConstants.NODESET);
                for (int k = 0; k < trainTypeListBranch.getLength(); k++) {
                    Node currentTrainType = trainTypeListBranch.item(k);
                    String type = currentTrainType.getTextContent();
                    branch.addTrainType(type);
                }

                NodeList stationList = (NodeList) path.evaluate("station", currentBranch, XPathConstants.NODESET);
                for (int j = 0; j < stationList.getLength(); j++) {
                    Node currentStation = stationList.item(j);
                    int position = ((Double) path.evaluate("position", currentStation, XPathConstants.NUMBER)).intValue();
                    String nameStation = path.evaluate("name", currentStation);
                    boolean boolTerminus = (boolean) path.evaluate("@terminus", currentStation, XPathConstants.BOOLEAN);
                    boolean endBranch = (boolean) path.evaluate("@endBranch", currentStation, XPathConstants.BOOLEAN);
                    int jglobal = ((Double) path.evaluate("globalOrder", currentStation, XPathConstants.NUMBER)).intValue();

                    Station station = new Station(nameStation, position, boolTerminus, j, endBranch, jglobal);
                    branch.addStation(station);

                    NodeList trainTypeList = (NodeList) path.evaluate("traintype/type", currentStation, XPathConstants.NODESET);
                    for (int k = 0; k < trainTypeList.getLength(); k++) {
                        Node currentTrainType = trainTypeList.item(k);
                        String type = currentTrainType.getTextContent();
                        station.addTrainType(type);
                    }

                    if (boolTerminus) {
                        boolean way;
                        way = !nameStation.equals("Paris Saint-Lazare");
                        NodeList trainList = (NodeList) path.evaluate("timetables/train", currentStation, XPathConstants.NODESET);
                        for (int l = 0; l < trainList.getLength(); l++) {
                            Node currentTrain = trainList.item(l);
                            String trainType = path.evaluate("@type", currentTrain);
                            String departureTimeString = currentTrain.getTextContent();

                            SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
                            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                            try {
                                Date departureTime = formatter.parse(departureTimeString);
                                Train train = new Train(branch, l, trainType, departureTime, way, nameStation);
                                line.addTrain(idBranch, train);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                branch.initBranch(cantonLength);
            }
            this.line = line;
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    public Line getLine() {
        return line;
    }

}
