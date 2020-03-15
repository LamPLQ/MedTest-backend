package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.Town;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TownService {
    Town saveTown(Town town);
    List<Town> getListTown();
    void deleteTown(String TownCode);
}
