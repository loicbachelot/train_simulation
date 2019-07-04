package factory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
//parser

/**
 * Created by mady on 19/01/17.
 * <p>
 * JSON parser using the navitia
 *
 * @author mady
 */
public class NavitiaParser {


    public static final String STOPLINE = "navitia_stop_info.json";
    private static final String FILENAME = "authKey.txt";
    private static final String INFOLINE = "navitia_line_info.json";
    public String auth = ""; //getting token from a file.
    private String urlLine = "https://api.navitia.io/v1/coverage/fr-idf/lines/line%3AOIF%3A800%3ALOIF742/?";
    private String urlStop = "https://api.navitia.io/v1/coverage/fr-idf/lines/line%3AOIF%3A800%3ALOIF742/stop_points?";


    // curl 'https://api.navitia.io/v1/coverage/sandbox/'-H 'Authorization: 3b036afe-0110-4202-b9ed-99718476c2e0'

    /**
     * constructor of the parser using navitia api and google json
     *
     * @link http://doc.navitia.io/
     * @link https://github.com/google/gson     *
     */
    public NavitiaParser() {
    }

    /**
     * test for the navitia integration
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {


        NavitiaParser navitiaParser = new NavitiaParser();
        navitiaParser.getJSON();
        System.out.println(navitiaParser.getLineName());
        System.out.println(navitiaParser.getStops());


    }

    /**
     * getter for the file with th authorization key
     *
     * @return String key
     */
    public String getAuth() {
        if (Objects.equals(auth, "")) {
            try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {

                String sCurrentLine;

                while ((sCurrentLine = br.readLine()) != null) {
                    auth = sCurrentLine;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println(auth);
        return auth;
    }

    /**
     * getter for the line information from the navitia API
     */
    public void getJSONline() {
        String line;
        try {


            URL url = new URL(urlLine);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("Authorization", getAuth());
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response code:" + connection.getResponseCode());
            System.out.println("Response message:" + connection.getResponseMessage());

            // Read the response:
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));

            while ((line = reader.readLine()) != null) {
                //  System.out.println(line);
                PrintWriter writer = new PrintWriter("navitia_line_info.json", "UTF-8");
                writer.println(line);
            }
            reader.close();

        } catch (IOException e2) {
            e2.printStackTrace();

        }
    }

    /**
     * getter for the stop points information from the navitia API
     */
    public void getJSONStop() {
        String line;
        try {

            URL url = new URL(urlStop);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("Authorization", getAuth());
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response code:" + connection.getResponseCode());
            System.out.println("Response message:" + connection.getResponseMessage());

            // Read the response:
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));

            while ((line = reader.readLine()) != null) {
                //  System.out.println(line);
                PrintWriter writer = new PrintWriter("navitia_stop_info.json", "UTF-8");
                writer.println(line);
            }
            reader.close();

        } catch (IOException e2) {
            e2.printStackTrace();

        }
    }

    /**
     * getter form line info to String
     *
     * @return String containing the file from the api
     */
    public String getInfoFile() {
        String txt = "";
        try (BufferedReader br = new BufferedReader(new FileReader(INFOLINE))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                txt += sCurrentLine;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return txt;
    }

    /**
     * getter from stop info to String
     *
     * @return String containing the file from the api
     */
    public String getStopFile() {
        String txt = "";
        try (BufferedReader br = new BufferedReader(new FileReader(STOPLINE))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                txt += sCurrentLine;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return txt;
    }

    /**
     * get the line name
     *
     * @return String line name
     */
    public String getLineName() {
        String line = getInfoFile();
        JsonElement jelement = new JsonParser().parse(line);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonArray jarray = jobject.getAsJsonArray("lines");
        jobject = jarray.get(0).getAsJsonObject();

        return (jobject.get("name").toString());
    }

    /**
     * get an arrayList of the stops
     *
     * @return ArrayList Stops
     */
    public ArrayList getStops() {
        ArrayList stops = new ArrayList<String>();
        String line = getStopFile();
        JsonElement jelement = new JsonParser().parse(line);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonArray jarray = jobject.getAsJsonArray("stop_points");
        for (int i = 0; i < jarray.size(); i++) {
            jobject = jarray.get(i).getAsJsonObject();
            stops.add(jobject.get("name").toString());
        }
        return stops;
    }

    /**
     * download all the navitia JSON files
     */
    private void getJSON() {
        getJSONline();
        getJSONStop();
    }

}



