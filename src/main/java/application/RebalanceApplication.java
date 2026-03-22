package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"controllers", "services", "dao"})
public class RebalanceApplication {

    public static void main(String[] args) {
        System.out.println("🚀 Starting Rebalance REST API...");
        SpringApplication.run(RebalanceApplication.class, args);
        System.out.println("✓ API is running on http://localhost:8080");
        System.out.println("✓ Endpoints available:");
        System.out.println("   POST /api/rebalance/calculate-batch - Calculate and save to DB");
        System.out.println("   GET  /api/rebalance/batch/{batchId} - Get results for batch");
        System.out.println("   POST /api/rebalance/calculate - Calculate without saving");
        System.out.println("   GET  /api/rebalance/health - Health check");
    }
}
