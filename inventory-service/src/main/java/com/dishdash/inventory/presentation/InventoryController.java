package com.dishdash.inventory.presentation;

import com.dishdash.inventory.application.dto.AddStockRequest;
import com.dishdash.inventory.application.dto.InventoryResponse;
import com.dishdash.inventory.application.dto.ReserveStockRequest;
import com.dishdash.inventory.application.usecase.AddStockUseCase;
import com.dishdash.inventory.application.usecase.ReserveStockUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

  private final AddStockUseCase addStockUseCase;
  private final ReserveStockUseCase reserveStockUseCase;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<InventoryResponse> addStock(@Valid @RequestBody AddStockRequest request) {

    return addStockUseCase.execute(request);
  }

  @PostMapping("/reserve")
  public Mono<InventoryResponse> reserve(@Valid @RequestBody ReserveStockRequest request) {

    return reserveStockUseCase.execute(request);
  }
}
