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

export default function RegisterPage() {
  const [firstName, setFirstName] =
    useState("");

  const [lastName, setLastName] =
    useState("");

  const [email, setEmail] =
    useState("");

  const [password, setPassword] =
    useState("");

  const [
    confirmPassword,
    setConfirmPassword,
  ] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState<string | null>(null);

  const [registered, setRegistered] =
    useState(false);

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>
  ) {
    event.preventDefault();

    setError(null);

    if (
      password !==
      confirmPassword
    ) {
      setError(
        "Passwords do not match."
      );

      return;
    }

    if (
      password.length < 8
    ) {
      setError(
        "Password must contain at least 8 characters."
      );

      return;
    }

    setLoading(true);

    try {
      const response =
        await fetch(
          "/api/auth/register",
          {
            method: "POST",

            headers: {
              "Content-Type":
                "application/json",
            },

            body: JSON.stringify({
              firstName,
              lastName,
              email,
              password,
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
            `Registration failed. HTTP ${response.status}.`
        );
      }

      setRegistered(true);
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
          "Registration failed."
        );
      }
    } finally {
      setLoading(false);
    }
  }

  if (registered) {
    return (
      <main className="min-h-screen bg-gray-50 px-6 py-16 text-gray-900">
        <div className="mx-auto max-w-lg rounded-3xl border border-green-200 bg-white p-10 shadow-sm">
          <div className="text-center">
            <div className="text-5xl">
              ✓
            </div>

            <p className="mt-5 text-sm font-bold uppercase tracking-[0.2em] text-green-600">
              Account Created
            </p>

            <h1 className="mt-3 text-3xl font-bold">
              Welcome to Fashion Sense
            </h1>

            <p className="mt-4 leading-7 text-gray-600">
              Your account has been
              created successfully.
              Email verification is
              required before login.
            </p>
          </div>

          <div className="mt-8 rounded-2xl bg-amber-50 p-5 text-sm text-amber-800">
            <p className="font-bold">
              Development verification
            </p>

            <p className="mt-2">
              Fashion Sense currently
              logs the verification
              token in the Spring Boot
              terminal instead of
              sending a real email.
            </p>

            <p className="mt-2">
              We will build the email
              verification screen next.
            </p>
          </div>

          <Link
            href="/login"
            className="mt-8 block w-full rounded-xl border border-gray-300 px-6 py-4 text-center font-bold transition hover:border-gray-950"
          >
            Go to Login
          </Link>
        </div>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-gray-50 text-gray-900">
      <div className="bg-gray-950 px-4 py-2 text-center text-sm text-white">
        Join Fashion Sense
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
            Already have an account?
          </Link>
        </div>
      </header>

      <section className="mx-auto flex max-w-7xl justify-center px-6 py-16">
        <div className="w-full max-w-lg rounded-3xl border border-gray-200 bg-white p-8 shadow-sm">
          <p className="text-sm font-bold uppercase tracking-[0.2em] text-pink-600">
            Create Account
          </p>

          <h1 className="mt-3 text-3xl font-bold">
            Join Fashion Sense
          </h1>

          <p className="mt-2 text-sm leading-6 text-gray-500">
            Create an account to use
            wishlist, cart, saved
            addresses, checkout and
            order history.
          </p>

          <form
            onSubmit={handleSubmit}
            className="mt-8 space-y-5"
          >
            <div className="grid gap-5 sm:grid-cols-2">
              <div>
                <label
                  htmlFor="firstName"
                  className="text-sm font-semibold"
                >
                  First Name
                </label>

                <input
                  id="firstName"
                  type="text"
                  required
                  value={firstName}
                  onChange={(event) =>
                    setFirstName(
                      event.target.value
                    )
                  }
                  className="mt-2 w-full rounded-xl border border-gray-300 px-4 py-3 outline-none transition focus:border-pink-500"
                />
              </div>

              <div>
                <label
                  htmlFor="lastName"
                  className="text-sm font-semibold"
                >
                  Last Name
                </label>

                <input
                  id="lastName"
                  type="text"
                  required
                  value={lastName}
                  onChange={(event) =>
                    setLastName(
                      event.target.value
                    )
                  }
                  className="mt-2 w-full rounded-xl border border-gray-300 px-4 py-3 outline-none transition focus:border-pink-500"
                />
              </div>
            </div>

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
                placeholder="you@example.com"
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
                placeholder="Minimum 8 characters"
                className="mt-2 w-full rounded-xl border border-gray-300 px-4 py-3 outline-none transition focus:border-pink-500"
              />
            </div>

            <div>
              <label
                htmlFor="confirmPassword"
                className="text-sm font-semibold"
              >
                Confirm Password
              </label>

              <input
                id="confirmPassword"
                type="password"
                required
                value={confirmPassword}
                onChange={(event) =>
                  setConfirmPassword(
                    event.target.value
                  )
                }
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
              className="w-full rounded-xl bg-gray-950 px-6 py-4 font-bold text-white transition hover:bg-pink-600 disabled:cursor-not-allowed disabled:bg-gray-300 disabled:text-gray-600"
            >
              {loading
                ? "Creating Account..."
                : "Create Account"}
            </button>
          </form>

          <p className="mt-6 text-center text-sm text-gray-500">
            Already registered?{" "}
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