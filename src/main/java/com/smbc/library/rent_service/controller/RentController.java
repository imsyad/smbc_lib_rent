package com.smbc.library.rent_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smbc.library.rent_service.dto.RentRequestDto;
import com.smbc.library.rent_service.dto.ResponseDto;
import com.smbc.library.rent_service.service.iservice.RentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("rent")
@RequiredArgsConstructor
public class RentController {

    private final RentService rentService;

    @GetMapping()
    public ResponseDto<?> getMyRentBookList(HttpServletRequest request) {
        return rentService.myRentBookList(request);
    }

    @PostMapping()
    public ResponseDto<?> rentBook(@RequestBody RentRequestDto requestDetail, HttpServletRequest request) {
        return rentService.rent(requestDetail, request);
    }

}
