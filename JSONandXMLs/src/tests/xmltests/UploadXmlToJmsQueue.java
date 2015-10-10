package tests.xmltests;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

import resources.xmltests.*;


/**
 * @author dschaich
 * @Objective The purpose of this test is to upload an XML file
 * 				into a Java Messaging Service queue.  All user names,
 * 				passwords, queue names, and URL's have been changed
 */

public class UploadXmlToJmsQueue extends XmlResourceMethods {
	private String user = "user"; //ActiveMQ username
	private String password = "pass";  //ActiveMQ Password
	private String url = "tcp://testserver01.domain.net:31013"; //Broker for ActiveMQ
	private Destination destination; //Holds the name of the queue
	private StringBuilder testMessage = new StringBuilder(); //Builds and Holds the XML
	private String queueName = "dsch.aich.fulfillmentOrderTest"; //Where we will send the XML message

	@Test
	public void sendStandardMessage_Test() {
		//Print out Date and Time to Console when run
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy hh:mm:ss");
		String date = sdf.format(new Date()); 
		System.out.println(date);
		
		try {
			String msg = getStandardMsg(testMessage);
			//System.out.println(msg);
			javax.jms.Connection connection = null;
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.user, this.password, this.url);
			connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, 1);
			this.destination = session.createQueue(queueName);
			MessageProducer producer = session.createProducer(this.destination);
			producer.setDeliveryMode(2);

			TextMessage message = session.createTextMessage(msg);

			producer.send(message);

			connection.close();

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}	
	}

}
