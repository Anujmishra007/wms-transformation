Feature: FN839 Decode Barcode - Screens 4641, 4642, 4645

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Country-Code = 'KR'
    * header X-Warehouse-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Decode location barcode - valid
    Given path '/api/v1/picking/decode'
    And request
    """
    {
      "barcode": "A01-B02-L03",
      "expectedType": "LOCATION",
      "taskId": "TSK001",
      "expectedLocation": "A01-B02-L03"
    }
    """
    When method POST
    Then status 200
    And match response ==
    """
    {
      "decodedType": "LOCATION",
      "validated": true,
      "decodedValue": "A01-B02-L03",
      "errorCode": null,
      "message": null
    }
    """

  Scenario: Decode location barcode - mismatch
    Given path '/api/v1/picking/decode'
    And request
    """
    {
      "barcode": "A01-B02-L99",
      "expectedType": "LOCATION",
      "taskId": "TSK001",
      "expectedLocation": "A01-B02-L03"
    }
    """
    When method POST
    Then status 200
    And match response.validated == false
    And match response.errorCode == 'LOCATION_MISMATCH'

  Scenario: Decode SKU barcode - valid
    Given path '/api/v1/picking/decode'
    And request
    """
    {
      "barcode": "NK123456",
      "expectedType": "SKU",
      "taskId": "TSK001",
      "expectedSku": "NK123456"
    }
    """
    When method POST
    Then status 200
    And match response.decodedType == 'SKU'
    And match response.validated == true

  Scenario: Decode SKU barcode - mismatch
    Given path '/api/v1/picking/decode'
    And request
    """
    {
      "barcode": "NK999999",
      "expectedType": "SKU",
      "taskId": "TSK001",
      "expectedSku": "NK123456"
    }
    """
    When method POST
    Then status 200
    And match response.validated == false
    And match response.errorCode == 'SKU_MISMATCH'

  Scenario: Decode LPN barcode
    Given path '/api/v1/picking/decode'
    And request
    """
    {
      "barcode": "LP0000000001",
      "expectedType": "LPN",
      "taskId": "TSK001"
    }
    """
    When method POST
    Then status 200
    And match response.decodedType == 'LPN'
    And match response.validated == true

  Scenario: Decode unknown barcode type
    Given path '/api/v1/picking/decode'
    And request
    """
    {
      "barcode": "UNKNOWN_FORMAT_123",
      "expectedType": "LOCATION"
    }
    """
    When method POST
    Then status 200
    And match response.validated == false
    And match response.errorCode == 'INVALID_BARCODE_FORMAT'
