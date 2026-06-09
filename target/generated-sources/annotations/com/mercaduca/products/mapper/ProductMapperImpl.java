package com.mercaduca.products.mapper;

import com.mercaduca.products.dto.ProductDTOs;
import com.mercaduca.products.entity.Category;
import com.mercaduca.products.entity.Product;
import com.mercaduca.users.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-08T19:19:15-0600",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Microsoft)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toEntity(ProductDTOs.CreateProductRequest request) {
        if ( request == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.title( request.getTitle() );
        product.description( request.getDescription() );
        product.price( request.getPrice() );
        product.stock( request.getStock() );
        List<String> list = request.getImages();
        if ( list != null ) {
            product.images( new ArrayList<String>( list ) );
        }
        product.weightKg( request.getWeightKg() );

        return product.build();
    }

    @Override
    public ProductDTOs.ProductResponse toResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDTOs.ProductResponse productResponse = new ProductDTOs.ProductResponse();

        productResponse.setCategoryId( productCategoryId( product ) );
        productResponse.setCategoryName( productCategoryName( product ) );
        productResponse.setSellerId( productSellerId( product ) );
        productResponse.setCreatedAt( product.getCreatedAt() );
        productResponse.setUpdatedAt( product.getUpdatedAt() );
        productResponse.setId( product.getId() );
        productResponse.setTitle( product.getTitle() );
        productResponse.setDescription( product.getDescription() );
        productResponse.setPrice( product.getPrice() );
        productResponse.setOriginalPrice( product.getOriginalPrice() );
        productResponse.setStock( product.getStock() );
        List<String> list = product.getImages();
        if ( list != null ) {
            productResponse.setImages( new ArrayList<String>( list ) );
        }
        productResponse.setStatus( product.getStatus() );
        productResponse.setFeatured( product.isFeatured() );
        productResponse.setAverageRating( product.getAverageRating() );
        productResponse.setTotalReviews( product.getTotalReviews() );
        productResponse.setTotalSold( product.getTotalSold() );
        productResponse.setViewCount( product.getViewCount() );
        productResponse.setSku( product.getSku() );

        productResponse.setSellerName( product.getSeller().getFirstName() + " " + product.getSeller().getLastName() );

        return productResponse;
    }

    @Override
    public void updateEntityFromRequest(ProductDTOs.UpdateProductRequest request, Product product) {
        if ( request == null ) {
            return;
        }

        if ( request.getTitle() != null ) {
            product.setTitle( request.getTitle() );
        }
        if ( request.getDescription() != null ) {
            product.setDescription( request.getDescription() );
        }
        if ( request.getPrice() != null ) {
            product.setPrice( request.getPrice() );
        }
        if ( request.getStock() != null ) {
            product.setStock( request.getStock() );
        }
        if ( product.getImages() != null ) {
            List<String> list = request.getImages();
            if ( list != null ) {
                product.getImages().clear();
                product.getImages().addAll( list );
            }
        }
        else {
            List<String> list = request.getImages();
            if ( list != null ) {
                product.setImages( new ArrayList<String>( list ) );
            }
        }
        if ( request.getStatus() != null ) {
            product.setStatus( request.getStatus() );
        }
        if ( request.getFeatured() != null ) {
            product.setFeatured( request.getFeatured() );
        }
        if ( request.getWeightKg() != null ) {
            product.setWeightKg( request.getWeightKg() );
        }
    }

    private Long productCategoryId(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        Long id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String productCategoryName(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        String name = category.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long productSellerId(Product product) {
        if ( product == null ) {
            return null;
        }
        User seller = product.getSeller();
        if ( seller == null ) {
            return null;
        }
        Long id = seller.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
