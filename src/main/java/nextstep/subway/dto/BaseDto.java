package nextstep.subway.dto;

import java.time.LocalDateTime;

public abstract class BaseDto {
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public BaseDto() {
    }

    public BaseDto(LocalDateTime createdDate, LocalDateTime modifiedDate) {
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
