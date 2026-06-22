Feature: Wave Creation

  Background:
    * url baseUrl
    * configure headers = headers

  Scenario: Create wave with multiple orders
    # Create first order
    Given path '/api/v1/outbound/orders'
    And def order1 = generateOrderNumber()
    And request
      """
      {
        "orderNumber": "#(order1)",
        "customerCode": "CUST001",
        "shipToName": "Wave Test 1",
        "shipToCity": "Dallas",
        "shipToState": "TX",
        "shipToCountry": "US",
        "carrierCode": "FEDEX",
        "lines": [
          { "lineNumber": "001", "sku": "SKU-W01", "quantity": 10, "uom": "EA" }
        ]
      }
      """
    When method POST
    Then status 200

    # Create second order
    Given path '/api/v1/outbound/orders'
    And def order2 = generateOrderNumber()
    And request
      """
      {
        "orderNumber": "#(order2)",
        "customerCode": "CUST002",
        "shipToName": "Wave Test 2",
        "shipToCity": "Houston",
        "shipToState": "TX",
        "shipToCountry": "US",
        "carrierCode": "FEDEX",
        "lines": [
          { "lineNumber": "001", "sku": "SKU-W02", "quantity": 5, "uom": "EA" }
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
        "orderNumbers": ["#(order1)", "#(order2)"],
        "waveType": "STANDARD"
      }
      """
    When method POST
    Then status 200
    And match response.waveNumber == '#notnull'
    And match response.status == 'NEW'
    And match response.orderCount == 2
    And match response.lineCount == 2
    And match response.totalUnits == 15

  Scenario: Create wave with no orders fails
    Given path '/api/v1/outbound/waves'
    And request
      """
      {
        "orderNumbers": [],
        "waveType": "STANDARD"
      }
      """
    When method POST
    Then status 400
