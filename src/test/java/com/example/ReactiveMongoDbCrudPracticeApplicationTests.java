package com.example;

import com.example.controller.ProductController;
import com.example.dto.ProductDto;
import com.example.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(ProductController.class)
class ReactiveMongoDbCrudPracticeApplicationTests {

	@Autowired
	private WebTestClient testClient;

	@MockBean
	private ProductService service;


	@Test
	public void addProductTest(){
		Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("101", "Charger", 5, 1200.00));
		when(service.saveProduct(productDtoMono)).thenReturn(productDtoMono);

		testClient.post().uri("/products/save")
				.body(Mono.just(productDtoMono), ProductDto.class)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void getProductsTest(){
		Flux<ProductDto> dtoFlux = Flux.just(
				new ProductDto("101", "Charger", 5, 1200.00),
				new ProductDto("102", "TWS", 4, 1500.00),
				new ProductDto("103", "Earphone", 3, 2000.00)
		);
		when(service.getProducts()).thenReturn(dtoFlux);

		Flux<ProductDto> responseBody = testClient.get().uri("/products")
				.exchange()
				.expectStatus().isOk()
				.returnResult(ProductDto.class)
				.getResponseBody();

		StepVerifier.create(responseBody)
				.expectSubscription()
				.expectNext(new ProductDto("101", "Charger", 5, 1200.00))
				.expectNext(new ProductDto("102", "TWS", 4, 1500.00))
				.expectNext(new ProductDto("103", "Earphone", 3, 2000.00))
				.verifyComplete();
	}

	@Test
	public void getProductTest(){
		Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("101", "Charger", 5, 1200.00));
		when(service.getProduct(any())).thenReturn(productDtoMono);

		Flux<ProductDto> responseBody = testClient.get().uri("/products/101")
				.exchange()
				.expectStatus().isOk()
				.returnResult(ProductDto.class)
				.getResponseBody();

		StepVerifier.create(responseBody)
				.expectSubscription()
				.expectNextMatches(p -> p.getName().equals("Charger"))
				.verifyComplete();
	}


	@Test
	public void updateProductTest(){
		Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("101", "Charger", 5, 1200.00));
		when(service.updateProduct(productDtoMono, "101")).thenReturn(productDtoMono);

		testClient.put().uri("/products/update/101")
				.body(Mono.just(productDtoMono), ProductDto.class)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void deleteProductTest(){
		given(service.deleteProduct(any())).willReturn(Mono.empty());

		testClient.delete().uri("/products/delete/101")
				.exchange()
				.expectStatus().isOk();
	}

}
