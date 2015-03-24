package spreadSheetHelper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;


public class SpreadsheetHandler {
	private SpreadsheetService service;
	private String spreadsheetTitle;
	
	private static String FIRST_COLUMN_DATE = "DATE";
	private static String SECOND_COLUMN_PAYMENT = "PAYMENT";
	private static String THIRD_COLUMN_BALANCE = "BALANCE";
	private static String FOURTH_COLUMN_APR = "APR";
	
	/*constructor, sets objects to null*/
	public SpreadsheetHandler()
	{
		this.service = null;
		this.spreadsheetTitle = null;
	}
	
	/*constructor - passes in the service and title of the spreadsheet*/
	public SpreadsheetHandler(SpreadsheetService s, String title)
	{
		this.service = s;
		this.spreadsheetTitle = title;
	}
	
	/*get and set methods*/
	public String getSpreadsheetTitle()
	{
		return this.spreadsheetTitle;
	}
	
	public void setSpreadsheetTitle(String t)
	{
		this.spreadsheetTitle = t;
	}
	
	public SpreadsheetService getSpreadsheetService()
	{
		return this.service;
	}
	
	public void setSpreadsheetService(SpreadsheetService s)
	{
		this.service = s;
	}
	
	/*HELPER FUNCTIONS*/
	public String getListOfCreditCards()
	{
		/*TODO: Research how to return a list of string*/
		return "TODO";
	}
	
	public void addNewPaymentInfo(String cardName, String date,String payment, String balance, String apr)
throws MalformedURLException, IOException, ServiceException
	{
		/*TODO: */
		System.out.println("Going to add the info");
		
		WorksheetEntry myWorksheet = getCreditCardWorksheet(this.getSpreadsheetTitle(), cardName, this.getSpreadsheetService());
		if(myWorksheet == null)
		{
			System.out.println("Worksheet could not be found.  TODO: handle this in a better way");
		}
		else
			this.addRowToWorksheet(myWorksheet, this.getSpreadsheetService(), date, payment, balance, apr);
	}
	
	  //returns a WorksheetEntry object with the given spreadsheetTitle and worksheetTitle (the first match)
	  //returns NULL if there is no worksheet with that given name (better solution?)
	private WorksheetEntry getCreditCardWorksheet(String spreadsheetTitle, 
			  String worksheetTitle, SpreadsheetService service) throws MalformedURLException, IOException, ServiceException
	  {

		  
		  // Define the URL to request.  This should never change.
		  URL SPREADSHEET_FEED_URL = new URL(
				  "https://spreadsheets.google.com/feeds/spreadsheets/private/full");
		  
		  // Make a request to the API and get all spreadsheets.
		  SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
		  List<SpreadsheetEntry> spreadsheets = feed.getEntries();
		  
		  // Iterate through all of the spreadsheets returned
		  for (SpreadsheetEntry spreadsheet : spreadsheets) 
		  {
			  if ( spreadsheet.getTitle().getPlainText().equals(spreadsheetTitle))
			  {
				  for (WorksheetEntry worksheet : spreadsheet.getWorksheets() )
				  {
					  if (worksheet.getTitle().getPlainText().equals(worksheetTitle))
						  return worksheet;
				  }
			  }
			
		   }
		  
		    //no worksheet matched
			return null;

	  }

	private String getCardEntry (WorksheetEntry worksheet, 
			  SpreadsheetService service,String cardEntry, String cardName) throws ServiceException, IOException
	  {
		  //looks through the worksheet for the specific entry of the specific card.  If no entry is
		  //found, returns an empty string
		    URL listFeedUrl = worksheet.getListFeedUrl();
		    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

		    // Iterate through each row, printing its cell values.
		    for (ListEntry row : listFeed.getEntries()) 
		    {
		     
		      if(row.getTitle().getPlainText().equals(cardName))
		    		  return row.getCustomElements().getValue(cardEntry);
		     
		    }
		    //no entry was found.  Find a better solution
		    return "";
	  }

	  private void addRowToWorksheet(WorksheetEntry worksheet,SpreadsheetService service, String date, 
			  String payment, String balance, String apr) throws ServiceException, IOException
	//adds a row to the end of the worksheet.
		{
		  URL listFeedURL = worksheet.getListFeedUrl();
		  //why do they have the following step in the API?
		  ListFeed listfeed = service.getFeed(listFeedURL,ListFeed.class);
		  ListEntry row = new ListEntry();
		  row.getCustomElements().setValueLocal(SpreadsheetHandler.FIRST_COLUMN_DATE,date );
		  row.getCustomElements().setValueLocal(SpreadsheetHandler.SECOND_COLUMN_PAYMENT, payment);
		  row.getCustomElements().setValueLocal(SpreadsheetHandler.THIRD_COLUMN_BALANCE, balance);
		  row.getCustomElements().setValueLocal(SpreadsheetHandler.FOURTH_COLUMN_APR, apr);
		  row = service.insert(listFeedURL, row);
		}
	  
	  private void updateEntry(WorksheetEntry worksheet, SpreadsheetService service, String creditCardName,
			  String entryToUpdate, String newValue) throws ServiceException, IOException
			  //updates the entry with the given credit card, given entry and new value.  If the entry is not 
			  //found, it will not update anything.  Need a better solution.
			  //WILL ONLY UPDATE THE FIRST CREDIT CARD that matches the name
		{
		  URL listFeedUrl = worksheet.getListFeedUrl();
		  ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
		  //search through the rows for the given credit card
		  for (ListEntry row : listFeed.getEntries() )
			  if(row.getTitle().getPlainText().equals(creditCardName))
			  {
				  row.getCustomElements().setValueLocal(entryToUpdate, newValue);
				  row.update();
				  return;  //otherwise, it will update all the rows.  What should we do with repeats?
			  }
		//if it gets to this point, it did not find the given credit card.  How do we signal this to the caller?
		  
		}
	  
	  
	  //TODO: Refactor the code so that we can have a function that searches for a given row, and
	  //returns that given row.  What should it do if it did not find it?  return NULL?
	  //Throw an exception?
	  private void deleteRow(WorksheetEntry worksheet, SpreadsheetService service, String creditCardName)
	  		throws ServiceException, IOException
	  		//deletes the first row that matches the given credit card
	  		{
		  URL listFeedUrl = worksheet.getListFeedUrl();
		  ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
		  for (ListEntry row : listFeed.getEntries() )
			  if (row.getTitle().getPlainText().equals(creditCardName))
				  row.delete();
	  		}
	  	//row not found.  What do we do?

}


