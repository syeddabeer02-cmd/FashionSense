import { apiGet } from "@/lib/api";
import type { ProductPage } from "@/types/product";

export const dynamic = "force-dynamic";

export default async function Home() {
  const categories = ["Men", "Women", "Kids", "Accessories"];

  const occasions = [
    "Wedding",
    "Party",
    "Office",
    "Casual",
    "Festive",
    "Date Night",
  ];

  let products: ProductPage | null = null;
  let productLoadError = false;

  try {
    products = await apiGet<ProductPage>("/api/products");
  } catch (error) {
    console.error("Failed to load products:", error);
    productLoadError = true;
  }

  return (
    <main className="min-h-screen bg-white text-gray-900">
      <div className="bg-gray-950 px-4 py-2 text-center text-sm text-white">
        Free standard shipping on orders over $75
      </div>

      <header className="border-b border-gray-200 bg-white">
        <div className="mx-auto flex max-w-7xl items-center gap-8 px-6 py-5">
          <div className="text-2xl font-bold tracking-tight">
            Fashion
            <span className="text-pink-600">Sense</span>
          </div>

          <nav className="hidden gap-6 font-medium lg:flex">
            {categories.map((category) => (
              <a
                key={category}
                href="#"
                className="transition hover:text-pink-600"
              >
                {category}
              </a>
            ))}
          </nav>

          <div className="ml-auto hidden flex-1 justify-center md:flex">
            <div className="w-full max-w-md">
              <input
                type="text"
                placeholder="Search products, brands and more..."
                className="w-full rounded-md border border-gray-300 bg-gray-50 px-4 py-2.5 outline-none transition focus:border-pink-500 focus:bg-white"
              />
            </div>
          </div>

          <div className="flex items-center gap-5 text-sm font-medium">
            <a href="#" className="hover:text-pink-600">
              Profile
            </a>

            <a href="#" className="hover:text-pink-600">
              Wishlist
            </a>

            <a href="#" className="hover:text-pink-600">
              Cart
            </a>
          </div>
        </div>
      </header>

      <section className="bg-gradient-to-r from-rose-50 via-pink-50 to-orange-50">
        <div className="mx-auto grid max-w-7xl gap-10 px-6 py-20 md:grid-cols-2 md:items-center">
          <div>
            <p className="mb-4 font-semibold uppercase tracking-[0.2em] text-pink-600">
              New Season
            </p>

            <h1 className="max-w-xl text-5xl font-bold leading-tight md:text-6xl">
              Find your style for every occasion.
            </h1>

            <p className="mt-6 max-w-xl text-lg leading-8 text-gray-600">
              Discover fashion across brands, styles, sizes, colors and
              occasions&mdash;all in one place.
            </p>

            <div className="mt-8 flex gap-4">
              <button className="rounded-md bg-gray-950 px-6 py-3 font-semibold text-white transition hover:bg-pink-600">
                Shop Now
              </button>

              <button className="rounded-md border border-gray-300 bg-white px-6 py-3 font-semibold transition hover:border-gray-950">
                Explore Collections
              </button>
            </div>
          </div>

          <div className="flex min-h-[380px] items-center justify-center rounded-3xl bg-gradient-to-br from-pink-200 via-orange-100 to-purple-200">
            <div className="text-center">
              <p className="text-sm font-semibold uppercase tracking-[0.3em] text-gray-600">
                Fashion Sense
              </p>

              <p className="mt-3 text-4xl font-bold">
                Wear the moment.
              </p>
            </div>
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-6 py-16">
        <div className="mb-8 flex items-end justify-between">
          <div>
            <p className="text-sm font-semibold uppercase tracking-widest text-pink-600">
              Explore
            </p>

            <h2 className="mt-2 text-3xl font-bold">
              Shop by department
            </h2>
          </div>
        </div>

        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
          {categories.map((category, index) => (
            <a
              key={category}
              href="#"
              className={`flex min-h-56 items-end rounded-2xl p-6 text-2xl font-bold transition hover:-translate-y-1 ${
                index === 0
                  ? "bg-slate-200"
                  : index === 1
                    ? "bg-rose-200"
                    : index === 2
                      ? "bg-amber-100"
                      : "bg-purple-200"
              }`}
            >
              {category}
            </a>
          ))}
        </div>
      </section>

      <section className="bg-gray-50">
        <div className="mx-auto max-w-7xl px-6 py-16">
          <p className="text-sm font-semibold uppercase tracking-widest text-pink-600">
            Curated for you
          </p>

          <h2 className="mt-2 text-3xl font-bold">
            Shop by occasion
          </h2>

          <div className="mt-8 grid gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6">
            {occasions.map((occasion) => (
              <button
                key={occasion}
                className="rounded-xl border border-gray-200 bg-white px-5 py-8 font-semibold shadow-sm transition hover:border-pink-400 hover:text-pink-600"
              >
                {occasion}
              </button>
            ))}
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-6 py-16">
        <p className="text-sm font-semibold uppercase tracking-widest text-pink-600">
          Trending
        </p>

        <h2 className="mt-2 text-3xl font-bold">
          Featured products
        </h2>

        {productLoadError ? (
          <div className="mt-8 rounded-xl border border-red-200 bg-red-50 p-6 text-red-700">
            Could not load products from the backend.
          </div>
        ) : products && products.content.length > 0 ? (
          <div className="mt-8 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
            {products.content.map((product) => (
              <article
                key={product.id}
                className="overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm transition hover:-translate-y-1 hover:shadow-md"
              >
                <div className="flex aspect-[4/5] items-center justify-center bg-gray-100">
                  <span className="text-sm text-gray-400">
                    Product image
                  </span>
                </div>

                <div className="p-5">
                  <p className="text-sm font-semibold text-gray-500">
                    {product.brandName ?? "Fashion Sense"}
                  </p>

                  <h3 className="mt-1 text-lg font-bold">
                    {product.name}
                  </h3>

                  <p className="mt-1 text-sm text-gray-500">
                    {product.categoryName ?? "Fashion"}
                  </p>

                  <p className="mt-4 text-lg font-bold">
                    ${Number(product.basePrice).toFixed(2)}
                  </p>

                  <a
                    href={`/products/${product.slug}`}
                    className="mt-5 inline-block rounded-md bg-gray-950 px-4 py-2 text-sm font-semibold text-white transition hover:bg-pink-600"
                  >
                    View product
                  </a>
                </div>
              </article>
            ))}
          </div>
        ) : (
          <div className="mt-8 rounded-xl border border-gray-200 bg-gray-50 p-8 text-center text-gray-500">
            No products found.
          </div>
        )}
      </section>

      <footer className="border-t border-gray-200 bg-gray-950">
        <div className="mx-auto max-w-7xl px-6 py-10 text-sm text-gray-400">
          &copy; 2026 Fashion Sense. Built as a full-stack ecommerce platform.
        </div>
      </footer>
    </main>
  );
}