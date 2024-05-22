package org.mabr.postservice.repository.data;

import org.mabr.postservice.entity.data.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    Currency findByName(String name);
}
