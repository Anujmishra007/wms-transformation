Feature: Order Allocation

  Background:
    * url baseUrl
    * def auth = call read('classpath:karate-auth.js')
    * header Authorization = auth.token
    * header X-Client-Code = 'NIKE'
    * header X-Facility-Code = 'KRIC01'
    * header X-User-Id = 'TEST_USER'

  Scenario: Allocate order - full allocation
    Given path '/api/v1/outbound/orders/ORD-2024-00001/allocate'
    When method POST
    Then status 200
    And match response ==
    """
    {
      "orderNumber": "ORD-2024-00001",
      "status": "ALLOCATED",
      "requestedQty": 15,
      "allocatedQty": 15,
      "shortQty": 0,
      "lines": "#array"
    }
    """

  Scenario: Allocate order - partial allocation (short)
    Given path '/api/v1/outbound/orders/ORD-2024-00002/allocate'
    When method POST
    Then status 200
    And match response.status == 'PARTIALLY_ALLOCATED'
    And match response.shortQty > 0

  Scenario: Allocate non-existent order fails
    Given path '/api/v1/outbound/orders/NONEXISTENT-ORDER/allocate'
    When method POST
    Then status 404

  Scenario: Re-allocate already allocated order
    # This should deallocate and re-allocate
    Given path '/api/v1/outbound/orders/ORD-2024-00001/allocate'
    When method POST
    Then status 200
    And match response.status == 'ALLOCATED'

  Scenario: Allocate with specific allocation strategy
    Given path '/api/v1/outbound/orders/ORD-2024-00003/allocate'
    And param strategy = 'FIFO'
    When method POST
    Then status 200
    And match response.status contains 'ALLOC'
