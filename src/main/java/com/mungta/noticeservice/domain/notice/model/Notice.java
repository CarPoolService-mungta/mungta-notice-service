package com.mungta.noticeservice.domain.notice.model;

import com.mungta.noticeservice.domain.BaseEntity;
import com.mungta.noticeservice.domain.notice.dto.response.NoticeListResponse;
import com.mungta.noticeservice.domain.notice.model.enums.DisplayStatus;
import com.mungta.noticeservice.domain.notice.model.vo.NoticeContents;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String adminId;

    @Embedded
    private NoticeContents notice;

    @Enumerated(EnumType.STRING)
    private DisplayStatus displayStatus;

    public static Notice of(String adminId, NoticeContents notice){
        return Notice.builder()
                .adminId(adminId)
                .notice(notice)
                .displayStatus(DisplayStatus.SHOW)
                .build();
    }

    public NoticeListResponse convertListView(){
        return NoticeListResponse.of(
                id,
                notice.getTitle(),
                Objects.isNull(getCreatedDate()) ? null : getCreatedDate().toLocalDate()
        );
    }
}
