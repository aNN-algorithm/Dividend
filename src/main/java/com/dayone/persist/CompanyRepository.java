package com.dayone.persist;

import com.dayone.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    // 존재 여부를 boolean 값으로
    boolean existsByTicker(String ticker);

    Optional<CompanyEntity> findByName(String name);

    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);
    // 찾기 / 이름으로 / 시작하는 글자의 / 대소문자 무시 / 반환은 Company 테이블의 List
}
