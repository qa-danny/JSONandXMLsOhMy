package resources.xmltests;

public class XmlResourceMethods {

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
}
