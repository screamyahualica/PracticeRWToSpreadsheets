
//import com.google.gdata.client.authn.oauth.*;
import com.google.gdata.client.spreadsheet.*;
//import com.google.gdata.data.*;
//import com.google.gdata.data.batch.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class CSC230SpreadsheetIntegration 
{
	
	public static final String GOOGLE_ACCOUNT_USERNAME = "screamyahualica2";  
	public static final String GOOGLE_ACCOUNT_PASSWORD = "csc230proj"; 
	public static final String SPREADSHEET_TITLE = "testws";
	public static final String WORKSHEET_TITLE = "test1";
	
	public static void main(String[] args)
      throws AuthenticationException, MalformedURLException, IOException, ServiceException 
      {

		System.out.println("Hello Juan! :)");
		SpreadsheetService service = setUpServiceAuthentication("CSC230 Demo",GOOGLE_ACCOUNT_USERNAME, GOOGLE_ACCOUNT_PASSWORD);
		WorksheetEntry creditCardWorksheet = getCreditCardWorksheet(SPREADSHEET_TITLE, WORKSHEET_TITLE,service);
		if (creditCardWorksheet != null)
		{
			//print how the worksheet looks like at the beginning
			System.out.println("Found worksheet: " + creditCardWorksheet.getTitle().getPlainText());
			printWorksheetEntries(creditCardWorksheet, service);
			System.out.println();
			
			//test - getting attributes from the worksheet
			System.out.println("Here are the APR for the two cards: ");		
			System.out.println("wells fargo:" + ": " + getCardEntry(creditCardWorksheet,service,"apr","wells fargo"));
			System.out.println("capital one:" + ": " + getCardEntry(creditCardWorksheet,service,"apr","capital one"));

			//test - adding a new row to the worksheet
			//addRowToWorksheet(creditCardWorksheet,service,"home depot","8%","5,672");
			//test: deleting a row
		//deleteRow(creditCardWorksheet, service, "home depot");

			//test updating an entry
			updateEntry(creditCardWorksheet,service,"capital one","Balance","$440");
			
			
		}
		else
			System.out.println("Could not find the worksheet");
		
      }
  
  
  public static SpreadsheetService setUpServiceAuthentication(String serviceTitle, 
		  String username, String password) throws AuthenticationException 
		  {
	  	//sets up the service and authentication
	   
	    SpreadsheetService service = new SpreadsheetService(serviceTitle);
	    
	    service.setUserCredentials(username, password);
	    
	    return service;

		  }

  public static WorksheetEntry getCreditCardWorksheet(String spreadsheetTitle, 
		  String worksheetTitle, SpreadsheetService service) throws MalformedURLException, IOException, ServiceException
  {
	  //returns a WorksheetEntry object with the given spreadsheetTitle and worksheetTitle (the first match)
	  //returns NULL if there is no worksheet with that given name (better solution?)
	  
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
  
  public static void printWorksheetEntries(WorksheetEntry worksheet, SpreadsheetService service)
  throws ServiceException, IOException
  {
	  //prints all entries of a worksheet
	    // Fetch the list feed of the worksheet.
	    URL listFeedUrl = worksheet.getListFeedUrl();
	    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

	    // Iterate through each row, printing its cell values.
	    for (ListEntry row : listFeed.getEntries()) 
	    {
	      // Print the first column's cell value
	      System.out.print(row.getTitle().getPlainText() + "\t");
	      // Iterate over the remaining columns, and print each cell value
	      for (String tag : row.getCustomElements().getTags()) {
	        System.out.print( row.getCustomElements().getValue(tag) + "\t");
	     }
	      System.out.println();
	    }
  }
  
  public static String getCardEntry (WorksheetEntry worksheet, 
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
  
  public static void addRowToWorksheet(WorksheetEntry worksheet,SpreadsheetService service, String cardValue,
		  String apr, String balance) throws ServiceException, IOException
//adds a row to the end of the worksheet.
	{
	  URL listFeedURL = worksheet.getListFeedUrl();
	  //why do they have the following step in the API?
	  //ListFeed listfeed = service.getFeed(listFeedURL,ListFeed.class);
	  ListEntry row = new ListEntry();
	  row.getCustomElements().setValueLocal("Card", cardValue);
	  row.getCustomElements().setValueLocal("APR", apr);
	  row.getCustomElements().setValueLocal("Balance", balance);
	  row = service.insert(listFeedURL, row);
	}
  
  public static void updateEntry(WorksheetEntry worksheet, SpreadsheetService service, String creditCardName,
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
  public static void deleteRow(WorksheetEntry worksheet, SpreadsheetService service, String creditCardName)
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