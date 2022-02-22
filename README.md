#Commission-calculator

Sample web application that exposes an API endpoint to calculate the commission on the given transaction.
This app is not intended to be run on production, but employs alle the necessary strategies to handle transactions and their commissions.

##Requirements:
* JDK 11 (or higher)
* Maven 3 (optional, can use mvnw)

##Running the app:
To compile the app run `mvn clean install` in any console, or use the given `mvnw` 
if Maven is not installed on the system.
You can also run the tests with `mvn test` or a single test or test class (e.g. `mvn test -Dtest=CommissionRulesServiceTest`)

App can then be run with the command `java -jar` and the generated `.jar` in the `/target` folder, like any other jar, 
or even easier with the command `mvn spring-boot:run`