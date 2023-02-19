package com.example.demo.memo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface memoRepository extends JpaRepository<memo, Long> {
}
