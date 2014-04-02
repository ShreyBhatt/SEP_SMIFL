import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import service.*;
import models.*;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public void simpleCheck() { 
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }
    
    /** YahooFinanceService.java UNIT TESTS */
    
    /** 
    * Test to check Instance remains the same.
    *
    */
    @Test
    public void instanceTest(){ 
      YahooFinanceService yahoo = YahooFinanceService.getInstance();
      
      YahooFinanceService yahoo2 = YahooFinanceService.getInstance();

      Assert.assertTrue( yahoo == yahoo2 );

    }
    
    /** Models Testing */

    /** League.java
    *   Testing Constructor
    *   Testing Json Objects
    *  
    */
    @Test
    public void JsonTest() { //Testing Constructor
    
      League league = new League("Jesse");
      Assert.assertTrue( league.name.equals("Jesse") );
      Assert.assertTrue( league.id == 0 );

    }

     @Test
      public void LeagueJsonTest(){
     
      League league = new League("Jesse");
      ObjectNode JsonObject = league.getJson();
      System.out.println( JsonObject.toString() );


     // Assert.assertTrue( expectedOutput.equals( JsonObject.toString() );
      
     }       
  

    /** Position.java
    *   Testing Position Constructor
    *   Testing Json Objects
    *  
    */
    @Test
    public void PositionTest() { //Testing Constructor
      Position position = new Position(0, "OWN", "GOOG", 1120, 10);
      
      Assert.assertTrue( position.id == 0 );
      Assert.assertTrue( position.typeOf.equals("OWN") );
      Assert.assertTrue( position.ticker.equals("GOOG") );

      Assert.assertTrue( position.price == 1120);
      Assert.assertTrue( position.qty == 10 );

    }

    @Test
      public void PositionJsonTest(){
    
      Position position = new Position(0, "OWN", "GOOG", 1120, 10);
      ObjectNode JsonObject = position.getJson();
      System.out.println( JsonObject.toString() );


     // Assert.assertTrue( expectedOutput.equals( JsonObject.toString() );
      
     }
   

    /** Portfolio.java
    *   Testing Portfolio Constructor
    *   Testing Json Objects
    *  
    */
    @Test
    public void PortfolioTest() { //Testing Constructor
      Portfolio portfolio = new Portfolio(0, 0);
      
      Assert.assertTrue( portfolio.userId == 0 );
      Assert.assertTrue( portfolio.leagueId == 0);

    }

    @Test
      public void PortfolioJsonTest(){
    
      Portfolio portfolio = new Portfolio(0, 0);     
      ObjectNode JsonObject = portfolio.getJson();
      System.out.println( JsonObject.toString() );


     // Assert.assertTrue( expectedOutput.equals( JsonObject.toString() );
      
     }
 

    /** Stock.java
    *   Testing Stock Constructor
    *   Testing Json Objects
    *  
    */
    @Test
    public void StockTest() { //Testing Constructor
      
      Stock stock = new Stock("GOOG", 1120, 10000, 1, 1, 1, 1, 1110, 1130, 1, "30B");
      
      Assert.assertTrue( stock.getTicker().equals("GOOG") );
      Assert.assertTrue( stock.getPrice() == 1120);
      Assert.assertTrue( stock.getVolume() == 10000 );
      Assert.assertTrue( stock.getPE() == 1);
      Assert.assertTrue( stock.getEPS() == 1);
      Assert.assertTrue( stock.getWeek52Low() == 1);
      Assert.assertTrue( stock.getWeek52High() == 1);
      Assert.assertTrue( stock.getDayLow() == 1110);
      Assert.assertTrue( stock.getDayHigh() == 1130);
      Assert.assertTrue( stock.getMoving50DayAvg() == 1);
      Assert.assertTrue( stock.getMarketCap().equals("30B") );


    }

    @Test
      public void StockJsonTest(){
      
      Stock stock = new Stock("GOOG", 1120, 10000, 1, 1, 1, 1, 1110, 1130, 1, "30B");

      ObjectNode JsonObject = stock.getJson();
      System.out.println( JsonObject.toString() );


     // Assert.assertTrue( expectedOutput.equals( JsonObject.toString() );
      
     }
 

}
