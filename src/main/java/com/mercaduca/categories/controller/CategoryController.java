package com.mercaduca.categories.controller;

import com.mercaduca.categories.dto.CategoryDTOs;
import com.mercaduca.categories.service.CategoryService;
import com.mercaduca.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController @RequestMapping("/categories") @RequiredArgsConstructor
@Tag(name = "Categorías", description = "Gestión de categorías de productos")
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping @Operation(summary = "Todas las categorías")
    public ResponseEntity<ApiResponse<List<CategoryDTOs.CategoryResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories())); }
    @GetMapping("/root") @Operation(summary = "Categorías raíz")
    public ResponseEntity<ApiResponse<List<CategoryDTOs.CategoryResponse>>> getRoots() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getRootCategories())); }
    @GetMapping("/{id}") @Operation(summary = "Categoría por ID")
    public ResponseEntity<ApiResponse<CategoryDTOs.CategoryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryById(id))); }
    @GetMapping("/{id}/subcategories") @Operation(summary = "Subcategorías")
    public ResponseEntity<ApiResponse<List<CategoryDTOs.CategoryResponse>>> getSubs(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getSubcategories(id))); }
    @PostMapping @PreAuthorize("hasRole('ADMIN')") @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear categoría (Admin)")
    public ResponseEntity<ApiResponse<CategoryDTOs.CategoryResponse>> create(@Valid @RequestBody CategoryDTOs.CreateCategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Categoría creada", categoryService.createCategory(req))); }
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar categoría (Admin)")
    public ResponseEntity<ApiResponse<CategoryDTOs.CategoryResponse>> update(@PathVariable Long id, @RequestBody CategoryDTOs.UpdateCategoryRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Actualizada", categoryService.updateCategory(id, req))); }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Desactivar categoría (Admin)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id); return ResponseEntity.ok(ApiResponse.success("Desactivada", null)); }
}
