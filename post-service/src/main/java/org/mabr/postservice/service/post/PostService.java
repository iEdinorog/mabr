package org.mabr.postservice.service.post;

import org.mabr.postservice.dto.post.CommentRequest;
import org.mabr.postservice.dto.post.PostBlockDto;
import org.mabr.postservice.dto.post.PostRequest;
import org.mabr.postservice.entity.post.Post;
import org.mabr.postservice.entity.post.PostBlock;
import org.mabr.postservice.entity.post.PostComment;
import org.mabr.postservice.entity.user.User;
import org.mabr.postservice.exception.ResourceNotFoundException;
import org.mabr.postservice.repository.post.PostCommentRepository;
import org.mabr.postservice.repository.post.PostRepository;
import org.mabr.postservice.service.data.DataService;
import org.mabr.postservice.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final List<String> SEARCHABLE_FIELDS = Arrays.asList("title", "blocks.content");
    private final PostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final DataService dataService;
    private final SearchService searchService;

    public Post create(PostRequest postRequest) {
        User user = null;

        var blocks = getPostBlocks(postRequest.blocks());

        var post = Post.builder()
                .author(user)
                .createdAt(postRequest.createdAt())
                .title(postRequest.title())
                .blocks(blocks)
                .views(0)
                .rating(0)
                .build();

        post = setLabels(post, postRequest.labelIds());

        if (StringUtils.hasText(postRequest.image())) {
            var image = dataService.getImage(postRequest.image());
            post.setImage(image);
        }

        return postRepository.save(post);
    }

    public List<Post> searchPost(String text, int page, int size, String sortField) {
        return searchService.searchBy(Post.class, text, SEARCHABLE_FIELDS, sortField, page, size);
    }

    private HashSet<PostBlock> getPostBlocks(Iterable<PostBlockDto> postBlocks) {
        var blocks = new HashSet<PostBlock>();
        for (var block : postBlocks) {
            var postBlock = new PostBlock();
            postBlock.setContent(block.content());
            postBlock.setType(block.type());
            blocks.add(postBlock);
        }
        return blocks;
    }

    public Post getById(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post", "id", String.valueOf(id)));
    }

    public Post setLabels(Post post, List<Integer> labelIds) {
        if (!labelIds.isEmpty()) {
            var labels = dataService.getLabelListById(labelIds);
            post.setLabels(labels);
        }

        return post;
    }

    public Post update(int id, PostRequest postRequest) {
        var post = getById(id);
        var blocks = getPostBlocks(postRequest.blocks());

        post.setTitle(postRequest.title());
        post.setBlocks(blocks);

        post = setLabels(post, postRequest.labelIds());

        if (postRequest.image() != null) {
            var image = dataService.getImage(postRequest.image());
            post.setImage(image);
        }

        return postRepository.save(post);
    }

    public Post updateRating(int id, float value) {
        var post = getById(id);
        var rating = post.getRating() != 0 ? (post.getRating() + value) / 2 : value;
        rating = (float) (Math.round(rating * 100.0) / 100.0);

        if (rating >= 4.98) {
            rating = 5;
        }

        post.setRating(rating);

        return postRepository.save(post);
    }

    public Post saveComment(int postId, CommentRequest commentRequest) {
        var post = getById(postId);
        User user = null;

        var comment = new PostComment();
        comment.setContent(commentRequest.content());
        comment.setUser(user);
        comment.setUserId(user.getId());

        post.getComments().add(comment);
        commentRepository.save(comment);

        return postRepository.save(post);
    }

    public void deleteComment(int postId, int commentId) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("comment", "id", String.valueOf(commentId)));

        var post = getById(postId);
        post.getComments().remove(comment);
        postRepository.save(post);

        commentRepository.deleteById(commentId);
    }
}
