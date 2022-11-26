package nextstep.subway.application;

import nextstep.subway.domain.line.Distance;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.SectionCreateRequest;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

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
    public SectionResponse addSection(Long lineId, SectionCreateRequest request) {
        Line line = lineRepository.findByIdWithSections(lineId)
                .orElseThrow(EntityNotFoundException::new);
        Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(EntityNotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(EntityNotFoundException::new);

        line.addSection(upStation, downStation, request.getDistance());
        return SectionResponse.of(upStation, downStation, request.getDistance());
    }
}
