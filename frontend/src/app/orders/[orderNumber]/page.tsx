"use client";

import Link from "next/link";

import {
  useEffect,
  useState,
} from "react";

import {
  useParams,
} from "next/navigation";

import type {
  OrderResponse,
} from "@/types/order";

interface ErrorResponse {
  message?: string;
  error?: string;
}

export default function OrderDetailPage() {
  const params = useParams<{
    orderNumber: string;
  }>();

  const orderNumber =
    params.orderNumber;

  const [order, setOrder] =
    useState<OrderResponse | null>(
      null
    );

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState<string | null>(null);

  useEffect(() => {
    async function loadOrder() {
      setLoading(true);
      setError(null);

      try {
        const token =
          window.localStorage.getItem(
            "fashionSenseToken"
          );

        if (!token) {
          throw new Error(
            "Please log in to view this order."
          );
        }

        const response =
          await fetch(
            `/api/orders/${orderNumber}`,
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
          | OrderResponse
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
              `Could not load order. HTTP ${response.status}.`
          );
        }

        if (!data) {
          throw new Error(
            "The order response was empty."
          );
        }

        setOrder(
          data as OrderResponse
        );
      } catch (requestError) {
        if (
          requestError instanceof Error
        ) {
          setError(
            requestError.message
          );
        } else {
          setError(
            "Could not load order."
          );
        }
      } finally {
        setLoading(false);
      }
    }

    if (orderNumber) {
      loadOrder();
    }
  }, [orderNumber]);

  function formatMoney(
    value: number
  ) {
    return Number(
      value
    ).toFixed(2);
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

  if (loading) {
    return (
      <main className="min-h-screen bg-gray-50 px-6 py-20 text-gray-900">
        <div className="mx-auto max-w-5xl rounded-2xl border border-gray-200 bg-white p-10 text-center">
          Loading order...
        </div>
      </main>
    );
  }

  if (error || !order) {
    return (
      <main className="min-h-screen bg-gray-50 px-6 py-20 text-gray-900">
        <div className="mx-auto max-w-5xl">
          <Link
            href="/orders"
            className="font-semibold text-pink-600 hover:text-pink-700"
          >
            ← Back to Orders
          </Link>

          <div className="mt-8 rounded-2xl border border-red-200 bg-red-50 p-8">
            <h1 className="text-2xl font-bold text-red-800">
              Could not load order
            </h1>

            <p className="mt-2 text-red-700">
              {error ??
                "Order was not found."}
            </p>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-gray-50 text-gray-900">
      <div className="bg-gray-950 px-4 py-2 text-center text-sm text-white">
        Fashion Sense Order Details
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
              className="hover:text-pink-600"
            >
              Shop
            </Link>

            <Link
              href="/cart"
              className="hover:text-pink-600"
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
        <Link
          href="/orders"
          className="text-sm font-semibold text-gray-500 hover:text-pink-600"
        >
          ← Back to Order History
        </Link>

        <div className="mt-8 flex flex-wrap items-start justify-between gap-6">
          <div>
            <p className="text-sm font-bold uppercase tracking-[0.2em] text-pink-600">
              Order
            </p>

            <h1 className="mt-2 text-3xl font-bold">
              {order.orderNumber}
            </h1>

            <p className="mt-2 text-sm text-gray-500">
              Placed{" "}
              {formatDate(
                order.createdAt
              )}
            </p>
          </div>

          <div className="rounded-xl bg-green-50 px-5 py-3">
            <p className="text-xs font-semibold uppercase tracking-wider text-green-700">
              Status
            </p>

            <p className="mt-1 font-bold text-green-700">
              {order.status}
            </p>
          </div>
        </div>

        <div className="mt-10 grid gap-8 lg:grid-cols-[1fr_360px]">
          <div className="space-y-8">
            {/* Items */}
            <section className="rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
              <h2 className="text-xl font-bold">
                Items
              </h2>

              <div className="mt-6 space-y-4">
                {order.items.map(
                  (item) => (
                    <article
                      key={item.id}
                      className="rounded-xl border border-gray-200 p-5"
                    >
                      <div className="flex flex-wrap justify-between gap-5">
                        <div>
                          <h3 className="text-lg font-bold">
                            {
                              item.productName
                            }
                          </h3>

                          <p className="mt-2 text-sm text-gray-500">
                            Size{" "}
                            {item.size}
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
                        </div>

                        <div className="text-right">
                          <p className="text-sm text-gray-500">
                            Quantity{" "}
                            {
                              item.quantity
                            }
                          </p>

                          <p className="mt-1 font-semibold">
                            $
                            {formatMoney(
                              item.unitPrice
                            )}{" "}
                            each
                          </p>

                          <p className="mt-2 text-xl font-bold">
                            $
                            {formatMoney(
                              item.lineTotal
                            )}
                          </p>
                        </div>
                      </div>
                    </article>
                  )
                )}
              </div>
            </section>

            {/* Shipping address */}
            <section className="rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
              <h2 className="text-xl font-bold">
                Shipping Address
              </h2>

              <div className="mt-5 text-gray-600">
                <p className="font-semibold text-gray-900">
                  {
                    order.recipientName
                  }
                </p>

                <p className="mt-2">
                  {
                    order.addressLine1
                  }
                </p>

                {order.addressLine2 && (
                  <p>
                    {
                      order.addressLine2
                    }
                  </p>
                )}

                <p>
                  {order.city},{" "}
                  {order.state}{" "}
                  {
                    order.postalCode
                  }
                </p>

                <p>
                  {
                    order.countryCode
                  }
                </p>

                <p className="mt-3">
                  Phone:{" "}
                  {order.phone}
                </p>
              </div>
            </section>

            {/* Fulfillment */}
            <section className="rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
              <h2 className="text-xl font-bold">
                Fulfillment
              </h2>

              <div className="mt-5 grid gap-5 sm:grid-cols-2">
                <div>
                  <p className="text-xs font-semibold uppercase tracking-wider text-gray-400">
                    Shipping Method
                  </p>

                  <p className="mt-2 font-bold">
                    {
                      order.shippingMethod
                    }
                  </p>
                </div>

                <div>
                  <p className="text-xs font-semibold uppercase tracking-wider text-gray-400">
                    Payment
                  </p>

                  <p className="mt-2 font-bold">
                    {
                      order.paymentMethod
                    }
                  </p>

                  <p className="mt-1 text-sm text-green-600">
                    {
                      order.paymentStatus
                    }
                  </p>
                </div>
              </div>
            </section>
          </div>

          {/* Price summary */}
          <aside className="h-fit rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
            <h2 className="text-xl font-bold">
              Price Summary
            </h2>

            <div className="mt-6 space-y-4">
              <div className="flex justify-between">
                <span className="text-gray-600">
                  Subtotal
                </span>

                <span className="font-semibold">
                  $
                  {formatMoney(
                    order.subtotal
                  )}
                </span>
              </div>

              <div className="flex justify-between">
                <span className="text-gray-600">
                  Discount
                </span>

                <span>
                  -$
                  {formatMoney(
                    order.discountAmount
                  )}
                </span>
              </div>

              <div className="flex justify-between">
                <span className="text-gray-600">
                  Gift Card
                </span>

                <span>
                  -$
                  {formatMoney(
                    order.giftCardAmount
                  )}
                </span>
              </div>

              <div className="flex justify-between">
                <span className="text-gray-600">
                  Shipping
                </span>

                <span>
                  $
                  {formatMoney(
                    order.shippingAmount
                  )}
                </span>
              </div>

              <div className="flex justify-between">
                <span className="text-gray-600">
                  Tax
                </span>

                <span>
                  $
                  {formatMoney(
                    order.taxAmount
                  )}
                </span>
              </div>
            </div>

            <div className="mt-6 flex justify-between border-t border-gray-200 pt-6 text-xl font-bold">
              <span>
                Total
              </span>

              <span>
                $
                {formatMoney(
                  order.totalAmount
                )}
              </span>
            </div>

            <div className="mt-6 rounded-xl bg-green-50 p-4 text-sm">
              <p className="font-semibold text-green-700">
                Payment Successful
              </p>

              <p className="mt-1 text-green-700">
                {
                  order.paymentStatus
                }
              </p>
            </div>
          </aside>
        </div>
      </section>
    </main>
  );
}