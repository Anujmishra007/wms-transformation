Feature: Task Assignment

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Facility-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Assign task to user
    Given path '/api/v1/tasks/12345/assign'
    And request
    """
    {
      "userId": "OPERATOR01",
      "equipment": "RF-001"
    }
    """
    When method POST
    Then status 200
    And match response.status == 'ASSIGNED'
    And match response.assignedUserId == 'OPERATOR01'
    And match response.assignedAt == '#present'

  Scenario: Assign task without equipment
    Given path '/api/v1/tasks/12346/assign'
    And request
    """
    {
      "userId": "OPERATOR02"
    }
    """
    When method POST
    Then status 200
    And match response.assignedUserId == 'OPERATOR02'
    And match response.status == 'ASSIGNED'

  Scenario: Cannot assign already assigned task
    Given path '/api/v1/tasks/12347/assign'
    And request { "userId": "OPERATOR02" }
    When method POST
    Then status 400
    And match response.errorCode == 'TASK_ALREADY_ASSIGNED'

  Scenario: Get tasks assigned to user
    Given path '/api/v1/tasks/user/OPERATOR01'
    When method GET
    Then status 200
    And match response == '#array'
    And match each response contains { assignedUserId: 'OPERATOR01' }

  Scenario: Unassign task
    Given path '/api/v1/tasks/12345/unassign'
    When method POST
    Then status 200
    And match response.status == 'CREATED'
    And match response.assignedUserId == null
