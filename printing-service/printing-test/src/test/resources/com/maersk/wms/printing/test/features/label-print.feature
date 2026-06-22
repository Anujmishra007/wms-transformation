Feature: Label Printing

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Country-Code = 'KR'
    * header X-Warehouse-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Print LPN label
    Given path '/api/v1/printing/labels'
    And request
    """
    {
      "labelType": "LPN",
      "printerName": "ZEBRA-001",
      "copies": 1,
      "labelData": {
        "lpn": "LP0000000001",
        "sku": "NK123456",
        "description": "Nike Air Max",
        "qty": "100",
        "lot": "LOT20240101"
      }
    }
    """
    When method POST
    Then status 200
    And match response ==
    """
    {
      "jobId": "#present",
      "status": "SUBMITTED",
      "labelType": "LPN",
      "printerName": "ZEBRA-001",
      "copiesPrinted": 1,
      "submittedAt": "#present",
      "completedAt": null,
      "errorMessage": null
    }
    """

  Scenario: Print shipping label
    Given path '/api/v1/printing/labels'
    And request
    """
    {
      "labelType": "SHIPPING",
      "printerName": "ZEBRA-002",
      "copies": 2,
      "labelData": {
        "trackingNumber": "1Z999AA10123456784",
        "carrier": "UPS",
        "shipToName": "John Doe",
        "shipToAddress": "123 Main St",
        "shipToCity": "Seoul",
        "shipToCountry": "KR"
      }
    }
    """
    When method POST
    Then status 200
    And match response.labelType == 'SHIPPING'
    And match response.copiesPrinted == 2

  Scenario: Print location label
    Given path '/api/v1/printing/labels'
    And request
    """
    {
      "labelType": "LOCATION",
      "printerName": "ZEBRA-003",
      "copies": 1,
      "labelData": {
        "location": "A01-B02-L03",
        "zone": "ZONE-A",
        "aisle": "01",
        "bay": "02",
        "level": "03"
      }
    }
    """
    When method POST
    Then status 200
    And match response.labelType == 'LOCATION'
    And match response.status == 'SUBMITTED'

  Scenario: Print item label
    Given path '/api/v1/printing/labels'
    And request
    """
    {
      "labelType": "ITEM",
      "printerName": "ZEBRA-001",
      "copies": 5,
      "labelData": {
        "sku": "NK123456",
        "description": "Nike Air Max 270",
        "barcode": "0123456789012",
        "price": "199.99",
        "size": "10.5"
      }
    }
    """
    When method POST
    Then status 200
    And match response.copiesPrinted == 5

  Scenario: Print label with custom template
    Given path '/api/v1/printing/labels'
    And request
    """
    {
      "labelType": "LPN",
      "printerName": "ZEBRA-001",
      "copies": 1,
      "templateName": "NIKE_LPN_4X6",
      "labelData": {
        "lpn": "LP0000000002",
        "sku": "NK789012",
        "description": "Nike Running Shoes",
        "qty": "50"
      }
    }
    """
    When method POST
    Then status 200
    And match response.jobId == '#present'

  Scenario: Print label with invalid printer - fails
    Given path '/api/v1/printing/labels'
    And request
    """
    {
      "labelType": "LPN",
      "printerName": "NON_EXISTENT_PRINTER",
      "copies": 1,
      "labelData": {
        "lpn": "LP0000000003"
      }
    }
    """
    When method POST
    Then status 404
    And match response.errorCode == 'PRINTER_NOT_FOUND'

  Scenario: Print label with missing required fields - validation error
    Given path '/api/v1/printing/labels'
    And request
    """
    {
      "copies": 1,
      "labelData": {
        "lpn": "LP0000000004"
      }
    }
    """
    When method POST
    Then status 400
