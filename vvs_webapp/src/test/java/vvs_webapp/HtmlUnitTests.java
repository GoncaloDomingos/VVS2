package vvs_webapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLTableRowElement;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

public class HtmlUnitTests {

	
	private static final String APPLICATION_URL = "http://localhost:8080/VVS_webappdemo/";
	private static final int APPLICATION_NUMBER_USE_CASES = 11;

	private static HtmlPage page;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) { 
	
			// possible configurations needed to prevent JUnit tests to fail for complex HTML pages
            webClient.setJavaScriptTimeout(15000);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
		    
			page = webClient.getPage(APPLICATION_URL);
			assertEquals(200, page.getWebResponse().getStatusCode()); // OK status
		}
	}
	/*
	@Test
	public void insertNewAddress() throws IOException {
		
		HtmlPage reportPage;
		
		try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) { 
			java.net.URL url = new java.net.URL(APPLICATION_URL+"GetCustomerPageController");
			WebRequest requestSettings = new WebRequest(url, HttpMethod.GET);

			// Set the request parameters
			requestSettings.setRequestParameters(new ArrayList<NameValuePair>());
			requestSettings.getRequestParameters().add(new NameValuePair("vat", "197672337"));
			requestSettings.getRequestParameters().add(new NameValuePair("submit", "Get+Customer"));

			reportPage = webClient.getPage(requestSettings);
			assertEquals(HttpMethod.GET, reportPage.getWebResponse().getWebRequest().getHttpMethod());		
		}
		
		HtmlTable table = (HtmlTable) reportPage.getByXPath("//table[@class='w3-table w3-bordered']").get(0);

		// get a specific link
		HtmlAnchor addAddressLink = page.getAnchorByHref("addAddressToCustomer.html");
		// click on it
		HtmlPage nextPage = (HtmlPage) addAddressLink.openLinkInNewWindow();
		// check if title is the one expected
		assertEquals("Enter Address", nextPage.getTitleText());

		// get the page first form:
		HtmlForm addAddressForm = nextPage.getForms().get(0);

		// place data at form
		HtmlInput vatInput = addAddressForm.getInputByName("vat");
		vatInput.setValueAttribute("197672337");

		HtmlInput addressInput = addAddressForm.getInputByName("address");
		addressInput.setValueAttribute("Rua dos olivais");
		
		HtmlInput doorInput = addAddressForm.getInputByName("door");
		doorInput.setValueAttribute("10");
		
		HtmlInput postalInput = addAddressForm.getInputByName("postalCode");
		postalInput.setValueAttribute("2700-500");
		
		HtmlInput localInput = addAddressForm.getInputByName("locality");
		localInput.setValueAttribute("Cova da Moura");
		// submit form
		HtmlInput submit = addAddressForm.getInputByValue("Insert");

		// check if report page includes the proper values
		submit.click();
		
		try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) { 
			java.net.URL url = new java.net.URL(APPLICATION_URL+"GetCustomerPageController");
			WebRequest requestSettings = new WebRequest(url, HttpMethod.GET);

			// Set the request parameters
			requestSettings.setRequestParameters(new ArrayList<NameValuePair>());
			requestSettings.getRequestParameters().add(new NameValuePair("vat", "197672337"));
			requestSettings.getRequestParameters().add(new NameValuePair("submit", "Get+Customer"));

			reportPage = webClient.getPage(requestSettings);
			assertEquals(HttpMethod.GET, reportPage.getWebResponse().getWebRequest().getHttpMethod());		
		}
		
		HtmlTable newtable = (HtmlTable) reportPage.getByXPath("//table[@class='w3-table w3-bordered']").get(0);
		
		assertTrue(table.getRowCount() + 1 == newtable.getRowCount());

		String[] myRow = {"Rua dos olivais", "10", "2700-500", "Cova da Moura"};
		
		boolean match = true;
		for(int i = 1; i < table.getRowCount(); i++) {	
			HtmlTableRow row = table.getRow(i);
			match = true;
			for(int j = 0; j < row.getCells().size(); j++) {
				if (!row.getCell(j).asText().equals(myRow[j]))
					match = false;
			}
			if(match)
				break;
		}
		
		assertTrue(match);
	}
	
	
	@Test
	public void addNewCostumers() throws IOException {
		
		final String NPC1 = "503183504";
		final String DESIGNATION1 = "Customer1";
		final String PHONE1 = "217500001";
		
		final String NPC2 = "150283440";
		final String DESIGNATION2 = "Customer2";
		final String PHONE2 = "217500002";
		
		final String NPC3 = "120001250";
		final String DESIGNATION3 = "Customer3";
		final String PHONE3 = "217500003";
		
		
		addCustomer(NPC1, DESIGNATION1, PHONE1);
		addCustomer(NPC2, DESIGNATION2, PHONE2);
		addCustomer(NPC3, DESIGNATION3, PHONE3);
		
		HtmlAnchor getCustomersLink = page.getAnchorByHref("GetAllCustomersPageController");
		HtmlPage nextPage = (HtmlPage) getCustomersLink.openLinkInNewWindow();
		
		final HtmlTable table = nextPage.getHtmlElementById("clients");

		boolean customer1 = false;
		boolean customer2 = false;
		boolean customer3 = false;
		
		for (final HtmlTableRow row : table.getRows()) {
			if (row.getCell(0).asText().equals(DESIGNATION1)
					&& row.getCell(1).asText().equals(PHONE1)
					&& row.getCell(2).asText().equals(NPC1))
				customer1 = true;
			
			if (row.getCell(0).asText().equals(DESIGNATION2)
					&& row.getCell(1).asText().equals(PHONE2)
					&& row.getCell(2).asText().equals(NPC2))
				customer2 = true;
			
			if (row.getCell(0).asText().equals(DESIGNATION3)
					&& row.getCell(1).asText().equals(PHONE3)
					&& row.getCell(2).asText().equals(NPC3))
				customer3 = true;
		}
		
		assertTrue(customer1 && customer2 && customer3);
		
	}
	*/
	private void addCustomer(String npc, String designation, String phone) throws IOException {
		HtmlAnchor addCustomerLink = page.getAnchorByHref("addCustomer.html");
		HtmlPage nextPage = (HtmlPage) addCustomerLink.openLinkInNewWindow();
		
		assertEquals("Enter Name", nextPage.getTitleText());
		HtmlForm addCustomerForm = nextPage.getForms().get(0);
		
		HtmlInput vatInput = addCustomerForm.getInputByName("vat");
		vatInput.setValueAttribute(npc);
		
		HtmlInput designationInput = addCustomerForm.getInputByName("designation");
		designationInput.setValueAttribute(designation);
		
		HtmlInput phoneInput = addCustomerForm.getInputByName("phone");
		phoneInput.setValueAttribute(phone);
		
		HtmlInput submit = addCustomerForm.getInputByName("submit");
		submit.click();
		
	}
	/*
	@Test
	public void newOpenSale() throws IOException {
		
		HtmlAnchor insertSaleLink = page.getAnchorByHref("addSale.html");
		HtmlPage nextPage = (HtmlPage) insertSaleLink.openLinkInNewWindow();
		
		assertEquals("New Sale", nextPage.getTitleText());
		HtmlForm insertSaleFormForm = nextPage.getForms().get(0);
		
		HtmlInput vatInput = insertSaleFormForm.getInputByName("customerVat");
		vatInput.setValueAttribute("197672337");
		
		HtmlInput submit = insertSaleFormForm.getInputByValue("Add Sale");
		submit.click();
		
		HtmlPage reportPage;
		
		try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) { 
			java.net.URL url = new java.net.URL(APPLICATION_URL+"GetSalePageController");
			WebRequest requestSettings = new WebRequest(url, HttpMethod.GET);

			// Set the request parameters
			requestSettings.setRequestParameters(new ArrayList<NameValuePair>());
			requestSettings.getRequestParameters().add(new NameValuePair("customerVat", "197672337"));

			reportPage = webClient.getPage(requestSettings);
			assertEquals(HttpMethod.GET, reportPage.getWebResponse().getWebRequest().getHttpMethod());		
		}
		
		HtmlTable table = (HtmlTable) reportPage.getByXPath("//table[@class='w3-table w3-bordered']").get(0);
		
		boolean match = false;
		for(int i = 1; i < table.getRowCount(); i++) {	
			HtmlTableRow row = table.getRow(i);
			if (row.getLastElementChild().asText().equals("197672337") && row.getCell(3).asText().equals("O"))
				match = true;
		}
		
		assertTrue(match);
	}*/
	
	@Test
	public void closeSale() throws IOException {
		
		//Cria utilizador
		final String NPC = "123456789";
		final String DESIGNATION = "CustomerCloseSale";
		final String PHONE = "217503001";
		
		addCustomer(NPC, DESIGNATION, PHONE);
		
		//Abre nova venda
		HtmlAnchor insertSaleLink = page.getAnchorByHref("addSale.html");
		HtmlPage nextPage = (HtmlPage) insertSaleLink.openLinkInNewWindow();
		
		assertEquals("New Sale", nextPage.getTitleText());
		HtmlForm insertSaleFormForm = nextPage.getForms().get(0);
		
		HtmlInput vatInput = insertSaleFormForm.getInputByName("customerVat");
		vatInput.setValueAttribute(NPC);
		
		HtmlInput submit = insertSaleFormForm.getInputByValue("Add Sale");
		submit.click();
		
		//Busca o id da venda e fecha-a
		HtmlAnchor closeSaleLink = page.getAnchorByHref("UpdateSaleStatusPageControler");
		nextPage = (HtmlPage) closeSaleLink.openLinkInNewWindow();
		
		HtmlTable table = (HtmlTable) nextPage.getByXPath("//table[@class='w3-table w3-bordered']").get(0);

		String id = "";
		for(int i = 2; i < table.getRowCount(); i++) {	
			HtmlTableRow row = table.getRow(i);
			if (row.getLastElementChild().asText().equals(NPC))
				id = row.getFirstElementChild().asText();
		}

		assertEquals("Enter Sale Id", nextPage.getTitleText());
		HtmlForm closeSaleFormForm = nextPage.getForms().get(0);
		
		HtmlInput idInput = closeSaleFormForm.getInputByName("id");
		idInput.setValueAttribute(id);
		
		HtmlInput submitClose = closeSaleFormForm.getInputByValue("Close Sale");
		HtmlPage report = submitClose.click();
		
		//Verificar venda fechada
		
		HtmlPage checkPage;
		
		try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) { 
			java.net.URL url = new java.net.URL(APPLICATION_URL+"GetSalePageController");
			WebRequest requestSettings = new WebRequest(url, HttpMethod.GET);

			// Set the request parameters
			requestSettings.setRequestParameters(new ArrayList<NameValuePair>());
			requestSettings.getRequestParameters().add(new NameValuePair("customerVat", NPC));

			checkPage = webClient.getPage(requestSettings);
			assertEquals(HttpMethod.GET, checkPage.getWebResponse().getWebRequest().getHttpMethod());		
		}
		
		HtmlTable salesTable = (HtmlTable) checkPage.getByXPath("//table[@class='w3-table w3-bordered']").get(0);

		boolean match = false;
		for(int i = 1; i < salesTable.getRowCount(); i++) {	
			HtmlTableRow row = salesTable.getRow(i);
			if (row.getCell(3).asText().equals("C"))
				match = true;
		}
		
		assertTrue(match);
	}
	
}
