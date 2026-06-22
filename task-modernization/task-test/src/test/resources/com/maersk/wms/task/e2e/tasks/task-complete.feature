Feature: Task Completion

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Facility-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Complete task - full quantity
    Given path '/api/v1/tasks/12345/complete'
    And request
    """
    {
      "completedQuantity": 10,
      "shortQuantity": 0
    }
    """
    When method POST
    Then status 200
    And match response.status == 'COMPLETED'
    And match response.pickedQuantity == 10
    And match response.completedAt == '#present'

  Scenario: Complete task - short pick
    Given path '/api/v1/tasks/12345/complete'
    And request
    """
    {
      "completedQuantity": 7,
      "shortQuantity": 3,
      "shortReasonCode": "INSUFFICIENT_STOCK",
      "notes": "Only 7 units available at location"
    }
    """
    When method POST
    Then status 200
    And match response.status == 'COMPLETED'
    And match response.pickedQuantity == 7
    And match response.shortQuantity == 3

  Scenario: Complete with zero quantity fails
    Given path '/api/v1/tasks/12345/complete'
    And request
    """
    {
      "completedQuantity": -5
    }
    """
    When method POST
    Then status 400

  Scenario: Cannot complete unassigned task
    Given path '/api/v1/tasks/99999/complete'
    And request { "completedQuantity": 10 }
    When method POST
    Then status 400
    And match response.errorCode == 'TASK_NOT_ASSIGNED'

  Scenario: Cancel task
    Given path '/api/v1/tasks/12345/cancel'
    And request
    """
    {
      "reason": "Order cancelled",
      "cancelledBy": "SUPERVISOR01"
    }
    """
    When method POST
    Then status 200
    And match response.status == 'CANCELLED'
