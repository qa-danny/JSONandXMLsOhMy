package resources.jsontests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonResources {
	
	private String MerchId = "9223372036413946231";
	private String MarketId = "199140";
	private String stockId = "123987";
	private String discountAmt = "49.99";
	
	protected String getMerchantId(){ return MerchId;	}
	protected String getMarketplaceId(){ return MarketId;	}
	protected String getStockId() { return stockId; }
	protected String getDiscountAmount() { return discountAmt; }
	

	public String getLastEntryInPromoIdLog() throws IOException {
		String lastLine = "";
		File file = new File("PromoIdLog.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String sCurrentLine = "";

		while ((sCurrentLine = reader.readLine()) != null) 
		{
			lastLine = sCurrentLine;
		}
		reader.close();
		return lastLine;
	}

	public void setLastEntryInPromoIdLog(String newPromoId) throws IOException {
		File file = new File("PromoIdLog.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
		bw.append("\n"+newPromoId);
		bw.close();
	}
	
	/**
	 * Updates the Promo ID Log text file.  Member of the FINALLY clause in main method.
	 * @param lastPromoEntry
	 * @throws IOException
	 */
	public void updatePromoIDLog(int lastPromoEntry) throws IOException {
		int newPromoIdEntry = lastPromoEntry+1;	
		String newPromoId = Integer.toString(newPromoIdEntry);
		setLastEntryInPromoIdLog(newPromoId);
		
	}

	/**
	 * Start Date is the current date.
	 * @return
	 */
	public String getStartDate() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
		Date date = new Date();
		String startDate = dateFormat.format(date);
		return startDate;
	}

	/**
	 * End Date will be 10 days from the current date.
	 * @return
	 */
	public String getEndDate() {
		Date date = new Date();
		 
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 10); // add 10 days
		 
		date = cal.getTime();
		DateFormat df = new SimpleDateFormat("MM/dd/YYYY");
		String endDate = df.format(date).toString();
		return endDate;
	}


	public void createJSONFile(File fileName, int promoId, String merchId, 
			String marketId, String startDate, String endDate, String stockId,
			String discountAmount) throws IOException, JSONException {

		JSONObject stockPromo = new JSONObject()
		.put("stock_id",stockId)
		.put("discount_price", discountAmount);

		JSONArray stockPromoArray = new JSONArray();
		stockPromoArray.put(stockPromo);

		JSONObject jsonPromo = new JSONObject().put("promotion_id", promoId)
				.put("merchant_id", merchId)
				.put("marketplace_id", marketId)
				.put("group_id", "null")
				.put("startDate", startDate)
				.put("endDate", endDate)
				.put("priority", 2000)
				.put("items", stockPromoArray);
		
		//put JSON to string
		String jsonPromoString = jsonPromo.toString();

		// try-with-resources statement based on post comment below :)
		try (FileWriter file = new FileWriter(fileName)) {
			file.write(jsonPromoString);
			System.out.println("Successfully Wrote JSON Object to File...");
		} catch (IOException ioe) {
			ioe.toString();
		}
		
		//Optional:
		System.out.println(jsonPromoString);
	}
}
