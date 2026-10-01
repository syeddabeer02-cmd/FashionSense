"use client";

import Link from "next/link";

import {
  useCallback,
  useEffect,
  useState,
} from "react";

import type {
  CartResponse,
} from "@/types/cart";

interface ErrorResponse {
  message?: string;
  error?: string;
}

export default function CartPage() {
  const [cart, setCart] =
    useState<CartResponse | null>(
      null
    );

  const [loading, setLoading] =
    useState(true);

  const [updatingItemId, setUpdatingItemId] =
    useState<number | null>(null);

  const [error, setError] =
    useState<string | null>(null);

  function getToken(): string {
    const token =
      window.localStorage.getItem(
        "fashionSenseToken"
      );

    if (!token) {
      throw new Error(
        "Please log in to manage your cart."
      );
    }

    return token;
  }

  async function parseResponse<T>(
    response: Response
  ): Promise<T | null> {
    const responseText =
      await response.text();

    if (!responseText) {
      return null;
    }

    try {
      return JSON.parse(
        responseText
      ) as T;
    } catch {
      throw new Error(
        `Server returned an invalid response. HTTP ${response.status}.`
      );
    }
  }

  const loadCart =
    useCallback(async () => {
      setLoading(true);
      setError(null);

      try {
        const token =
          getToken();

        const response =
          await fetch(
            "/api/cart",
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

        const data =
          await parseResponse<
            CartResponse | ErrorResponse
          >(response);

        if (!response.ok) {
          const errorData =
            data as
              | ErrorResponse
              | null;

          throw new Error(
            errorData?.message ??
              errorData?.error ??
              `Could not load cart. HTTP ${response.status}.`
          );
        }

        if (!data) {
          throw new Error(
            "The cart response was empty."
          );
        }

        setCart(
          data as CartResponse
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
            "Could not load cart."
          );
        }
      } finally {
        setLoading(false);
      }
    }, []);

  useEffect(() => {
    loadCart();
  }, [loadCart]);

  async function updateQuantity(
    cartItemId: number,
    quantity: number
  ) {
    if (quantity < 1) {
      return;
    }

    setUpdatingItemId(
      cartItemId
    );

    setError(null);

    try {
      const token =
        getToken();

      const response =
        await fetch(
          `/api/cart/items/${cartItemId}`,
          {
            method: "PUT",

            headers: {
              "Content-Type":
                "application/json",

              Authorization:
                `Bearer ${token}`,
            },

            body: JSON.stringify({
              quantity,
            }),
          }
        );

      const data =
        await parseResponse<
          CartResponse | ErrorResponse
        >(response);

      if (!response.ok) {
        const errorData =
          data as
            | ErrorResponse
            | null;

        throw new Error(
          errorData?.message ??
            errorData?.error ??
            `Could not update cart. HTTP ${response.status}.`
        );
      }

      if (
        data &&
        "items" in data
      ) {
        setCart(
          data as CartResponse
        );
      } else {
        await loadCart();
      }
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
          "Could not update cart."
        );
      }
    } finally {
      setUpdatingItemId(null);
    }
  }

  async function removeItem(
    cartItemId: number
  ) {
    setUpdatingItemId(
      cartItemId
    );

    setError(null);

    try {
      const token =
        getToken();

      const response =
        await fetch(
          `/api/cart/items/${cartItemId}`,
          {
            method:
              "DELETE",

            headers: {
              Authorization:
                `Bearer ${token}`,
            },
          }
        );

      const data =
        await parseResponse<
          CartResponse | ErrorResponse
        >(response);

      if (!response.ok) {
        const errorData =
          data as
            | ErrorResponse
            | null;

        throw new Error(
          errorData?.message ??
            errorData?.error ??
            `Could not remove item. HTTP ${response.status}.`
        );
      }

      if (
        data &&
        "items" in data
      ) {
        setCart(
          data as CartResponse
        );
      } else {
        await loadCart();
      }
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
      setUpdatingItemId(null);
    }
  }

  return (
    <main className="min-h-screen bg-gray-50 text-gray-900">
      <div className="bg-gray-950 px-4 py-2 text-center text-sm text-white">
        Free standard shipping on
        orders over $75
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
              href="/cart"
              className="font-bold text-pink-600"
            >
              Cart
            </Link>
          </nav>
        </div>
      </header>

      <section className="mx-auto max-w-7xl px-6 py-12">
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <p className="text-sm font-bold uppercase tracking-[0.2em] text-pink-600">
              Shopping Bag
            </p>

            <h1 className="mt-2 text-4xl font-bold">
              Your Cart
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
            Loading your cart...
          </div>
        )}

        {!loading &&
          cart &&
          cart.items.length ===
            0 && (
            <div className="mt-10 rounded-3xl border border-gray-200 bg-white p-12 text-center">
              <h2 className="text-2xl font-bold">
                Your cart is empty
              </h2>

              <p className="mt-2 text-gray-500">
                Add something you like
                and it will appear
                here.
              </p>

              <Link
                href="/"
                className="mt-6 inline-block rounded-xl bg-gray-950 px-6 py-3 font-bold text-white transition hover:bg-pink-600"
              >
                Start shopping
              </Link>
            </div>
          )}

        {!loading &&
          cart &&
          cart.items.length >
            0 && (
            <div className="mt-10 grid gap-8 lg:grid-cols-[1fr_360px]">
              <div className="space-y-4">
                {cart.items.map(
                  (item) => {
                    const updating =
                      updatingItemId ===
                      item.cartItemId;

                    return (
                      <article
                        key={
                          item.cartItemId
                        }
                        className="rounded-2xl border border-gray-200 bg-white p-6 shadow-sm"
                      >
                        <div className="flex flex-col gap-6 sm:flex-row">
                          <div className="flex h-40 w-full shrink-0 items-center justify-center rounded-xl bg-gray-100 sm:w-32">
                            <span className="text-center text-xs text-gray-400">
                              Product
                              <br />
                              image
                            </span>
                          </div>

                          <div className="flex flex-1 flex-col justify-between">
                            <div>
                              <Link
                                href={`/products/${item.productSlug}`}
                                className="text-xl font-bold transition hover:text-pink-600"
                              >
                                {
                                  item.productName
                                }
                              </Link>

                              <p className="mt-2 text-sm text-gray-500">
                                Size{" "}
                                {
                                  item.size
                                }
                                {" • "}
                                {
                                  item.color
                                }
                              </p>

                              <p className="mt-1 text-sm text-gray-500">
                                {
                                  item.style
                                }
                                {" • "}
                                {
                                  item.material
                                }
                              </p>

                              <p className="mt-2 text-xs text-gray-400">
                                SKU:{" "}
                                {
                                  item.sku
                                }
                              </p>

                              <p className="mt-3 text-sm font-semibold text-green-600">
                                {
                                  item.availability ===
                                  "IN_STOCK"
                                    ? "In Stock"
                                    : "Out of Stock"
                                }
                              </p>
                            </div>

                            <div className="mt-6 flex flex-wrap items-end justify-between gap-5">
                              <div>
                                <p className="text-xs uppercase tracking-wider text-gray-400">
                                  Quantity
                                </p>

                                <div className="mt-2 flex items-center overflow-hidden rounded-xl border border-gray-300">
                                  <button
                                    type="button"
                                    disabled={
                                      updating ||
                                      item.quantity <=
                                        1
                                    }
                                    onClick={() =>
                                      updateQuantity(
                                        item.cartItemId,
                                        item.quantity -
                                          1
                                      )
                                    }
                                    className="h-10 w-10 font-bold transition hover:bg-gray-100 disabled:cursor-not-allowed disabled:text-gray-300"
                                  >
                                    −
                                  </button>

                                  <div className="flex h-10 min-w-12 items-center justify-center border-x border-gray-300 px-3 font-bold">
                                    {
                                      item.quantity
                                    }
                                  </div>

                                  <button
                                    type="button"
                                    disabled={
                                      updating
                                    }
                                    onClick={() =>
                                      updateQuantity(
                                        item.cartItemId,
                                        item.quantity +
                                          1
                                      )
                                    }
                                    className="h-10 w-10 font-bold transition hover:bg-gray-100 disabled:cursor-not-allowed disabled:text-gray-300"
                                  >
                                    +
                                  </button>
                                </div>

                                <button
                                  type="button"
                                  disabled={
                                    updating
                                  }
                                  onClick={() =>
                                    removeItem(
                                      item.cartItemId
                                    )
                                  }
                                  className="mt-3 text-sm font-semibold text-red-600 transition hover:text-red-700 disabled:text-gray-400"
                                >
                                  {updating
                                    ? "Updating..."
                                    : "Remove"}
                                </button>
                              </div>

                              <div className="text-right">
                                <p className="text-sm text-gray-500">
                                  $
                                  {Number(
                                    item.unitPrice
                                  ).toFixed(
                                    2
                                  )}{" "}
                                  each
                                </p>

                                <p className="mt-1 text-xl font-bold">
                                  $
                                  {Number(
                                    item.lineTotal
                                  ).toFixed(
                                    2
                                  )}
                                </p>
                              </div>
                            </div>
                          </div>
                        </div>
                      </article>
                    );
                  }
                )}
              </div>

              <aside className="h-fit rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
                <h2 className="text-xl font-bold">
                  Order Summary
                </h2>

                <div className="mt-6 space-y-4 border-b border-gray-200 pb-6">
                  <div className="flex justify-between text-gray-600">
                    <span>
                      Subtotal
                    </span>

                    <span className="font-semibold text-gray-900">
                      $
                      {Number(
                        cart.subtotal
                      ).toFixed(
                        2
                      )}
                    </span>
                  </div>

                  <div className="flex justify-between text-gray-600">
                    <span>
                      Shipping
                    </span>

                    <span>
                      Calculated at
                      checkout
                    </span>
                  </div>

                  <div className="flex justify-between text-gray-600">
                    <span>
                      Tax
                    </span>

                    <span>
                      Calculated at
                      checkout
                    </span>
                  </div>
                </div>

                <div className="mt-6 flex justify-between text-xl font-bold">
                  <span>
                    Subtotal
                  </span>

                  <span>
                    $
                    {Number(
                      cart.subtotal
                    ).toFixed(2)}
                  </span>
                </div>

                <Link
                  href="/checkout"
                  className="mt-6 block w-full rounded-xl bg-gray-950 px-6 py-4 text-center font-bold text-white transition hover:bg-pink-600"
                >
                  Continue to Checkout
                </Link>

                {Number(
                  cart.subtotal
                ) >= 75 ? (
                  <p className="mt-4 text-center text-sm font-semibold text-green-600">
                    You qualify for free
                    Standard shipping.
                  </p>
                ) : (
                  <p className="mt-4 text-center text-sm text-gray-500">
                    Add $
                    {(
                      75 -
                      Number(
                        cart.subtotal
                      )
                    ).toFixed(
                      2
                    )}{" "}
                    more for free
                    Standard shipping.
                  </p>
                )}
              </aside>
            </div>
          )}
      </section>
    </main>
  );
}