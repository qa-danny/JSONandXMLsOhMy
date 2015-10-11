package tests.xmltests;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;

import resources.xmltests.*;

public class CreateXmlWithDB extends XmlResourceMethods {
	
	
	@Test
	public void sendMessage() {
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy hh:mm:ss");
		String date = sdf.format(new Date()); 
		System.out.println(date);
		
		int orderId = 123789;
		int shipLevel = 1;
		
		try {
			String msg = createXml(orderId, shipLevel);
			
			System.out.println("Here is the message: \n" + msg);

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
	}
}
