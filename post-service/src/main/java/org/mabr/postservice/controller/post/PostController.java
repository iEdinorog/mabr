package org.mabr.postservice.controller.post;

import org.mabr.postservice.dto.post.CommentRequest;
import org.mabr.postservice.dto.post.PostRequest;
import org.mabr.postservice.dto.post.PostSortField;
import org.mabr.postservice.entity.post.Post;
import org.mabr.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService service;

    @PostMapping()
    public ResponseEntity<Post> create(@RequestBody PostRequest postRequest) {
        var post = service.create(postRequest);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Post>> search(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(defaultValue = "") String text,
                                             @RequestParam(defaultValue = "createdAt") PostSortField field) {
        var posts = service.searchPost(text, page, size, field.name());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getById(@PathVariable int id) {
        var post = service.getById(id);
        return ResponseEntity.ok(post);
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<Post> update(@PathVariable int id, @RequestBody PostRequest postRequest) {
        var post = service.update(id, postRequest);
        return ResponseEntity.ok(post);
    }

    @PostMapping("/{id}/rating")
    public ResponseEntity<Post> updateRating(@PathVariable int id, @RequestParam float value) {
        var post = service.updateRating(id, value);
        return ResponseEntity.ok(post);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Post> saveComment(@PathVariable int id, @RequestBody CommentRequest request) {
        var post = service.saveComment(id, request);
        return ResponseEntity.ok(post);
    }

    @PostMapping("/{id}/comment/{commentId}/delete")
    public ResponseEntity<String> deleteComment(@PathVariable int id, @PathVariable int commentId) {
        service.deleteComment(id, commentId);
        return ResponseEntity.ok("Success");
    }
}
