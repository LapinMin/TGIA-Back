package capstone.market.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private Integer mannerscore = 0;
    private String user_id;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "track_id")
//    private Track track;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "a_track_id")
    private FirstTrack firstTrack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b_track_id")
    private SecondTrack secondTrack;

    private String username;

    @OneToMany(mappedBy = "memberB", fetch = FetchType.LAZY)
    private List<ChatRoom> chatRoom = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Favorite> favorites = new ArrayList<>();

    // 3월 17일 이미지 테이블 매핑 추가
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    public Member() {
    }

    public Member(String user_id) {
        this.user_id = user_id;
    }
}