package com.mitrais.service1.service;

import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.devskiller.jfairy.Fairy;
import com.devskiller.jfairy.producer.person.Person;
import com.mitrais.service1.domain.Member;

@Service
public class MemberServiceImpl implements MemberService{
    private final KafkaTemplate<UUID, Object> kafkaTemplate;
    private ConcurrentMap<UUID, Member> members = new ConcurrentHashMap<>();

    public MemberServiceImpl(KafkaTemplate<UUID, Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void save(Member member) {
        this.members.putIfAbsent(member.getId(), member);
        kafkaTemplate.send("member", member.getId(), member);
    }

    @Override
    public List<Member> list() {
        return members.entrySet().stream().map(Entry::getValue).collect(Collectors.toList());
    }

    @Override
    public Optional<Member> findById(UUID id) {
        return Optional.ofNullable(members.get(id));
    }

    @Override
    public List<Member> findByFirstNameOrLastName(String value) {
        return members.entrySet()
          .stream()
          .map(Entry::getValue)
          .filter(m-> m.getFirstName().contains(value) || m.getLastName().contains(value))
          .collect(Collectors.toList());
    }

    @Async
    @Override
    public ListenableFuture<Void> generateData() {
        Fairy fairy = Fairy.create();
        IntStream.range(0, 10).forEach(i -> {            
            Person person = fairy.person();
            Member m = new Member();
            m.setFirstName(person.getFirstName());
            m.setLastName(person.getLastName());
            save(m);
        });
        members.entrySet().stream().forEach(entry ->{
            Person person = fairy.person();
            Member m = entry.getValue();
            m.setLastName(person.getLastName());
            save(m);
        });
        return AsyncResult.forValue(null);
    }

    @Async
    @Override
    public ListenableFuture<Void> randomUpdate() {
        Fairy fairy = Fairy.create();
        members.entrySet().stream().forEach(entry ->{
            Person person = fairy.person();
            Member m = entry.getValue();
            m.setLastName(person.getLastName());
            save(m);
        });
        return AsyncResult.forValue(null);
    }
    
}
