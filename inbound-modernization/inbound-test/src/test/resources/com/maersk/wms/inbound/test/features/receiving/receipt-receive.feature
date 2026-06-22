Feature: Receive Inventory Operations

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Country-Code = 'KR'
    * header X-Warehouse-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Receive inventory for receipt line
    Given path '/api/v1/receipts/RCP0000001/receive'
    And request
    """
    {
      "sku": "NK123456",
      "lot": "LOT20240101",
      "lpn": "LP0000000001",
      "receivedQty": 100,
      "damagedQty": 0,
      "location": "RECV-01",
      "conditionCode": "GOOD",
      "expirationDate": "2025-12-31"
    }
    """
    When method POST
    Then status 200
    And match response ==
    """
    {
      "receiptKey": "RCP0000001",
      "lineNumber": "#present",
      "sku": "NK123456",
      "lot": "LOT20240101",
      "lpn": "LP0000000001",
      "receivedQty": 100,
      "damagedQty": 0,
      "location": "RECV-01",
      "status": "5"
    }
    """

  Scenario: Receive with damaged inventory
    Given path '/api/v1/receipts/RCP0000001/receive'
    And request
    """
    {
      "sku": "NK123456",
      "lot": "LOT20240102",
      "lpn": "LP0000000002",
      "receivedQty": 95,
      "damagedQty": 5,
      "location": "RECV-01",
      "conditionCode": "PARTIAL_DAMAGE"
    }
    """
    When method POST
    Then status 200
    And match response.damagedQty == 5

  Scenario: Receive with expiration date
    Given path '/api/v1/receipts/RCP0000002/receive'
    And request
    """
    {
      "sku": "NK789012",
      "lot": "LOT002",
      "lpn": "LP0000000003",
      "receivedQty": 50,
      "location": "RECV-02",
      "conditionCode": "GOOD",
      "expirationDate": "2025-12-31"
    }
    """
    When method POST
    Then status 200
    And match response.receivedQty == 50

  Scenario: Receive without required SKU fails
    Given path '/api/v1/receipts/RCP0000001/receive'
    And request
    """
    {
      "lot": "LOT001",
      "receivedQty": 100,
      "location": "RECV-01"
    }
    """
    When method POST
    Then status 400

  Scenario: Receive with negative quantity fails
    Given path '/api/v1/receipts/RCP0000001/receive'
    And request
    """
    {
      "sku": "NK123456",
      "receivedQty": -10,
      "location": "RECV-01"
    }
    """
    When method POST
    Then status 400

  Scenario: Close receipt after receiving
    # Create receipt
    Given path '/api/v1/receipts'
    And request { "storerKey": "NIKE", "receiptType": "BLIND" }
    When method POST
    Then status 201
    * def receiptKey = response.receiptKey

    # Receive inventory
    Given path '/api/v1/receipts/' + receiptKey + '/receive'
    And request { "sku": "NK111111", "receivedQty": 25, "location": "RECV-01" }
    When method POST
    Then status 200

    # Close receipt
    Given path '/api/v1/receipts/' + receiptKey + '/close'
    When method POST
    Then status 200
    And match response.status == '12'
