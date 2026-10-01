"use client";

import Link from "next/link";

import {
  useCallback,
  useEffect,
  useState,
} from "react";

import type {
  OrderResponse,
} from "@/types/order";

interface ErrorResponse {
  message?: string;
  error?: string;
}

export default function OrdersPage() {
  const [orders, setOrders] =
    useState<OrderResponse[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState<string | null>(null);

  const loadOrders =
    useCallback(async () => {
      setLoading(true);
      setError(null);

      try {
        const token =
          window.localStorage.getItem(
            "fashionSenseToken"
          );

        if (!token) {
          throw new Error(
            "Please log in to view your orders."
          );
        }

        const response =
          await fetch(
            "/api/orders",
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
          | OrderResponse[]
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
              `Could not load orders. HTTP ${response.status}.`
          );
        }

        if (!data) {
          throw new Error(
            "The orders response was empty."
          );
        }

        if (!Array.isArray(data)) {
          throw new Error(
            "The backend returned an unexpected orders format."
          );
        }

        setOrders(data);
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
            "Could not load orders."
          );
        }
      } finally {
        setLoading(false);
      }
    }, []);

  useEffect(() => {
    void Promise.resolve().then(
      loadOrders
    );
  }, [loadOrders]);

  function formatMoney(
    value:
      | number
      | undefined
  ) {
    return Number(
      value ?? 0
    ).toFixed(2);
  }

  function formatDate(
    value:
      | string
      | undefined
  ) {
    if (!value) {
      return "—";
    }

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
        Fashion Sense Orders
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
              className="transition hover:text-pink-600"
            >
              Cart
            </Link>

            <Link
              href="/orders"
              className="font-bold text-pink-600"
            >
              Orders
            </Link>
          </nav>
        </div>
      </header>

      <section className="mx-auto max-w-6xl px-6 py-12">
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <p className="text-sm font-bold uppercase tracking-[0.2em] text-pink-600">
              Your Account
            </p>

            <h1 className="mt-2 text-4xl font-bold">
              Order History
            </h1>
          </div>

          <Link
            href="/"
            className="text-sm font-semibold text-gray-600 transition hover:text-pink-600"
          >
            ← Continue shopping
          </Link>
        </div>

        {loading && (
          <div className="mt-10 rounded-2xl border border-gray-200 bg-white p-10 text-center">
            Loading your orders...
          </div>
        )}

        {!loading &&
          error && (
            <div className="mt-10 rounded-2xl border border-red-200 bg-red-50 p-8">
              <h2 className="text-lg font-bold text-red-800">
                Could not load orders
              </h2>

              <p className="mt-2 text-red-700">
                {error}
              </p>

              <div className="mt-6 flex gap-3">
                <button
                  type="button"
                  onClick={
                    loadOrders
                  }
                  className="rounded-xl bg-gray-950 px-5 py-3 text-sm font-bold text-white transition hover:bg-pink-600"
                >
                  Try Again
                </button>

                <Link
                  href="/login"
                  className="rounded-xl border border-gray-300 bg-white px-5 py-3 text-sm font-bold"
                >
                  Login
                </Link>
              </div>
            </div>
          )}

        {!loading &&
          !error &&
          orders.length ===
            0 && (
            <div className="mt-10 rounded-3xl border border-gray-200 bg-white p-12 text-center">
              <h2 className="text-2xl font-bold">
                No orders yet
              </h2>

              <p className="mt-2 text-gray-500">
                Your completed
                Fashion Sense orders
                will appear here.
              </p>

              <Link
                href="/"
                className="mt-6 inline-block rounded-xl bg-gray-950 px-6 py-3 font-bold text-white transition hover:bg-pink-600"
              >
                Start Shopping
              </Link>
            </div>
          )}

        {!loading &&
          !error &&
          orders.length >
            0 && (
            <div className="mt-10 space-y-5">
              {orders.map(
                (
                  order,
                  index
                ) => (
                  <article
                    key={
                      order.orderNumber ??
                      order.id ??
                      index
                    }
                    className="overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm"
                  >
                    <div className="flex flex-wrap items-center justify-between gap-4 border-b border-gray-200 bg-gray-50 px-6 py-5">
                      <div>
                        <p className="text-xs font-semibold uppercase tracking-wider text-gray-400">
                          Order
                        </p>

                        <p className="mt-1 font-bold">
                          {order.orderNumber ??
                            `#${order.id ?? index + 1}`}
                        </p>
                      </div>

                      <div>
                        <p className="text-xs font-semibold uppercase tracking-wider text-gray-400">
                          Placed
                        </p>

                        <p className="mt-1 text-sm font-semibold">
                          {formatDate(
                            order.createdAt
                          )}
                        </p>
                      </div>

                      <div>
                        <p className="text-xs font-semibold uppercase tracking-wider text-gray-400">
                          Total
                        </p>

                        <p className="mt-1 font-bold">
                          $
                          {formatMoney(
                            order.totalAmount
                          )}
                        </p>
                      </div>
                    </div>

                    <div className="grid gap-6 p-6 md:grid-cols-3">
                      <div>
                        <p className="text-xs font-semibold uppercase tracking-wider text-gray-400">
                          Order Status
                        </p>

                        <p className="mt-2 font-bold text-green-600">
                          {order.status ??
                            "CONFIRMED"}
                        </p>
                      </div>

                      <div>
                        <p className="text-xs font-semibold uppercase tracking-wider text-gray-400">
                          Payment
                        </p>

                        <p className="mt-2 font-bold">
                          {order.paymentStatus ??
                            "SUCCEEDED"}
                        </p>

                        <p className="mt-1 text-sm text-gray-500">
                          {order.paymentMethod ??
                            "—"}
                        </p>
                      </div>

                      <div>
                        <p className="text-xs font-semibold uppercase tracking-wider text-gray-400">
                          Shipping
                        </p>

                        <p className="mt-2 font-bold">
                          {order.shippingMethod ??
                            "—"}
                        </p>

                        <p className="mt-1 text-sm text-gray-500">
                          {order.city &&
                          order.state
                            ? `${order.city}, ${order.state}`
                            : "Saved delivery address"}
                        </p>
                      </div>
                    </div>

                    <div className="flex flex-wrap justify-between gap-4 border-t border-gray-200 px-6 py-5">
                      <div className="text-sm text-gray-500">
                        Subtotal:{" "}
                        <span className="font-semibold text-gray-900">
                          $
                          {formatMoney(
                            order.subtotal
                          )}
                        </span>
                      </div>

                      {order.orderNumber && (
                        <Link
                          href={`/orders/${order.orderNumber}`}
                          className="font-semibold text-pink-600 transition hover:text-pink-700"
                        >
                          View Order →
                        </Link>
                      )}
                    </div>
                  </article>
                )
              )}
            </div>
          )}
      </section>
    </main>
  );
}