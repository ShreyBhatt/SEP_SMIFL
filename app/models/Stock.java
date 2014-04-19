package models;

import play.libs.Json;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A Stock class.
 */
public class Stock {
    private final String name;
    private final String sym;
    private final double price;
    private final int volume;
    private final double pe;
    private final double eps;
    private final double week52Low;
    private final double week52High;
    private final double dayLow;
    private final double dayHigh;
    private final double moving50DayAvg;
    private final double open;
    private final String marketCap;

    public Stock (
            String sym, double price, int volume,
            double pe, double eps, double week52Low, double week52High,
            double dayLow, double dayHigh, double moving50DayAvg,
            String marketCap, String name, double open
            ) {
        //TODO, change this
        this.name = name;
        this.sym = sym;
        this.price = price;
        this.volume = volume;
        this.pe = pe;
        this.eps = eps;
        this.week52Low = week52Low;
        this.week52High = week52High;
        this.dayLow = dayLow;
        this.dayHigh = dayHigh;
        this.moving50DayAvg = moving50DayAvg;
        this.marketCap = marketCap;
        this.open = open;
            }


    public ObjectNode getJson() {
        return Json.newObject()
            .put("company", this.name)
            .put("ticker", this.sym)
            .put("price", this.price)
            .put("volume", this.volume)
            .put("pe", this.pe)
            .put("eps", this.eps)
            .put("week52High", this.week52High)
            .put("week52Low", this.week52Low)
            .put("dayLow", this.dayLow)
            .put("dayHigh", this.dayHigh)
            .put("moving50DayAvg", this.moving50DayAvg)
            .put("marketCap",this.marketCap)
            .put("open",this.open);

    }


    //Getter Methods
    public String getName() { return this.name; }
    public String getTicker() { return this.sym; }
    public double getPrice() { return this.price; }
    public int getVolume() { return this.volume; }
    public double getPE() { return this.pe; }
    public double getEPS() { return this.eps; }
    public double getWeek52Low() { return this.week52Low; }
    public double getWeek52High() { return this.week52High; }
    public double getDayLow() { return this.dayLow; }
    public double getDayHigh() { return this.dayHigh; }
    public double getMoving50DayAvg() { return this.moving50DayAvg; }
    public double getOpen() { return this.open; }
    public String getMarketCap() { return this.marketCap; }
}

