package com.javatechie.group.controller;

import com.javatechie.group.entity.Post;
import com.javatechie.group.entity.PostStatus;
import com.javatechie.group.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
public class PostController {


    @Autowired
    private PostRepository postRepository;

    @PostMapping("/create")
    public String createPost(@RequestBody Post post, Principal principal) {
        post.setStatus(PostStatus.PENDING);
        post.setUserName(principal.getName());
        postRepository.save(post);
        return principal.getName() + " Your post published successfully , Required ADMIN/MODERATOR Action !";
    }

    @GetMapping("/approvePost/{postId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String approvePost(@PathVariable int postId) {
        Post post = postRepository.findById(postId).get();
        post.setStatus(PostStatus.APPROVED);
        postRepository.save(post);
        return "Post Approved !!";
    }

    @GetMapping("/approveAll")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String approveAll() {
        postRepository.findAll().stream().filter(post -> post.getStatus().equals(PostStatus.PENDING)).forEach(post -> {
            post.setStatus(PostStatus.APPROVED);
            postRepository.save(post);
        });
        return "Approved all posts !";
    }

    @GetMapping("/removePost/{postId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String removePost(@PathVariable int postId) {
        Post post = postRepository.findById(postId).get();
        post.setStatus(PostStatus.REJECTED);
        postRepository.save(post);
        return "Post Rejected !!";
    }


    @GetMapping("/rejectAll")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String rejectAll() {
        postRepository.findAll().stream().filter(post -> post.getStatus().equals(PostStatus.PENDING)).forEach(post -> {
            post.setStatus(PostStatus.REJECTED);
            postRepository.save(post);
        });
        return "Rejected all posts !";
    }

    @GetMapping("/viewAll")
    public  List<Post> viewAll(){
        return postRepository.findAll().stream()
                .filter(post -> post.getStatus().equals(PostStatus.APPROVED))
                .collect(Collectors.toList());
    }
}
