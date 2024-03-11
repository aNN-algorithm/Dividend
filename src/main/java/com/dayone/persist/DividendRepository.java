package com.dayone.persist;

import com.dayone.persist.entity.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
    List<DividendEntity> findAllByCompanyId(Long id);
    // Dividend 테이블에 있는 company id와 Id의 일치하는 데이터를 리스트로 담아 반환

    boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime date);
}
