package tests.xmltests;

import static org.junit.Assert.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.junit.*;

public class SOACreateOrder {

	/**
	 * @author dschaich
	 * @objective This test will create a Purchase Order through the SOAServices, bypassing
	 *  the UI layer.  The services will connect to the database and all information will be 
	 *  available after the test is completed.
	 */
	@Test
	public void createPurchaseOrder() {

		String serviceURL = "http://test-qa1-soa2.domain.net:8080";

		try {
			// Create client (JerseyAPI), send XML to client.
			Client client = Client.create();
			WebResource webResource = client.resource(serviceURL+"/po-services/services/PurchaseOrderCreationSOAP12Service");

			String createPurchaseOrderXml = 
					"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:v1=\"http://www.domain.com/test/schema/messages/purchase_order_messages/v1_0\" xmlns:v11=\"http://www.domain.com/test/schema/model/purchase_order/v1_0\">"
							+ "\n   <soap:Header/>"
							+ "\n   <soap:Body>"
							+ "\n      <v1:createPurchaseOrderRequest>"
							+ "\n         <v1:contactId>22590</v1:contactId>"
							+ "\n         <v1:planningType>INITIAL</v1:planningType>"
							+ "\n         <v1:transactionType>SA</v1:transactionType>"
							+ "\n         <v1:items>"
							+ "\n            <v11:stockId>14983006</v11:stockId>"
							+ "\n            <v11:quantity>15</v11:quantity>"
							+ "\n            <v11:stockId>14983007</v11:stockId>"
							+ "\n            <v11:quantity>15</v11:quantity>"
							+ "\n            <v11:stockId>14983008</v11:stockId>"
							+ "\n            <v11:quantity>15</v11:quantity>"
							+ "\n         </v1:items>"
							+ "\n         <v1:ignoreWarnings>false</v1:ignoreWarnings>"
							+ "\n         <v1:FTZ>false</v1:FTZ>"
							+ "\n         <v1:applyStandardDiscount>false</v1:applyStandardDiscount>"
							+ "\n      </v1:createPurchaseOrderRequest>"
							+ "\n   </soap:Body>"
							+ "\n</soap:Envelope>";

			ClientResponse response = webResource.type("text/xml").post(ClientResponse.class, createPurchaseOrderXml);
			// Get the response and save it as line
			String line = response.getEntity(String.class);
			//If you really want to see it, uncomment this next line:
			//System.out.println(line);
			
			// If the response status is not 200 stop the test.
			if (response.getStatus() != 200) {
				System.out.println(line);
				throw new RuntimeException("Failed response status!! \n" + response.getStatus());
			}

			// Get the purchase order ID from the response
			String pattern = "(purchaseOrderId>)(.+)(</purchaseOrderId)";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(line);
			m.find();
			String po1 = m.group(2); //Purchase Order ID

			// Get the data for the new purchase order an verify that the status is INITIATED.
			String line2 = getPurchaseOrderData(po1);
			String pattern2 = "(<ns2:lifecycleStatus>)(.+)(</ns2:lifecycleStatus>)";
			Pattern r2 = Pattern.compile(pattern2);
			Matcher m2 = r2.matcher(line2);
			m2.find();
			
			assertEquals("INITIATED", m2.group(2));

			// Print the PO ID and the response
			System.out.println(po1);
			//System.out.println(line + "\n");

		}

		// If there is an exception fail the test.
		catch (Exception e) {
			e.printStackTrace();
			fail("CreatePurchaseOrder Failed!");
		}
	}

	// Given a PO ID look up a purchase order and return the response

	public String getPurchaseOrderData(String po1) {
		Client client = Client.create();
		WebResource webResource = client.resource("http://test-qa1-soa2.domain.net:8080/po-services/services/PurchaseOrderSearchSOAP12Service");
		String getPurchaseOrderXml = 
				"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:v1=\"http://www.domain.com/test/schema/messages/purchase_order_messages/v1_0\">"
						+ "\n   <soap:Header/>"
						+ "\n   <soap:Body>"
						+ "\n       <v1:getPurchaseOrderRequest>"
						+ "\n           <v1:purchaseOrderId>" + po1 + "</v1:purchaseOrderId>"
						+ "\n           <v1:contactId>22590</v1:contactId>"
						+ "\n       </v1:getPurchaseOrderRequest>"
						+ "\n   </soap:Body>"
						+ "\n</soap:Envelope>";

		ClientResponse response = webResource.type("text/xml").post(ClientResponse.class, getPurchaseOrderXml);
		String line = response.getEntity(String.class);
		return line;

	}
}