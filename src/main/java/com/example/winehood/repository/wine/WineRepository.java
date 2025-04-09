package com.example.winehood.repository.wine;

import com.example.winehood.model.Wine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface WineRepository extends JpaRepository<Wine, Long>,
        JpaSpecificationExecutor<Wine> {
    @Query("from Wine w left join fetch w.region r where r.id = :regionId")
    Page<Wine> findAllByRegionId(Long regionId, Pageable pageable);
}
