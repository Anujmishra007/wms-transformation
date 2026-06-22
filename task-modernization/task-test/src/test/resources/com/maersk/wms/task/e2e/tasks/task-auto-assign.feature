Feature: Task Auto-Assignment

  Background:
    * url baseUrl
    * configure headers = headers

  Scenario: Auto-assign task to available user
    Given path '/api/v1/tasks'
    And def taskId = generateTaskId()
    And request
      """
      {
        "taskId": "#(taskId)",
        "taskType": "PICK",
        "sourceLocation": "A01-B01-L01",
        "sourceZone": "ZONE1",
        "quantity": 10,
        "workGroup": "PICK-TEAM1",
        "workZone": "ZONE1"
      }
      """
    When method POST
    Then status 200
    And def taskKey = response.taskKey

    # Trigger auto-assignment
    Given path '/api/v1/tasks', taskKey, 'auto-assign'
    When method POST
    Then status 200
    And match response.assignedUserId != null
    And match response.status == 'ASSIGNED'

  Scenario: Get unassigned tasks by work zone
    # Create unassigned task
    Given path '/api/v1/tasks'
    And def taskId = generateTaskId()
    And request
      """
      {
        "taskId": "#(taskId)",
        "taskType": "PICK",
        "sourceLocation": "A01-B01-L01",
        "quantity": 10,
        "workZone": "TEST-ZONE"
      }
      """
    When method POST
    Then status 200

    # Get unassigned tasks for zone
    Given path '/api/v1/tasks/unassigned'
    And param workZone = 'TEST-ZONE'
    When method GET
    Then status 200
    And match response[*].workZone contains 'TEST-ZONE'
    And match response[*].assignedUserId contains null
