"use client";

import Link from "next/link";

import {
  useEffect,
  useRef,
  useState,
} from "react";

import type {
  Address,
} from "@/types/address";

import type {
  CartResponse,
} from "@/types/cart";

import type {
  OrderResponse,
  PaymentMethod,
  ShippingMethod,
} from "@/types/order";

interface ErrorResponse {
  message?: string;
  error?: string;
}

export default function CheckoutPage() {
  const [addresses, setAddresses] =
    useState<Address[]>([]);

  const [cart, setCart] =
    useState<CartResponse | null>(
      null
    );

  const [
    selectedAddressId,
    setSelectedAddressId,
  ] =
    useState<number | null>(
      null
    );

  const [
    shippingMethod,
    setShippingMethod,
  ] =
    useState<ShippingMethod>(
      "STANDARD"
    );

  const [
    paymentMethod,
    setPaymentMethod,
  ] =
    useState<PaymentMethod>(
      "CARD"
    );

  const [
    promotionCode,
    setPromotionCode,
  ] =
    useState("");

  const [loading, setLoading] =
    useState(true);

  const [
    submitting,
    setSubmitting,
  ] =
    useState(false);

  const [error, setError] =
    useState<string | null>(
      null
    );

  const [order, setOrder] =
    useState<OrderResponse | null>(
      null
    );

  const idempotencyKeyRef =
    useRef<string | null>(
      null
    );

  function getToken(): string {
    const token =
      window.localStorage.getItem(
        "fashionSenseToken"
      );

    if (!token) {
      throw new Error(
        "Please log in before checking out."
      );
    }

    return token;
  }

  function getIdempotencyKey(): string {
    if (
      !idempotencyKeyRef.current
    ) {
      idempotencyKeyRef.current =
        crypto.randomUUID();
    }

    return idempotencyKeyRef.current;
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

  useEffect(() => {
    async function loadCheckoutData() {
      setLoading(true);
      setError(null);

      try {
        const token =
          getToken();

        const [
          addressResponse,
          cartResponse,
        ] = await Promise.all([
          fetch(
            "/api/addresses",
            {
              headers: {
                Authorization:
                  `Bearer ${token}`,
              },

              cache:
                "no-store",
            }
          ),

          fetch(
            "/api/cart",
            {
              headers: {
                Authorization:
                  `Bearer ${token}`,
              },

              cache:
                "no-store",
            }
          ),
        ]);

        const addressData =
          await parseResponse<
            Address[] |
            {
              value?: Address[];
            } |
            ErrorResponse
          >(addressResponse);

        const cartData =
          await parseResponse<
            CartResponse |
            ErrorResponse
          >(cartResponse);

        if (
          !addressResponse.ok
        ) {
          const errorData =
            addressData as
              | ErrorResponse
              | null;

          throw new Error(
            errorData?.message ??
              `Could not load addresses. HTTP ${addressResponse.status}.`
          );
        }

        if (!cartResponse.ok) {
          const errorData =
            cartData as
              | ErrorResponse
              | null;

          throw new Error(
            errorData?.message ??
              `Could not load cart. HTTP ${cartResponse.status}.`
          );
        }

        let addressList:
          Address[] = [];

        if (
          Array.isArray(
            addressData
          )
        ) {
          addressList =
            addressData;
        } else if (
          addressData &&
          "value" in
            addressData &&
          Array.isArray(
            addressData.value
          )
        ) {
          addressList =
            addressData.value;
        }

        setAddresses(
          addressList
        );

        setCart(
          cartData as CartResponse
        );

        const defaultAddress =
          addressList.find(
            (address) =>
              address.defaultAddress
          );

        if (defaultAddress) {
          setSelectedAddressId(
            defaultAddress.id
          );
        } else if (
          addressList.length > 0
        ) {
          setSelectedAddressId(
            addressList[0].id
          );
        }
      } catch (
        requestError
      ) {
        if (
          requestError instanceof
          Error
        ) {
          setError(
            requestError.message
          );
        } else {
          setError(
            "Could not load checkout."
          );
        }
      } finally {
        setLoading(false);
      }
    }

    loadCheckoutData();
  }, []);

  async function placeOrder() {
    if (
      selectedAddressId ===
      null
    ) {
      setError(
        "Please select a shipping address."
      );

      return;
    }

    if (
      !cart ||
      cart.items.length === 0
    ) {
      setError(
        "Your cart is empty."
      );

      return;
    }

    setSubmitting(true);
    setError(null);

    try {
      const token =
        getToken();

      const idempotencyKey =
        getIdempotencyKey();

      const response =
        await fetch(
          "/api/checkout",
          {
            method: "POST",

            headers: {
              "Content-Type":
                "application/json",

              Authorization:
                `Bearer ${token}`,

              "Idempotency-Key":
                idempotencyKey,
            },

            body: JSON.stringify(
              {
                addressId:
                  selectedAddressId,

                shippingMethod,

                paymentMethod,

                promotionCode:
                  promotionCode.trim()
                    ? promotionCode
                        .trim()
                        .toUpperCase()
                    : null,
              }
            ),
          }
        );

      const data =
        await parseResponse<
          OrderResponse |
          ErrorResponse
        >(response);

      if (!response.ok) {
        const errorData =
          data as
            | ErrorResponse
            | null;

        throw new Error(
          errorData?.message ??
            errorData?.error ??
            `Checkout failed. HTTP ${response.status}.`
        );
      }

      if (!data) {
        throw new Error(
          "Checkout succeeded but the server returned no order."
        );
      }

      setOrder(
        data as OrderResponse
      );
    } catch (
      requestError
    ) {
      if (
        requestError instanceof
        Error
      ) {
        setError(
          requestError.message
        );
      } else {
        setError(
          "Checkout failed."
        );
      }
    } finally {
      setSubmitting(false);
    }
  }

  if (order) {
    return (
      <main className="min-h-screen bg-gray-50 px-6 py-16 text-gray-900">
        <div className="mx-auto max-w-2xl rounded-3xl border border-green-200 bg-white p-10 shadow-sm">
          <div className="text-center">
            <div className="text-5xl">
              ✓
            </div>

            <p className="mt-5 text-sm font-bold uppercase tracking-[0.2em] text-green-600">
              Order confirmed
            </p>

            <h1 className="mt-3 text-3xl font-bold">
              Thank you for your
              order
            </h1>

            {order.orderNumber && (
              <p className="mt-3 text-gray-500">
                Order{" "}
                <span className="font-bold text-gray-900">
                  {
                    order.orderNumber
                  }
                </span>
              </p>
            )}
          </div>

          <div className="mt-8 rounded-2xl bg-gray-50 p-6">
            <div className="flex justify-between py-2">
              <span>
                Status
              </span>

              <span className="font-bold">
                {order.status ??
                  "CONFIRMED"}
              </span>
            </div>

            <div className="flex justify-between py-2">
              <span>
                Payment
              </span>

              <span className="font-bold">
                {order.paymentStatus ??
                  "SUCCEEDED"}
              </span>
            </div>

            {order.discountAmount !==
              undefined &&
              Number(
                order.discountAmount
              ) > 0 && (
                <div className="flex justify-between py-2 text-green-700">
                  <span>
                    Discount
                  </span>

                  <span className="font-bold">
                    -$
                    {Number(
                      order.discountAmount
                    ).toFixed(2)}
                  </span>
                </div>
              )}

            {order.totalAmount !==
              undefined && (
              <div className="mt-3 flex justify-between border-t border-gray-200 pt-4 text-xl font-bold">
                <span>
                  Total
                </span>

                <span>
                  $
                  {Number(
                    order.totalAmount
                  ).toFixed(2)}
                </span>
              </div>
            )}
          </div>

          <div className="mt-8 flex flex-col gap-3 sm:flex-row">
            <Link
              href="/"
              className="flex-1 rounded-xl border border-gray-300 px-6 py-3 text-center font-bold transition hover:border-gray-950"
            >
              Continue Shopping
            </Link>

            <Link
              href="/orders"
              className="flex-1 rounded-xl bg-gray-950 px-6 py-3 text-center font-bold text-white transition hover:bg-pink-600"
            >
              View Orders
            </Link>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-gray-50 text-gray-900">
      <div className="bg-gray-950 px-4 py-2 text-center text-sm text-white">
        Secure Fashion Sense
        Checkout
      </div>

      <header className="border-b border-gray-200 bg-white">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-5">
          <Link
            href="/"
            className="text-2xl font-bold"
          >
            Fashion
            <span className="text-pink-600">
              Sense
            </span>
          </Link>

          <Link
            href="/cart"
            className="text-sm font-semibold hover:text-pink-600"
          >
            ← Back to Cart
          </Link>
        </div>
      </header>

      <section className="mx-auto max-w-6xl px-6 py-12">
        <h1 className="text-4xl font-bold">
          Checkout
        </h1>

        {error && (
          <div className="mt-6 rounded-xl border border-red-200 bg-red-50 p-4 font-semibold text-red-700">
            {error}
          </div>
        )}

        {loading ? (
          <div className="mt-10 rounded-2xl bg-white p-10 text-center">
            Loading checkout...
          </div>
        ) : (
          <div className="mt-10 grid gap-8 lg:grid-cols-[1fr_360px]">
            <div className="space-y-8">
              {/* Address */}
              <section className="rounded-2xl border border-gray-200 bg-white p-6">
                <h2 className="text-xl font-bold">
                  Shipping Address
                </h2>

                <div className="mt-5 space-y-3">
                  {addresses.map(
                    (address) => (
                      <button
                        key={
                          address.id
                        }
                        type="button"
                        onClick={() =>
                          setSelectedAddressId(
                            address.id
                          )
                        }
                        className={`w-full rounded-xl border p-5 text-left transition ${
                          selectedAddressId ===
                          address.id
                            ? "border-pink-600 bg-pink-50"
                            : "border-gray-200 hover:border-gray-400"
                        }`}
                      >
                        <div className="flex justify-between">
                          <p className="font-bold">
                            {
                              address.label
                            }
                          </p>

                          {address.defaultAddress && (
                            <span className="text-sm font-semibold text-pink-600">
                              Default
                            </span>
                          )}
                        </div>

                        <p className="mt-2">
                          {
                            address.recipientName
                          }
                        </p>

                        <p className="text-sm text-gray-500">
                          {
                            address.addressLine1
                          }

                          {address.addressLine2
                            ? `, ${address.addressLine2}`
                            : ""}
                        </p>

                        <p className="text-sm text-gray-500">
                          {
                            address.city
                          }
                          ,{" "}
                          {
                            address.state
                          }{" "}
                          {
                            address.postalCode
                          }
                        </p>
                      </button>
                    )
                  )}
                </div>
              </section>

              {/* Shipping */}
              <section className="rounded-2xl border border-gray-200 bg-white p-6">
                <h2 className="text-xl font-bold">
                  Shipping Method
                </h2>

                <div className="mt-5 grid gap-3 sm:grid-cols-2">
                  <button
                    type="button"
                    onClick={() =>
                      setShippingMethod(
                        "STANDARD"
                      )
                    }
                    className={`rounded-xl border p-5 text-left ${
                      shippingMethod ===
                      "STANDARD"
                        ? "border-pink-600 bg-pink-50"
                        : "border-gray-200"
                    }`}
                  >
                    <p className="font-bold">
                      Standard
                    </p>

                    <p className="mt-1 text-sm text-gray-500">
                      3–5 days
                    </p>

                    <p className="mt-3 font-semibold">
                      {cart &&
                      Number(
                        cart.subtotal
                      ) >= 75
                        ? "FREE"
                        : "$5.99"}
                    </p>
                  </button>

                  <button
                    type="button"
                    onClick={() =>
                      setShippingMethod(
                        "EXPRESS"
                      )
                    }
                    className={`rounded-xl border p-5 text-left ${
                      shippingMethod ===
                      "EXPRESS"
                        ? "border-pink-600 bg-pink-50"
                        : "border-gray-200"
                    }`}
                  >
                    <p className="font-bold">
                      Express
                    </p>

                    <p className="mt-1 text-sm text-gray-500">
                      1–2 days
                    </p>

                    <p className="mt-3 font-semibold">
                      $14.99
                    </p>
                  </button>
                </div>
              </section>

              {/* Payment */}
              <section className="rounded-2xl border border-gray-200 bg-white p-6">
                <h2 className="text-xl font-bold">
                  Payment Method
                </h2>

                <p className="mt-1 text-sm text-gray-500">
                  Payment is simulated
                  in this portfolio
                  version.
                </p>

                <div className="mt-5 grid gap-3 sm:grid-cols-2">
                  <button
                    type="button"
                    onClick={() =>
                      setPaymentMethod(
                        "CARD"
                      )
                    }
                    className={`rounded-xl border p-5 font-bold ${
                      paymentMethod ===
                      "CARD"
                        ? "border-pink-600 bg-pink-50"
                        : "border-gray-200"
                    }`}
                  >
                    Card
                  </button>

                  <button
                    type="button"
                    onClick={() =>
                      setPaymentMethod(
                        "PAYPAL"
                      )
                    }
                    className={`rounded-xl border p-5 font-bold ${
                      paymentMethod ===
                      "PAYPAL"
                        ? "border-pink-600 bg-pink-50"
                        : "border-gray-200"
                    }`}
                  >
                    PayPal
                  </button>
                </div>
              </section>

              {/* Promotion */}
              <section className="rounded-2xl border border-gray-200 bg-white p-6">
                <h2 className="text-xl font-bold">
                  Promotion Code
                </h2>

                <p className="mt-1 text-sm text-gray-500">
                  Enter a valid discount
                  code to apply it during
                  checkout.
                </p>

                <input
                  type="text"
                  value={
                    promotionCode
                  }
                  onChange={(
                    event
                  ) =>
                    setPromotionCode(
                      event.target.value.toUpperCase()
                    )
                  }
                  placeholder="e.g. SAVE10"
                  maxLength={50}
                  autoComplete="off"
                  className="mt-5 w-full rounded-xl border border-gray-300 px-4 py-3 outline-none transition focus:border-pink-600"
                />

                {promotionCode && (
                  <p className="mt-3 text-sm text-gray-500">
                    Code{" "}
                    <span className="font-bold text-gray-900">
                      {promotionCode}
                    </span>{" "}
                    will be validated by
                    the backend when you
                    place the order.
                  </p>
                )}
              </section>
            </div>

            {/* Summary */}
            <aside className="h-fit rounded-2xl border border-gray-200 bg-white p-6">
              <h2 className="text-xl font-bold">
                Order Summary
              </h2>

              <div className="mt-5 space-y-3">
                <div className="flex justify-between">
                  <span>
                    Subtotal
                  </span>

                  <span className="font-bold">
                    $
                    {Number(
                      cart?.subtotal ??
                        0
                    ).toFixed(2)}
                  </span>
                </div>

                <div className="flex justify-between">
                  <span>
                    Shipping
                  </span>

                  <span>
                    {shippingMethod ===
                    "EXPRESS"
                      ? "$14.99"
                      : Number(
                            cart?.subtotal ??
                              0
                          ) >= 75
                        ? "FREE"
                        : "$5.99"}
                  </span>
                </div>

                {promotionCode && (
                  <div className="flex justify-between text-green-700">
                    <span>
                      Promotion
                    </span>

                    <span className="font-semibold">
                      {promotionCode}
                    </span>
                  </div>
                )}

                <div className="flex justify-between text-gray-500">
                  <span>
                    Discount
                  </span>

                  <span>
                    Calculated by backend
                  </span>
                </div>

                <div className="flex justify-between text-gray-500">
                  <span>
                    Tax
                  </span>

                  <span>
                    Calculated by backend
                  </span>
                </div>
              </div>

              <button
                type="button"
                disabled={
                  submitting ||
                  !cart ||
                  cart.items.length ===
                    0 ||
                  selectedAddressId ===
                    null
                }
                onClick={
                  placeOrder
                }
                className="mt-7 w-full rounded-xl bg-gray-950 px-6 py-4 font-bold text-white transition hover:bg-pink-600 disabled:cursor-not-allowed disabled:bg-gray-300 disabled:text-gray-600"
              >
                {submitting
                  ? "Placing Order..."
                  : "Place Order"}
              </button>
            </aside>
          </div>
        )}
      </section>
    </main>
  );
}