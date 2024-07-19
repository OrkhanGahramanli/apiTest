Feature: Api


  Scenario: Get customer invoice details
    When User post request with username "API_KASSAM_AZ" and password "gd0B04@0" to get authentication code
    And User get request with fin code "29N8GNF" and customer code "1000517614"
    Then Customer code in response body should equals "1000517614"

    @SmsTest
   Scenario Outline: Send customer invoice credit details/ "<text>"
     When User post request with username "atl" and password "atl2023@!" to get authentication code for sending customer invoice details
     And User send sms from "994558851939" number to "9700" address with "<text>" text

      Examples:
        | text |
        | borc |
        | b0rc |
        | Borc |
        | B0rc |
        | bOrc |
        | boRc |
        | b0Rc |
        | borC |
        | b0rC |
        | brc  |