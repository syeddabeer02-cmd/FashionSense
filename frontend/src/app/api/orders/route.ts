const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ??
  "http://localhost:8080";

export async function GET(
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

  try {
    const backendResponse =
      await fetch(
        `${API_BASE_URL}/api/customers/me/orders`,
        {
          method: "GET",

          headers: {
            Accept:
              "application/json",

            Authorization:
              authorization,
          },

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
              ? "Backend returned an empty order response."
              : `Order request failed with HTTP ${backendResponse.status}.`,
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
      "Orders proxy error:",
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