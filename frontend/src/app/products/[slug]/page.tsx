import Link from "next/link";

import ProductPurchasePanel from "@/components/ProductPurchasePanel";
import ProductWishlistButton from "@/components/ProductWishlistButton";

import { apiGet } from "@/lib/api";

import type {
  ProductDetailResponse,
} from "@/types/product-detail";

interface ProductPageProps {
  params: Promise<{
    slug: string;
  }>;
}

export default async function ProductPage({
  params,
}: ProductPageProps) {
  const { slug } = await params;

  let details:
    | ProductDetailResponse
    | null = null;

  try {
    details =
      await apiGet<ProductDetailResponse>(
        `/api/products/${slug}/details`
      );
  } catch (error) {
    console.error(
      "Failed to load product details:",
      error
    );
  }

  if (!details) {
    return (
      <main className="min-h-screen bg-white px-6 py-20 text-gray-900">
        <div className="mx-auto max-w-4xl">
          <Link
            href="/"
            className="font-semibold text-pink-600 hover:text-pink-700"
          >
            ← Back to Fashion Sense
          </Link>

          <div className="mt-10 rounded-2xl border border-red-200 bg-red-50 p-8">
            <h1 className="text-2xl font-bold">
              Product could not be loaded
            </h1>

            <p className="mt-2 text-red-700">
              Make sure the Spring Boot backend is running on port 8080.
            </p>
          </div>
        </div>
      </main>
    );
  }

  const {
    product,
    variants,
    images,
  } = details;

  const sortedImages = [
    ...images,
  ].sort(
    (a, b) =>
      a.displayOrder -
      b.displayOrder
  );

  const primaryImage =
    sortedImages.find(
      (image) =>
        image.primaryImage
    ) ?? sortedImages[0];

  return (
    <main className="min-h-screen bg-white text-gray-900">
      <div className="bg-gray-950 px-4 py-2 text-center text-sm text-white">
        Free standard shipping on orders over $75
      </div>

      <header className="border-b border-gray-200 bg-white">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-5">
          <Link
            href="/"
            className="text-2xl font-bold tracking-tight"
          >
            Fashion
            <span className="text-pink-600">
              Sense
            </span>
          </Link>

          <nav className="flex items-center gap-6 text-sm font-medium">
            <Link
              href="/"
              className="transition hover:text-pink-600"
            >
              Shop
            </Link>

            <Link
              href="/wishlist"
              className="transition hover:text-pink-600"
            >
              Wishlist
            </Link>

            <Link
              href="/cart"
              className="transition hover:text-pink-600"
            >
              Cart
            </Link>

            <Link
              href="/orders"
              className="transition hover:text-pink-600"
            >
              Orders
            </Link>
          </nav>
        </div>
      </header>

      <section className="mx-auto max-w-7xl px-6 py-12">
        <Link
          href="/"
          className="text-sm font-semibold text-gray-500 transition hover:text-pink-600"
        >
          ← Back to products
        </Link>

        <div className="mt-8 grid gap-12 lg:grid-cols-2">
          {/* Product image area */}
          <div>
            <div className="flex aspect-[4/5] items-center justify-center overflow-hidden rounded-3xl bg-gradient-to-br from-gray-100 to-gray-200">
              {primaryImage ? (
                <div className="px-8 text-center">
                  <p className="text-xl font-bold text-gray-700">
                    {product.name}
                  </p>

                  <p className="mt-2 text-sm text-gray-500">
                    {primaryImage.altText}
                  </p>

                  <p className="mt-6 text-xs text-gray-400">
                    Product image placeholder
                  </p>
                </div>
              ) : (
                <p className="font-semibold text-gray-500">
                  No product image available
                </p>
              )}
            </div>

            {sortedImages.length >
              1 && (
              <div className="mt-4 grid grid-cols-2 gap-4">
                {sortedImages.map(
                  (image) => (
                    <div
                      key={image.id}
                      className="flex aspect-square items-center justify-center rounded-xl border border-gray-200 bg-gray-100 p-4 text-center"
                    >
                      <div>
                        <p className="text-sm font-semibold text-gray-600">
                          {image.primaryImage
                            ? "Front View"
                            : "Back View"}
                        </p>

                        <p className="mt-2 text-xs text-gray-400">
                          {image.altText}
                        </p>
                      </div>
                    </div>
                  )
                )}
              </div>
            )}
          </div>

          {/* Product information */}
          <div>
            <p className="text-sm font-bold uppercase tracking-[0.2em] text-pink-600">
              {product.brandName}
            </p>

            <h1 className="mt-3 text-4xl font-bold tracking-tight md:text-5xl">
              {product.name}
            </h1>

            <p className="mt-3 text-gray-500">
              {product.categoryName}
            </p>

            <p className="mt-6 text-3xl font-bold">
              $
              {Number(
                product.basePrice
              ).toFixed(2)}
            </p>

            <p className="mt-6 max-w-xl leading-7 text-gray-600">
              {product.description}
            </p>

            {/* Occasion tags */}
            <div className="mt-8">
              <h2 className="font-bold">
                Perfect for
              </h2>

              <div className="mt-3 flex flex-wrap gap-2">
                {product.occasions.map(
                  (occasion) => (
                    <span
                      key={occasion}
                      className="rounded-full bg-pink-50 px-4 py-2 text-sm font-semibold capitalize text-pink-700"
                    >
                      {occasion}
                    </span>
                  )
                )}
              </div>
            </div>

            {/* Wishlist */}
            <ProductWishlistButton
              productSlug={
                product.slug
              }
            />

            {/* Variant and cart */}
            <ProductPurchasePanel
              variants={
                variants
              }
              basePrice={
                product.basePrice
              }
            />

            {/* Shipping */}
            <div className="mt-6 rounded-xl bg-gray-50 p-5 text-sm text-gray-600">
              <p className="font-semibold text-gray-900">
                Shipping
              </p>

              <p className="mt-2">
                Standard shipping:
                3–5 days
              </p>

              <p className="mt-1">
                Free Standard shipping
                on orders over $75.
              </p>

              <p className="mt-1">
                Express shipping:
                1–2 days • $14.99
              </p>
            </div>

            <div className="mt-4 rounded-xl border border-gray-200 p-5 text-sm text-gray-500">
              Fashion Sense displays
              availability without
              exposing exact remaining
              stock quantity.
            </div>
          </div>
        </div>
      </section>

      <footer className="mt-16 border-t border-gray-200 bg-gray-950">
        <div className="mx-auto max-w-7xl px-6 py-10 text-sm text-gray-400">
          © 2026 Fashion Sense.
          Full-stack ecommerce
          platform.
        </div>
      </footer>
    </main>
  );
}