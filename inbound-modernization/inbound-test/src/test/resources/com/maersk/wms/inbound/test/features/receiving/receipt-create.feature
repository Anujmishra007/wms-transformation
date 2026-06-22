Feature: Receipt Creation Operations

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Country-Code = 'KR'
    * header X-Warehouse-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Create receipt from ASN
    Given path '/api/v1/receipts'
    And request
    """
    {
      "storerKey": "NIKE",
      "receiptType": "ASN",
      "asnKey": "ASN0000000001",
      "carrierKey": "FEDEX",
      "trailerNumber": "TRL123456",
      "sealNumber": "SEAL789",
      "door": "DOOR01",
      "notes": "Test receipt from ASN"
    }
    """
    When method POST
    Then status 201
    And match response ==
    """
    {
      "receiptKey": "#present",
      "externalReceiptKey": "#ignore",
      "storerKey": "NIKE",
      "receiptType": "ASN",
      "status": "0",
      "statusDescription": "New",
      "poKey": null,
      "asnKey": "ASN0000000001",
      "totalExpectedQty": "#number",
      "totalReceivedQty": 0,
      "totalDamagedQty": 0,
      "variance": "#number"
    }
    """

  Scenario: Create receipt from PO
    Given path '/api/v1/receipts'
    And request
    """
    {
      "storerKey": "NIKE",
      "receiptType": "PO",
      "poKey": "PO0000000001",
      "carrierKey": "UPS",
      "door": "DOOR02"
    }
    """
    When method POST
    Then status 201
    And match response.receiptKey == '#present'
    And match response.poKey == 'PO0000000001'

  Scenario: Create blind receipt (no ASN/PO)
    Given path '/api/v1/receipts'
    And request
    """
    {
      "storerKey": "NIKE",
      "receiptType": "BLIND",
      "notes": "Blind receipt - no ASN/PO"
    }
    """
    When method POST
    Then status 201
    And match response.receiptType == 'BLIND'

  Scenario: Create receipt with missing storer fails
    Given path '/api/v1/receipts'
    And request
    """
    {
      "receiptType": "ASN",
      "asnKey": "ASN0000000002"
    }
    """
    When method POST
    Then status 400
