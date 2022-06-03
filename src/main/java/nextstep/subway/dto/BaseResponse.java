package nextstep.subway.dto;

import java.time.LocalDateTime;

public class BaseResponse {

    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public BaseResponse(LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
