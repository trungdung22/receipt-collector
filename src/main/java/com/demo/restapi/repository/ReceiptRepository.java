package com.demo.restapi.repository;

import com.demo.restapi.model.Receipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long>{
    Page<Receipt> findByCreatedBy(Long userId, Pageable pageable);
    Page<Receipt> findByUserId(Long userId, Pageable pageable);
}
