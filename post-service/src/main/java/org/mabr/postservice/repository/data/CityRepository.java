package org.mabr.postservice.repository.data;

import org.mabr.postservice.entity.data.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Integer> {
    City findByNameRu(String nameRu);
}
