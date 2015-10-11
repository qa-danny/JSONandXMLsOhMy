package resources.xmltests;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

public class XmlResourceMethods {

	private String user = "user"; //ActiveMQ username
	private String password = "pass";  //ActiveMQ Password
	private String url = "tcp://testserver01.domain.net:31013"; //Broker for ActiveMQ
	private StringBuilder testMessage = new StringBuilder(); //Builds and Holds the XML
	private String queueNameFulfillmentOrder = "dschaich.testDomain.fulfillmentOrderTest"; //Where we will send the XML message
	private String xml; // Holds the XML built in the database function
	private String dbUserName = "username"; //how creative...
	private String dbPassword = "password";
	
	// the '/testings' portion signifies which db schema
	private String dbUrl = "jdbc:mysql://testserver01.domain.net:3306/testings";
	
	public String getUser() {
		return user;
	}
	public String getPassword(){
		return password;
	}
	public String getUrl(){
		return url;
	}
	public StringBuilder getTestMessage(){
		return testMessage;
	}
	public String getQueueNameFulfillmentOrder(){
		return queueNameFulfillmentOrder;
	}
	
	public String getDbUserName(){
		return dbUserName;
	}
	public String getDbPassword(){
		return dbPassword;
	}
	public String getDbUrl(){
		return dbUrl;
	}
	
	protected String getStandardMsg(StringBuilder testMessage) {
		
		testMessage.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
		//Google doesn't do fulfillment orders....or do they? ;)
		.append("<ns2:fulfillmentPreviewResponse xmlns:ns2=\"http://test.google.com/domain/fulfillmentOrder/v1_0/\">") 
		.append("<fulfillable>true</fulfillable></ns2:fulfillmentPreviewResponse>");

		//Convert StringBuilder to String
		String message = testMessage.toString();
		System.out.println(message);

		return message;
	}
	
	protected String createXml(int orderTestID, int shipLevel) {
		try {
			Connection conn = DriverManager.getConnection(getDbUrl(), getDbUserName(), getDbPassword());;

			xml = "";
			String query = "";
			Class.forName ("com.mysql.jdbc.Driver").newInstance();

			Statement s = conn.createStatement ();
			query ="SELECT DISTINCT DATE_FORMAT(SUBTIME(NOW(), '01:00:00'),'%Y-%m-%dT%H:%i:%s-08:00') AS 'TIME', "
						+ "o.order_id, a.name, a.address_one, a.city, a.state, a.postal_code, a.old_country, "
						+ "a.phone_number, oi.return_id, oi.order_item_id,"+
					//testings.test_order_id: testings = DB Schema . test_order_id = DB Table
					"IFNULL(testings.test_order_id, 'NA') AS 'TOId'"+
					" FROM orders o " +
					" JOIN order_item oi ON o.order_id = oi.order_id" +
					" JOIN address_user au ON o.shipping_address_user_id = au.address_user_id" +
					" JOIN address a ON au.address_id = a.address_id" +
					" LEFT OUTER JOIN return_ext re ON oi.return_id = re.return_id" +
					" WHERE o.order_id = " + orderTestID;
			

			//System.out.println("query: " + query);
			ResultSet rs = null;
			s.executeQuery (query);
			rs = s.getResultSet ();
			rs.next ();

			xml = createXmlFromDb(rs);

			//Close ResultSet, Statement, and Connection to DB:
			rs.close ();
			s.close ();
			conn.close();

		}catch (Exception ex) {
			System.out.println("Error Message: \n" + ex.getMessage());
		}
		
		return xml;
	}
	
		String createXmlFromDb(ResultSet rs) throws SQLException {
			
			String time = rs.getString ("TIME");
			String orderId = rs.getString ("o.order_id");
			String Name = rs.getString ("a.name");
			String address = rs.getString("a.address_one");
			String city = rs.getString("a.city");
			String state = rs.getString("a.state");
			String zip = rs.getString("a.postal_code");
			String country = rs.getString("a.old_country");
			String phone = rs.getString("a.phone_number");

			xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> " +
					"<ns2:returnNotification xmlns:ns2=\"http://test.google.com/domain/fulfillmentOrder/v1_0/\"> "+
					"<messageGenerationTimeStamp>" + time + "</messageGenerationTimeStamp> "+
					"<returnOrders> " +
					"<orderHeader> "+
					"<orderId>" + orderId + "</orderId> " +
					"<fulfillmentOrderStatus>Complete</fulfillmentOrderStatus> "+
					"<statusUpdatedDateTime>" + time + "</statusUpdatedDateTime> "+
					"<destinationAddress> "+ 
					"<name>" + Name + "</name> "+
					"<addressLine1>" + address + "</addressLine1> "+
					"<city>" + city + "</city> "+
					"<region>" + state + "</region> "+
					"<postalCode>" + zip + "</postalCode> "+
					"<countryCode>" + country + "</countryCode> "+
					"<phone>" + phone + "</phone> " +
					"</destinationAddress> "+
					"<fulfillmentPolicy>FillAllAvail</fulfillmentPolicy> "+
					"<fulfillmentMethod>Customer</fulfillmentMethod> "+
					"<coOrderId>DLS05-" + orderId + "</coOrderId> "+
					"<displayableOrderId>" + orderId + "</displayableOrderId> "+
					"<displayableOrderComment>Test Comment abcd</displayableOrderComment> "+
					"<displayableOrderDateTime>" + time + "</displayableOrderDateTime> "+
					"<receivedDateTime>" + time + "</receivedDateTime> "+
					//Standard, 2DA, NDA are options
					"<shippingSpeed>NDA</shippingSpeed> "+
					"</orderHeader> " +
					"</returnOrders> " +
					"</ns2:returnNotification>";
			
			return xml;
		}
	
}
