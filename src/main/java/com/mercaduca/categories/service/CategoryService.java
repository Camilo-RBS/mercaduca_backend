package com.mercaduca.categories.service;

import com.mercaduca.categories.dto.CategoryDTOs;

import java.util.List;
public interface CategoryService {
    CategoryDTOs.CategoryResponse createCategory(CategoryDTOs.CreateCategoryRequest request);
    CategoryDTOs.CategoryResponse updateCategory(Long id, CategoryDTOs.UpdateCategoryRequest request);
    CategoryDTOs.CategoryResponse getCategoryById(Long id);
    List<CategoryDTOs.CategoryResponse> getAllCategories();
    List<CategoryDTOs.CategoryResponse> getRootCategories();
    List<CategoryDTOs.CategoryResponse> getSubcategories(Long parentId);
    void deleteCategory(Long id);
}
