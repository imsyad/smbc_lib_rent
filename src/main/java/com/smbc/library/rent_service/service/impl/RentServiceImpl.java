package com.smbc.library.rent_service.service.impl;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.smbc.library.rent_service.dto.BookDto;
import com.smbc.library.rent_service.dto.RentRequestDto;
import com.smbc.library.rent_service.dto.ResponseDto;
import com.smbc.library.rent_service.dto.UpdateBookDto;
import com.smbc.library.rent_service.exception.InternalServerErrorException;
import com.smbc.library.rent_service.model.RentBooks;
import com.smbc.library.rent_service.repository.RentBookRepository;
import com.smbc.library.rent_service.service.iservice.RentService;
import com.smbc.library.rent_service.utils.ResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentServiceImpl implements RentService {

    @Value("${smbc.properties.external.catalogue_service.url}")
    private String CATALOGUE_SERVICE_URL;

    private final RentBookRepository rentBookRepository;
    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public ResponseDto<?> rent(RentRequestDto requestDetail, HttpServletRequest request) {
        Long memberId = Long.valueOf(request.getAttribute("memberId").toString());

        if (memberId == null) {
            return ResponseUtil.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Member ID is not found");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<ResponseDto<BookDto>> responseType = new ParameterizedTypeReference<ResponseDto<BookDto>>() {
        };

        String memberServiceUrlWithParameter = CATALOGUE_SERVICE_URL
                .concat("/catalogue?bookId=" + requestDetail.getBookId());

        var response = restTemplate.exchange(memberServiceUrlWithParameter, HttpMethod.GET,
                getEntity, responseType);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody().getStatus() != HttpStatus.OK.value()) {
            throw new InternalServerErrorException("Failed to find catalogue data");
        }
        BookDto book = response.getBody().getData();

        if (book == null) {
            return ResponseUtil.failed(HttpStatus.BAD_REQUEST.value(), "Book data is not found");
        }

        if (book.getTotal() < requestDetail.getAmount()) {
            return ResponseUtil.failed(HttpStatus.BAD_REQUEST.value(),
                    "The number of books in stock is less than the number to be borrowed");
        }

        Integer updatedTotal = book.getTotal() - requestDetail.getAmount();
        UpdateBookDto updateBookRequest = UpdateBookDto.builder()
                .author(book.getAuthor())
                .name(book.getName())
                .total(updatedTotal)
                .imageUrl(book.getImageUrl())
                .build();

        HttpEntity<UpdateBookDto> requestHttpEntity = new HttpEntity<>(updateBookRequest, headers);
        ParameterizedTypeReference<ResponseDto<?>> responseTypeUpdateCatalogue = new ParameterizedTypeReference<ResponseDto<?>>() {
        };
        var updateCatalogueResponse = restTemplate.exchange(
                CATALOGUE_SERVICE_URL.concat("/catalogue/" + requestDetail.getBookId()), HttpMethod.PUT,
                requestHttpEntity, responseTypeUpdateCatalogue);

        if (updateCatalogueResponse.getStatusCode() != HttpStatus.OK) {
            throw new InternalServerErrorException("Failed to update the catalogue data");
        }

        RentBooks model = RentBooks.builder()
                .bookId(requestDetail.getBookId())
                .amount(requestDetail.getAmount())
                .startDate(requestDetail.getStartDate())
                .endDate(requestDetail.getEndDate())
                .memberId(memberId)
                .build();

        RentBooks saved = rentBookRepository.save(model);

        if (saved == null) {
            return ResponseUtil.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to rent a book");
        }

        return ResponseUtil.success(HttpStatus.OK.value(), "Successfully rent a book", saved);
    }

    @Override
    public ResponseDto<?> myRentBookList(HttpServletRequest request) {
        Long memberId = Long.valueOf(request.getAttribute("memberId").toString());

        var list = rentBookRepository.findAllByMemberId(memberId);
        if (list == null) {
            return ResponseUtil.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch rent book list.");
        }

        return ResponseUtil.success(HttpStatus.OK.value(), "Successfully fetch rent book list data", list);
    }
}
