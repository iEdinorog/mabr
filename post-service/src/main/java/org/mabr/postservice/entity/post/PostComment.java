package org.mabr.postservice.entity.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.mabr.postservice.entity.user.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(schema = "post", name = "comment")
@Getter
@Setter
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(insertable = false, updatable = false)
    private int userId;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String content;

    @ManyToMany(mappedBy = "comments")
    @JsonIgnore
    private Set<Post> posts = new HashSet<>();

}
