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
    *
    *   Testing Json Objects
    *  
    */
    @Test
    public void JsonTest() { //Testing Constructor
      League league = new League("Jesse");
      Assert.assertTrue( league.name.equals("Jesse") );
      

      ObjectNode JsonObject = league.getJson();
      System.out.println( JsonObject.toString() );


     // Assert.assertTrue( expectedOutput.equals( JsonObject.toString() );
      
    }

}
