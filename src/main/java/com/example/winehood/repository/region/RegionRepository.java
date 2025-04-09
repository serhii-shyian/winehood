package com.example.winehood.repository.region;

import com.example.winehood.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RegionRepository extends JpaRepository<Region, Long>,
        JpaSpecificationExecutor<Region> {
}
