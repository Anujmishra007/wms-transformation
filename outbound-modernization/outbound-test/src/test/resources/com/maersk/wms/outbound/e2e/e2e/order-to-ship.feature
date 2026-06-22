Feature: Order to Ship E2E Flow

  Background:
    * url baseUrl
    * configure headers = headers

  Scenario: Complete order fulfillment flow - Order to Ship
    # Step 1: Create Order
    Given path '/api/v1/outbound/orders'
    And def orderNumber = generateOrderNumber()
    And request
      """
      {
        "orderNumber": "#(orderNumber)",
        "externalOrderNumber": "EXT-E2E-001",
        "orderType": "STANDARD",
        "priority": "HIGH",
        "customerCode": "PREMIUM-CUST",
        "shipToName": "E2E Test Customer",
        "shipToAddress1": "789 E2E Lane",
        "shipToCity": "San Francisco",
        "shipToState": "CA",
        "shipToZip": "94102",
        "shipToCountry": "US",
        "carrierCode": "FEDEX",
        "shipMethod": "EXPRESS",
        "lines": [
          {
            "lineNumber": "001",
            "sku": "SKU-E2E-001",
            "skuDescription": "E2E Test Product 1",
            "quantity": 10,
            "uom": "EA"
          },
          {
            "lineNumber": "002",
            "sku": "SKU-E2E-002",
            "skuDescription": "E2E Test Product 2",
            "quantity": 5,
            "uom": "EA"
          }
        ]
      }
      """
    When method POST
    Then status 200
    And match response.orderNumber == orderNumber
    And match response.status == 'NEW'
    And match response.lineCount == 2
    And match response.totalQty == 15
    * karate.log('Order created:', orderNumber)

    # Step 2: Allocate Order
    Given path '/api/v1/outbound/orders', orderNumber, 'allocate'
    When method POST
    Then status 200
    And match response.success == true
    And match response.status == 'ALLOCATED'
    * karate.log('Order allocated:', orderNumber)

    # Step 3: Release Order
    Given path '/api/v1/outbound/orders', orderNumber, 'release'
    When method POST
    Then status 200
    And match response.status == 'RELEASED'
    * karate.log('Order released:', orderNumber)

    # Step 4: Create Shipment
    Given path '/api/v1/outbound/shipments'
    And request { "orderNumber": "#(orderNumber)" }
    When method POST
    Then status 200
    And match response.status == 'NEW'
    And match response.orderNumber == orderNumber
    * def shipmentId = response.shipmentId
    * karate.log('Shipment created:', shipmentId)

    # Step 5: Generate Manifest
    Given path '/api/v1/outbound/shipments', shipmentId, 'manifest'
    When method POST
    Then status 200
    And match response.status == 'MANIFESTED'
    * karate.log('Manifest generated for shipment:', shipmentId)

    # Step 6: Ship Confirm
    Given path '/api/v1/outbound/shipments', shipmentId, 'confirm'
    When method POST
    Then status 200
    And match response.status == 'SHIPPED'
    And match response.shippedAt == '#notnull'
    * karate.log('Shipment confirmed:', shipmentId)

    # Verify final state
    Given path '/api/v1/outbound/shipments', shipmentId
    When method GET
    Then status 200
    And match response.status == 'SHIPPED'
    And match response.shipToCity == 'San Francisco'
    * karate.log('E2E Order to Ship flow completed successfully!')
