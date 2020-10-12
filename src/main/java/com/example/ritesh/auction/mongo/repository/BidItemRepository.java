package com.example.ritesh.auction.mongo.repository;

import com.example.ritesh.auction.mongo.model.BidItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BidItemRepository extends MongoRepository<BidItem, String> {

    List<BidItem> findAllByStatus(String status);

}
