package org.mabr.postservice.entity.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mabr.postservice.dto.post.BlockType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(schema = "post", name = "block")
@Getter
@Setter
public class PostBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @FullTextField
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private BlockType type;

    @ManyToMany(mappedBy = "blocks")
    @JsonIgnore
    private Set<Post> posts = new HashSet<>();
}
