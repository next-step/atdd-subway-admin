package nextstep.subway.line.domain;

@FunctionalInterface
public interface DistanceFinder {
    int measure(int preSection, int postSection);
}
