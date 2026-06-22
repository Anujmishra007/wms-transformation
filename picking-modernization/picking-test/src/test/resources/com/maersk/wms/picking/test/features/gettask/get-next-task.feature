Feature: FN839 Get Next Task - Screen 4640

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Country-Code = 'KR'
    * header X-Warehouse-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Get next task - happy path
    Given path '/api/v1/picking/tasks/next'
    And param userId = 'OPERATOR01'
    And param warehouse = 'KRIC01'
    And param zone = 'A'
    When method GET
    Then status 200
    And match response ==
    """
    {
      "taskId": "#present",
      "orderId": "#present",
      "sku": "#present",
      "skuDescription": "#string",
      "fromLocation": "#present",
      "toLocation": "#string",
      "lpn": "#string",
      "lot": "#string",
      "requestedQty": "#number",
      "zone": "A",
      "aisle": "#string",
      "priority": "#number",
      "status": "ASSIGNED"
    }
    """

  Scenario: Get next task - no tasks available
    Given path '/api/v1/picking/tasks/next'
    And param userId = 'OPERATOR99'
    And param warehouse = 'KRIC01'
    And param zone = 'Z'
    When method GET
    Then status 204

  Scenario: Get next task - zone filtering
    Given path '/api/v1/picking/tasks/next'
    And param userId = 'OPERATOR01'
    And param warehouse = 'KRIC01'
    And param zone = 'B'
    When method GET
    Then status 200
    And match response.zone == 'B'
    And match response.status == 'ASSIGNED'

  Scenario: Get next task - high priority first
    Given path '/api/v1/picking/tasks/next'
    And param userId = 'OPERATOR01'
    And param warehouse = 'KRIC01'
    And param priorityOnly = true
    When method GET
    Then status 200
    And match response.priority >= 5

  Scenario: Get task by specific ID
    Given path '/api/v1/picking/tasks/TSK001'
    When method GET
    Then status 200
    And match response.taskId == 'TSK001'
    And match response.sku == '#present'
    And match response.requestedQty > 0
