import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = {"classpath:features/Api.feature"},
        tags = "@SmsTest"
)

public class TestRunner extends AbstractTestNGCucumberTests {


}
