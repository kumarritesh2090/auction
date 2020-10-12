package com.example.ritesh.auction.postgres.repository;

import com.example.ritesh.auction.postgres.model.BidActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidActivityRepository extends JpaRepository<BidActivity, Long> {

    Page<BidActivity> getByItemCode(String itemCode, Pageable pageable);

}
