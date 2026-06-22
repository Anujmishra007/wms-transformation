Feature: FN839 Complete Pick Workflow E2E

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Country-Code = 'KR'
    * header X-Warehouse-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Complete pick workflow - happy path
    # Step 1: Get task (Screen 4640)
    Given path '/api/v1/picking/tasks/next'
    And param userId = 'OPERATOR01'
    And param warehouse = 'KRIC01'
    When method GET
    Then status 200
    * def task = response

    # Step 2: Scan location (Screen 4641)
    Given path '/api/v1/picking/decode'
    And request { barcode: '#(task.fromLocation)', expectedType: 'LOC', taskId: '#(task.taskId)', expectedLocation: '#(task.fromLocation)' }
    When method POST
    Then status 200
    And match response.validated == true

    # Step 3: Scan SKU (Screen 4642)
    Given path '/api/v1/picking/decode'
    And request { barcode: '#(task.sku)', expectedType: 'SKU', taskId: '#(task.taskId)', expectedSku: '#(task.sku)' }
    When method POST
    Then status 200
    And match response.validated == true

    # Step 4: Confirm pick (Screen 4643)
    Given path '/api/v1/picking/confirm'
    And request { taskId: '#(task.taskId)', pickedQty: '#(task.requestedQty)', requestedQty: '#(task.requestedQty)' }
    When method POST
    Then status 200
    And match response.success == true
    And match response.status == 'COMPLETED'

  Scenario: Complete pick workflow - short pick flow
    # Step 1: Get task
    Given path '/api/v1/picking/tasks/next'
    And param userId = 'OPERATOR02'
    And param warehouse = 'KRIC01'
    When method GET
    Then status 200
    * def task = response

    # Step 2: Scan location
    Given path '/api/v1/picking/decode'
    And request { barcode: '#(task.fromLocation)', expectedType: 'LOC', taskId: '#(task.taskId)', expectedLocation: '#(task.fromLocation)' }
    When method POST
    Then status 200

    # Step 3: Scan SKU
    Given path '/api/v1/picking/decode'
    And request { barcode: '#(task.sku)', expectedType: 'SKU', taskId: '#(task.taskId)', expectedSku: '#(task.sku)' }
    When method POST
    Then status 200

    # Step 4: Confirm short pick (Screen 4644)
    * def shortQty = task.requestedQty * 0.8
    Given path '/api/v1/picking/confirm'
    And request { taskId: '#(task.taskId)', pickedQty: '#(shortQty)', requestedQty: '#(task.requestedQty)', shortReason: 'INSUFFICIENT_STOCK' }
    When method POST
    Then status 200
    And match response.status == 'SHORT_PICK'

  Scenario: Complete pick workflow - skip task
    # Step 1: Get task
    Given path '/api/v1/picking/tasks/next'
    And param userId = 'OPERATOR03'
    And param warehouse = 'KRIC01'
    When method GET
    Then status 200
    * def task = response

    # Step 2: Skip task (Screen 4647)
    Given path '/api/v1/picking/tasks/' + task.taskId + '/skip'
    And param reason = 'LOCATION_INACCESSIBLE'
    When method POST
    Then status 200
