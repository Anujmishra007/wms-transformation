Feature: Inventory Hold Operations

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Country-Code = 'KR'
    * header X-Warehouse-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Apply hold by lot
    Given path '/api/v1/inventory/holds'
    And request
    """
    {
      "holdCode": "QA",
      "scope": "LOT",
      "sku": "NK123456",
      "lot": "LOT001",
      "reasonCode": "QA_INSPECTION",
      "comments": "Pending quality inspection"
    }
    """
    When method POST
    Then status 200
    And match response ==
    """
    {
      "holdKey": "#present",
      "holdCode": "QA",
      "status": "ACTIVE",
      "scope": "LOT",
      "affectedRecords": "#number"
    }
    """

  Scenario: Apply hold by SKU
    Given path '/api/v1/inventory/holds'
    And request
    """
    {
      "holdCode": "RECALL",
      "scope": "SKU",
      "sku": "NK789012",
      "reasonCode": "PRODUCT_RECALL",
      "comments": "Manufacturer recall - safety issue"
    }
    """
    When method POST
    Then status 200
    And match response.holdCode == 'RECALL'
    And match response.scope == 'SKU'
    And match response.status == 'ACTIVE'

  Scenario: Apply hold by location
    Given path '/api/v1/inventory/holds'
    And request
    """
    {
      "holdCode": "MAINT",
      "scope": "LOCATION",
      "location": "A01-02-03",
      "reasonCode": "LOCATION_MAINTENANCE",
      "comments": "Rack maintenance scheduled"
    }
    """
    When method POST
    Then status 200
    And match response.scope == 'LOCATION'
    And match response.affectedRecords >= 0

  Scenario: Apply hold by LPN
    Given path '/api/v1/inventory/holds'
    And request
    """
    {
      "holdCode": "DMG",
      "scope": "LPN",
      "lpn": "LP0000000001",
      "reasonCode": "DAMAGED_CONTAINER",
      "comments": "Forklift damage to pallet"
    }
    """
    When method POST
    Then status 200
    And match response.scope == 'LPN'
    And match response.status == 'ACTIVE'

  Scenario: Release hold
    # First create a hold
    Given path '/api/v1/inventory/holds'
    And request
    """
    {
      "holdCode": "TEMP",
      "scope": "LOT",
      "sku": "NK123456",
      "lot": "LOT002",
      "reasonCode": "TEMPORARY_HOLD",
      "comments": "Temporary hold for verification"
    }
    """
    When method POST
    Then status 200
    * def holdKey = response.holdKey

    # Now release the hold
    Given path '/api/v1/inventory/holds/' + holdKey
    When method DELETE
    Then status 200

  Scenario: Apply hold with invalid scope fails
    Given path '/api/v1/inventory/holds'
    And request
    """
    {
      "holdCode": "QA",
      "scope": "INVALID_SCOPE",
      "sku": "NK123456",
      "reasonCode": "TEST"
    }
    """
    When method POST
    Then status 400

  Scenario: Apply hold without required hold code fails
    Given path '/api/v1/inventory/holds'
    And request
    """
    {
      "scope": "SKU",
      "sku": "NK123456",
      "reasonCode": "TEST"
    }
    """
    When method POST
    Then status 400

  Scenario: Get hold by key
    # First create a hold
    Given path '/api/v1/inventory/holds'
    And request
    """
    {
      "holdCode": "INV",
      "scope": "LOT",
      "sku": "NK123456",
      "lot": "LOT003",
      "reasonCode": "INVESTIGATION",
      "comments": "Under investigation"
    }
    """
    When method POST
    Then status 200
    * def holdKey = response.holdKey

    # Now retrieve it
    Given path '/api/v1/inventory/holds/' + holdKey
    When method GET
    Then status 200
    And match response.holdKey == holdKey
    And match response.holdCode == 'INV'

  Scenario: Get active holds for SKU
    Given path '/api/v1/inventory/holds'
    And param sku = 'NK123456'
    And param status = 'ACTIVE'
    When method GET
    Then status 200
    And match response == '#array'
