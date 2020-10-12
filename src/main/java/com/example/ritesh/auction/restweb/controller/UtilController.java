package com.example.ritesh.auction.restweb.controller;

import com.example.ritesh.auction.restweb.path.ApiPath;
import com.example.ritesh.auction.mongo.model.BidItem;
import com.example.ritesh.auction.mongo.repository.BidItemRepository;
import com.example.ritesh.auction.redis.model.Auction;
import com.example.ritesh.auction.redis.repository.AuctionRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log4j
public class UtilController {

    private static final String RUNNING = "RUNNING";
    private static final String OVER = "OVER";
    @Autowired
    private BidItemRepository bidItemRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @GetMapping(value = ApiPath.INIT)
    ResponseEntity initialize() {
        log.info("initializing mongodb and redis for auction");

        //create some auction entries in mongodb
        bidItemRepository.save(new BidItem("item1", 1000f, 100, RUNNING, 0f));
        bidItemRepository.save(new BidItem("item2", 10006f, 150, RUNNING, 0f));
        bidItemRepository.save(new BidItem("item3", 10007f, 160, RUNNING, 0f));
        bidItemRepository.save(new BidItem("item4", 10003f, 1700, RUNNING, 0f));
        bidItemRepository.save(new BidItem("item5", 10004f, 170, RUNNING, 0f));
        bidItemRepository.save(new BidItem("item6", 10009f, 1080, RUNNING, 0f));
        bidItemRepository.save(new BidItem("item7", 10008f, 1050, RUNNING, 0f));
        bidItemRepository.save(new BidItem("item8", 10004f, 1080, RUNNING, 0f));
        bidItemRepository.save(new BidItem("item9", 10007f, 1040, RUNNING, 0f));
        bidItemRepository.save(new BidItem("item10", 10040f, 1090, RUNNING, 0f));
        bidItemRepository.save(new BidItem("item11", 10007f, 1020, RUNNING, 0f));
        bidItemRepository.save(new BidItem("item12", 10050f, 1020, RUNNING, 0f));
        bidItemRepository.save(new BidItem("item13", 1030f, 1001, OVER, 0f));
        bidItemRepository.save(new BidItem("item14", 10050f, 1020, OVER, 0f));
        bidItemRepository.save(new BidItem("item15", 1030f, 1001, OVER, 0f));
        bidItemRepository.save(new BidItem("item16", 10050f, 1020, OVER, 0f));

        //load up running auctions in redis cache for operations
        List<BidItem> bidItems = bidItemRepository.findAllByStatus(RUNNING);
        List<Auction> bids = bidItems.stream().map(bidItem -> new Auction(bidItem.getItemCode(), bidItem.getBasePrice(), bidItem.getStepRate(), bidItem.getStatus(), bidItem.getHighestBid())).collect(Collectors.toList());
        auctionRepository.saveAll(bids);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
