package apiSteps;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeoutException;

import static io.restassured.RestAssured.given;


public class ApiSteps {

    @Before
    public static void setup(){
//        RestAssured.baseURI = "https://testsrv.abc-telecom.az:5007";
    }

    String authCode;
    String customerCode;
    int statusCode;

    @When("User post request with username {string} and password {string} to get authentication code")
    public void user_post_request_for_getting_authentication_code(String username, String password) {

        Response response = given().relaxedHTTPSValidation()
                .header("Content-type", "application/json")
                .when()
                .queryParam("username", username).queryParam("password", password)
                .post("/api/Auth/login")
                .then()
                .extract().response();
        System.out.println(response.getBody().asString());
        authCode = response.jsonPath().getString("_Token");
    }


    @And("User get request with fin code {string} and customer code {string}")
    public void userGetRequestWithFinCodeAndCustomerCode(String finCode, String customerCode) {

        Response response = given().relaxedHTTPSValidation()
                .header("Authorization", authCode)
                .when()
                .queryParam("fin_code", finCode).queryParam("customer_code", customerCode)
                .get("/api/InvoiceInfo/Fin_CariKod")
                .then()
                .extract().response();
        System.out.println(response.jsonPath().get("customer.cari_kod").toString());
        this.customerCode = response.jsonPath().get("customer.cari_kod").toString();
    }

    @Then("Customer code in response body should equals {string}")
    public void customerCodeInResponseBodyShouldEquals(String customerCode) {
        Assert.assertEquals(this.customerCode, customerCode);
    }

    @When("User post request with username {string} and password {string} to get authentication code for sending customer invoice details")
    public void userPostRequestWithUsernameAndPasswordToGetAuthenticationCodeForSendingCustomerInvoiceDetails(String username, String password) {
        String url = "https://testsrv.abc-telecom.az:5002";
        String body = "{\n" +
                "  \"userName\": \""+username+"\",\n" +
                "  \"password\": \""+password+"\"\n" +
                "}";
        Response response = given().relaxedHTTPSValidation()
                .baseUri(url)
                .header("Content-type", "application/json")
                .when()
                .body(body)
                .post("/Auth")
                .then()
                .extract().response();
        authCode = response.getBody().asString();
    }

    @And("User send sms from {string} number to {string} address with {string} text")
    public void userSendSmsFromNumberToAddressWithText(String mobileNum, String smsAddress, String text) throws SQLException {
        String url = "https://testsrv.abc-telecom.az:5002";
        String body = "{\n" +
                "  \"from\": \"" + mobileNum+ "\",\n" +
                "  \"to\": \"" + smsAddress + "\",\n" +
                "  \"text\": \"" + text + "\",\n" +
                "  \"msgid\": \"5590\"\n" +
                "}";

        Response response = given().relaxedHTTPSValidation()
                .baseUri(url)
                .header("Content-Type", "application/json").header("Authorization", "Bearer " + authCode)
                .when()
                .body(body)
                .post("/CustomerRatings")
                .then().extract().response();
        statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 200, "Status code is " + statusCode);
        String selectQuery = "select * from CustomerDebtRequests where CustomerPhone like '%" + mobileNum.substring(mobileNum.length() - (mobileNum.length() - 3)) + "'";
        String deleteQuery = "delete from CustomerDebtRequests where CustomerPhone like '%" + mobileNum.substring(mobileNum.length() - (mobileNum.length() - 3)) + "'";

        Statement stmt = DBConnection.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(selectQuery);

        long startTime = System.currentTimeMillis();
        long endTime = startTime + 50000;


            while (rs.next() && rs.getString("IsProcessed").equals("0") && System.currentTimeMillis() < endTime) {
                rs = stmt.executeQuery(selectQuery);
            }



        while(rs.next()){
            Assert.assertEquals(rs.getString("IsProcessed") , "1" );
        }

        stmt.executeUpdate(deleteQuery);
    }
}
