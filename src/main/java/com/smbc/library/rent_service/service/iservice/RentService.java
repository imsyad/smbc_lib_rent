package com.smbc.library.rent_service.service.iservice;

import com.smbc.library.rent_service.dto.RentRequestDto;
import com.smbc.library.rent_service.dto.ResponseDto;

import jakarta.servlet.http.HttpServletRequest;

public interface RentService {

    ResponseDto<?> myRentBookList(HttpServletRequest request);

    ResponseDto<?> rent(RentRequestDto requestDetail, HttpServletRequest request);
}
