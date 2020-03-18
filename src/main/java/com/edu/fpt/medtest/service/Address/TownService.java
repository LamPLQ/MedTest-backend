package com.edu.fpt.medtest.service.Address;

import com.edu.fpt.medtest.entity.Town;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TownService {
    void saveTown(Town town);

    List<Town> listTown();

    void deleteTown(String TownCode);

    Optional<Town> getTownByCode(String townCode);
}
