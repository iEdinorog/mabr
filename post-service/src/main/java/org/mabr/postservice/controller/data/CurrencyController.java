package org.mabr.postservice.controller.data;

import org.mabr.postservice.entity.data.Currency;
import org.mabr.postservice.service.data.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/data/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final DataService service;

    @GetMapping()
    public ResponseEntity<List<Currency>> getCurrencyList() {
        var currency = service.getCurrencyList();
        if (currency.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(currency);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Currency> getCurrency(@PathVariable int id) {
        var currency = service.getCurrency(id);
        return ResponseEntity.ok(currency);
    }
}
