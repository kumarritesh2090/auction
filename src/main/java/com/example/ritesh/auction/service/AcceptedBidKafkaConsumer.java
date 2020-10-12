package com.example.ritesh.auction.service;

import com.example.ritesh.auction.mongo.model.AcceptedBidMessage;
import com.example.ritesh.auction.mongo.model.BidItem;
import com.example.ritesh.auction.mongo.repository.BidItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Log4j
public class AcceptedBidKafkaConsumer {

    private static final String NEW_HIGHEST_BID_FOR_ITEM_FOR_BIDDING_AMOUNT = "processing new highest bid for item:{} for biddingAmount:{}";

    @Autowired
    private BidItemRepository bidItemRepository;

    @KafkaListener(topics = "${bidserver.highestbid.success.topic}",
            groupId = "auction-group-id")
    @KafkaHandler
    public void receiveNewAcceptedBid(@Payload Object payload,
                                      @Headers MessageHeaders headers) {
        AcceptedBidMessage data = new ObjectMapper().convertValue(((ConsumerRecord) payload).value(), AcceptedBidMessage.class);
        log.info(new StringFormattedMessage(NEW_HIGHEST_BID_FOR_ITEM_FOR_BIDDING_AMOUNT, data.getItemCode(), data.getBidValue()));

        Optional<BidItem> optionalBidItem = bidItemRepository.findById(data.getItemCode());
        if (optionalBidItem.isPresent()) {
            BidItem bidItem = optionalBidItem.get();
            bidItem.setHighestBid(data.getBidValue());
            bidItemRepository.save(bidItem);
        }
    }
}
