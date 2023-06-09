package capstone.market.controller;

import capstone.market.domain.*;

import capstone.market.post_dto.*;


import capstone.market.profile_dto.PostDetailDto;
import capstone.market.profile_dto.PostSellDetailDto;
import capstone.market.profile_dto.SearchFilterDto;
import capstone.market.service.*;
import capstone.market.session.SessionConst;
import capstone.market.session.SessionManager;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * react native 와 연동시 Controller 가 아닌 RestController 로 해야한다!
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    // post 를 작성한 Member 의 PK 를 알아내기 위해 memberService 사용
    private final MemberService memberService;
    private final CategoryService categoryService;
    private final ImageService imageService;
    private final DepartmentService departmentService;
//    private final TogetherViewedService togetherViewedService;
    private final SessionManager sessionManager;
    private final FileService fileService;

    //@@@@@@@@@@@@@@@@@찐 필터링 구현@@@@@@@@@@@@@@@@@@@ 3월 23일
    @PostMapping("/detailSearch")
    public List<PostDetailDto> Search(@RequestBody SearchFilterDto searchFilterDto) {

        List<PostDetailDto> postDetailDtos = postService.SearchFilter(searchFilterDto);
        return postDetailDtos;
    }

    //@@@@@@@@@@@@@@@@@찐 필터링 구현@@@@@@@@@@@@@@@@@@@ 3월 23일
    //@@@@@@@@@@@@@@@@@카테고리로 포스트 필터링@@@@@@@@@@@@@@@@@@@ 3월 17일
    @GetMapping("/category")
    public List<PostListResponse> SearchByCategory(@RequestParam CategoryType category) {

        List<Post> posts = postService.SearchByCategory(category);

        List<PostListResponse> result = posts.stream()
                .map(p -> new PostListResponse(p))
                .collect(Collectors.toList());

        return result;
    }
    //@@@@@@@@@@@@@@@@@카테고리로 포스트 필터링@@@@@@@@@@@@@@@@@@@ 3월 17일


    //@@@@@@@@@@@@@@@@@포스트 제목으로 검색하기 추가@@@@@@@@@@@@@@@@@@@ 3월 15일
    @GetMapping("/search")
    public List<PostListResponse> findByTitleContaining(@RequestParam String keyword) {

        List<Post> posts = postService.findByTitleContaing(keyword);

        List<PostListResponse> result = posts.stream()
                .map(p -> new PostListResponse(p))
                .collect(Collectors.toList());

        return result;
    }

    //@@@@@@@@@@@@@@@@@포스트 제목으로 검색하기 추가@@@@@@@@@@@@@@@@@@@ 3월 15일
    // 3월 18일 추가
    // 게시물을 수정하기 위한 기존 정보 가져온다.
    @GetMapping("post/edit")
    public PostForm updateItemForm(Long post_id) {
        Post post = postService.findPostByPostId(post_id);

        // form 을 업데이트 하는데 이 PostForm 을 보낸다 entity 가 아니라
        PostForm editPost = new PostForm();
//        editPost.setId(post.getPostId());
        editPost.setPrice(post.getPrice());
        editPost.setTitle(post.getPost_title());
        editPost.setContent(post.getPost_text());
        return editPost;
    }

    // 3월 18일 추가
    // 게시글 수정
    @PutMapping("post/edit")
    public PostForm updatePost(@RequestBody PostForm request) {
        postService.update(request);
        return new PostForm(request);
    }

    @DeleteMapping("")

    //@GetMapping("/post/list")
    public List<Post> postListV3(HttpServletRequest request) {
        log.info("@GetMapping(\"/post/list\")");
        // 세션 관리자에 저장된 회원 정보 조회
        while (!SessionConst.POST_ENDED) {
            log.info("WHILE");
        }

        log.info("BREAK");

        HttpSession session = request.getSession(false);

        if (session == null) {
            log.info("session error");
        }

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        if (member == null) {
            log.info("login error");
        }

        log.info("hello world" + member.getUser_id());
        List<Post> posts = postService.findPostByUserId(member.getUser_id());
        return posts;
    }

    // @GetMapping("/post/list") // 2.17
    // + 이미지 정보 추가 3월 17일
    public List<PostListResponse> postListV4(String user_id) {
        log.info("@GetMapping(\"/post/list\")");
//        while(!SessionConst.POST_ENDED) {
//            log.info("WHILE");
//        }
        log.info("BREAK");
        log.info("user_id = {}", user_id);

        List<Post> posts = postService.findPostByUserId(user_id);

        List<PostListResponse> result = posts.stream()
                .map(p -> new PostListResponse(p))
                .collect(Collectors.toList());

        return result;
    }

    //@@@@@@@@@@@@@@@@@@@@@@내가 올린 게시글 == 내가 판매중인 게시글 @@@@@@@@@@@@
    @GetMapping("/post/my_list") // 2.17 -> 판매중인 목록
    @Nullable
    public List<PostDetailDto> findMyPostList(@RequestParam Long userId) {

        Member findMember = memberService.findOne(userId);

        List<Post> posts = postService.findPostByUserId(findMember.getUser_id());


        List<Post> myListPosts = new ArrayList<>();

        for (Post post : posts) {

            if (post.getPurchased() == null) {
                myListPosts.add(postService.findPostByPostId(post.getPostId()));
            }
        }
        List<PostDetailDto> result = myListPosts.stream()
                .map(p -> new PostDetailDto(p))
                .collect(Collectors.toList());

        return result;
    }
    //@@@@@@@@@@@@@@@@@@@@@@내가 올린 게시글 == 내가 판매중인 게시글 @@@@@@@@@@@@

    //@@@@@@@@@@@@@@@@@@@@@@내가 판매 완료한 게시글 @@@@@@@@@@@@
    @GetMapping("/post/my_SellList") //
    public List<PostSellDetailDto> findMySellPostList(@RequestParam Long userId) {

        Member findMember = memberService.findOne(userId);

        List<Post> posts = postService.findPostByUserId(findMember.getUser_id());


        List<Post> mySellListPosts = new ArrayList<>();

        for (Post post : posts) {

            if (post.getPurchased() != null) {
                mySellListPosts.add(postService.findPostByPostId(post.getPostId()));
            }
        }


        List<PostSellDetailDto> result = mySellListPosts.stream()
                .map(p -> new PostSellDetailDto(p))
                .collect(Collectors.toList());

        return result;
    }


    //@@@@@@@@@@@@@@@@@@@@@@내가 판매 완료한 게시글 @@@@@@@@@@@@

    //PostDetailResponse 이걸로 추후 바꿔야함 PostListResponse이거 대신에
//    @GetMapping("/post/list") // 2.17
//    public List<PostListResponse> postListV5(HttpServletRequest request) {
//
//        HttpSession session = request.getSession(false);
//
//        List<Post> posts = postService.findAll();
//
//        List<PostListResponse> result = posts.stream()
//                .map(p -> new PostListResponse(p))
//                .collect(Collectors.toList());
//
//        return result;
//    }

    @GetMapping("/post/list_all") // 2.17
    public List<PostListResponse> postListV6() {

        List<Post> posts = postService.findAll();

        List<PostListResponse> result = posts.stream()
                .map(p -> new PostListResponse(p))
                .collect(Collectors.toList());

        return result;
    }

    // 3월 17일 프론트와 연동 시 Image 테이블과 Post 테이블 매핑 문제 해결
    @PostMapping("/post/insert")
    public void postAdd(@RequestBody AddPostRequest request) {
        Post post = new Post();

        log.info("request_info = {}", request.getUser_id());
        log.info("request_info.title = {}", request.getTitle());
        post.setWho_posted(memberService.findOne(request.getUser_id()));
        post.setPost_title(request.getTitle());
        post.setPost_text(request.getContent());
        post.setPrice(request.getPrice());
        Category category = new Category();
        categoryService.UpdateCategory(category, request.getCategory());
        post.setCategory(category);
        Department department = new Department();
        departmentService.UpdateDepartment(department, request.getDepartment());
        post.setDepartment(department);

        List<Image> images = imageService.findImages(request.getImages());
        post.setImages(images);
//        post.setImage(fileService.findImageFilename(request.image_file_name));
        postService.savePost(post);
    }

    @PostMapping("/post/insert_image_test")
    public void postAddImage(@RequestBody PostRequestImage request) {
        Post post = new Post();

        log.info("request_info = {}", request.getUser_id());
        log.info("request_info.title = {}", request.getTitle());
        post.setWho_posted(memberService.findOne(request.getUser_id()));
        post.setPost_title(request.getTitle());
        post.setPost_text(request.getContent());
        post.setPrice(request.getPrice());
//        Category category = new Category();
//        categoryService.UpdateCategory(category,request.getCategory());
//        post.setCategory(category);
//        post.setImage(fileService.findImageFilename(request.image_file_name));
        List<Image> images = imageService.findImages(request.getImages());
        post.setImages(images);
        postService.savePost(post);
    }

    // 게시물 상세 구현 2월 21일
    // + 가격 추가 3월 3일
    @GetMapping("/post/details")
    public PostDetailResponse postDetails(@RequestParam Long postId, @RequestParam Long userId) {
        Post post = postService.findPostByPostId(postId);

        postService.increaseViewCount(postId,userId);
//        List<Post> posts = togetherViewedService.add(post, postId);

        return new PostDetailResponse(post);
    }
    // hhhhhh
    @GetMapping("/post/get_info")
    public PostDetailResponse postInfo(Long post_id) {
        Post post = postService.findPostByPostId(post_id);

//        postService.increaseViewCount(postId, userId);

        return new PostDetailResponse(post);
    }

    //테스트용@@@@@@2
    @GetMapping("/post/all")
    public List<PostDetailDto> postDetails2() {
        List<Post> all = postService.findAll();


        List<Post> myListPosts = new ArrayList<>();

        for (Post post : all) {

            if (post.getPurchased() == null) {
                myListPosts.add(postService.findPostByPostId(post.getPostId()));
            }
        }
        List<PostDetailDto> result = myListPosts.stream()
                .map(p -> new PostDetailDto(p))
                .collect(Collectors.toList());


        return result;

    }
    //테스트용@@@@@@2

    // 찜 목록 구현 2월 21일
//    @PostMapping("/post/liked")
//    public void postLike(Long post_id, HttpServletRequest request) {
//        HttpSession session = request.getSession();
//        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
//
//        memberService.addLiked(post_id, member.getId());
//    }

//    @GetMapping("/post/liked")
//    public List<PostLikedResponse> postLikedList() {
//
//    }

    // 게시물 상세 화면을 위한 dto

//    @Data
//    static class PostDetailResponse {
//        private Long post_id;
//        private String title;
//        private String user_id;
//        private CategoryType category;
//        private DepartmentType department;
//        private String text;
//        private Integer price;
//
//        private Integer views;
//        private Integer likes;
//        private LocationType locationType;
//        private String location_text;
//
//        public PostDetailResponse(Post post) {
//            this.post_id = post.getPostId();
//            this.title = post.getPost_title();
//            this.user_id = post.getWho_posted().getUser_id();
//            this.category = post.getCategory().getCategory_type();
//            this.text = post.getPost_text();
//            this.price = post.getPrice();
//            this.department = post.getDepartment().getDepartmentType();
//            this.views = post.getViews();
//            this.likes = post.getLikes();
//            this.locationType = post.getLocationType();
//            this.location_text = post.getLocation_text();
//        }
//    }


//    static class PostLikedResponse {
//        private
//    }

    // 게시글 추가를 위한 FORM 객체

    /**
     * params: {
     *   title: title,
     *   user: id,
     *   category: category,
     *   content: content,
     *   date: time,
     *  },
     */

//    @Data
//    static class AddPostRequest {
//        private String title;
//        private Long user_id;
//        private CategoryType category;
//
//        private DepartmentType department;
//        private String content;
////        private String time;
//        private Integer price;
//
////        private String image_file_name;
//    }
//    //
//    @Data
//    static class ForUserId {
//        private String user_id;
//    }

//    @Data
//    static class PostListResponse {
////        private Long id;
//        private String title;
//        // private String user;
//        private CategoryType category;
////        private String content;
//        private Integer price;
//        private String image_filename;
//
//
//        public PostListResponse(Post post) {
//            title = post.getPost_title();
//            // user = post.getWho_posted().getUser_id();
//            category = post.getCategory().getCategory_type();
////            content = post.getPost_text();
//            price = post.getPrice();
//            //image_filename = post.getImage().getImageFilename();
//        }
//    }
//    @GetMapping("/post/list")
//    public PostListResponse postList() {
//        postService.findPostByUserId();
//    }
//
//    public Member findMe() {
//
//    }

}
