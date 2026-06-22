package com.maersk.wms.masterdata.domain.repository;

import com.maersk.wms.masterdata.domain.Customer;
import com.maersk.wms.masterdata.domain.CustomerStatus;
import com.maersk.wms.masterdata.domain.CustomerType;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entities.
 */
public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(Long id);

    Optional<Customer> findByCustomerCode(String customerCode);

    List<Customer> findByStatus(CustomerStatus status);

    List<Customer> findByCustomerType(CustomerType customerType);

    List<Customer> searchByName(String searchTerm);

    List<Customer> findByCountry(String country);

    List<Customer> findAll();

    void delete(Customer customer);

    boolean existsByCustomerCode(String customerCode);
}
