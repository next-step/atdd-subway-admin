package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.domain.Line;

public class LineResponsesDTO {
    List<LineResponseDTO> lineResponsesDTO;

    public LineResponsesDTO(List<LineResponseDTO> lineResponsesDTO) {
        this.lineResponsesDTO = lineResponsesDTO;
    }

    public static LineResponsesDTO of(List<Line> lines) {
        List<LineResponseDTO> lineResponsesDTO = new ArrayList<>();
        for (Line line : lines) {
            lineResponsesDTO.add(LineResponseDTO.of(line));
        }
        return new LineResponsesDTO(lineResponsesDTO);
    }

    public List<LineResponseDTO> getLineResponses() {
        return lineResponsesDTO;
    }
}
