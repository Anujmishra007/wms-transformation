Feature: Wave Release

  Background:
    * url baseUrl
    * configure headers = headers

  Scenario: Release wave successfully
    # Create an order
    Given path '/api/v1/outbound/orders'
    And def orderNumber = generateOrderNumber()
    And request
      """
      {
        "orderNumber": "#(orderNumber)",
        "customerCode": "CUST001",
        "shipToName": "Wave Release Test",
        "shipToCity": "Phoenix",
        "shipToState": "AZ",
        "shipToCountry": "US",
        "lines": [
          { "lineNumber": "001", "sku": "SKU-WR01", "quantity": 8, "uom": "EA" }
        ]
      }
      """
    When method POST
    Then status 200

    # Create wave
    Given path '/api/v1/outbound/waves'
    And request
      """
      {
        "orderNumbers": ["#(orderNumber)"],
        "waveType": "STANDARD"
      }
      """
    When method POST
    Then status 200
    * def waveNumber = response.waveNumber

    # Release wave
    Given path '/api/v1/outbound/waves', waveNumber, 'release'
    When method POST
    Then status 200
    And match response.status == 'RELEASED'
    And match response.releasedBy == '#notnull'
    And match response.releasedAt == '#notnull'

  Scenario: Get wave by wave number
    # Create order and wave
    Given path '/api/v1/outbound/orders'
    And def orderNumber = generateOrderNumber()
    And request
      """
      {
        "orderNumber": "#(orderNumber)",
        "customerCode": "CUST001",
        "shipToName": "Get Wave Test",
        "shipToCity": "Denver",
        "shipToState": "CO",
        "shipToCountry": "US",
        "lines": [
          { "lineNumber": "001", "sku": "SKU-GW01", "quantity": 3, "uom": "EA" }
        ]
      }
      """
    When method POST
    Then status 200

    Given path '/api/v1/outbound/waves'
    And request
      """
      {
        "orderNumbers": ["#(orderNumber)"],
        "waveType": "PRIORITY"
      }
      """
    When method POST
    Then status 200
    * def waveNumber = response.waveNumber

    # Get wave
    Given path '/api/v1/outbound/waves', waveNumber
    When method GET
    Then status 200
    And match response.waveNumber == waveNumber
    And match response.waveType == 'PRIORITY'
