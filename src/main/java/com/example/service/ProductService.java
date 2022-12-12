package com.example.service;

import com.example.dto.ProductDto;
import com.example.entity.Product;
import com.example.repository.ProductRepository;
import com.example.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public Flux<ProductDto> getProducts() {
        return repository
                .findAll()
                .map(AppUtils::entityToDto);
    }

    public Mono<ProductDto> getProduct(String id) {
        return repository.findById(id).map(AppUtils::entityToDto);
    }

    public Flux<ProductDto> getProductInPriceRange(double min, double max) {
        return repository.findByPriceBetween(Range.closed(min, max));
    }

    public Mono<ProductDto> saveProduct(Mono<ProductDto> productDtoMono) {
        System.out.println("Controller Method Called ... ");
        return productDtoMono.map(AppUtils::dtoToEntity)
                .flatMap(repository::insert)
                .map(AppUtils::entityToDto);

    }

    public Mono<ProductDto> updateProduct(Mono<ProductDto> productDtoMono, String id) {
        return repository.findById(id)
                .flatMap(p -> productDtoMono
                        .map(AppUtils::dtoToEntity).doOnNext(e -> e.setId(id))
                )
                .flatMap(repository::save)
                .map(AppUtils::entityToDto);
    }

    public Mono<Void> deleteProduct(String id){
        return repository.deleteById(id);
    }
}
