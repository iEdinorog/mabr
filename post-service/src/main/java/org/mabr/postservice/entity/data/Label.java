package org.mabr.postservice.entity.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mabr.postservice.entity.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(schema = "data", name = "label")
@Getter
@Setter
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "labels")
    @JsonIgnore
    private Set<Post> posts = new HashSet<>();
}
