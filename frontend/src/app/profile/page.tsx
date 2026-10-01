"use client";

import Link from "next/link";
import {
  useCallback,
  useEffect,
  useState,
} from "react";

import type { ProfileResponse } from "@/types/profile";

interface ErrorResponse {
  message?: string;
}

export default function ProfilePage() {
  const [profile, setProfile] =
    useState<ProfileResponse | null>(
      null
    );

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState<string | null>(
      null
    );

  const loadProfile =
    useCallback(async () => {
      const token =
        window.localStorage.getItem(
          "fashionSenseToken"
        );

      if (!token) {
        setProfile(null);

        setError(
          "Please log in to view your profile."
        );

        setLoading(false);

        return;
      }

      try {
        setLoading(true);
        setError(null);

        const response =
          await fetch(
            "/api/profile",
            {
              method: "GET",

              headers: {
                Accept:
                  "application/json",

                Authorization:
                  `Bearer ${token}`,
              },

              cache: "no-store",
            }
          );

        const data =
          (await response.json()) as
            | ProfileResponse
            | ErrorResponse;

        if (!response.ok) {
          const errorData =
            data as ErrorResponse;

          setProfile(null);

          setError(
            errorData.message ??
              `Could not load profile. HTTP ${response.status}.`
          );

          return;
        }

        setProfile(
          data as ProfileResponse
        );
      } catch (requestError) {
        console.error(
          "Profile request failed:",
          requestError
        );

        setProfile(null);

        setError(
          "Could not load your profile."
        );
      } finally {
        setLoading(false);
      }
    }, []);

  useEffect(() => {
    void Promise.resolve().then(
      loadProfile
    );
  }, [loadProfile]);

  return (
    <main className="min-h-screen bg-gray-50 text-gray-900">
      <header className="border-b border-gray-200 bg-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-5">
          <Link
            href="/"
            className="text-2xl font-bold tracking-tight"
          >
            Fashion
            <span className="text-pink-600">
              Sense
            </span>
          </Link>

          <nav className="flex items-center gap-5 text-sm font-medium">
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

      <section className="mx-auto max-w-4xl px-6 py-14">
        <div>
          <p className="text-sm font-semibold uppercase tracking-widest text-pink-600">
            My Account
          </p>

          <h1 className="mt-2 text-4xl font-bold">
            Profile
          </h1>

          <p className="mt-3 text-gray-600">
            View your Fashion Sense
            account information.
          </p>
        </div>

        {loading && (
          <div className="mt-10 rounded-2xl border border-gray-200 bg-white p-8 shadow-sm">
            Loading your profile...
          </div>
        )}

        {!loading && error && (
          <div className="mt-10 rounded-2xl border border-red-200 bg-red-50 p-8">
            <p className="font-semibold text-red-700">
              {error}
            </p>

            <Link
              href="/login"
              className="mt-5 inline-block rounded-md bg-gray-950 px-5 py-3 font-semibold text-white transition hover:bg-pink-600"
            >
              Log In
            </Link>
          </div>
        )}

        {!loading &&
          !error &&
          profile && (
            <>
              <div className="mt-10 overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm">
                <div className="border-b border-gray-200 px-8 py-6">
                  <h2 className="text-xl font-bold">
                    Account Information
                  </h2>
                </div>

                <div className="divide-y divide-gray-100">
                  <div className="grid gap-2 px-8 py-6 sm:grid-cols-3">
                    <p className="font-semibold text-gray-500">
                      Email
                    </p>

                    <p className="sm:col-span-2">
                      {profile.email}
                    </p>
                  </div>

                  <div className="grid gap-2 px-8 py-6 sm:grid-cols-3">
                    <p className="font-semibold text-gray-500">
                      Account Role
                    </p>

                    <p className="sm:col-span-2">
                      {profile.role}
                    </p>
                  </div>

                  <div className="grid gap-2 px-8 py-6 sm:grid-cols-3">
                    <p className="font-semibold text-gray-500">
                      Customer ID
                    </p>

                    <p className="sm:col-span-2">
                      {profile.userId}
                    </p>
                  </div>
                </div>
              </div>

              <div className="mt-8 grid gap-4 sm:grid-cols-3">
                <Link
                  href="/orders"
                  className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm transition hover:-translate-y-1 hover:border-pink-300 hover:shadow-md"
                >
                  <p className="font-bold">
                    My Orders
                  </p>

                  <p className="mt-2 text-sm text-gray-500">
                    View your order history.
                  </p>
                </Link>

                <Link
                  href="/wishlist"
                  className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm transition hover:-translate-y-1 hover:border-pink-300 hover:shadow-md"
                >
                  <p className="font-bold">
                    Wishlist
                  </p>

                  <p className="mt-2 text-sm text-gray-500">
                    View your saved products.
                  </p>
                </Link>

                <Link
                  href="/cart"
                  className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm transition hover:-translate-y-1 hover:border-pink-300 hover:shadow-md"
                >
                  <p className="font-bold">
                    Shopping Cart
                  </p>

                  <p className="mt-2 text-sm text-gray-500">
                    Continue your shopping.
                  </p>
                </Link>
              </div>
            </>
          )}
      </section>
    </main>
  );
}