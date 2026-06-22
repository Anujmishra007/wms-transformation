Feature: Order Creation

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Facility-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Create a new order successfully
    Given path '/api/v1/outbound/orders'
    And request
    """
    {
      "orderNumber": "ORD-2024-00001",
      "externalOrderNumber": "EXT-123",
      "orderType": "STANDARD",
      "priority": "NORMAL",
      "customerCode": "CUST001",
      "shipToName": "John Doe",
      "shipToAddress1": "123 Main St",
      "shipToCity": "Seoul",
      "shipToState": "Seoul",
      "shipToZip": "06164",
      "shipToCountry": "KR",
      "carrierCode": "FEDEX",
      "shipMethod": "GROUND",
      "lines": [
        {
          "lineNumber": "001",
          "sku": "NK123456",
          "skuDescription": "Nike Air Max",
          "quantity": 10,
          "uom": "EA"
        },
        {
          "lineNumber": "002",
          "sku": "NK789012",
          "skuDescription": "Nike Running Shoes",
          "quantity": 5,
          "uom": "EA"
        }
      ]
    }
    """
    When method POST
    Then status 200
    And match response ==
    """
    {
      "orderKey": "#present",
      "orderNumber": "ORD-2024-00001",
      "externalOrderNumber": "EXT-123",
      "orderType": "STANDARD",
      "status": "NEW",
      "customerCode": "CUST001",
      "shipToName": "John Doe",
      "carrierCode": "FEDEX",
      "lineCount": 2,
      "totalQty": 15,
      "allocatedQty": 0,
      "pickedQty": 0,
      "shippedQty": 0
    }
    """

  Scenario: Create order with missing required fields fails
    Given path '/api/v1/outbound/orders'
    And request { "customerCode": "CUST001", "lines": [] }
    When method POST
    Then status 400

  Scenario: Create express order with high priority
    Given path '/api/v1/outbound/orders'
    And request
    """
    {
      "orderNumber": "ORD-2024-00002",
      "orderType": "EXPRESS",
      "priority": "URGENT",
      "customerCode": "CUST002",
      "shipToName": "Jane Smith",
      "shipToAddress1": "456 High St",
      "shipToCity": "Seoul",
      "shipToCountry": "KR",
      "carrierCode": "DHL",
      "shipMethod": "EXPRESS",
      "lines": [
        {
          "lineNumber": "001",
          "sku": "NK999888",
          "quantity": 1,
          "uom": "EA"
        }
      ]
    }
    """
    When method POST
    Then status 200
    And match response.orderType == 'EXPRESS'
    And match response.lineCount == 1

  Scenario: Get order by order key
    Given path '/api/v1/outbound/orders/ORD-2024-00001'
    When method GET
    Then status 200
    And match response.orderNumber == 'ORD-2024-00001'
    And match response.customerCode == 'CUST001'
