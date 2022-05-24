package nextstep.subway.application;

import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineRequestDTO;
import nextstep.subway.dto.LineResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class LineService {

    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponseDTO saveLine(LineRequestDTO lineRequestDTO){

        return null;
    }

}
