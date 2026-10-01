package com.fashionsense.catalog.occasion;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/occasions")
public class OccasionController {

    private final OccasionService occasionService;

    public OccasionController(OccasionService occasionService) {
        this.occasionService = occasionService;
    }

    @GetMapping
    public List<OccasionResponse> getAllOccasions() {
        return occasionService.getAllOccasions()
                .stream()
                .map(OccasionResponse::from)
                .toList();
    }

    @GetMapping("/{slug}")
    public OccasionResponse getOccasionBySlug(
            @PathVariable String slug
    ) {
        return OccasionResponse.from(
                occasionService.getOccasionBySlug(slug)
        );
    }
}