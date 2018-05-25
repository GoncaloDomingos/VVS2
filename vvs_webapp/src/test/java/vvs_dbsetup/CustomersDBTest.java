package vvs_dbsetup;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.junit.*;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import static vvs_dbsetup.DBSetupUtils.*;
import webapp.services.*;

public class CustomersDBTest {

	private static Destination dataSource;
	
    // the tracker is static because JUnit uses a separate Test instance for every test method.
    private static DbSetupTracker dbSetupTracker = new DbSetupTracker();
	
    @BeforeClass
    public static void setupClass() {
    	startApplicationDatabaseForTesting();
		dataSource = DriverManagerDestination.with(DB_URL, DB_USERNAME, DB_PASSWORD);
    }
    
	@Before
	public void setup() throws SQLException {

		Operation initDBOperations = Operations.sequenceOf(
			  DELETE_ALL
			, INSERT_CUSTOMER_SALE_DATA
			, INSERT_CUSTOMER_ADDRESS_DATA
			);
		
		DbSetup dbSetup = new DbSetup(dataSource, initDBOperations);
		
        // Use the tracker to launch the DbSetup. This will speed-up tests 
		// that do not not change the BD. Otherwise, just use dbSetup.launch();
        dbSetupTracker.launchIfNecessary(dbSetup);
		
	}
	
	@Test(expected=ApplicationException.class)
	public void addExistingClient() throws ApplicationException {
		CustomerService.INSTANCE.addCustomer(150283440, "Goncalo", 912345678);
		CustomerService.INSTANCE.addCustomer(150283440, "Miguel", 912345677);
	}
	
	@Test
	public void updateCostumerContact() throws ApplicationException {
		CustomerService.INSTANCE.updateCustomerPhone(168027852, 912345678);
		CustomerDTO customerDTO = CustomerService.INSTANCE.getCustomerByVat(168027852);
		assertEquals(912345678, customerDTO.phoneNumber);
	}
	
	@Test
	public void deleteAllCostumers() throws ApplicationException {
		for (CustomerDTO c : CustomerService.INSTANCE.getAllCustomers().customers) {
			CustomerService.INSTANCE.removeCustomer(c.vat);
		}
		assertTrue(CustomerService.INSTANCE.getAllCustomers().customers.isEmpty());
	}
	
	@Test
	public void reInsertCostumer() throws ApplicationException {
		CustomerService.INSTANCE.removeCustomer(168027852);
		CustomerService.INSTANCE.addCustomer(168027852, "LUIS SANTOS", 964294317);
		assertNotNull(CustomerService.INSTANCE.getCustomerByVat(168027852));
	}
	
	@Test(expected=ApplicationException.class)
	public void removeDeadSales() throws ApplicationException {
		SaleService.INSTANCE.addSaleDelivery(1, 1);
		SaleService.INSTANCE.addSaleDelivery(2, 2);
		CustomerService.INSTANCE.removeCustomer(197672337);
		assertTrue(SaleService.INSTANCE.getAllSales(197672337).sales.isEmpty());
		assertTrue(SaleService.INSTANCE.getSalesDeliveryByVat(197672337).sales_delivery.isEmpty());
		CustomerService.INSTANCE.getCustomerByVat(197672337);
	}
	
	@Test
	public void increaseSales() throws ApplicationException {
		SaleService.INSTANCE.addSale(197672337);
		assertEquals(NUM_INIT_SALES + 1, SaleService.INSTANCE.getAllSales().sales.size());
	}
	
	@Test
	public void closeSale() throws ApplicationException {
		SaleService.INSTANCE.updateSale(1);
		for(SaleDTO s : SaleService.INSTANCE.getAllSales(197672337).sales){
			if (s.id == 1)
				assertEquals(s.statusId, "C");
			else
				assertEquals(s.statusId, "O");
		}
	}
	
}
