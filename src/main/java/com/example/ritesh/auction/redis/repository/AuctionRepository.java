package com.example.ritesh.auction.redis.repository;

import com.example.ritesh.auction.redis.model.Auction;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends CrudRepository<Auction, String> {

    List<Auction> findAllByStatus(String status, PageRequest pageRequest);

    List<Auction> findAll();

}
