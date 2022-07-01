package com.mitrais.service1.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.util.concurrent.ListenableFuture;

import com.mitrais.service1.domain.Member;

public interface MemberService {
    Optional<Member> findById(UUID id);
    void save(Member member);
    List<Member> list();
    List<Member> findByFirstNameOrLastName(String value);
    ListenableFuture<Void> generateData();
    ListenableFuture<Void> randomUpdate();
}
