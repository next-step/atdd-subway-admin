package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.CreateLineDto;
import nextstep.subway.dto.DtoConverter;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line register(CreateLineDto dto) {
        Station upStation = findStation(dto.getUpStationId());
        Station downStation = findStation(dto.getDownStationId());
        Section section = new Section(upStation, downStation, dto.getDistance());
        Line line = DtoConverter.toLineEntity(dto);
        line.addSection(section);
        return lineRepository.save(line);
    }

    private Station findStation(long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 역은 존재하지 않습니다."));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }
}
