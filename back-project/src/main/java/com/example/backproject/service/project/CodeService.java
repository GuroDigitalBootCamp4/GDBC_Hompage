package com.example.backproject.service.project;


import com.example.backproject.entity.User;
import com.example.backproject.entity.announcement.Post;
import com.example.backproject.entity.project.Code;
import com.example.backproject.repository.project.CodeRepository;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CodeService {

    @Autowired
    private CodeRepository postRepository;

    public Code createPost(Code post) {
        return postRepository.save(post);
    }

    public Code updatePost(Integer id, Code postDetails, boolean origin1, boolean origin2,boolean origin3, boolean origin4 ) {
        Optional<Code> existingPostOpt = postRepository.findById(id);
        if (existingPostOpt.isPresent()) {
            Code existingPost = existingPostOpt.get();
            existingPost.setTitle(postDetails.getTitle());
            existingPost.setContent(postDetails.getContent());
            if(!origin1) {
                existingPost.setImagePath(postDetails.getImagePath());
            }
            if(!origin2) {
                existingPost.setImagePath2(postDetails.getImagePath2());
            }
            if(!origin3) {
                existingPost.setImagePath3(postDetails.getImagePath3());
            }
            if(!origin4) {
                existingPost.setImagePath4(postDetails.getImagePath4());
            }

            return postRepository.save(existingPost);
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    public void deletePost(Integer id) {
        postRepository.deleteById(id);
    }

    public void incrementViews(Integer id) {
        Optional<Code> postOpt = postRepository.findById(id);
        if (postOpt.isPresent()) {
            Code post = postOpt.get();
            post.setViews(post.getViews() + 1);
            postRepository.save(post);
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    public Code getPostById(Integer id) {
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public Page<Code> getAllPosts(int page, int limit, String kw, String category) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page - 1, limit,Sort.by(sorts));

        Specification<Code> spec;
        if(category.equals("title")){
            spec = searchTitle(kw);
        }
        else{
            spec = searchUserId(kw);
        }
        return postRepository.findAll(spec,pageable);
    }

    private Specification<Code> searchTitle(String kw) {
        return new Specification<Code>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Code> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.like(q.get("title"), "%" + kw + "%");
            }
        };
    }
    private Specification<Code> searchUserId(String kw) {
        return new Specification<Code>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Code> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join<Code, User> u1 = q.join("user", JoinType.LEFT);
                return cb.like(u1.get("username"), "%" + kw + "%");    // 질문 작성자
            }
        };
    }
}
