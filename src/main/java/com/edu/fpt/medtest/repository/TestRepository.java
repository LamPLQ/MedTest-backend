package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Integer> {
    List<Test> getAllByTestTypeID(int testTypeID);

    boolean existsByTestID(int testID);

    List<Test> getAllByVersionID(int versionID);

    List<Test> getAllByVersionIDAndTestTypeID(int version, int testType);
}
