package models;


/**
 * A Stock class.
 * TODO: Flesh this out. Need to get the company name.
 */
public class Stock {
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
    private final String marketCap;

    public Stock (
            String sym, double price, int volume,
            double pe, double eps, double week52Low, double week52High,
            double dayLow, double dayHigh, double moving50DayAvg,
            String marketCap
            ) {
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
            }

    public String getSymbol() { return this.sym; }
    public double getPrice() { return this.price; }
    public int getVolume() { return this.volume; }
    public double getPE() { return this.pe; }
    public double getEPS() { return this.eps; }
    public double getWeek52Low() { return this.week52Low; }
    public double getWeek52High() { return this.week52High; }
    public double getDayLow() { return this.dayLow; }
    public double getDayHigh() { return this.dayHigh; }
    public double getMoving50DayAvg() { return this.moving50DayAvg; }
    public String getMarketCap() { return this.marketCap; }
}
