package com.example.ritesh.auction.service;

import com.example.ritesh.auction.postgres.model.BidActivity;
import com.example.ritesh.auction.postgres.repository.BidActivityRepository;
import com.example.ritesh.auction.restweb.response.BidActivityResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;


public class BidActivityServiceTest {

    @Mock
    private BidActivityRepository bidActivityRepository;

    @InjectMocks
    private BidActivityService bidActivityService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getAllBiddingActivityByItemCode() {
        Mockito.when(bidActivityRepository.getByItemCode("item1", PageRequest.of(0, 5))).thenReturn(getBidActivity());
        List<BidActivityResponse> bidActivityResponses = bidActivityService.getAllBiddingActivityByItemCode("item1", PageRequest.of(0, 5));
        Assert.assertEquals(getBidActivity().getTotalElements(), bidActivityResponses.size());
        Mockito.verify(bidActivityRepository).getByItemCode("item1", PageRequest.of(0, 5));
    }

    @Test
    public void getAllBiddingActivityByItemCodeWithNoActivity() {
        Mockito.when(bidActivityRepository.getByItemCode("item1", PageRequest.of(0, 5))).thenReturn(new PageImpl<>(new ArrayList<>()));
        List<BidActivityResponse> bidActivityResponses = bidActivityService.getAllBiddingActivityByItemCode("item1", PageRequest.of(0, 5));
        Assert.assertTrue(bidActivityResponses.isEmpty());
        Mockito.verify(bidActivityRepository).getByItemCode("item1", PageRequest.of(0, 5));
    }

    @Test(expected = RuntimeException.class)
    public void getAllBiddingActivityByItemCodeException() {
        Mockito.when(bidActivityRepository.getByItemCode("item1", PageRequest.of(0, 5))).thenReturn(null);
        List<BidActivityResponse> bidActivityResponses = bidActivityService.getAllBiddingActivityByItemCode("item1", PageRequest.of(0, 5));
        Mockito.verify(bidActivityRepository).getByItemCode("item1", PageRequest.of(0, 5));
    }

    private Page<BidActivity> getBidActivity() {
        List<BidActivity> bidActivities = new ArrayList<>();
        bidActivities.add(new BidActivity("item1", 10f, HttpStatus.NOT_ACCEPTABLE, new Date()));
        bidActivities.add(new BidActivity("item1", 0f, HttpStatus.BAD_REQUEST, new Date()));
        bidActivities.add(new BidActivity("item1", -10f, HttpStatus.BAD_REQUEST, new Date()));
        bidActivities.add(new BidActivity("item1", 100f, HttpStatus.ACCEPTED, new Date()));
        return new PageImpl<>(bidActivities);
    }
}