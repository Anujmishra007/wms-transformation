Feature: FN839 Confirm Pick - Screens 4643, 4644

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Country-Code = 'KR'
    * header X-Warehouse-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Confirm full pick
    Given path '/api/v1/picking/confirm'
    And request
    """
    {
      "taskId": "TSK001",
      "pickedQty": 10,
      "requestedQty": 10,
      "fromLpn": "LP0000000001",
      "toLpn": "CART001"
    }
    """
    When method POST
    Then status 200
    And match response ==
    """
    {
      "success": true,
      "taskId": "TSK001",
      "status": "COMPLETED",
      "pickedQty": 10,
      "remainingQty": 0,
      "message": "#ignore"
    }
    """

  Scenario: Confirm short pick
    Given path '/api/v1/picking/confirm'
    And request
    """
    {
      "taskId": "TSK002",
      "pickedQty": 7,
      "requestedQty": 10,
      "fromLpn": "LP0000000002",
      "toLpn": "CART001",
      "shortReason": "INSUFFICIENT_STOCK"
    }
    """
    When method POST
    Then status 200
    And match response.success == true
    And match response.status == 'SHORT_PICK'
    And match response.remainingQty == 3

  Scenario: Confirm zero pick - validation error
    Given path '/api/v1/picking/confirm'
    And request
    """
    {
      "taskId": "TSK003",
      "pickedQty": 0,
      "requestedQty": 10
    }
    """
    When method POST
    Then status 400

  Scenario: Confirm pick with missing task ID - validation error
    Given path '/api/v1/picking/confirm'
    And request
    """
    {
      "pickedQty": 10,
      "requestedQty": 10
    }
    """
    When method POST
    Then status 400
