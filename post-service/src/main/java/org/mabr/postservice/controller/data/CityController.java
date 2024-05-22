package org.mabr.postservice.controller.data;

import org.mabr.postservice.entity.data.City;
import org.mabr.postservice.service.data.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/data/city")
@RequiredArgsConstructor
public class CityController {

    private final DataService service;

    @GetMapping()
    public ResponseEntity<List<City>> getCityList() {
        var city = service.getCityList();
        if (city.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(city);
    }

    @GetMapping("/{id}")
    public ResponseEntity<City> getCity(@PathVariable int id) {
        var city = service.getCity(id);
        return ResponseEntity.ok(city);
    }
}
