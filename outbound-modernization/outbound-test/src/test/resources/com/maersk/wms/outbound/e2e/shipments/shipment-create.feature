Feature: Shipment Creation

  Background:
    * url baseUrl
    * configure headers = headers

  Scenario: Create shipment for order
    # First create an order
    Given path '/api/v1/outbound/orders'
    And def orderNumber = generateOrderNumber()
    And request
      """
      {
        "orderNumber": "#(orderNumber)",
        "customerCode": "CUST001",
        "shipToName": "Shipment Test",
        "shipToAddress1": "456 Oak Ave",
        "shipToCity": "Portland",
        "shipToState": "OR",
        "shipToZip": "97201",
        "shipToCountry": "US",
        "carrierCode": "UPS",
        "shipMethod": "GROUND",
        "lines": [
          { "lineNumber": "001", "sku": "SKU-SH01", "quantity": 5, "uom": "EA" }
        ]
      }
      """
    When method POST
    Then status 200

    # Create shipment
    Given path '/api/v1/outbound/shipments'
    And request
      """
      {
        "orderNumber": "#(orderNumber)",
        "carrier": "UPS",
        "shipMethod": "GROUND"
      }
      """
    When method POST
    Then status 200
    And match response.shipmentId == '#notnull'
    And match response.orderNumber == orderNumber
    And match response.status == 'NEW'
    And match response.carrierCode == 'UPS'
    And match response.shipToCity == 'Portland'
    And match response.shipToState == 'OR'

  Scenario: Create shipment for non-existent order fails
    Given path '/api/v1/outbound/shipments'
    And request
      """
      {
        "orderNumber": "NONEXISTENT-ORD"
      }
      """
    When method POST
    Then status 500
