package com.example.ritesh.auction.restweb.controller;

import com.example.ritesh.auction.restweb.path.ApiPath;
import com.example.ritesh.auction.restweb.response.BidActivityResponse;
import com.example.ritesh.auction.service.BidActivityService;
import lombok.extern.log4j.Log4j;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j
public class BidActivityController {

    private static final String ITEM_CODE = "itemCode";
    private static final String PAGE = "page";
    private static final String SIZE = "size";
    private static final String GET_ALL_BIDDING_ACTIVITY_FOR_ITEM_FOR_PAGE_AND_SIZE = "get all bidding activity for item:{} for page:{} and size:{}";

    @Autowired
    private BidActivityService bidActivityService;

    @GetMapping(value = ApiPath.BIDDING_ACTIVITY_BY_ITEMCODE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<BidActivityResponse>> getAllBiddingActivityByItemCode(@PathVariable(ITEM_CODE) String itemCode, @RequestParam(PAGE) int page, @RequestParam(SIZE) int size) {
        log.info(new StringFormattedMessage(GET_ALL_BIDDING_ACTIVITY_FOR_ITEM_FOR_PAGE_AND_SIZE, itemCode, page, size));
        return new ResponseEntity<>(bidActivityService.getAllBiddingActivityByItemCode(itemCode, PageRequest.of(page, size)), HttpStatus.OK);
    }
}
