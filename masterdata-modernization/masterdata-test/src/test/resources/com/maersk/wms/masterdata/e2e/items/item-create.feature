Feature: Item Master Data

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Facility-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Create a new item
    Given path '/api/v1/masterdata/items'
    And request
    """
    {
      "sku": "NK-AIR-MAX-001",
      "description": "Nike Air Max 270",
      "itemType": "FINISHED_GOOD",
      "itemGroup": "FOOTWEAR",
      "weight": 0.35,
      "weightUom": "KG",
      "length": 35,
      "width": 15,
      "height": 12,
      "dimensionUom": "CM",
      "lotControlled": true,
      "serialControlled": false,
      "expirationControlled": false,
      "baseUom": "EA",
      "uoms": [
        { "uom": "EA", "conversionFactor": 1 },
        { "uom": "PAIR", "conversionFactor": 2 },
        { "uom": "CASE", "conversionFactor": 12 }
      ]
    }
    """
    When method POST
    Then status 200
    And match response ==
    """
    {
      "sku": "NK-AIR-MAX-001",
      "description": "Nike Air Max 270",
      "itemType": "FINISHED_GOOD",
      "itemGroup": "FOOTWEAR",
      "status": "ACTIVE",
      "weight": 0.35,
      "weightUom": "KG",
      "lotControlled": true,
      "serialControlled": false,
      "expirationControlled": false,
      "baseUom": "EA",
      "uoms": "#array"
    }
    """

  Scenario: Create item without required SKU fails
    Given path '/api/v1/masterdata/items'
    And request { "description": "Missing SKU Item" }
    When method POST
    Then status 400

  Scenario: Create duplicate item fails
    Given path '/api/v1/masterdata/items'
    And request { "sku": "EXISTING-SKU", "description": "Duplicate" }
    When method POST
    Then status 409
    And match response.errorCode == 'DUPLICATE_SKU'

  Scenario: Create item with UOMs
    Given path '/api/v1/masterdata/items'
    And request
    """
    {
      "sku": "NK-RUNNING-001",
      "description": "Nike Running Shoes",
      "itemType": "FINISHED_GOOD",
      "itemGroup": "FOOTWEAR",
      "weight": 0.30,
      "weightUom": "KG",
      "lotControlled": false,
      "serialControlled": true,
      "baseUom": "EA",
      "uoms": [
        { "uom": "EA", "conversionFactor": 1 },
        { "uom": "BOX", "conversionFactor": 6 }
      ]
    }
    """
    When method POST
    Then status 200
    And match response.serialControlled == true
    And match response.uoms.length == 2

  Scenario: Get item by SKU
    Given path '/api/v1/masterdata/items/NK-AIR-MAX-001'
    When method GET
    Then status 200
    And match response.sku == 'NK-AIR-MAX-001'
    And match response.status == 'ACTIVE'
