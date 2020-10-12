package com.example.ritesh.auction.service;

import com.example.ritesh.auction.mongo.model.AcceptedBidMessage;
import lombok.extern.log4j.Log4j;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Log4j
public class KafkaProducerService {

    private static final String PUBLISHING_NEW_HIGHEST_BID_FOR_ITEM_FOR_BIDDING_AMOUNT = "publishing new highest bid for item:{} for biddingAmount:{}";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${bidserver.highestbid.success.topic}")
    String acceptedBidTopic;

    public void publishAcceptedBids(AcceptedBidMessage payload) {
        log.info(new StringFormattedMessage(PUBLISHING_NEW_HIGHEST_BID_FOR_ITEM_FOR_BIDDING_AMOUNT, payload.getItemCode(), payload.getBidValue()));
        Message<AcceptedBidMessage> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, acceptedBidTopic)
                .build();
        kafkaTemplate.send(message);
    }
}
