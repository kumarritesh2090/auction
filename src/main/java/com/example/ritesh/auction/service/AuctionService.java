package com.example.ritesh.auction.service;

import com.example.ritesh.auction.restweb.response.AuctionResponse;
import com.example.ritesh.auction.mongo.model.AcceptedBidMessage;
import com.example.ritesh.auction.redis.repository.AuctionRepository;
import lombok.extern.log4j.Log4j;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j
public class AuctionService {
    private static final String AUCTION = "auction:";
    private static final String STATUS = "status";
    private static final String RUNNING = "RUNNING";
    private static final String HIGHEST_BID = "highestBid";
    private static final String STEP_RATE = "stepRate";
    private static final String BASE_PRICE = "basePrice";
    private static final String RECIEVED_BID_FOR_KEY_FOR_BID_VALUE = "recieved bid for key:{} for bidValue:{}";
    private static final String SUCCESSFUL_BIDDING_FOR_HIGHEST_VALUE_FOR_KEY = "successful bidding for highestValue:{} for key:{}";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private AuctionRepository auctionRepository;

    public List<AuctionResponse> findAllByStatus(String status, PageRequest pageRequest) {
        try {
            return auctionRepository.findAllByStatus(status, pageRequest).stream().map(bids -> new AuctionResponse(bids.getItemCode(), bids.getHighestBid(), bids.getStepRate())).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch auctions, please retry ", e.getCause());
        }
    }

    public HttpStatus updateHighestBidForItemcode(String itemCode, Float bidValue) {
        try {
            String key = AUCTION + itemCode;
            log.info(new StringFormattedMessage(RECIEVED_BID_FOR_KEY_FOR_BID_VALUE, key, bidValue));
            HttpStatus response;
            Float minimumAllowedBid;
            Map<String, String> oldBidItem = redisTemplate.opsForHash().entries(key);
            if (!oldBidItem.isEmpty()) {
                minimumAllowedBid = (Float.valueOf(oldBidItem.get(HIGHEST_BID)) == 0f) ? Float.valueOf(oldBidItem.get(BASE_PRICE)) : (Float.valueOf(oldBidItem.get(HIGHEST_BID)) + Integer.valueOf(oldBidItem.get(STEP_RATE)));
                if (oldBidItem.get(STATUS).equalsIgnoreCase(RUNNING) && bidValue >= minimumAllowedBid) {
                    redisTemplate.watch(key);
                    redisTemplate.multi();
                    oldBidItem.put(HIGHEST_BID, bidValue.toString());
                    redisTemplate.opsForHash().putAll(key, oldBidItem);
                    redisTemplate.opsForHash().entries(key);
                    List<Object> multiExecResult = redisTemplate.exec();
                    if (!multiExecResult.isEmpty()) {
                        response = HttpStatus.CREATED;
                        kafkaProducerService.publishAcceptedBids(new AcceptedBidMessage(itemCode, bidValue));
                        log.info(new StringFormattedMessage(SUCCESSFUL_BIDDING_FOR_HIGHEST_VALUE_FOR_KEY, bidValue, key));
                    } else {
                        response = HttpStatus.NOT_ACCEPTABLE;
                    }
                } else {
                    response = HttpStatus.NOT_ACCEPTABLE;
                }

            } else {
                response = HttpStatus.NOT_FOUND;
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to place bid, please retry ", e.getCause());
        }
    }
}
