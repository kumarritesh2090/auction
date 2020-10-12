package com.example.ritesh.auction.restweb.controller;

import com.example.ritesh.auction.restweb.path.ApiPath;
import com.example.ritesh.auction.restweb.request.BidRequestVo;
import com.example.ritesh.auction.restweb.response.AuctionResponse;
import com.example.ritesh.auction.service.AuctionService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

public class AuctionControllerTest {

    private static final String RUNNING = "RUNNING";
    private static final int PAGE = 0;
    private static final int SIZE = 5;
    public static final String ITEM_1 = "item1";
    private MockMvc mockMvc;

    @Mock
    private AuctionService auctionService;

    @InjectMocks
    private AuctionController auctionController;

    @Before
    public void setUp() {
        initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.auctionController).build();
    }

    @After
    public void tearDown() {
    }

    private static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    @Test
    public void getAllAuctionByStatus() throws Exception {
        Mockito.when(auctionService.findAllByStatus(RUNNING, PageRequest.of(PAGE, SIZE))).thenReturn(getAuctionResponse());
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get("/").param("status", RUNNING).param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        JSONArray response = new JSONArray(result.getResponse().getContentAsString());
        Assert.assertEquals(getAuctionResponse().size(), response.length());
        Mockito.verify(auctionService).findAllByStatus(RUNNING, PageRequest.of(PAGE, SIZE));
    }

    @Test
    public void getAllAuctionByStatusNoOutput() throws Exception {
        Mockito.when(auctionService.findAllByStatus(RUNNING, PageRequest.of(PAGE, SIZE))).thenReturn(new ArrayList<>());
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get("/").param("status", RUNNING).param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        JSONArray response = new JSONArray(result.getResponse().getContentAsString());
        Assert.assertEquals(0, response.length());
        Mockito.verify(auctionService).findAllByStatus(RUNNING, PageRequest.of(PAGE, SIZE));
    }

    private List<AuctionResponse> getAuctionResponse() {
        List<AuctionResponse> auctionResponses = new ArrayList<>();
        auctionResponses.add(new AuctionResponse(ITEM_1, 100f, 10));
        auctionResponses.add(new AuctionResponse("item2", 100f, 10));
        auctionResponses.add(new AuctionResponse("item3", 100f, 10));
        auctionResponses.add(new AuctionResponse("item4", 100f, 10));
        auctionResponses.add(new AuctionResponse("item5", 100f, 10));
        return auctionResponses;
    }

    @Test
    public void bidAccepted() throws Exception {
        BidRequestVo bidRequestVo = new BidRequestVo(10f);
        Mockito.when(auctionService.updateHighestBidForItemcode(ITEM_1, bidRequestVo.getBidValue())).thenReturn(HttpStatus.CREATED);
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.post(ApiPath.BID_BY_ITEMCODE, ITEM_1)
                        .content(convertObjectToJsonBytes(bidRequestVo))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        Mockito.verify(auctionService).updateHighestBidForItemcode(ITEM_1, bidRequestVo.getBidValue());
    }

    @Test
    public void bidNotFound() throws Exception {
        BidRequestVo bidRequestVo = new BidRequestVo(10f);
        Mockito.when(auctionService.updateHighestBidForItemcode(ITEM_1, bidRequestVo.getBidValue())).thenReturn(HttpStatus.NOT_FOUND);
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.post(ApiPath.BID_BY_ITEMCODE, ITEM_1)
                        .content(convertObjectToJsonBytes(bidRequestVo))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
        Mockito.verify(auctionService).updateHighestBidForItemcode(ITEM_1, bidRequestVo.getBidValue());
    }

    @Test
    public void bidRejected() throws Exception {
        BidRequestVo bidRequestVo = new BidRequestVo(10f);
        Mockito.when(auctionService.updateHighestBidForItemcode(ITEM_1, bidRequestVo.getBidValue())).thenReturn(HttpStatus.NOT_ACCEPTABLE);
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.post(ApiPath.BID_BY_ITEMCODE, ITEM_1)
                        .content(convertObjectToJsonBytes(bidRequestVo))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andReturn();
        Mockito.verify(auctionService).updateHighestBidForItemcode(ITEM_1, bidRequestVo.getBidValue());
    }
}