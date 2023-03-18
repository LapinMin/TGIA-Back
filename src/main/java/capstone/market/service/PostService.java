package capstone.market.service;

import capstone.market.domain.Post;
import capstone.market.domain.Purchased;
import capstone.market.post_dto.PostForm;
import capstone.market.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Post findPostByPostId(Long post_id) {
        return postRepository.findOne(post_id);
    }

    public void savePost(Post post) {
        postRepository.savePost(post);
    }

    public List<Post> findPostByUserId(String user_id) {
        return postRepository.findByUserId(user_id);
    }
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public List<Purchased> findByadfasf(Long user_id) {
        return postRepository.findBoughtListByUserId(user_id);
    }

    // 3월 18일 추가
    // 게시글 수정
    public void update(PostForm request) {
        Post post = postRepository.findOne(request.getId());
        post.setPost_title(request.getTitle());
        post.setPost_text(request.getContent());
        post.setPrice(request.getPrice());
    }
}
