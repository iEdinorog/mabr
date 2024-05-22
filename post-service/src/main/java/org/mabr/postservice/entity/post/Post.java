package org.mabr.postservice.entity.post;

import org.mabr.postservice.entity.data.Image;
import org.mabr.postservice.entity.data.Label;
import org.mabr.postservice.entity.security.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(schema = "post", name = "post")
@Indexed
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @GenericField(name = "createdAt_sort", sortable = Sortable.YES)
    @Column(nullable = false)
    private LocalDate createdAt;

    @ManyToOne
    private User author;

    @FullTextField(searchable = Searchable.YES)
    @Column(nullable = false)
    private String title;

    @GenericField(name = "views_sort", sortable = Sortable.YES)
    @Column(nullable = false)
    private int views;

    @ManyToOne
    private Image image;

    @GenericField(name = "rating_sort", sortable = Sortable.YES)
    @Column(nullable = false)
    private float rating;

    @IndexedEmbedded
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            schema = "post", name = "post_block",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "block_id"))
    private Set<PostBlock> blocks = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            schema = "post", name = "post_label",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id"))
    private Set<Label> labels = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            schema = "post", name = "post_comment",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id"))
    private Set<PostComment> comments = new HashSet<>();
}
