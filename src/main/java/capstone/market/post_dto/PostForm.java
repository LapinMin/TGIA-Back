package capstone.market.post_dto;

import capstone.market.domain.DepartmentType;
import capstone.market.domain.LocationType;
import lombok.AllArgsConstructor;
import lombok.Data;

// 3월 18일 수정
// 게시글 수정
@Data
public class PostForm {
    private Long id;
//    게시글 수정시 게시물을 올린 사람의 정보는 수정할 일은 없을 것 같다.
//    private Long who_posted;
    private Integer price;
    private String title;
    private String content;
    private DepartmentType departmentType;

    private Long image_id;
    private LocationType locationType;
    private String location_text;
    private String item_name; // 상품명

    public PostForm() {
    }

    public PostForm(PostForm postForm) {
        this.price = postForm.getPrice();
        this.title = postForm.getTitle();
        this.content = postForm.getContent();
        this.departmentType = postForm.getDepartmentType();
        this.locationType = postForm.getLocationType();
        this.location_text = postForm.getLocation_text();
        this.item_name = postForm.getItem_name();
    }
}
