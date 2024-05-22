package org.mabr.postservice.repository.post;

import org.mabr.postservice.entity.post.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {
}
