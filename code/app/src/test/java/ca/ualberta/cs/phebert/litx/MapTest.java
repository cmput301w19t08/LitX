package ca.ualberta.cs.phebert.litx;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;


public class MapTest {
    /**
     * Tests to see if the setLocation method works properly
     * Depending on coordinate will have to finish properly
     */
    @Test
    public void setLocationTest()
    {
        Coordinate coordinate = new Coordinate(2, 3);
        MapObject map = new MapObject();
        assertNull(map.getLocation());
        map.setLocation(coordinate);
        assertEquals(coordinate, map.getLocation());

    }

    /**
     * Unable to test until further implentation is decided
     */
    @Test
    public void viewMapTest()
    {
        // TODO viewMapTest()
        // unsure how this is implemented
    }
}
