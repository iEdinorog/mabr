package org.mabr.postservice.entity.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "data", name = "temp_image")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TempImage {

    @Id
    @GeneratedValue
    @JsonIgnore
    private int id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Lob
    @Column(nullable = false)
    @JsonIgnore
    private byte[] data;

    @Column(nullable = false)
    private String url;
}
