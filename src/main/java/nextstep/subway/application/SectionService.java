package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static nextstep.subway.constants.ErrorMessage.NO_SUCH_ELEMENT_EXCEPTION_MSG;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new NoSuchElementException(NO_SUCH_ELEMENT_EXCEPTION_MSG));
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(() -> new NoSuchElementException("존재하지 않는 상행역"));
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(() -> new NoSuchElementException("존재하지 않는 하행역"));
        line.addSection(Section.of(line, upStation, downStation, new Distance(sectionRequest.getDistance())));
        return LineResponse.from(line);
    }
}
