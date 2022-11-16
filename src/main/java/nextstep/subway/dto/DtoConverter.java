package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class DtoConverter {

    public static Line toLineEntity(CreateLineDto dto) {
        return new Line(dto.getName(), dto.getColor());
    }

    public static Line toLineEntity(UpdateLineDto dto) {
        return new Line(dto.getName(), dto.getColor());
    }
}
