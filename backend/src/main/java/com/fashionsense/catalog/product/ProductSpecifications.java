package com.fashionsense.catalog.product;

import com.fashionsense.catalog.variant.ProductVariant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> matches(
            ProductSearchCriteria criteria
    ) {

        return (root, query, builder) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    builder.isTrue(root.get("active"))
            );

            if (hasText(criteria.brand())) {
                predicates.add(
                        builder.equal(
                                builder.lower(
                                        root.get("brand").get("slug")
                                ),
                                criteria.brand().toLowerCase()
                        )
                );
            }

            if (hasText(criteria.category())) {
                predicates.add(
                        builder.equal(
                                builder.lower(
                                        root.get("category").get("slug")
                                ),
                                criteria.category().toLowerCase()
                        )
                );
            }

            if (criteria.minPrice() != null) {
                predicates.add(
                        builder.greaterThanOrEqualTo(
                                root.get("basePrice"),
                                criteria.minPrice()
                        )
                );
            }

            if (criteria.maxPrice() != null) {
                predicates.add(
                        builder.lessThanOrEqualTo(
                                root.get("basePrice"),
                                criteria.maxPrice()
                        )
                );
            }

            if (hasText(criteria.occasion())) {

                Join<Object, Object> occasionJoin =
                        root.join("occasions", JoinType.INNER);

                predicates.add(
                        builder.equal(
                                builder.lower(
                                        occasionJoin.get("slug")
                                ),
                                criteria.occasion().toLowerCase()
                        )
                );

                query.distinct(true);
            }

            boolean hasVariantFilter =
                    hasText(criteria.size())
                            || hasText(criteria.color())
                            || hasText(criteria.style())
                            || hasText(criteria.material());

            if (hasVariantFilter) {

                Subquery<Long> variantSubquery =
                        query.subquery(Long.class);

                Root<ProductVariant> variant =
                        variantSubquery.from(ProductVariant.class);

                List<Predicate> variantPredicates =
                        new ArrayList<>();

                variantPredicates.add(
                        builder.equal(
                                variant.get("product").get("id"),
                                root.get("id")
                        )
                );

                variantPredicates.add(
                        builder.isTrue(
                                variant.get("active")
                        )
                );

                if (hasText(criteria.size())) {
                    variantPredicates.add(
                            builder.equal(
                                    builder.lower(
                                            variant.get("size")
                                    ),
                                    criteria.size().toLowerCase()
                            )
                    );
                }

                if (hasText(criteria.color())) {
                    variantPredicates.add(
                            builder.equal(
                                    builder.lower(
                                            variant.get("color")
                                    ),
                                    criteria.color().toLowerCase()
                            )
                    );
                }

                if (hasText(criteria.style())) {
                    variantPredicates.add(
                            builder.equal(
                                    builder.lower(
                                            variant.get("style")
                                    ),
                                    criteria.style().toLowerCase()
                            )
                    );
                }

                if (hasText(criteria.material())) {
                    variantPredicates.add(
                            builder.equal(
                                    builder.lower(
                                            variant.get("material")
                                    ),
                                    criteria.material().toLowerCase()
                            )
                    );
                }

                variantSubquery
                        .select(variant.get("id"))
                        .where(
                                variantPredicates.toArray(
                                        new Predicate[0]
                                )
                        );

                predicates.add(
                        builder.exists(variantSubquery)
                );
            }

            return builder.and(
                    predicates.toArray(
                            new Predicate[0]
                    )
            );
        };
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}