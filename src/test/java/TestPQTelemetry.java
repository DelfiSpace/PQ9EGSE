/*
 * Copyright (C) 2018 Stefano Speretta
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.io.File;
import org.delfispace.protocols.pq9.PQ9;
import org.delfispace.protocols.pq9.PQ9Exception;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xtce.toolkit.XTCEContainerContentModel;
import org.xtce.toolkit.XTCEDatabase;
import org.xtce.toolkit.XTCEDatabaseException;
import org.xtce.toolkit.XTCETMStream;

/**
 *
 * @author Stefano Speretta <s.speretta@tudelft.nl>
 */
public class TestPQTelemetry 
{
    private final static String INPUT_FILE = "EPS.xml";
    private static XTCEDatabase db;
    private static XTCETMStream stream;
    
    @BeforeClass
    public static void setUpClass() throws XTCEDatabaseException 
    {
        db = new XTCEDatabase(new File(INPUT_FILE), true, true, true);
        stream = db.getStream( "PQ9bus" );  
        if (db.getErrorCount() != 0)
        {
            db.getDocumentWarnings().forEach((item) -> 
            {
                System.out.println("XML parsing error: "  + item);
            });
            Assert.fail();
        }
    }
    
    @AfterClass
    public static void tearDownClass() 
    {
    }
    
    @Before
    public void setUp() 
    {
        
    }
    
    @After
    public void tearDown() 
    {
    }

    @Test
    public void testPing() throws PQ9Exception, XTCEDatabaseException 
    {    
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        PQ9 pingRequest = new PQ9(1, 1, new byte[]{(byte)0x11, (byte)0x01});

        XTCEContainerContentModel pingRequestDecoded = 
                stream.processStream( pingRequest.getFrame() );
        Assert.assertEquals("PingService", pingRequestDecoded.getName());
        
        PQ9 pingResponse = new PQ9(1, 1, new byte[]{(byte)0x11, (byte)0x02});
        
        XTCEContainerContentModel pingResponseDecoded = 
                stream.processStream( pingResponse.getFrame() );
        Assert.assertEquals("PingService", pingResponseDecoded.getName());        
    }

    @Test
    public void testOBCHousekeeping() throws PQ9Exception, XTCEDatabaseException  
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        PQ9 OBCHKRequest = new PQ9(1, 1, 
                new byte[]{(byte)0x03, (byte)0x01});
        
        XTCEContainerContentModel OBCHKRequestDecoded = 
                stream.processStream( OBCHKRequest.getFrame() );
        Assert.assertEquals("HousekeepingRequest", OBCHKRequestDecoded.getName());
        // check if it is actually coming from OBC
                
        PQ9 OBCHKResponse = new PQ9(1, 1, new byte[]{ 
            (byte)0x03, (byte)0x02, (byte)0x00, (byte)0x54, (byte)0x1E, (byte)0xD7, 
            (byte)0xDE, (byte)0xAD, (byte)0xBE, (byte)0xEF, (byte)0x01, (byte)0xA1, 
            (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x1F, (byte)0x0A, (byte)0x36, 
            (byte)0xCA, (byte)0xFE});
            
        XTCEContainerContentModel OBCHKResponseDecoded = 
                stream.processStream( OBCHKResponse.getFrame() );
        Assert.assertEquals("OBCHousekeepingReply", OBCHKResponseDecoded.getName());                
    }
    
    @Test
    public void testEPSHousekeeping() throws PQ9Exception, XTCEDatabaseException  
    {
        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );
        
        PQ9 EPSHKRequest = new PQ9(2, 1, 
                new byte[]{(byte)0x03, (byte)0x01});
        
        XTCEContainerContentModel OBCHKRequestDecoded = 
                stream.processStream( EPSHKRequest.getFrame() );
        Assert.assertEquals("HousekeepingRequest", OBCHKRequestDecoded.getName());
        // check if it is actually coming from EPS

        PQ9 EPSHKResponse = new PQ9(1, 2, new byte[]{ 
            (byte)0x03, (byte)0x02, (byte)0x00, (byte)0x14, (byte)0x89, (byte)0x13, 
            (byte)0xDE, (byte)0xAD, (byte)0xBE, (byte)0xEF, (byte)0x03, (byte)0x3A, 
            (byte)0x00, (byte)0x83, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, 
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
            (byte)0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, 
            (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0x00, (byte)0xB4, (byte)0x00, 
            (byte)0x93, (byte)0x00, (byte)0x67, (byte)0x00, (byte)0x84, (byte)0x0A, 
            (byte)0x35, (byte)0x0A, (byte)0x36, (byte)0x0A, (byte)0x38, (byte)0x0A, 
            (byte)0x36, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, 
            (byte)0xFF, (byte)0xFF, (byte)0x01, (byte)0x43, (byte)0x00, (byte)0x6E, 
            (byte)0x09, (byte)0x5D, (byte)0x02, (byte)0x95, (byte)0x0A, (byte)0x39, 
            (byte)0xCA, (byte)0xFE});
            
        XTCEContainerContentModel EPSHKResponseDecoded = 
                stream.processStream( EPSHKResponse.getFrame() );
        Assert.assertEquals("EPSHousekeepingReply", EPSHKResponseDecoded.getName());                
    }
}
