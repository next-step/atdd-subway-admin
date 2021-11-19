package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.time.LocalDateTime;

public class UpdateLineResponseDto {
    private final LocalDateTime modifiedDate;

    private UpdateLineResponseDto(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public static UpdateLineResponseDto of(Line line) {
        return new UpdateLineResponseDto(line.getModifiedDate());
    }
}
