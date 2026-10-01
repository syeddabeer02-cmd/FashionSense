"use client";

import Link from "next/link";

import {
  useCallback,
  useEffect,
  useState,
} from "react";

import type {
  WishlistItem,
} from "@/types/wishlist";

interface ErrorResponse {
  message?: string;
  error?: string;
}

interface WrappedWishlist {
  value?: WishlistItem[];
  Count?: number;
}

export default function WishlistPage() {
  const [
    wishlist,
    setWishlist,
  ] =
    useState<WishlistItem[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [
    removingSlug,
    setRemovingSlug,
  ] =
    useState<string | null>(null);

  const [error, setError] =
    useState<string | null>(null);

  function getToken(): string {
    const token =
      window.localStorage.getItem(
        "fashionSenseToken"
      );

    if (!token) {
      throw new Error(
        "Please log in to view your wishlist."
      );
    }

    return token;
  }

  const loadWishlist =
    useCallback(async () => {
      setLoading(true);
      setError(null);

      try {
        const token =
          getToken();

        const response =
          await fetch(
            "/api/wishlist",
            {
              method: "GET",

              headers: {
                Authorization:
                  `Bearer ${token}`,
              },

              cache:
                "no-store",
            }
          );

        const responseText =
          await response.text();

        let data:
          | WishlistItem[]
          | WrappedWishlist
          | ErrorResponse
          | null = null;

        if (responseText) {
          try {
            data =
              JSON.parse(
                responseText
              );
          } catch {
            throw new Error(
              `Server returned an invalid response. HTTP ${response.status}.`
            );
          }
        }

        if (!response.ok) {
          const errorData =
            data as
              | ErrorResponse
              | null;

          throw new Error(
            errorData?.message ??
              errorData?.error ??
              `Could not load wishlist. HTTP ${response.status}.`
          );
        }

        if (!data) {
          setWishlist([]);
          return;
        }

        if (Array.isArray(data)) {
          setWishlist(data);
          return;
        }

        if (
          "value" in data &&
          Array.isArray(data.value)
        ) {
          setWishlist(
            data.value
          );
          return;
        }

        throw new Error(
          "The backend returned an unexpected wishlist format."
        );
      } catch (requestError) {
        if (
          requestError instanceof
          Error
        ) {
          setError(
            requestError.message
          );
        } else {
          setError(
            "Could not load wishlist."
          );
        }
      } finally {
        setLoading(false);
      }
    }, []);

  useEffect(() => {
    void Promise.resolve().then(
      loadWishlist
    );
  }, [loadWishlist]);

  async function removeFromWishlist(
    slug: string
  ) {
    setRemovingSlug(slug);
    setError(null);

    try {
      const token =
        getToken();

      const response =
        await fetch(
          `/api/wishlist/${slug}`,
          {
            method: "DELETE",

            headers: {
              Authorization:
                `Bearer ${token}`,
            },
          }
        );

      const responseText =
        await response.text();

      if (!response.ok) {
        let message =
          `Could not remove item. HTTP ${response.status}.`;

        if (responseText) {
          try {
            const data =
              JSON.parse(
                responseText
              ) as ErrorResponse;

            message =
              data.message ??
              data.error ??
              message;
          } catch {
            // Keep fallback message.
          }
        }

        throw new Error(
          message
        );
      }

      setWishlist(
        (current) =>
          current.filter(
            (item) =>
              item.product.slug !==
              slug
          )
      );
    } catch (requestError) {
      if (
        requestError instanceof
        Error
      ) {
        setError(
          requestError.message
        );
      } else {
        setError(
          "Could not remove item."
        );
      }
    } finally {
      setRemovingSlug(null);
    }
  }

  function formatDate(
    value: string
  ) {
    const date =
      new Date(value);

    if (
      Number.isNaN(
        date.getTime()
      )
    ) {
      return value;
    }

    return date.toLocaleString();
  }

  return (
    <main className="min-h-screen bg-gray-50 text-gray-900">
      <div className="bg-gray-950 px-4 py-2 text-center text-sm text-white">
        Your Fashion Sense Wishlist
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
              className="font-bold text-pink-600"
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
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <p className="text-sm font-bold uppercase tracking-[0.2em] text-pink-600">
              Saved Products
            </p>

            <h1 className="mt-2 text-4xl font-bold">
              Your Wishlist
            </h1>
          </div>

          <Link
            href="/"
            className="text-sm font-semibold text-gray-600 transition hover:text-pink-600"
          >
            ← Continue shopping
          </Link>
        </div>

        {error && (
          <div className="mt-8 rounded-xl border border-red-200 bg-red-50 p-4 text-sm font-semibold text-red-700">
            {error}
          </div>
        )}

        {loading && (
          <div className="mt-10 rounded-2xl border border-gray-200 bg-white p-10 text-center">
            Loading your wishlist...
          </div>
        )}

        {!loading &&
          !error &&
          wishlist.length ===
            0 && (
            <div className="mt-10 rounded-3xl border border-gray-200 bg-white p-12 text-center">
              <div className="text-5xl">
                ♡
              </div>

              <h2 className="mt-5 text-2xl font-bold">
                Your wishlist is empty
              </h2>

              <p className="mt-2 text-gray-500">
                Save products you like
                and they will appear
                here.
              </p>

              <Link
                href="/"
                className="mt-6 inline-block rounded-xl bg-gray-950 px-6 py-3 font-bold text-white transition hover:bg-pink-600"
              >
                Explore Products
              </Link>
            </div>
          )}

        {!loading &&
          wishlist.length >
            0 && (
            <div className="mt-10 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
              {wishlist.map(
                (item) => {
                  const product =
                    item.product;

                  const removing =
                    removingSlug ===
                    product.slug;

                  return (
                    <article
                      key={
                        item.wishlistItemId
                      }
                      className="overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm transition hover:-translate-y-1 hover:shadow-md"
                    >
                      <div className="flex aspect-[4/3] items-center justify-center bg-gradient-to-br from-gray-100 to-gray-200">
                        <div className="text-center">
                          <p className="font-bold text-gray-600">
                            {
                              product.name
                            }
                          </p>

                          <p className="mt-2 text-xs text-gray-400">
                            Product image
                            placeholder
                          </p>
                        </div>
                      </div>

                      <div className="p-6">
                        <p className="text-sm font-bold uppercase tracking-wider text-pink-600">
                          {
                            product.brandName
                          }
                        </p>

                        <Link
                          href={`/products/${product.slug}`}
                          className="mt-2 block text-xl font-bold transition hover:text-pink-600"
                        >
                          {
                            product.name
                          }
                        </Link>

                        <p className="mt-2 text-sm text-gray-500">
                          {
                            product.categoryName
                          }
                        </p>

                        <p className="mt-4 text-xl font-bold">
                          $
                          {Number(
                            product.basePrice
                          ).toFixed(
                            2
                          )}
                        </p>

                        <div className="mt-4 flex flex-wrap gap-2">
                          {product.occasions.map(
                            (
                              occasion
                            ) => (
                              <span
                                key={
                                  occasion
                                }
                                className="rounded-full bg-gray-100 px-3 py-1 text-xs font-semibold capitalize text-gray-600"
                              >
                                {
                                  occasion
                                }
                              </span>
                            )
                          )}
                        </div>

                        <p className="mt-5 text-xs text-gray-400">
                          Added{" "}
                          {formatDate(
                            item.addedAt
                          )}
                        </p>

                        <div className="mt-6 grid gap-3 sm:grid-cols-2">
                          <Link
                            href={`/products/${product.slug}`}
                            className="rounded-xl bg-gray-950 px-4 py-3 text-center text-sm font-bold text-white transition hover:bg-pink-600"
                          >
                            View Product
                          </Link>

                          <button
                            type="button"
                            disabled={
                              removing
                            }
                            onClick={() =>
                              removeFromWishlist(
                                product.slug
                              )
                            }
                            className="rounded-xl border border-red-200 px-4 py-3 text-sm font-bold text-red-600 transition hover:bg-red-50 disabled:cursor-not-allowed disabled:text-gray-400"
                          >
                            {removing
                              ? "Removing..."
                              : "Remove"}
                          </button>
                        </div>
                      </div>
                    </article>
                  );
                }
              )}
            </div>
          )}
      </section>
    </main>
  );
}