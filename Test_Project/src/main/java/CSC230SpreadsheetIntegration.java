
//import com.google.gdata.client.authn.oauth.*;
import com.google.gdata.client.spreadsheet.*;
//import com.google.gdata.data.*;
//import com.google.gdata.data.batch.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;

import java.io.*;
import java.net.*;
import java.util.*;
import spreadSheetHelper.*;

public class CSC230SpreadsheetIntegration 
{
	
	public static final String GOOGLE_ACCOUNT_USERNAME = "screamyahualica2";  
	public static final String GOOGLE_ACCOUNT_PASSWORD = "csc230proj"; 
	public static final String SPREADSHEET_TITLE = "testws";
	public static final String WORKSHEET_TITLE = "test1";
	
	public static void main(String[] args)
      throws AuthenticationException, MalformedURLException, IOException, ServiceException 
      {

		System.out.println("Please enter file name:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    String fileName = br.readLine();
	    System.out.println("file name was: " + fileName);
	    
	    if( fileName.equals(""))  //nothing was entered
	    	System.out.println("No file was entered");
	    else
	    {
	    	System.out.println("Setting up authentication");
	    	SpreadsheetService service = setUpServiceAuthentication("CSC230 Demo",GOOGLE_ACCOUNT_USERNAME, GOOGLE_ACCOUNT_PASSWORD);
	    	SpreadsheetHandler sshandler = new SpreadsheetHandler(service,fileName);
	    	System.out.println("Authentication step done, using file: " + sshandler.getSpreadsheetTitle());
	    	
	    	while(true) //run forever
	    	{
	    		displayCommandLineOptions();
	    		String option = br.readLine();
	    		int optionNumber = 0;
	    		try
	    		{
	    			optionNumber = Integer.parseInt(option);
	    			switch(optionNumber)
	    			{
	    			case 1: System.out.println("Coming soon: adding a new credit card");
	    					break;
	    			case 2: getPaymentInfoAndWriteToFile(sshandler,br);
	    					break;
	    			case 3: System.out.println("Coming soon: generating a rerpot");
	    					break;
	    			case 4: System.out.println("Good bye.");
	    					return; //exit main
	    			default: System.out.println("You must enter a valid option.");
	    				
	    			}
	    		}
	    		catch (NumberFormatException e)
	    		{
	    			System.out.println("The option that you entered is not a valid option");
	    		}
	    	}
		
	    }
	    	
		
		/*OLD IMPLEMENTATION
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
			//updateEntry(creditCardWorksheet,service,"capital one","Balance","$440");
		}
		else
			System.out.println("Could not find the worksheet");
		*/
      } 
  
  
  public static void displayCommandLineOptions()
  {
	  	System.out.println("Welcome to credit card debt viewer application.  Please select one of the following options)");
		System.out.println("1) Add new credit card");
		System.out.println("2) Add new payment information");
		System.out.println("3) generate a report");
		System.out.println("4) Exit");
  }
  
  /*This function will get the payment information from the user through the command line and call the
   * spreadsheet handler to add the information to the file
   */
  public static void getPaymentInfoAndWriteToFile(SpreadsheetHandler sshandler,BufferedReader reader)
  	throws IOException, ServiceException
  {
	  
	  System.out.println("Enter the credit card name: ");
	  String creditCard = reader.readLine();
	  
	  System.out.println("Enter the payment amount:");
	  String paymentAmount = reader.readLine();
	  
	  System.out.println("Enter the remaining balance:");
	  String balance = reader.readLine();
	  
	  System.out.println("Enter the month and year of payment:");
	  String date = reader.readLine();
	  
	  System.out.println("Enter the APR:");
	  String apr = reader.readLine();
	  
	  sshandler.addNewPaymentInfo(creditCard, date,paymentAmount, balance, apr);
  }
  
  public static SpreadsheetService setUpServiceAuthentication(String serviceTitle, 
		  String username, String password) throws AuthenticationException 
		  {
	  	//sets up the service and authentication
	   
	    SpreadsheetService service = new SpreadsheetService(serviceTitle);
	    
	    service.setUserCredentials(username, password);
	    
	    return service;

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
  
  
}