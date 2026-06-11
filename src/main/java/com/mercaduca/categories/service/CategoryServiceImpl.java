package com.mercaduca.categories.service;
import com.mercaduca.categories.dto.CategoryDTOs;
import com.mercaduca.exceptions.custom.BusinessException;
import com.mercaduca.exceptions.custom.ResourceNotFoundException;
import com.mercaduca.products.entity.Category;
import com.mercaduca.products.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override @Transactional
    public CategoryDTOs.CategoryResponse createCategory(CategoryDTOs.CreateCategoryRequest req) {
        if (categoryRepository.existsByName(req.getName()))
            throw new BusinessException("Ya existe una categoría con ese nombre");
        Category c = Category.builder().name(req.getName()).description(req.getDescription())
                .imageUrl(req.getImageUrl()).active(true).build();
        if (req.getParentId() != null) c.setParent(categoryRepository.findById(req.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", req.getParentId())));
        return toResponse(categoryRepository.save(c));
    }
    @Override @Transactional
    public CategoryDTOs.CategoryResponse updateCategory(Long id, CategoryDTOs.UpdateCategoryRequest req) {
        Category c = findById(id);
        if (req.getName() != null) c.setName(req.getName());
        if (req.getDescription() != null) c.setDescription(req.getDescription());
        if (req.getImageUrl() != null) c.setImageUrl(req.getImageUrl());
        if (req.getActive() != null) c.setActive(req.getActive());
        return toResponse(categoryRepository.save(c));
    }
    @Override @Transactional(readOnly = true)
    public CategoryDTOs.CategoryResponse getCategoryById(Long id) { return toResponse(findById(id)); }
    @Override @Transactional(readOnly = true)
    public List<CategoryDTOs.CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList(); }
    @Override @Transactional(readOnly = true)
    public List<CategoryDTOs.CategoryResponse> getRootCategories() {
        return categoryRepository.findByParentIsNull().stream().map(this::toResponse).toList(); }
    @Override @Transactional(readOnly = true)
    public List<CategoryDTOs.CategoryResponse> getSubcategories(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream().map(this::toResponse).toList(); }
    @Override @Transactional
    public void deleteCategory(Long id) { Category c = findById(id); c.setActive(false); categoryRepository.save(c); }
    private Category findById(Long id) { return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", id)); }
    private CategoryDTOs.CategoryResponse toResponse(Category c) {
        CategoryDTOs.CategoryResponse r = new CategoryDTOs.CategoryResponse();
        r.setId(c.getId()); r.setName(c.getName()); r.setDescription(c.getDescription());
        r.setImageUrl(c.getImageUrl()); r.setActive(c.isActive()); r.setCreatedAt(c.getCreatedAt());
        if (c.getParent() != null) { r.setParentId(c.getParent().getId()); r.setParentName(c.getParent().getName()); }
        if (c.getChildren() != null && !c.getChildren().isEmpty())
            r.setChildren(c.getChildren().stream().map(this::toResponse).toList());
        return r;
    }
}
