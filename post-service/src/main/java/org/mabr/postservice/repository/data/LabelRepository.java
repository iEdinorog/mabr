package org.mabr.postservice.repository.data;

import org.mabr.postservice.entity.data.Label;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Integer> {
}
