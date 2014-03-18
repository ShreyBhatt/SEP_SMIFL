package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLConnection;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import static java.util.concurrent.TimeUnit.SECONDS;

import models.Stock;

/**
 * An updating cache of stock symbols and current data.
 * TODO: Should fix this to only hold onto the ~100 most requested stocks.
 */
public class YahooFinanceService {

    /** The HashMap that acts as a cache. */
    private HashMap<String, Stock> cache;

    /** A string of all the stocks we should look for. */
    private String stockString;

    /** Scheduler to help schedule the updates to run. */
    private final ScheduledExecutorService scheduler =
        Executors.newScheduledThreadPool(1);

    /** Call this to begin updating. */
    private void updater() {
        final Runnable update = new Runnable() {
            public void run() {
                YahooFinanceService.getInstance().update();
            }
        };

        final ScheduledFuture<?> updaterHandle =
            scheduler.scheduleAtFixedRate(update, 10, 10, SECONDS);
    }

    /**
     * Work horse for updating the cache, runs on its own thread and
     * does a complete replacement of the Global HashMap.
     * There should be no race case here, that concerns us.
     */
    public void update() {

        System.out.println(stockString);
        final HashMap<String, Stock> newCache = new HashMap<String, Stock>();
        int count = 0;

        try {
            BufferedReader br = callYahooAPI(stockString);

            String line = br.readLine();
            String[] a = stockString.split(",");
            while ( line != null ) {

                System.out.println(line);
                Stock stock = stockFromCSV(line.split(","), a[count]);
                newCache.put(a[count++], stock);
                line = br.readLine();
            }
        }
        catch (IOException e) {
            Logger log = Logger.getLogger(YahooFinanceService.class.getName());
            log.log(Level.SEVERE, e.toString(), e);
        }
        cache = newCache;
    }

    /**
     * A Helper method for making a new stock from the CSVs.
     * @param stockinfo is an array of CSVs
     * @param sym is the stock ticker symbol.
     * @return returns a new stock or null if the stock is invalid
     */
    private Stock stockFromCSV ( final String[] stockinfo, final String sym ) {
        if (
                "N/A".equals(stockinfo[1]) &&
                "N/A".equals(stockinfo[2]) &&
                "N/A".equals(stockinfo[3]) &&
                "N/A".equals(stockinfo[4]) &&
                "N/A".equals(stockinfo[5]) &&
                "N/A".equals(stockinfo[6]) &&
                "N/A".equals(stockinfo[7]) &&
                "N/A".equals(stockinfo[8])
                )
            return null;
        return new Stock(
                sym,
                handleDouble(stockinfo[0]),
                handleInt(stockinfo[1]),
                handleDouble(stockinfo[2]),
                handleDouble(stockinfo[3]),
                handleDouble(stockinfo[4]),
                handleDouble(stockinfo[5]),
                handleDouble(stockinfo[6]),
                handleDouble(stockinfo[7]),
                handleDouble(stockinfo[8]),
                stockinfo[9]
                );
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
                    "http://finance.yahoo.com/d/quotes.csv?s="
                    + symbolString
                    + "&f=l1vrejkghm3j1"
                    );
            return new BufferedReader(
                    new InputStreamReader(
                        yahoo.openConnection().getInputStream()
                        )
                    );
    }

    /**
     * Returns a Stock Object that contains info about a specified stock.
     * @param symbol the company's stock symbol
     * @return a stock object containing info about the company's stock or
     *         null if invalid
     * @see Stock
     */
    //@ThreadSafe
    public Stock getStock ( final String symbol ) {

        final String sym = symbol.toUpperCase();
        final Stock stock;

        if ( cache.containsKey(sym) ) {
            return cache.get(sym);
        }

        synchronized(this) {
            if ( cache.containsKey(sym) ) {
              return cache.get(sym);
            }

            try {

                String line = callYahooAPI(sym).readLine();
                System.out.println(line);

                stock = stockFromCSV(line.split(","), sym);

            }
            catch (IOException e) {
                Logger log = Logger.getLogger(YahooFinanceService.class.getName());
                log.log(Level.SEVERE, e.toString(), e);
                return null;
            }
            if (stock == null) {
                return stock;
            }
            cache.put(sym, stock);

            if (stockString == null) {
                stockString = sym;
            }
            else {
                stockString = stockString + "," + sym;
            }
        }
        return stock;
    }

    /**
     * Constructor for the YahooFinacneService.
     */
    private YahooFinanceService() {
        cache = new HashMap<String, Stock>();
    }

    /**
     * Class to instantiate a single YahooFinanceService.
     * Accomplishes this by harnessing the way the JVM works.
     * Initialization-on-demand holder idiom
     */
    private static class Wrapper {
        /** A singleton instance of YahooFinanceService. */
        private static final YahooFinanceService YAHOO;
        static {
            YAHOO = new YahooFinanceService();
            YAHOO.updater();
        }
    }

    /**
     * Method to return the singleton instance of this class.
     * @return a YahooFinanceService object
     */
    //@ThreadSafe - needs jcip.net library
    public static YahooFinanceService getInstance() {
        return Wrapper.YAHOO;
    }
}

