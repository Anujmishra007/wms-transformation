Feature: Task Creation

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Facility-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Create a new pick task
    Given path '/api/v1/tasks'
    And request
    """
    {
      "taskId": "TSK-2024-00001",
      "taskType": "PICK",
      "priority": "NORMAL",
      "sourceLocation": "A01-B01-L01",
      "sourceZone": "ZONE1",
      "destinationLocation": "STAGING-01",
      "destinationZone": "STAGING",
      "sku": "NK123456",
      "quantity": 10,
      "workGroup": "PICK-TEAM1",
      "workZone": "ZONE1"
    }
    """
    When method POST
    Then status 200
    And match response ==
    """
    {
      "taskKey": "#number",
      "taskId": "TSK-2024-00001",
      "taskType": "PICK",
      "status": "CREATED",
      "priority": "NORMAL",
      "sourceLocation": "A01-B01-L01",
      "sourceZone": "ZONE1",
      "destinationLocation": "STAGING-01",
      "destinationZone": "STAGING",
      "sku": "NK123456",
      "quantity": 10,
      "pickedQuantity": 0,
      "shortQuantity": 0,
      "assignedUserId": null,
      "assignedUserName": null,
      "workGroup": "PICK-TEAM1",
      "workZone": "ZONE1",
      "createdAt": "#present",
      "assignedAt": null,
      "startedAt": null,
      "completedAt": null
    }
    """

  Scenario: Create replenishment task
    Given path '/api/v1/tasks'
    And request
    """
    {
      "taskId": "TSK-2024-00002",
      "taskType": "REPLENISHMENT",
      "priority": "HIGH",
      "sourceLocation": "RES-A01-B05",
      "sourceZone": "RESERVE",
      "destinationLocation": "PF-A01-B01",
      "destinationZone": "PICKFACE",
      "sku": "NK789012",
      "quantity": 100,
      "workZone": "ZONE1"
    }
    """
    When method POST
    Then status 200
    And match response.taskType == 'REPLENISHMENT'
    And match response.priority == 'HIGH'

  Scenario: Create move task
    Given path '/api/v1/tasks'
    And request
    """
    {
      "taskId": "TSK-2024-00003",
      "taskType": "MOVE",
      "priority": "LOW",
      "sourceLocation": "LOC-001",
      "destinationLocation": "LOC-002",
      "sku": "NK111111",
      "quantity": 5
    }
    """
    When method POST
    Then status 200
    And match response.taskType == 'MOVE'
    And match response.status == 'CREATED'

  Scenario: Create task without required task ID fails
    Given path '/api/v1/tasks'
    And request
    """
    {
      "taskType": "PICK",
      "sourceLocation": "A01-B01-L01",
      "quantity": 10
    }
    """
    When method POST
    Then status 400

  Scenario: Get task by key
    Given path '/api/v1/tasks/12345'
    When method GET
    Then status 200
    And match response.taskKey == 12345
