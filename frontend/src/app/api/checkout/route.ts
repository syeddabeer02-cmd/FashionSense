const API_BASE_URL =
  process.env.BACKEND_API_BASE_URL ??
  "http://localhost:8080";

export async function POST(
  request: Request
) {
  const authorization =
    request.headers.get(
      "authorization"
    );

  if (!authorization) {
    return Response.json(
      {
        message:
          "Authentication token is required.",
      },
      {
        status: 401,
      }
    );
  }

  const idempotencyKey =
    request.headers.get(
      "idempotency-key"
    );

  if (!idempotencyKey?.trim()) {
    return Response.json(
      {
        message:
          "Idempotency-Key header is required.",
      },
      {
        status: 400,
      }
    );
  }

  if (
    idempotencyKey.trim().length >
    100
  ) {
    return Response.json(
      {
        message:
          "Idempotency-Key must be 100 characters or fewer.",
      },
      {
        status: 400,
      }
    );
  }

  try {
    const requestBody =
      await request.text();

    const backendResponse =
      await fetch(
        `${API_BASE_URL}/api/customers/me/checkout`,
        {
          method: "POST",

          headers: {
            "Content-Type":
              "application/json",

            Accept:
              "application/json",

            Authorization:
              authorization,

            "Idempotency-Key":
              idempotencyKey.trim(),
          },

          body: requestBody,

          cache: "no-store",
        }
      );

    const responseText =
      await backendResponse.text();

    if (!responseText) {
      return Response.json(
        {
          message:
            backendResponse.ok
              ? "Backend returned an empty checkout response."
              : `Checkout failed with HTTP ${backendResponse.status}.`,
        },
        {
          status:
            backendResponse.ok
              ? 502
              : backendResponse.status,
        }
      );
    }

    return new Response(
      responseText,
      {
        status:
          backendResponse.status,

        headers: {
          "Content-Type":
            "application/json",
        },
      }
    );
  } catch (error) {
    console.error(
      "Checkout proxy error:",
      error
    );

    return Response.json(
      {
        message:
          "Could not connect to the Fashion Sense backend.",
      },
      {
        status: 503,
      }
    );
  }
}