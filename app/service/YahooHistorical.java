package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLConnection;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import play.libs.Json;

/**
 * An updating cache of stock symbols and current data.
 */
public class YahooHistorical {

    ConcurrentHashMap<String, ObjectNode> historicalData;

    private YahooHistorical() {
        this.historicalData = new ConcurrentHashMap<String, ObjectNode>();
    }

    /**
     * A Helper method to change a CSV string to a double.
     */
    private double handleDouble(String x) {
        Double y;
        if (Pattern.matches("N/A", x)) {
            y = 0.00;
        }
        else {
            y = Double.parseDouble(x);
        }
        return y;
    }

    /**
     * A Helper method to change a CSV string to an integer.
     */
    private int handleInt(String x) {
        int y;
        if (Pattern.matches("N/A", x)) {
            y = 0;
        }
        else {
            y = Integer.parseInt(x);
        }
        return y;
    }

    /**
     * A helper method for calling into the Yahoo Finance API and
     * returning the result as a BufferedReader.
     * @param symbolString is a common seperated string of symbols to look up
     * @return a BufferedReader of the CSV returned by the Yahoo API
     */
    private BufferedReader callYahooAPI ( final String symbolString )
        throws IOException {
        final URL yahoo = new URL(
                "http://ichart.finance.yahoo.com/table.csv?s="
                + symbolString
                + "&a=00&b=01&c=2012&d=06&e=05&f=2015&g=d&ignore=.csv"
                );
        return new BufferedReader(
                new InputStreamReader(
                    yahoo.openConnection().getInputStream()
                    )
                );
    }

    private static class StockClosing {
        public long time;
        public double price;
        public StockClosing( final long time, final double price) {
            this.time = time;
            this.price = price;
        }
    }

    private long handleDate( final String date ) {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date, new ParsePosition(0)).getTime();
    }

    /**
     * @param symbol the company's stock symbol
     * @return a stock object containing info about the company's stock or
     *         null if invalid
     * @see Stock
     */
    public ObjectNode getHistoricalStock ( final String symbol ) {

        final String sym = symbol.toUpperCase();
        if ( historicalData.contains(sym) ) {
            return historicalData.get(sym);
        }
        ArrayList<StockClosing> data = new ArrayList<StockClosing>();
        try {
            BufferedReader reader = callYahooAPI(sym);
            String line = reader.readLine();
            if ( line == null )
                return null;
            while ( (line = reader.readLine()) != null ) {
                String[] arr = line.split(",");
                System.out.println(line);
                data.add(
                        new StockClosing(
                            handleDate(arr[0]), handleDouble(arr[4])
                            ));

            }
        }
        catch (IOException e) {
            Logger log = Logger.getLogger(YahooHistorical.class.getName());
            log.log(Level.SEVERE, e.toString(), e);
        }
        Collections.reverse(data);
        ObjectNode node = Json.newObject();
        node.put("company", sym);
        ArrayNode arr = node.putArray("arr");
        int i = 0;
        for ( StockClosing sc : data ) {
            ArrayNode nest = arr.insertArray(i++);
            nest.add(sc.time);
            nest.add(sc.price);
        }
        historicalData.put(sym, node);
        System.out.println(node);
        return node;
    }

    /**
     * Class to instantiate a single YahooFinanceService.
     * Accomplishes this by harnessing the way the JVM works.
     * Initialization-on-demand holder idiom
     */
    private static class Wrapper {
        /** A singleton instance of YahooFinanceService. */
        private static final YahooHistorical YAHOO;
        static {
            YAHOO = new YahooHistorical();
        }
    }

    /**
     * Method to return the singleton instance of this class.
     * @return a YahooFinanceService object
     */
    public static YahooHistorical getInstance() {
        return Wrapper.YAHOO;
    }
}

