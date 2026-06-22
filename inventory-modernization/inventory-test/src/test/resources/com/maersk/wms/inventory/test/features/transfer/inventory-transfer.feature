Feature: Inventory Transfer Operations

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Country-Code = 'KR'
    * header X-Warehouse-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Create replenishment transfer
    Given path '/api/v1/inventory/transfers'
    And request
    """
    {
      sku: 'NK123456',
      fromLocation: 'R01-01-01',
      toLocation: 'A01-02-03',
      transferQty: 50,
      transferType: 'RPL'
    }
    """
    When method POST
    Then status 200
    And match response.transferKey == '#present'
    And match response.status == 'COMPLETED'

  Scenario: Create manual move transfer
    Given path '/api/v1/inventory/transfers'
    And request
    """
    {
      sku: 'NK123456',
      lot: 'LOT001',
      fromLocation: 'A01-02-03',
      fromLpn: 'LP0000000001',
      toLocation: 'A01-02-04',
      toLpn: 'LP0000000002',
      transferQty: 25,
      transferType: 'MOV'
    }
    """
    When method POST
    Then status 200
    And match response.status == 'COMPLETED'
