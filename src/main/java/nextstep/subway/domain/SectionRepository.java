package nextstep.subway.domain;

import nextstep.subway.domain.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {
}