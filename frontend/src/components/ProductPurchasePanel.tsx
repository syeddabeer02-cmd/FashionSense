"use client";

import { useState } from "react";

import type { ProductVariant } from "@/types/product-detail";

interface ProductPurchasePanelProps {
  variants: ProductVariant[];
  basePrice: number;
}

export default function ProductPurchasePanel({
  variants,
  basePrice,
}: ProductPurchasePanelProps) {
  const [selectedVariantId, setSelectedVariantId] =
    useState<number | null>(null);

  const [addingToCart, setAddingToCart] =
    useState(false);

  const [message, setMessage] =
    useState<string | null>(null);

  const [error, setError] =
    useState<string | null>(null);

  const selectedVariant =
    variants.find(
      (variant) =>
        variant.id === selectedVariantId
    ) ?? null;

  async function handleAddToCart() {
    if (!selectedVariant) {
      setError("Please select a variant.");
      return;
    }

    if (
      selectedVariant.availability !==
      "IN_STOCK"
    ) {
      setError(
        "This variant is currently out of stock."
      );
      return;
    }

    const token =
      window.localStorage.getItem(
        "fashionSenseToken"
      );

    if (!token) {
      setError(
        "Please log in before adding items to your cart."
      );
      return;
    }

    setAddingToCart(true);
    setError(null);
    setMessage(null);

    try {
      const response = await fetch(
        "/api/cart/items",
        {
          method: "POST",

          headers: {
            "Content-Type":
              "application/json",

            Authorization: `Bearer ${token}`,
          },

          body: JSON.stringify({
            sku: selectedVariant.sku,
            quantity: 1,
          }),
        }
      );

      const data = await response.json();

      if (!response.ok) {
        throw new Error(
          data.message ??
            "Could not add item to cart."
        );
      }

      setMessage(
        `${selectedVariant.size} / ${selectedVariant.color} added to cart.`
      );
    } catch (requestError) {
      if (requestError instanceof Error) {
        setError(requestError.message);
      } else {
        setError(
          "Could not add item to cart."
        );
      }
    } finally {
      setAddingToCart(false);
    }
  }

  return (
    <div className="mt-10">
      <h2 className="text-lg font-bold">
        Select a variant
      </h2>

      <p className="mt-1 text-sm text-gray-500">
        Choose the size and color you want.
      </p>

      <div className="mt-4 space-y-3">
        {variants.map((variant) => {
          const inStock =
            variant.availability ===
            "IN_STOCK";

          const selected =
            selectedVariantId ===
            variant.id;

          return (
            <button
              key={variant.id}
              type="button"
              disabled={!inStock}
              onClick={() => {
                setSelectedVariantId(
                  variant.id
                );

                setError(null);
                setMessage(null);
              }}
              className={`flex w-full flex-wrap items-center justify-between gap-4 rounded-xl border p-4 text-left transition ${
                !inStock
                  ? "cursor-not-allowed border-gray-200 bg-gray-50 opacity-60"
                  : selected
                    ? "border-pink-600 bg-pink-50"
                    : "border-gray-200 bg-white hover:border-gray-400"
              }`}
            >
              <div>
                <p className="font-semibold">
                  Size {variant.size}
                  {" • "}
                  {variant.color}
                </p>

                <p className="mt-1 text-sm text-gray-500">
                  {variant.style}
                  {" • "}
                  {variant.material}
                </p>

                <p className="mt-2 text-xs text-gray-400">
                  SKU: {variant.sku}
                </p>
              </div>

              <div className="text-right">
                <p className="font-bold">
                  $
                  {Number(
                    variant.price ??
                      basePrice
                  ).toFixed(2)}
                </p>

                <p
                  className={`mt-1 text-sm font-semibold ${
                    inStock
                      ? "text-green-600"
                      : "text-red-600"
                  }`}
                >
                  {inStock
                    ? selected
                      ? "Selected"
                      : "In Stock"
                    : "Out of Stock"}
                </p>
              </div>
            </button>
          );
        })}
      </div>

      {selectedVariant && (
        <div className="mt-5 rounded-xl bg-gray-50 p-4">
          <p className="text-sm text-gray-500">
            Selected
          </p>

          <p className="mt-1 font-semibold">
            {selectedVariant.size}
            {" / "}
            {selectedVariant.color}
            {" / "}
            {selectedVariant.material}
          </p>

          <p className="mt-1 text-sm text-gray-500">
            ${Number(
              selectedVariant.price ??
                basePrice
            ).toFixed(2)}
          </p>
        </div>
      )}

      <button
        type="button"
        disabled={
          !selectedVariant ||
          addingToCart
        }
        onClick={handleAddToCart}
        className={`mt-6 w-full rounded-xl px-6 py-4 font-bold transition ${
          selectedVariant &&
          !addingToCart
            ? "bg-gray-950 text-white hover:bg-pink-600"
            : "cursor-not-allowed bg-gray-300 text-gray-600"
        }`}
      >
        {addingToCart
          ? "Adding..."
          : selectedVariant
            ? "Add to Cart"
            : "Select a variant"}
      </button>

      {message && (
        <div className="mt-4 rounded-xl border border-green-200 bg-green-50 p-4 text-sm font-semibold text-green-700">
          {message}
        </div>
      )}

      {error && (
        <div className="mt-4 rounded-xl border border-red-200 bg-red-50 p-4 text-sm font-semibold text-red-700">
          {error}
        </div>
      )}
    </div>
  );
}