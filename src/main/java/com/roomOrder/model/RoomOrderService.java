package com.roomOrder.model;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomOrderService {
    private final RoomOrderRepository repository;

    public RoomOrderService(RoomOrderRepository repository) {
        this.repository = repository;
    }

    public List<RoomOrder> findAll() {
        return repository.findAll();
    }

    public Optional<RoomOrder> findById(Long id) {
        return repository.findById(id);
    }

    public RoomOrder save(RoomOrder order) {
        return repository.save(order);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}