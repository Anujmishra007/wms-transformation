Feature: Ship Confirm

  Background:
    * url baseUrl
    * configure headers = headers

  Scenario: Confirm shipment successfully
    # Create order
    Given path '/api/v1/outbound/orders'
    And def orderNumber = generateOrderNumber()
    And request
      """
      {
        "orderNumber": "#(orderNumber)",
        "customerCode": "CUST001",
        "shipToName": "Ship Confirm Test",
        "shipToCity": "Atlanta",
        "shipToState": "GA",
        "shipToCountry": "US",
        "carrierCode": "FEDEX",
        "lines": [
          { "lineNumber": "001", "sku": "SKU-SC01", "quantity": 2, "uom": "EA" }
        ]
      }
      """
    When method POST
    Then status 200

    # Create shipment
    Given path '/api/v1/outbound/shipments'
    And request { "orderNumber": "#(orderNumber)" }
    When method POST
    Then status 200
    * def shipmentId = response.shipmentId

    # Generate manifest first
    Given path '/api/v1/outbound/shipments', shipmentId, 'manifest'
    When method POST
    Then status 200
    And match response.status == 'MANIFESTED'

    # Ship confirm
    Given path '/api/v1/outbound/shipments', shipmentId, 'confirm'
    When method POST
    Then status 200
    And match response.status == 'SHIPPED'
    And match response.shippedBy == '#notnull'
    And match response.shippedAt == '#notnull'

  Scenario: Get shipment by ID
    # Create order and shipment
    Given path '/api/v1/outbound/orders'
    And def orderNumber = generateOrderNumber()
    And request
      """
      {
        "orderNumber": "#(orderNumber)",
        "customerCode": "CUST001",
        "shipToName": "Get Shipment Test",
        "shipToCity": "Charlotte",
        "shipToState": "NC",
        "shipToCountry": "US",
        "lines": [
          { "lineNumber": "001", "sku": "SKU-GS01", "quantity": 1, "uom": "EA" }
        ]
      }
      """
    When method POST
    Then status 200

    Given path '/api/v1/outbound/shipments'
    And request { "orderNumber": "#(orderNumber)" }
    When method POST
    Then status 200
    * def shipmentId = response.shipmentId

    # Get shipment
    Given path '/api/v1/outbound/shipments', shipmentId
    When method GET
    Then status 200
    And match response.shipmentId == shipmentId
    And match response.orderNumber == orderNumber
