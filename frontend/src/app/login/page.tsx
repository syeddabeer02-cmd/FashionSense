"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import {
  FormEvent,
  useState,
} from "react";

interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
  userId: number;
  email: string;
  role: string;
}

interface ErrorResponse {
  message?: string;
  error?: string;
}

export default function LoginPage() {
  const router = useRouter();

  const [email, setEmail] =
    useState("");

  const [password, setPassword] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState<string | null>(null);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>
  ) {
    event.preventDefault();

    setLoading(true);
    setError(null);

    try {
      const response = await fetch(
        "/api/auth/login",
        {
          method: "POST",
          headers: {
            "Content-Type":
              "application/json",
          },
          body: JSON.stringify({
            email,
            password,
          }),
        }
      );

      const responseText =
        await response.text();

      let data:
        | LoginResponse
        | ErrorResponse
        | null = null;

      if (responseText) {
        try {
          data = JSON.parse(
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
          data as ErrorResponse | null;

        throw new Error(
          errorData?.message ??
            errorData?.error ??
            `Login failed. HTTP ${response.status}.`
        );
      }

      if (!data) {
        throw new Error(
          "Login succeeded but the server returned no data."
        );
      }

      const loginResponse =
        data as LoginResponse;

      if (
        !loginResponse.accessToken
      ) {
        throw new Error(
          "Login response did not contain an access token."
        );
      }

      window.localStorage.setItem(
        "fashionSenseToken",
        loginResponse.accessToken
      );

      window.localStorage.setItem(
        "fashionSenseUser",
        JSON.stringify({
          userId:
            loginResponse.userId,
          email:
            loginResponse.email,
          role:
            loginResponse.role,
          expiresInSeconds:
            loginResponse.expiresInSeconds,
        })
      );

      router.push(
        "/products/nike-sportswear-hoodie"
      );

      router.refresh();
    } catch (requestError) {
      if (
        requestError instanceof Error
      ) {
        setError(
          requestError.message
        );
      } else {
        setError(
          "Login failed."
        );
      }
    } finally {
      setLoading(false);
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

          <Link
            href="/"
            className="text-sm font-semibold text-gray-600 transition hover:text-pink-600"
          >
            Back to shop
          </Link>
        </div>
      </header>

      <section className="mx-auto flex max-w-7xl justify-center px-6 py-16">
        <div className="w-full max-w-md rounded-3xl border border-gray-200 bg-white p-8 shadow-sm">
          <p className="text-sm font-bold uppercase tracking-[0.2em] text-pink-600">
            Welcome back
          </p>

          <h1 className="mt-3 text-3xl font-bold">
            Login to Fashion Sense
          </h1>

          <p className="mt-2 text-sm text-gray-500">
            Login to manage your
            wishlist, cart, addresses
            and orders.
          </p>

          <form
            onSubmit={handleSubmit}
            className="mt-8 space-y-5"
          >
            <div>
              <label
                htmlFor="email"
                className="text-sm font-semibold"
              >
                Email
              </label>

              <input
                id="email"
                type="email"
                required
                value={email}
                onChange={(event) =>
                  setEmail(
                    event.target.value
                  )
                }
                placeholder="customer@example.com"
                className="mt-2 w-full rounded-xl border border-gray-300 px-4 py-3 outline-none transition focus:border-pink-500"
              />
            </div>

            <div>
              <label
                htmlFor="password"
                className="text-sm font-semibold"
              >
                Password
              </label>

              <input
                id="password"
                type="password"
                required
                value={password}
                onChange={(event) =>
                  setPassword(
                    event.target.value
                  )
                }
                placeholder="Enter your password"
                className="mt-2 w-full rounded-xl border border-gray-300 px-4 py-3 outline-none transition focus:border-pink-500"
              />
            </div>

            {error && (
              <div className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm font-semibold text-red-700">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className={`w-full rounded-xl px-6 py-4 font-bold transition ${
                loading
                  ? "cursor-not-allowed bg-gray-300 text-gray-600"
                  : "bg-gray-950 text-white hover:bg-pink-600"
              }`}
            >
              {loading
                ? "Logging in..."
                : "Login"}
            </button>
          </form>

          <div className="mt-6 border-t border-gray-200 pt-6 text-center text-sm text-gray-500">
            New to Fashion Sense?
            <span className="ml-1 font-semibold text-pink-600">
              Registration UI comes
              next.
            </span>
          </div>
        </div>
      </section>
    </main>
  );
}