package com.edu.fpt.medtest.service.Address;

import com.edu.fpt.medtest.entity.Town;
import com.edu.fpt.medtest.repository.TownRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TownServiceImpl implements TownService {

    @Autowired
    private TownRepository townRepository;

    @Override
    public void saveTown(Town town) {
        townRepository.save(town);
    }

    @Override
    public List<Town> listTown() {
        List<Town> towns = townRepository.findAll();
        return townRepository.findAll();
    }

    @Override
    public void deleteTown(String TownCode) {
        townRepository.deleteById(TownCode);
    }

    @Override
    public Optional<Town> getTownByCode(String townCode) {
        Optional<Town> getTownByCode = townRepository.findById(townCode);
        return getTownByCode;
    }


}
