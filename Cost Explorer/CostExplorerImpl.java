class PricingPlan {
    String planId;
    double monthlyCost;
}

class Subscription {
    String planId;
    LocalDate startDate;
}

class Product {
    String name;
    Subscription subscription;
}

class Customer {
    String customerId;
    Product product;
}

interface CostExplorer {
    List<Double> monthlyCostList(Customer customer);
    double annualCost(Customer customer);
}

import java.time.LocalDate;
import java.util.*;

class CostExplorerImpl implements CostExplorer {

    private final Map<String, PricingPlan> pricingPlanMap;

    public CostExplorerImpl(List<PricingPlan> plans) {
        this.pricingPlanMap = new HashMap<>();
        for (PricingPlan p : plans) {
            pricingPlanMap.put(p.planId, p);
        }
    }

    @Override
    public List<Double> monthlyCostList(Customer customer) {
        List<Double> result = new ArrayList<>(Collections.nCopies(12, 0.0));

        Subscription sub = customer.product.subscription;
        PricingPlan plan = pricingPlanMap.get(sub.planId);
        double monthlyCost = plan.monthlyCost;

        int startMonth = sub.startDate.getMonthValue();  // 1 to 12

        for (int month = startMonth; month <= 12; month++) {
            result.set(month - 1, monthlyCost);
        }

        return result;
    }

    @Override
    public double annualCost(Customer customer) {
        return monthlyCostList(customer)
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

  public static void main(String[] args) {
    List<PricingPlan> plans = Arrays.asList(
            new PricingPlan("BASIC", 9.99),
            new PricingPlan("STANDARD", 49.99),
            new PricingPlan("PREMIUM", 249.99)
    );

    Customer customer = new Customer(
            "c1",
            new Product(
                    "Jira",
                    new Subscription("BASIC", LocalDate.of(2021, 3, 27))
            )
    );

    CostExplorer explorer = new CostExplorerImpl(plans);

    System.out.println(explorer.monthlyCostList(customer));
    System.out.println("Annual cost: " + explorer.annualCost(customer));
}
}


///////////////////////////////////


import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CostExplorerImplTest {

    private List<PricingPlan> getPlans() {
        return Arrays.asList(
                new PricingPlan("BASIC", 9.99),
                new PricingPlan("STANDARD", 49.99),
                new PricingPlan("PREMIUM", 249.99)
        );
    }

    private Customer createCustomer(String planId, int year, int month, int day) {
        return new Customer(
                "c1",
                new Product(
                        "Jira",
                        new Subscription(planId, LocalDate.of(year, month, day))
                )
        );
    }

    @Test
    void testMonthlyCost_JanuaryStart() {
        CostExplorerImpl explorer = new CostExplorerImpl(getPlans());

        Customer customer = createCustomer("BASIC", 2021, 1, 1);

        List<Double> list = explorer.monthlyCostList(customer);

        // all 12 months billed
        assertEquals(12, list.size());
        for (double cost : list) {
            assertEquals(9.99, cost, 0.001);
        }

        assertEquals(9.99 * 12, explorer.annualCost(customer), 0.001);
    }

    @Test
    void testMonthlyCost_MarchStart() {
        CostExplorerImpl explorer = new CostExplorerImpl(getPlans());

        Customer customer = createCustomer("BASIC", 2021, 3, 27);

        List<Double> list = explorer.monthlyCostList(customer);

        // Jan, Feb should be 0
        assertEquals(0.0, list.get(0));
        assertEquals(0.0, list.get(1));

        // March -> December billed
        for (int i = 2; i < 12; i++) {
            assertEquals(9.99, list.get(i), 0.001);
        }

        double expected = 9.99 * 10; // months March through Dec
        assertEquals(expected, explorer.annualCost(customer), 0.001);
    }

    @Test
    void testMonthlyCost_DecemberStart() {
        CostExplorerImpl explorer = new CostExplorerImpl(getPlans());

        Customer customer = createCustomer("PREMIUM", 2021, 12, 15);

        List<Double> list = explorer.monthlyCostList(customer);

        // Jan to Nov → 0
        for (int i = 0; i < 11; i++) {
            assertEquals(0.0, list.get(i));
        }

        // December only
        assertEquals(249.99, list.get(11), 0.001);

        assertEquals(249.99, explorer.annualCost(customer), 0.001);
    }

    @Test
    void testCorrectPlanMapping() {
        CostExplorerImpl explorer = new CostExplorerImpl(getPlans());

        Customer customerBasic = createCustomer("BASIC", 2021, 5, 1);
        Customer customerStandard = createCustomer("STANDARD", 2021, 5, 1);
        Customer customerPremium = createCustomer("PREMIUM", 2021, 5, 1);

        List<Double> basic = explorer.monthlyCostList(customerBasic);
        List<Double> standard = explorer.monthlyCostList(customerStandard);
        List<Double> premium = explorer.monthlyCostList(customerPremium);

        // May-Dec is 8 months → check one month billing value
        assertEquals(9.99, basic.get(4), 0.001);
        assertEquals(49.99, standard.get(4), 0.001);
        assertEquals(249.99, premium.get(4), 0.001);
    }

    @Test
    void testAnnualCostSumMatchesList() {
        CostExplorerImpl explorer = new CostExplorerImpl(getPlans());

        Customer customer = createCustomer("STANDARD", 2021, 4, 10);

        List<Double> list = explorer.monthlyCostList(customer);
        double sum = list.stream().mapToDouble(Double::doubleValue).sum();

        assertEquals(sum, explorer.annualCost(customer), 0.0001);
    }

    @Test
    void testMidMonthStillFullCharge() {
        CostExplorerImpl explorer = new CostExplorerImpl(getPlans());

        // Start on last day of month
        Customer customer = createCustomer("STANDARD", 2021, 6, 30);

        List<Double> list = explorer.monthlyCostList(customer);

        // June is fully charged even though startDate is 30th
        assertEquals(49.99, list.get(5), 0.001);
    }
}


