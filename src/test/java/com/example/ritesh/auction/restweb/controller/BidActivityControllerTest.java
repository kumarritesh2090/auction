package com.example.ritesh.auction.restweb.controller;

import com.example.ritesh.auction.restweb.path.ApiPath;
import com.example.ritesh.auction.restweb.response.BidActivityResponse;
import com.example.ritesh.auction.service.BidActivityService;
import org.json.JSONArray;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

public class BidActivityControllerTest {

    public static final String ITEM_1 = "item1";
    private MockMvc mockMvc;

    @Mock
    private BidActivityService bidActivityService;

    @InjectMocks
    private BidActivityController bidActivityController;

    @Before
    public void setUp() {
        initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.bidActivityController).build();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getAllBiddingActivityByItemCode() throws Exception {
        Mockito.when(bidActivityService.getAllBiddingActivityByItemCode(ITEM_1, PageRequest.of(0, 5))).thenReturn(getBidActivityResponse());
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get(ApiPath.BIDDING_ACTIVITY_BY_ITEMCODE, ITEM_1).param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        JSONArray response = new JSONArray(result.getResponse().getContentAsString());
        Assert.assertEquals(getBidActivityResponse().size(), response.length());
        Mockito.verify(bidActivityService).getAllBiddingActivityByItemCode(ITEM_1, PageRequest.of(0, 5));
    }

    @Test
    public void getAllBiddingActivityByItemCodeNoActivity() throws Exception {
        Mockito.when(bidActivityService.getAllBiddingActivityByItemCode(ITEM_1, PageRequest.of(0, 5))).thenReturn(new ArrayList<>());
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get(ApiPath.BIDDING_ACTIVITY_BY_ITEMCODE, ITEM_1).param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        JSONArray response = new JSONArray(result.getResponse().getContentAsString());
        Assert.assertEquals(0, response.length());
        Mockito.verify(bidActivityService).getAllBiddingActivityByItemCode(ITEM_1, PageRequest.of(0, 5));
    }

    private List<BidActivityResponse> getBidActivityResponse() {
        List<BidActivityResponse> bidActivityResponses = new ArrayList<>();
        bidActivityResponses.add(new BidActivityResponse(ITEM_1, 10f, HttpStatus.NOT_ACCEPTABLE.name(), new Date()));
        bidActivityResponses.add(new BidActivityResponse(ITEM_1, 100f, HttpStatus.CREATED.name(), new Date()));
        bidActivityResponses.add(new BidActivityResponse(ITEM_1, 1000f, HttpStatus.NOT_FOUND.name(), new Date()));
        bidActivityResponses.add(new BidActivityResponse(ITEM_1, 109f, HttpStatus.NOT_ACCEPTABLE.name(), new Date()));
        return bidActivityResponses;
    }
}