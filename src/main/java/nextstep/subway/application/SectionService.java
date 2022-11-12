package nextstep.subway.application;

import nextstep.subway.constant.ErrorCode;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public SectionService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.상행종착역은_비어있을_수_없음.getErrorMessage()));
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.하행종착역은_비어있을_수_없음.getErrorMessage()));
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.노선_정보가_없음.getErrorMessage()));
        line.addSection(Section.of(upStation, downStation, line, sectionRequest.getDistance()));
        return LineResponse.from(line);
    }
}
