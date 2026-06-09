package com.mercaduca.products.repository;

import com.mercaduca.products.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    List<Category> findByParentIsNull();
    List<Category> findByParentId(Long parentId);
    boolean existsByName(String name);
}
