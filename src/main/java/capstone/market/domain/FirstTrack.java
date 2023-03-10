package capstone.market.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class FirstTrack {
    @Id @GeneratedValue
    private Long track_id;
    //private String track_name;
    @Enumerated(EnumType.STRING)
    private TrackType first_track;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department departMent;

}
