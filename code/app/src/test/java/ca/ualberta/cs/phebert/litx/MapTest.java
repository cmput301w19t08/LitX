package ca.ualberta.cs.phebert.litx;
import org.junit.Test;
import static org.junit.Assert.assertNull;


public class MapTest {
    /**
     * Tests to see if the setLocation method works properly
     * Depending on coordinate will have to finish properly
     */
    @Test
    public void setLocationTest()
    {
        Coordinate coordinate = new Coordinate();
        Map map = new Map();
        assertNull(map.getLocation());
        map.setLocation(coordinate);
        assertEquals(coordinate, map.getLocation());

    }

    @Test
    public void viewLocationTest()
    {
        // TODO viewLocationTest()
        // unsure how this is implemented
    }
}
