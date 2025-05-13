package com.smbc.library.rent_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smbc.library.rent_service.model.RentBooks;

@Repository
public interface RentBookRepository extends JpaRepository<RentBooks, Long> {
    List<RentBooks> findAllByMemberId(Long memberId);
}
