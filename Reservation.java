package javafxtesting.interfaces;

/**
 * Interface for classes that can generate reports
 * Implemented by Administrator class for revenue reporting
 */
public interface Reportable {

    /**
     * Generate a report based on implementation-specific criteria
     * @return String containing the formatted report
     */
    String generateReport();
}
