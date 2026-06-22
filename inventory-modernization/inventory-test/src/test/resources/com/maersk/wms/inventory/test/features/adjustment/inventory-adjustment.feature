Feature: Inventory Adjustment Operations

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Country-Code = 'KR'
    * header X-Warehouse-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Create cycle count adjustment
    Given path '/api/v1/inventory/adjustments'
    And request
    """
    {
      sku: 'NK123456',
      location: 'A01-02-03',
      adjustmentType: 'CC',
      systemQty: 100,
      adjustedQty: 98,
      reasonCode: 'SHRINKAGE',
      comments: 'Cycle count variance'
    }
    """
    When method POST
    Then status 200
    And match response.adjustmentKey == '#present'
    And match response.variance == -2

  Scenario: Adjustment requiring approval
    Given path '/api/v1/inventory/adjustments'
    And request
    """
    {
      sku: 'NK123456',
      location: 'A01-02-03',
      adjustmentType: 'DMG',
      systemQty: 100,
      adjustedQty: 0,
      reasonCode: 'DAMAGED',
      comments: 'Water damage'
    }
    """
    When method POST
    Then status 200
    And match response.requiresApproval == true
    And match response.status == 'PENDING_APPROVAL'
