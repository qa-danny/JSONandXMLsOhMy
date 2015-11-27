package tests.jsontests;

import java.io.File;
import java.io.IOException;
import org.junit.Test;
import resources.jsontests.JsonResources;
import com.amazonaws.util.json.JSONException;


public class CreateJSONobject extends JsonResources{

	/**
	 * This test will create a JSON Object and write it to the main folder.
	 *  It will read from a file and take the last entered number as the 
	 *  new Promotion ID.  After the JSON file is created, the script will 
	 *  update the text file for the next time the test is executed.
	 *  
	 * @throws IOException
	 */
	@Test
	public void CreateJSONObject() throws IOException {

		//Reads last line in Promo ID Log, uses it as promo ID.
		String lastPromoIdEntry = getLastEntryInPromoIdLog();
		System.out.println("Last Promo ID Entry: " + lastPromoIdEntry);

		String promoName = "promotion-" + lastPromoIdEntry;
		System.out.println("We are creating " + promoName);

		int lastPromoEntry = Integer.parseInt(lastPromoIdEntry);


		File promo = new File(promoName);

		try {
			createJSONFile(promo, lastPromoEntry, getMerchantId(), getMarketplaceId(), 
					getStartDate(), getEndDate(), getStockId(), getDiscountAmount());
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			//Update Promo ID Log for next time test is run.
			updatePromoIDLog(lastPromoEntry);
		}
	}

}
