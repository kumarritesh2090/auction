package com.example.ritesh.auction.service;

import com.example.ritesh.auction.mongo.model.AcceptedBidMessage;
import com.example.ritesh.auction.redis.model.Auction;
import com.example.ritesh.auction.redis.repository.AuctionRepository;
import com.example.ritesh.auction.restweb.response.AuctionResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.AtMost;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuctionServiceTest {

    private static final String RUNNING = "RUNNING";
    private static final String KEY = "auction:item1";
    private static final String OVER = "OVER";
    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private HashOperations hashOperations;

    @Mock
    private AuctionRepository auctionRepository;

    @InjectMocks
    private AuctionService auctionService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void findAllByRunningStatusSuccess() {
        when(auctionRepository.findAllByStatus(RUNNING, PageRequest.of(0, 5))).thenReturn(getBids());
        List<AuctionResponse> auctionResponses = auctionService.findAllByStatus(RUNNING, PageRequest.of(0, 5));
        Assert.assertEquals(getBids().size(), auctionResponses.size());
        verify(auctionRepository).findAllByStatus(RUNNING, PageRequest.of(0, 5));

    }

    @Test
    public void findAllByRunningStatusEmpty() {
        when(auctionRepository.findAllByStatus(RUNNING, PageRequest.of(0, 5))).thenReturn(new ArrayList<>());
        List<AuctionResponse> auctionResponses = auctionService.findAllByStatus(RUNNING, PageRequest.of(0, 5));
        Assert.assertEquals(0, auctionResponses.size());
        verify(auctionRepository).findAllByStatus(RUNNING, PageRequest.of(0, 5));

    }

    @Test(expected = RuntimeException.class)
    public void findAllByRunningStatusFailure() {
        when(auctionRepository.findAllByStatus(RUNNING, PageRequest.of(0, 5))).thenReturn(null);
        List<AuctionResponse> auctionResponses = auctionService.findAllByStatus(RUNNING, PageRequest.of(0, 5));
        verify(auctionRepository).findAllByStatus(RUNNING, PageRequest.of(0, 5));

    }

    private List<Auction> getBids() {
        List<Auction> auctions = new ArrayList<>();
        auctions.add(new Auction("item1", 100f, 10, RUNNING, 0f));
        auctions.add(new Auction("item2", 100f, 10, RUNNING, 0f));
        auctions.add(new Auction("item3", 100f, 10, RUNNING, 0f));
        auctions.add(new Auction("item4", 100f, 10, RUNNING, 0f));
        auctions.add(new Auction("item5", 100f, 10, RUNNING, 0f));
        return auctions;
    }

    @Test
    public void updateHighestBidSurpassingOldHighestBid() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(KEY)).thenReturn(getRunningBidMap());
        doNothing().when(redisTemplate).watch(KEY);
        doNothing().when(redisTemplate).multi();
        doNothing().when(hashOperations).putAll(anyString(), anyMap());
        when(redisTemplate.exec()).thenReturn(getBids());
        doNothing().when(kafkaProducerService).publishAcceptedBids(any(AcceptedBidMessage.class));
        HttpStatus httpStatus = auctionService.updateHighestBidForItemcode("item1", 120f);
        verify(hashOperations, new AtMost(2)).entries(KEY);
        verify(redisTemplate).watch(KEY);
        verify(redisTemplate).multi();
        verify(hashOperations).putAll(anyString(), anyMap());
        verify(redisTemplate).exec();
        verify(kafkaProducerService).publishAcceptedBids(any(AcceptedBidMessage.class));
        Assert.assertEquals(HttpStatus.CREATED, httpStatus);
    }

    @Test
    public void updateHighestBidSurpassingOldHighestBidConcurrencyFail() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(KEY)).thenReturn(getRunningBidMap());
        doNothing().when(redisTemplate).watch(KEY);
        doNothing().when(redisTemplate).multi();
        doNothing().when(hashOperations).putAll(anyString(), anyMap());
        when(redisTemplate.exec()).thenReturn(new ArrayList<>());
        HttpStatus httpStatus = auctionService.updateHighestBidForItemcode("item1", 120f);
        verify(hashOperations, new AtMost(2)).entries(KEY);
        verify(redisTemplate).watch(KEY);
        verify(redisTemplate).multi();
        verify(hashOperations).putAll(anyString(), anyMap());
        verify(redisTemplate).exec();
        Assert.assertEquals(HttpStatus.NOT_ACCEPTABLE, httpStatus);
    }

    @Test
    public void updateHighestBidSurpassingBasePrice() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(KEY)).thenReturn(getRunningBidMap());
        doNothing().when(redisTemplate).watch(KEY);
        doNothing().when(redisTemplate).multi();
        doNothing().when(hashOperations).putAll(anyString(), anyMap());
        when(redisTemplate.exec()).thenReturn(getBids());
        HttpStatus httpStatus = auctionService.updateHighestBidForItemcode("item1", Float.valueOf(getRunningBidMap().get("basePrice")));
        verify(hashOperations, new AtMost(2)).entries(KEY);
        verify(redisTemplate).watch(KEY);
        verify(redisTemplate).multi();
        verify(hashOperations).putAll(anyString(), anyMap());
        verify(redisTemplate).exec();
        Assert.assertEquals(HttpStatus.CREATED, httpStatus);
    }

    @Test
    public void failUpdateHighestBidBelowBasePrice() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(KEY)).thenReturn(getRunningBidMap());
        HttpStatus httpStatus = auctionService.updateHighestBidForItemcode("item1", Float.valueOf(getRunningBidMap().get("basePrice")) - 10f);
        verify(hashOperations, new AtMost(1)).entries(KEY);
        Assert.assertEquals(HttpStatus.NOT_ACCEPTABLE, httpStatus);
    }

    @Test
    public void failUpdateHighestBidNoAuction() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(KEY)).thenReturn(new HashMap());
        HttpStatus httpStatus = auctionService.updateHighestBidForItemcode("item1", 10f);
        verify(hashOperations, new AtMost(1)).entries(KEY);
        Assert.assertEquals(HttpStatus.NOT_FOUND, httpStatus);
    }

    @Test
    public void failUpdateHighestBidNoRunningAuction() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(KEY)).thenReturn(getOverBidMap());
        HttpStatus httpStatus = auctionService.updateHighestBidForItemcode("item1", 10f);
        verify(hashOperations, new AtMost(1)).entries(KEY);
        Assert.assertEquals(HttpStatus.NOT_ACCEPTABLE, httpStatus);
    }

    @Test(expected = RuntimeException.class)
    public void failUpdateHighestBidException() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(KEY)).thenReturn(null);
        HttpStatus httpStatus = auctionService.updateHighestBidForItemcode("item1", 10f);
        verify(hashOperations, new AtMost(1)).entries(KEY);
    }

    private Map<String, String> getRunningBidMap() {
        Map<String, String> bidMap = new HashMap<>();
        bidMap.put("itemCode", "item1");
        bidMap.put("basePrice", "100f");
        bidMap.put("stepRate", "10");
        bidMap.put("status", RUNNING);
        bidMap.put("highestBid", "0f");
        return bidMap;
    }

    private Map<String, String> getOverBidMap() {
        Map<String, String> bidMap = new HashMap<>();
        bidMap.put("itemCode", "item1");
        bidMap.put("basePrice", "100f");
        bidMap.put("stepRate", "10");
        bidMap.put("status", OVER);
        bidMap.put("highestBid", "0f");
        return bidMap;
    }
}