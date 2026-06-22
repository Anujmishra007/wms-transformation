Feature: Location Creation

  Background:
    * url baseUrl
    * configure headers = headers

  Scenario: Create a new location successfully
    Given path '/api/v1/masterdata/locations'
    And def locationCode = generateLocationCode('ZONE1')
    And request
      """
      {
        "locationCode": "#(locationCode)",
        "description": "Test Location",
        "locationType": "RESERVE",
        "zone": "ZONE1",
        "aisle": "A01",
        "bay": "B01",
        "level": "L01",
        "position": "P01",
        "maxWeight": 1000,
        "maxPallets": 2,
        "pickLocation": false,
        "putawayLocation": true,
        "mixedSku": true
      }
      """
    When method POST
    Then status 200
    And match response.locationCode == locationCode
    And match response.status == 'AVAILABLE'
    And match response.zone == 'ZONE1'
    And match response.putawayLocation == true

  Scenario: Create location with missing code fails
    Given path '/api/v1/masterdata/locations'
    And request
      """
      {
        "description": "Missing Code Location",
        "zone": "ZONE1"
      }
      """
    When method POST
    Then status 400

  Scenario: Get location by code
    Given path '/api/v1/masterdata/locations'
    And def locationCode = generateLocationCode('ZONE2')
    And request
      """
      {
        "locationCode": "#(locationCode)",
        "description": "Retrieve Test",
        "locationType": "PICKFACE",
        "zone": "ZONE2",
        "pickLocation": true
      }
      """
    When method POST
    Then status 200

    Given path '/api/v1/masterdata/locations', locationCode
    When method GET
    Then status 200
    And match response.locationCode == locationCode
    And match response.locationType == 'PICKFACE'
    And match response.pickLocation == true
