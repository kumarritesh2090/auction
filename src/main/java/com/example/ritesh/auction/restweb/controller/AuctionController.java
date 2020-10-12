package com.example.ritesh.auction.restweb.controller;

import com.example.ritesh.auction.annotation.Auditable;
import com.example.ritesh.auction.restweb.path.ApiPath;
import com.example.ritesh.auction.restweb.request.BidRequestVo;
import com.example.ritesh.auction.restweb.response.AuctionResponse;
import com.example.ritesh.auction.service.AuctionService;
import lombok.extern.log4j.Log4j;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j
public class AuctionController {

    private static final String STATUS = "status";
    private static final String PAGE = "page";
    private static final String SIZE = "size";
    private static final String ITEMCODE = "itemcode";
    private static final String GET_AUCTION_BY_STATUS_FOR_PAGE_AND_SIZE = "get auction by status:{}, for page:{} and size:{}";
    private static final String BID_ON_ITEM_WITH_BID_REQUEST = "bid on item:{}, with bidRequest:{}";

    @Autowired
    private AuctionService auctionService;

    @GetMapping(value = ApiPath.ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<AuctionResponse>> getAllAuctionByStatus(@RequestParam(STATUS) String status, @RequestParam(PAGE) int page, @RequestParam(SIZE) int size) {
        log.info(new StringFormattedMessage(GET_AUCTION_BY_STATUS_FOR_PAGE_AND_SIZE, status, page, size));
        return new ResponseEntity<>(auctionService.findAllByStatus(status, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Auditable
    @PostMapping(value = ApiPath.BID_BY_ITEMCODE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity bid(@PathVariable(ITEMCODE) String itemcode, @Validated @RequestBody BidRequestVo bidRequestVo) {
        log.info(new StringFormattedMessage(BID_ON_ITEM_WITH_BID_REQUEST, itemcode, bidRequestVo));
        return new ResponseEntity(auctionService.updateHighestBidForItemcode(itemcode, bidRequestVo.getBidValue()));
    }
}
