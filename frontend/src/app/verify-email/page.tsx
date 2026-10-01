"use client";

import Link from "next/link";

import {
  FormEvent,
  useState,
} from "react";

interface ErrorResponse {
  message?: string;
  error?: string;
}

export default function VerifyEmailPage() {
  const [token, setToken] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const [verified, setVerified] =
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
      const response =
        await fetch(
          "/api/auth/verify-email",
          {
            method: "POST",

            headers: {
              "Content-Type":
                "application/json",
            },

            body: JSON.stringify({
              token:
                token.trim(),
            }),
          }
        );

      const responseText =
        await response.text();

      let data:
        | ErrorResponse
        | Record<string, unknown>
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
            `Verification failed. HTTP ${response.status}.`
        );
      }

      setVerified(true);
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
          "Email verification failed."
        );
      }
    } finally {
      setLoading(false);
    }
  }

  if (verified) {
    return (
      <main className="min-h-screen bg-gray-50 px-6 py-16 text-gray-900">
        <div className="mx-auto max-w-lg rounded-3xl border border-green-200 bg-white p-10 shadow-sm">
          <div className="text-center">
            <div className="text-5xl">
              ✓
            </div>

            <p className="mt-5 text-sm font-bold uppercase tracking-[0.2em] text-green-600">
              Email Verified
            </p>

            <h1 className="mt-3 text-3xl font-bold">
              Your account is active
            </h1>

            <p className="mt-4 leading-7 text-gray-600">
              Your Fashion Sense
              email has been verified
              successfully. You can
              now log in and use the
              complete customer
              experience.
            </p>
          </div>

          <Link
            href="/login"
            className="mt-8 block w-full rounded-xl bg-gray-950 px-6 py-4 text-center font-bold text-white transition hover:bg-pink-600"
          >
            Login to Fashion Sense
          </Link>

          <Link
            href="/"
            className="mt-3 block w-full rounded-xl border border-gray-300 px-6 py-4 text-center font-bold transition hover:border-gray-950"
          >
            Return to Shop
          </Link>
        </div>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-gray-50 text-gray-900">
      <div className="bg-gray-950 px-4 py-2 text-center text-sm text-white">
        Fashion Sense Account Verification
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
            href="/login"
            className="text-sm font-semibold text-gray-600 transition hover:text-pink-600"
          >
            Login
          </Link>
        </div>
      </header>

      <section className="mx-auto flex max-w-7xl justify-center px-6 py-16">
        <div className="w-full max-w-lg rounded-3xl border border-gray-200 bg-white p-8 shadow-sm">
          <p className="text-sm font-bold uppercase tracking-[0.2em] text-pink-600">
            Verify Email
          </p>

          <h1 className="mt-3 text-3xl font-bold">
            Activate your account
          </h1>

          <p className="mt-3 leading-7 text-gray-500">
            Enter the verification
            token generated when you
            registered.
          </p>

          <div className="mt-6 rounded-xl bg-amber-50 p-4 text-sm text-amber-800">
            <p className="font-bold">
              Development mode
            </p>

            <p className="mt-2">
              The verification token
              currently appears in
              the Spring Boot terminal.
              Later we will replace
              this with real email
              delivery.
            </p>
          </div>

          <form
            onSubmit={handleSubmit}
            className="mt-8"
          >
            <label
              htmlFor="token"
              className="text-sm font-semibold"
            >
              Verification Token
            </label>

            <textarea
              id="token"
              required
              rows={4}
              value={token}
              onChange={(event) =>
                setToken(
                  event.target.value
                )
              }
              placeholder="Paste verification token here"
              className="mt-2 w-full resize-none rounded-xl border border-gray-300 px-4 py-3 font-mono text-sm outline-none transition focus:border-pink-500"
            />

            {error && (
              <div className="mt-5 rounded-xl border border-red-200 bg-red-50 p-4 text-sm font-semibold text-red-700">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={
                loading ||
                token.trim()
                  .length === 0
              }
              className="mt-6 w-full rounded-xl bg-gray-950 px-6 py-4 font-bold text-white transition hover:bg-pink-600 disabled:cursor-not-allowed disabled:bg-gray-300 disabled:text-gray-600"
            >
              {loading
                ? "Verifying..."
                : "Verify Email"}
            </button>
          </form>

          <p className="mt-6 text-center text-sm text-gray-500">
            Already verified?{" "}
            <Link
              href="/login"
              className="font-bold text-pink-600 hover:text-pink-700"
            >
              Login
            </Link>
          </p>
        </div>
      </section>
    </main>
  );
}