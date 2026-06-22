Feature: Order Release

  Background:
    * url baseUrl
    * configure headers = headers

  Scenario: Release allocated order successfully
    # Create and allocate order first
    Given path '/api/v1/outbound/orders'
    And def orderNumber = generateOrderNumber()
    And request
      """
      {
        "orderNumber": "#(orderNumber)",
        "customerCode": "CUST001",
        "shipToName": "Release Test",
        "shipToCity": "Seattle",
        "shipToState": "WA",
        "shipToCountry": "US",
        "lines": [
          {
            "lineNumber": "001",
            "sku": "SKU-200",
            "quantity": 2,
            "uom": "EA"
          }
        ]
      }
      """
    When method POST
    Then status 200

    # Allocate
    Given path '/api/v1/outbound/orders', orderNumber, 'allocate'
    When method POST
    Then status 200
    And match response.status == 'ALLOCATED'

    # Release
    Given path '/api/v1/outbound/orders', orderNumber, 'release'
    When method POST
    Then status 200
    And match response.status == 'RELEASED'
    And match response.releasedBy == '#notnull'
    And match response.releasedAt == '#notnull'

  Scenario: Release non-allocated order fails
    Given path '/api/v1/outbound/orders'
    And def orderNumber = generateOrderNumber()
    And request
      """
      {
        "orderNumber": "#(orderNumber)",
        "customerCode": "CUST001",
        "shipToName": "Cannot Release",
        "shipToCity": "Miami",
        "shipToState": "FL",
        "shipToCountry": "US",
        "lines": [
          {
            "lineNumber": "001",
            "sku": "SKU-300",
            "quantity": 1,
            "uom": "EA"
          }
        ]
      }
      """
    When method POST
    Then status 200
    And match response.status == 'NEW'

    # Try to release without allocating
    Given path '/api/v1/outbound/orders', orderNumber, 'release'
    When method POST
    Then status 500
