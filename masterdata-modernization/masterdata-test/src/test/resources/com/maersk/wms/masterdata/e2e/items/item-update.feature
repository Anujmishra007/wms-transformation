Feature: Item Update

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Facility-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Update item description
    Given path '/api/v1/masterdata/items/NK-AIR-MAX-001'
    And request
    """
    {
      "description": "Nike Air Max 270 - Updated",
      "weight": 0.40,
      "weightUom": "KG"
    }
    """
    When method PUT
    Then status 200
    And match response.description == 'Nike Air Max 270 - Updated'
    And match response.weight == 0.40

  Scenario: Deactivate item
    Given path '/api/v1/masterdata/items/NK-OLD-MODEL'
    And request { "status": "INACTIVE" }
    When method PUT
    Then status 200
    And match response.status == 'INACTIVE'

  Scenario: Update item dimensions
    Given path '/api/v1/masterdata/items/NK-AIR-MAX-001'
    And request
    """
    {
      "length": 36,
      "width": 16,
      "height": 13,
      "dimensionUom": "CM"
    }
    """
    When method PUT
    Then status 200
    And match response.length == 36
    And match response.width == 16

  Scenario: Update non-existent item fails
    Given path '/api/v1/masterdata/items/NON-EXISTENT-SKU'
    And request { "description": "Should Fail" }
    When method PUT
    Then status 404

  Scenario: Discontinue item
    Given path '/api/v1/masterdata/items/NK-DISCONTINUED-001'
    And request { "status": "DISCONTINUED" }
    When method PUT
    Then status 200
    And match response.status == 'DISCONTINUED'
