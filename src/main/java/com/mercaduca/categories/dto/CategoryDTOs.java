package com.mercaduca.categories.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
public class CategoryDTOs {
    @Data public static class CreateCategoryRequest {
        @NotBlank(message = "El nombre es requerido") @Size(max = 100) private String name;
        private String description; private String imageUrl; private Long parentId;
    }
    @Data public static class UpdateCategoryRequest {
        @Size(max = 100) private String name; private String description;
        private String imageUrl; private Boolean active;
    }
    @Data public static class CategoryResponse {
        private Long id; private String name; private String description;
        private String imageUrl; private Long parentId; private String parentName;
        private boolean active; private List<CategoryResponse> children; private LocalDateTime createdAt;
    }
}
