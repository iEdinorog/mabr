package org.mabr.postservice.dto.post;

import java.time.LocalDate;
import java.util.List;

public record PostRequest(

        String username,

        String title,

        LocalDate createdAt,

        List<PostBlockDto> blocks,

        String image,

        List<Integer> labelIds
) {
}
