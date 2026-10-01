"use client";

import Link from "next/link";
import { useState } from "react";

interface ProductWishlistButtonProps {
  productSlug: string;
}

interface ErrorResponse {
  message?: string;
  error?: string;
}

export default function ProductWishlistButton({
  productSlug,
}: ProductWishlistButtonProps) {
  const [loading, setLoading] =
    useState(false);

  const [added, setAdded] =
    useState(false);

  const [error, setError] =
    useState<string | null>(null);

  async function addToWishlist() {
    setLoading(true);
    setError(null);

    try {
      const token =
        window.localStorage.getItem(
          "fashionSenseToken"
        );

      if (!token) {
        throw new Error(
          "Please log in before adding products to your wishlist."
        );
      }

      const response =
        await fetch(
          `/api/wishlist/${productSlug}`,
          {
            method: "PUT",

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
          `Could not add product to wishlist. HTTP ${response.status}.`;

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

        throw new Error(message);
      }

      setAdded(true);
    } catch (requestError) {
      if (
        requestError instanceof Error
      ) {
        setError(
          requestError.message
        );
      } else {
        setError(
          "Could not add product to wishlist."
        );
      }
    } finally {
      setLoading(false);
    }
  }

  if (added) {
    return (
      <div className="mt-4">
        <div className="rounded-xl border border-green-200 bg-green-50 p-4 text-sm font-semibold text-green-700">
          Added to wishlist.
        </div>

        <Link
          href="/wishlist"
          className="mt-3 block text-center text-sm font-bold text-pink-600 hover:text-pink-700"
        >
          View Wishlist →
        </Link>
      </div>
    );
  }

  return (
    <div className="mt-4">
      <button
        type="button"
        disabled={loading}
        onClick={addToWishlist}
        className="w-full rounded-xl border border-gray-300 bg-white px-6 py-4 font-bold text-gray-900 transition hover:border-pink-600 hover:text-pink-600 disabled:cursor-not-allowed disabled:text-gray-400"
      >
        {loading
          ? "Adding..."
          : "♡ Add to Wishlist"}
      </button>

      {error && (
        <div className="mt-3 rounded-xl border border-red-200 bg-red-50 p-4 text-sm font-semibold text-red-700">
          {error}

          {error.includes(
            "log in"
          ) && (
            <Link
              href="/login"
              className="ml-2 underline"
            >
              Login
            </Link>
          )}
        </div>
      )}
    </div>
  );
}