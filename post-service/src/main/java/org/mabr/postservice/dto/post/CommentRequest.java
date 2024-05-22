package org.mabr.postservice.dto.post;

public record CommentRequest(

        String username,

        String content
) {
}
